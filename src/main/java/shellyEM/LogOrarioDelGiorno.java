package shellyEM;

import static shellyEM.Estrai.Channel.CHANNELL_CASA;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import shellyEM.Estrai.Channel;


public class LogOrarioDelGiorno {

	private boolean USA_CACHE=true;
	private boolean USA_IERI=true;
	private Channel CHANNEL=CHANNELL_CASA;
	
	public static void main(String[] args) throws Exception {
		LogOrarioDelGiorno l = new LogOrarioDelGiorno();
		l.go();
	}

	private void go() throws Exception {
		CreaStoricoShelly st= new CreaStoricoShelly(USA_CACHE);
		Map<LocalDateTime, BigDecimal> fromLocal = st.fromLocal(CHANNEL.getValore());
		
		Map<String, Number> collectMinute = fromLocal
		.entrySet()
		.stream()
		.filter(filtraGiorno::test)
		.sorted((e1,e2) -> e1.getValue().compareTo(e2.getValue()))
		.map(e->Map.entry(formattaHourMinute.apply(e), e.getValue().doubleValue()))
		.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,(a,b) -> a, LinkedHashMap::new ));
		collectMinute.entrySet().forEach(formatEntry::accept);

		System.out.println("*****************");
		
		Map<String, Number> collectHourOfAllDay = fromLocal
		.entrySet()
		.stream()
		.collect(
					Collectors.groupingBy(
						formattaHour::apply,
						Collectors.averagingInt(map -> map.getValue().intValue())
					)
				)
		.entrySet()
		.stream()
		.sorted((e1,e2) -> e1.getValue().compareTo(e2.getValue()))
		.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,(a,b) -> a, LinkedHashMap::new ));
		collectHourOfAllDay.entrySet().forEach(formatEntry::accept);

		System.out.println("*****************");


		Map<String, List<Entry<LocalDateTime, BigDecimal>>> collectWithList = fromLocal
		.entrySet()
		.stream()
		.collect(
					Collectors.groupingBy(
						map -> formattaHour.apply(map),
						Collectors.toList()
					)
				);
		collectWithList.forEach(printListOfString::accept);
	}

	

	Predicate<Entry<LocalDateTime, BigDecimal>> filtraGiorno = entry -> entry.getKey().toLocalDate().isEqual((USA_IERI?LocalDate.now().minusDays(1):LocalDate.now()));
	Function<Entry<LocalDateTime, BigDecimal>, String> formattaHourMinute=entry->entry.getKey().format(DateTimeFormatter.ofPattern("HH:mm"));
	Function<Entry<LocalDateTime, BigDecimal>, String> formattaHour=entry->entry.getKey().format(DateTimeFormatter.ofPattern("EEEE HH"));
	Consumer<Entry<String, Number>> formatEntry = entry -> System.out.println(entry.getKey() + " -> " +  NumberFormat.getNumberInstance(Locale.ITALY).format(entry.getValue()));
	Consumer<Entry<LocalDateTime, BigDecimal>> printRiga=riga -> System.out.println("\t" + riga);
	BiConsumer<String, List<Entry<LocalDateTime, BigDecimal>>> printListOfString= (k,v)->{
		System.out.println(k + " --> ");
		v.forEach(printRiga::accept);
	};

	
	

	
}
