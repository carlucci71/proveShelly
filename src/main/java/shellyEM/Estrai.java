package shellyEM;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.net.ssl.SSLHandshakeException;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class Estrai {

	

	private static final String PRIMO_GIORNO_DA_ELABORARE = "2022-01-01";
	private static final String URL_SHELLY_LOCAL = "101.56.193.166";
	private static final String ID_SHELLY = "244cab418407";
	private String token;
	private static final boolean USA_CACHE = true;
	private static final String ROOT_FILE = "/1/shelly/";
	
	enum Frequenza{GIORNO, SETTIMANA, MESE, ANNO};
	
	
	enum Channel{
		CHANNELL_PIANO(1),
		CHANNELL_CASA(0);
		int valore;
		private Channel(int valore){
			this.valore=valore;
		}
		public int getValore() {
			return valore;
		}
		
	}
	
	static ObjectMapper mapper=null;
	SimpleDateFormat sdfCloud = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	SimpleDateFormat sdfLocal = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	SimpleDateFormat sdfSimple  = new SimpleDateFormat("yyyy-MM-dd");
	DecimalFormat df = new DecimalFormat("0.##");

	public static void main(String[] args) throws Exception {
		Estrai estrai = new Estrai();
		estrai.go(0);
		estrai.go(1);
	}

	public Estrai() {
		mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
	}
	
	private String getToken() throws Exception {
		if (token == null) {
			Map<String, Object> mapLogin = callHTTP("POST", "application/x-www-form-urlencoded", "https://api.shelly.cloud/auth/login", "email=carlucci.daniele%40gmail.com&password=3537eb27fa0b51b6952c5ee7bb742d6aad61b314&var=2", null);
			Map fromJson = (Map) fromJson(mapLogin.get("response").toString(), Map.class).get("data");
			token = fromJson.get("token").toString();
		}
		return token;
	}

	private void go(int channell) throws Exception {
		Map<String, BigDecimal[]> fromLocal = fromLocal(channell, false);
		Calendar c = Calendar.getInstance();
		Date parse = sdfSimple.parse(PRIMO_GIORNO_DA_ELABORARE);
		c.setTime(parse);
		Calendar oggi=Calendar.getInstance();
		String strOggi=sdfSimple.format(oggi.getTime());
		while(c.compareTo(oggi)<0) {
			String ggDaElaborare = sdfSimple.format(c.getTime());
			Map<String, BigDecimal[]> fromCloud = fromCloud(Frequenza.GIORNO, ggDaElaborare, (ggDaElaborare.equals(strOggi)?false:true),channell);
			System.out.println(ggDaElaborare + "@" + channell + "@local@giorno@con@" + (fromLocal.get(ggDaElaborare)==null?"0,00":df.format(fromLocal.get(ggDaElaborare)[0])));
//			System.out.println(ggDaElaborare + "@local@giorno@ret@" + (fromLocal.get(ggDaElaborare)==null?"0,00":df.format(fromLocal.get(ggDaElaborare)[1])));
			System.out.println(ggDaElaborare + "@" + channell + "@cloud@giorno@con@" + (fromCloud.get(ggDaElaborare)==null?"0,00":df.format(fromCloud.get(ggDaElaborare)[0])));
//			System.out.println(ggDaElaborare + "@cloud@giorno@ret@" + (fromCloud.get(ggDaElaborare)==null?"0,00":df.format(fromCloud.get(ggDaElaborare)[1])));
//			System.out.println(ggDaElaborare + "@cloud@giorno@conTot@" + (fromCloud.get(ggDaElaborare)==null?"0,00":df.format(fromCloud.get(ggDaElaborare)[2])));
//			System.out.println(ggDaElaborare + "@cloud@giorno@retTot@" + (fromCloud.get(ggDaElaborare)==null?"0,00":df.format(fromCloud.get(ggDaElaborare)[3])));
			/*
			if (c.get(Calendar.DAY_OF_WEEK)==2) {
				Map<String, BigDecimal[]> fromCloud2 =fromCloud(Frequenza.SETTIMANA, ggDaElaborare);
//				System.out.println(ggDaElaborare + "@cloud@settimana@con@" + (fromCloud2.get(ggDaElaborare)==null?"0,00":df.format(fromCloud2.get(ggDaElaborare)[0])));
//				System.out.println(ggDaElaborare + "@cloud@settimana@ret@" + (fromCloud2.get(ggDaElaborare)==null?"0,00":df.format(fromCloud2.get(ggDaElaborare)[1])));
//				System.out.println(ggDaElaborare + "@cloud@settimana@conTot@" + (fromCloud2.get(ggDaElaborare)==null?"0,00":df.format(fromCloud2.get(ggDaElaborare)[2])));
//				System.out.println(ggDaElaborare + "@cloud@settimana@retTot@" + (fromCloud2.get(ggDaElaborare)==null?"0,00":df.format(fromCloud2.get(ggDaElaborare)[3])));
			}
			if (c.get(Calendar.DAY_OF_MONTH)==1) {
				Map<String, BigDecimal[]> fromCloud2 =fromCloud(Frequenza.MESE, ggDaElaborare);
//				System.out.println(ggDaElaborare + "@cloud@mese@con@" + (fromCloud2.get(ggDaElaborare)==null?"0,00":df.format(fromCloud2.get(ggDaElaborare)[0])));
//				System.out.println(ggDaElaborare + "@cloud@mese@ret@" + (fromCloud2.get(ggDaElaborare)==null?"0,00":df.format(fromCloud2.get(ggDaElaborare)[1])));
//				System.out.println(ggDaElaborare + "@cloud@mese@conTot@" + (fromCloud2.get(ggDaElaborare)==null?"0,00":df.format(fromCloud2.get(ggDaElaborare)[2])));
//				System.out.println(ggDaElaborare + "@cloud@mese@retTot@" + (fromCloud2.get(ggDaElaborare)==null?"0,00":df.format(fromCloud2.get(ggDaElaborare)[3])));
			}
			if (c.get(Calendar.DAY_OF_MONTH)==1 && c.get(Calendar.MONTH)==0) {
				Map<String, BigDecimal[]> fromCloud2 =fromCloud(Frequenza.ANNO, ggDaElaborare);
//				System.out.println(ggDaElaborare + "@cloud@anno@con@" + (fromCloud2.get(ggDaElaborare)==null?"0,00":df.format(fromCloud2.get(ggDaElaborare)[0])));
//				System.out.println(ggDaElaborare + "@cloud@anno@ret@" + (fromCloud2.get(ggDaElaborare)==null?"0,00":df.format(fromCloud2.get(ggDaElaborare)[1])));
//				System.out.println(ggDaElaborare + "@cloud@anno@conTot@" + (fromCloud2.get(ggDaElaborare)==null?"0,00":df.format(fromCloud2.get(ggDaElaborare)[2])));
//				System.out.println(ggDaElaborare + "@cloud@anno@retTot@" + (fromCloud2.get(ggDaElaborare)==null?"0,00":df.format(fromCloud2.get(ggDaElaborare)[3])));
			}
			*/
			c.add(Calendar.DAY_OF_MONTH, 1);
		}
		
		
		
		
		
		/*
		Calendar c = Calendar.getInstance();
		c.setTime(sdfSimple.parse(SETTIMANA_DA_ELABORARE));
		System.out.println(sdfSimple.format(c.getTime()) + " --> ");
		fromCloud(Frequenza.SETTIMANA, SETTIMANA_DA_ELABORARE);
		System.out.println("------------------");
		for (int i=0;i<7;i++) {
			String ggDaElaborare = sdfSimple.format(c.getTime());
			System.out.println(ggDaElaborare + " --> ");
			fromCloud(Frequenza.GIORNO, ggDaElaborare);
			c.add(Calendar.DAY_OF_MONTH, 1);
		}
		*/
	}


	private Map<String, BigDecimal[]> fromLocal(int channell, boolean usaCache) throws Exception {
		String response =  cachGetHTTP("GET", channell + "-" + "fromLocal.csv","text/csv","http://" + URL_SHELLY_LOCAL + "/emeter/" + channell + "/em_data.csv", null, true, true);
		String[] righe = response.split(System.lineSeparator());
		boolean bPrima=true;
		Map<String, BigDecimal[]> totConsumption=new TreeMap<>();
		for (String riga : righe) {
			if (bPrima) {
				bPrima=false;
			}
			else {
				String[] colonne = riga.split(",");
				Date datetime = sdfLocal.parse(colonne[0]);
				String key=sdfSimple.format(datetime);
				BigDecimal consumptionOfDay[] = totConsumption.get(key);
				if (consumptionOfDay==null) {
					consumptionOfDay=new BigDecimal[] {new BigDecimal("0"),new BigDecimal("0")};
				}
				BigDecimal consumption = new BigDecimal(colonne[1]);
				consumptionOfDay[0]=consumptionOfDay[0].add(consumption);
				BigDecimal reversed = new BigDecimal(colonne[2]);
				consumptionOfDay[1]=consumptionOfDay[1].add(reversed);
				BigDecimal min_voltage = new BigDecimal(colonne[3]);
				BigDecimal max_voltage = new BigDecimal(colonne[4]);
//				System.out.println(datetime);
//				System.out.println(consumption);
//				System.out.println(reversed);
//				System.out.println(min_voltage);
//				System.out.println(max_voltage);
				totConsumption.put(key, consumptionOfDay);
			}
		}
		return totConsumption;
	}


	private Map<String, BigDecimal[]> fromCloud(Frequenza frequenza, String dateFrom, boolean usaCache, int channell) throws Exception, ParseException {
		Map<String, BigDecimal[]> ret=new TreeMap<>();
		String responseFromCloud = cachGetHTTP("POST", "fromCloud-" + channell + "-" + frequenza + "-" + dateFrom + ".json", "application/x-www-form-urlencoded", 
				"https://shelly-9-eu.shelly.cloud/statistics/emeter/consumption", 
				generateBody(frequenza, dateFrom,channell), usaCache, usaCache).toString();
		Map fromJson = fromJson(responseFromCloud, Map.class);
		Map m = (Map) fromJson.get("data");
		Map unit=(Map) m.get("units");
//		System.out.println("unit: " + unit);
//		System.out.println("interval:" +m.get("history_interval"));

		BigDecimal bdTotal=new BigDecimal(m.get("total").toString());
		bdTotal = bdTotal.multiply(new BigDecimal("1000"));
//		System.out.println("total " + bdTotal);
		

		BigDecimal bdTotalR=new BigDecimal(m.get("total_r").toString());
//		System.out.println("totalR " + bdTotalR);
		
		
		List<Map> l = (List<Map>) m.get("history");
		BigDecimal totConsumption=new BigDecimal("0");
		BigDecimal totReversed=new BigDecimal("0");
		for (Map map : l) {
			Boolean available = Boolean.parseBoolean(map.get("available").toString());
			if (available) {
				Date datetime = sdfCloud.parse(map.get("datetime").toString());
				BigDecimal consumption = new BigDecimal(map.get("consumption").toString());
				BigDecimal reversed = new BigDecimal(map.get("reversed").toString());
				BigDecimal min_voltage = new BigDecimal(map.get("min_voltage").toString());
				BigDecimal max_voltage = new BigDecimal(map.get("max_voltage").toString());
				totConsumption=totConsumption.add(consumption);
				totReversed=totReversed.add(reversed);
			}
		}
//		System.out.println("totConsumption " + totConsumption.doubleValue());
//		System.out.println("totReversed " + totReversed.doubleValue());
		ret.put(dateFrom, new BigDecimal[] {totConsumption, totReversed,bdTotal,bdTotalR});
		return ret;

	}

	public String generateBody(Frequenza frequenza, String dateFrom, int channell) throws Exception {
		String ret = "";
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
		Calendar cTo=Calendar.getInstance();
		cTo.setTime(sdf.parse(dateFrom));
		switch (frequenza) {
		case GIORNO:
			ret=getBody("day",dateFrom, null,channell);
			break;
		case SETTIMANA:
			cTo.add(Calendar.DAY_OF_MONTH, 6);
			ret=getBody("custom",dateFrom, sdf.format(cTo.getTime()),channell);
			break;
		case MESE:
			cTo.add(Calendar.MONTH, 1);
			cTo.add(Calendar.DAY_OF_MONTH, -1);
			ret=getBody("custom",dateFrom, sdf.format(cTo.getTime()),channell);
			break;
		case ANNO:
			cTo.add(Calendar.YEAR, 1);
			cTo.add(Calendar.DAY_OF_MONTH, -1);
			ret=getBody("custom",dateFrom, sdf.format(cTo.getTime()),channell);
			break;

		default:
			break;
		}
		return ret;
	}
	
	private String getBody(String dateRange, String dateFrom, String dateTo, int channell) throws Exception {
		StringBuilder sb = new StringBuilder();
		sb.append(getParamBody("id", ID_SHELLY));
		sb.append(getParamBody("channel", Integer.toString(channell)));
		sb.append(getParamBody("date_range",dateRange));
		sb.append(getParamBody("date_from",encodeDate(dateFrom, false)));
		if (dateTo!=null) {
			sb.append(getParamBody("date_to",encodeDate(dateTo, true)));
		}
		String ret=sb.toString();
		ret=ret.substring(0,ret.length()-1);
		return ret;
	}
	
	private String getParamBody(String key, String value) {
		StringBuilder sb = new StringBuilder();
		sb.append(key).append("=").append(value).append("&");
		return sb.toString();
	}
	
	private  Map<String,Object> callHTTP(String verbo, String contentType, String url, String body,  Map<String, String> headers) throws Exception {
		//		System.out.println(verbo + " " + url + " " + printMap(headers));
		Map <String, Object> ret = new HashMap<>();
		URL obj = new URL(url);
		HttpURLConnection connectionHTTP = (HttpURLConnection) obj.openConnection();
		connectionHTTP.setRequestMethod(verbo);
		if (headers != null) {
			Iterator<String> iterator = headers.keySet().iterator();
			while (iterator.hasNext()) {
				String key = (String) iterator.next();
				connectionHTTP.setRequestProperty(key, headers.get(key));
			}

		}
		if (!verbo.equals("GET")) {
			connectionHTTP.setRequestProperty("content-type", contentType);
			connectionHTTP.setDoOutput(true);
			OutputStream os = connectionHTTP.getOutputStream();
			os.write(body.getBytes());
			os.flush();
			os.close();
		}
		else {
			if (body != null) {
				throw new RuntimeException("Per le chiamate con verbo GET non valorizzare il body");
			}
		}
		int responseCode=0;
		try
		{
			responseCode = connectionHTTP.getResponseCode();
		}
		catch (SSLHandshakeException e)
		{
			throw new RuntimeException("Aggiornare i certificati per: " + url);
		}
		StringBuilder response = new StringBuilder();
		if (responseCode == HttpURLConnection.HTTP_OK) {
			Map<String, List<String>> headerFields = connectionHTTP.getHeaderFields();
			ret.put("headerFields", headerFields);
			BufferedReader in = new BufferedReader(new InputStreamReader(connectionHTTP.getInputStream()));
			String inputLine;
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
				if (contentType.equals("text/csv")) {
					response.append(System.lineSeparator());
				}
			}
			in.close();
		} else {
			BufferedReader bfOutputResponse = new BufferedReader(
					new InputStreamReader(connectionHTTP.getErrorStream()));
			String outputLine;
			StringBuilder sfResponse = new StringBuilder();
			while ((outputLine = bfOutputResponse.readLine()) != null) {
				sfResponse.append(outputLine);
			}
			bfOutputResponse.close();
			String stringResponse = sfResponse.toString();
			throw new RuntimeException(verbo + " NOT WORKED ".concat(url).concat(" -> ").concat((body==null?"":body)).concat("STACK:")
					.concat(stringResponse));
		}
		ret.put("response", response.toString());
		return ret; 
	}

	public static <T> T fromJson(String json, Class<T> clazz) throws Exception
	{
		return mapper.readValue(json, clazz);
	}

	public static String toJson(Object o) throws Exception
	{
			byte[] data = mapper.writeValueAsBytes(o);
			return new String(data, StandardCharsets.ISO_8859_1);
	}

	private String encodeDate(String day, boolean toHour) {
		int anno=Integer.parseInt(day.substring(0,4));
		int mese=Integer.parseInt(day.substring(5,7))-1;
		int giorno=Integer.parseInt(day.substring(8,10));
		Calendar c = Calendar.getInstance();
		c.set(Calendar.YEAR, anno);
		c.set(Calendar.MONTH, mese);
		c.set(Calendar.DAY_OF_MONTH, giorno);
		int ora=0;
		int minuti=0;
		int secondi=0;
		if (toHour) {
			ora=23;
			minuti=59;
			secondi=59;
		}
		c.set(Calendar.HOUR_OF_DAY, ora);
		c.set(Calendar.MINUTE, minuti);
		c.set(Calendar.SECOND, secondi);
//		System.out.println(sdfCloud.format(c.getTime()));
		String encode = URLEncoder.encode(sdfCloud.format(c.getTime()), StandardCharsets.US_ASCII);
		encode=encode.replace("+", "%20");
		return encode;
	}
	
	public String cachGetHTTP(String verbo, String nomeFile, String contentType, String url, String body, boolean usaCache, boolean salva) throws Exception {
		String directory = ROOT_FILE;
		Path pathFile = Paths.get(directory + nomeFile);
		if (Files.exists(pathFile) && USA_CACHE && usaCache) {
//			System.err.println("CACHE: " + nomeFile);
			try {
				List<String> readAllLines = Files.readAllLines(pathFile, Charset.defaultCharset());
				StringBuilder sb = new StringBuilder();
				for (String linea : readAllLines) {
					
					if (nomeFile.endsWith(".csv")) {
						linea = linea + System.lineSeparator();
//						linea = linea + "@@";
					}
					
					sb.append(linea);
				}
				return sb.toString();
			} catch (Exception e)
			{
				e.printStackTrace(System.err);
				throw new RuntimeException("Errore in " + nomeFile);
			}
		} else {
//			System.err.println("NON CACHE: " + nomeFile);
			if (url == null) {
				throw new RuntimeException("File non esistente: " + nomeFile);
			}
			try {
				Path pathDir = Paths.get(directory);
				if (!Files.isDirectory(pathDir))
				{
					Files.createDirectories(pathDir);
				}
				Files.deleteIfExists(pathFile);
				Map<String, String> header=new HashMap<>();
				if (verbo.equals("POST")) {
					header.put("Authorization", "Bearer " + getToken());
				}
				String s = callHTTP(verbo,contentType,url, body, header).get("response").toString();
				String sPretty="";
				if (nomeFile.endsWith(".csv")) {
					/*
					byte[] bytes = s.getBytes();
					for (byte b : bytes) {
						System.out.println(b + ":" + (char)b);
					}
					*/
//					sPretty=s.replace("@@", "\n");
					sPretty=s;
				}
				else {
					try {
						sPretty = toJson(fromJson(s, Map.class));
					}
					catch(Exception e ) {
						try {
							sPretty = toJson(fromJson(s, List.class));
						}
						catch(Exception e2 ) {
							sPretty=s;
						}
					}
				}
				if (salva) {
					Files.createFile(pathFile);
					Files.write(pathFile, sPretty.getBytes());
				}
				else {
//					System.err.println("NON SALVO: " + pathFile);
				}
				return s;
			} catch (Exception e)
			{
				e.printStackTrace(System.err);
				throw new RuntimeException("Errore in " + url);
			}
		}
	}

	
}
