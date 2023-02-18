package shellyEM;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import shellyEM.Estrai.Frequenza;

public class CreaStoricoShelly {
	static private boolean USA_CACHE_FORZATA_DEFAULT=true;
	private Estrai estrai=new Estrai();
	
	public static void main(String[] args) throws Exception {
		CreaStoricoShelly c = new CreaStoricoShelly(USA_CACHE_FORZATA_DEFAULT);
		c.go(0);
		c.go(1);
	}

	public CreaStoricoShelly(boolean usaCacheForzata) throws Exception {
		super();
		USA_CACHE_FORZATA = usaCacheForzata;
	}

	private void go(int channell) throws Exception {
		Map<LocalDateTime, BigDecimal> dati;
		dati = fromLocal(channell);
		if (channell==0) {
			System.out.println("DOVE"+ "\t" + "CHANNELL" + "\t" + "DATA" + "\t" + "VALUE"+ "\t" + "ORA"+ "\t" + "GIORNO");
		}
		dati.forEach((k,v)->{
			System.out.println("LOCAL"+ "\t" + channell + "\t" + formatter_yyyy_MM_dd_HH_mm.format(k) + "\t" + df.format(v)
			+ "\t" + getOraFromData(k)+ "\t" + getGiornoFromData(k));
		});
		dati=getFromClud(channell);
		dati.forEach((k,v)->{
			System.out.println("CLOUD"+ "\t" + channell + "\t" + formatter_yyyy_MM_dd_HH_mm.format(k) + "\t" + df.format(v)
			+ "\t" + getOraFromData(k)+ "\t" + getGiornoFromData(k));
		});
	}

	private Map<LocalDateTime, BigDecimal> getFromClud(int channell) throws Exception {
		Map<LocalDateTime, BigDecimal> dati=new HashMap<>();
		LocalDate elabDay = LocalDate.parse(PRIMO_GIORNO_DA_ELABORARE, formatterSimple);
		LocalDate oggi=LocalDate.now();
		while(elabDay.compareTo(oggi)<=0) {
			String ggDaElaborare=formatterSimple.format(elabDay);
			Map<LocalDateTime, BigDecimal> fromCloud = fromCloud(Frequenza.GIORNO, ggDaElaborare, elabDay.compareTo(oggi)<0,channell);
			dati.putAll(fromCloud);
			elabDay=elabDay.plusDays(1);
		}
		return dati;
	}

	private Map<LocalDateTime, BigDecimal> fromCloud(Frequenza frequenza, String dateFrom, boolean usaCache, int channell) throws Exception, ParseException {
		Map<LocalDateTime, BigDecimal> dati=new TreeMap<>();
		String responseFromCloud = estrai.cachGetHTTP("POST", "fromCloud-" + channell + "-" + frequenza + "-" + dateFrom + ".json", "application/x-www-form-urlencoded", 
				"https://shelly-9-eu.shelly.cloud/statistics/emeter/consumption", 
				estrai.generateBody(frequenza, dateFrom,channell), (USA_CACHE_FORZATA?USA_CACHE_FORZATA:usaCache), true).toString();
		Map fromJson = estrai.fromJson(responseFromCloud, Map.class);
		Map m = (Map) fromJson.get("data");
		List<Map> l = (List<Map>) m.get("history");
		for (Map map : l) {
			Boolean available = Boolean.parseBoolean(map.get("available").toString());
			if (available) {
				LocalDateTime dateTime = LocalDateTime.parse(map.get("datetime").toString(), formatterCloud);
				BigDecimal consumption = new BigDecimal(map.get("consumption").toString());
				if (consumption.doubleValue()>0) {
					dati.put(dateTime, consumption);
				}
			}
		}
		return dati;

	}
	
	public Map<LocalDateTime, BigDecimal> fromLocal(int channell) throws Exception {
		String response =  estrai.cachGetHTTP("GET", channell + "-" + "fromLocal.csv","text/csv","http://" + URL_SHELLY_LOCAL + "/emeter/" + channell + "/em_data.csv", null, (USA_CACHE_FORZATA?USA_CACHE_FORZATA:false), true);
		String[] righe = response.split(System.lineSeparator());
		boolean bPrima=true;
		Map<LocalDateTime, BigDecimal> totConsumption=new TreeMap<>();
		for (String riga : righe) {
			if (bPrima) {
				bPrima=false;
			}
			else {
				String[] colonne = riga.split(",");
				LocalDateTime datetime = LocalDateTime.parse(colonne[0], formatter_yyyy_MM_dd_HH_mm);
				datetime = datetime.atZone(ZoneId.of("UTC")).withZoneSameInstant(ZoneId.of("Europe/Rome")).toLocalDateTime();
				BigDecimal consumption = new BigDecimal(colonne[1]);
				if (consumption.doubleValue()>0 && datetime.toLocalDate().isAfter(LocalDate.parse(PRIMO_GIORNO_DA_ELABORARE, formatterSimple).plusDays(-1))) {
					totConsumption.put(datetime, consumption);
				}
			}
		}
		return totConsumption;
	}

	private String getOraFromData(LocalDateTime localDateTime)  {
		localDateTime=localDateTime.truncatedTo(ChronoUnit.HOURS);
		return formatterOut_YYYYMMDDHHMM.format(localDateTime);
	}
	
	private String getGiornoFromData(LocalDateTime localDateTime)  {
		return formatterOut_YYYYMMDD.format(localDateTime);
	}
	
	private boolean USA_CACHE_FORZATA;
	private DateTimeFormatter formatterOut_YYYYMMDDHHMM = DateTimeFormatter.ofPattern("yyyyMMddHH");
	private static DateTimeFormatter formatter_yyyy_MM_dd_HH_mm = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
	private static DateTimeFormatter formatter_yyyy_MM_dd_HH_mmZ = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm z");
	private DateTimeFormatter formatterOut_YYYYMMDD = DateTimeFormatter.ofPattern("yyyyMMdd");
	private DateTimeFormatter formatterSimple = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	private DateTimeFormatter formatterCloud = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
	private DecimalFormat df = new DecimalFormat("0.00");
	private static final String PRIMO_GIORNO_DA_ELABORARE = "2023-01-15";
	private static final String URL_SHELLY_LOCAL = "101.56.193.166";

	/*
	 * F1 è la fascia delle ore di punta, quindi la più costosa. Si applica nei giorni feriali dal lunedì al venerdì, dalle 8.00 alle 19.00.
F2 riguarda i giorni feriali dal lunedì al venerdì, dalle7.00 alle 8.00 e dalle 19.00 alle 23.00, e il sabato, dalle 7.00 alle 23.00.
F3 è la fascia oraria attiva dal lunedì al sabato dalle 23.00 alle 7.00 e la domenica e i festivi per l’intera giornata.
	 */
	
}
