package shellyEM;

import static shellyEM.Estrai.Channel.CHANNELL_CASA;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.BinaryOperator;
import java.util.stream.Collectors;

import shellyEM.Estrai.Channel;


public class LogOrarioDelGiorno {

	private boolean USA_CACHE=true;
	private Channel CHANNEL=CHANNELL_CASA;
	
	public static void main(String[] args) throws Exception {
		LogOrarioDelGiorno l = new LogOrarioDelGiorno();
		l.go();
	}

	private void go() throws Exception {
		CreaStoricoShelly st= new CreaStoricoShelly(USA_CACHE);
		Map<LocalDateTime, BigDecimal> fromLocal = st.fromLocal(CHANNEL.getValore());
		
		BinaryOperator<BigDecimal> xx=(a,b) -> a;
		Map<LocalDateTime, BigDecimal> collect = fromLocal.
		entrySet().
		stream().
		filter(e -> e.getKey().toLocalDate().isEqual(LocalDate.now())).
		sorted((e1,e2) -> e1.getValue().compareTo(e2.getValue()))
		.collect(Collectors.toMap(x -> x.getKey(), x -> x.getValue(),xx, LinkedHashMap::new ));
//		.collect(Collectors.toMap(x -> x.getKey(), x -> x.getValue(),xx, TreeMap::new ));
		
		
		System.out.println(collect);
		
	}
	
}
