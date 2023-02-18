package shellyEM.lam;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

public class ProvaLambda {
	public static void main(String[] args) {
		/*
		Consumer<String> myConsumer = s -> System.out.println(s);
		myConsumer.accept("prova");

		Predicate<String> myPredicate = s -> s.startsWith("x");
		System.out.println(myPredicate.test("xxx"));
		System.out.println(myPredicate.test("yyy"));

		Function<String, String> myFunct = s -> s;
		System.out.println(myFunct.apply("sss"));

		Supplier<String> mySupplier = () -> "s";
		System.out.println(mySupplier.get());

		BiConsumer<String, String> myBi = (s1,s2) -> System.out.println(s1 + "-" + s2);
		myBi.accept("1", "22");

		BinaryOperator<String> myBin = (s1,s2) -> s1 + "-" + s2;
		System.out.println(myBin.apply("1", "23"));

		BiFunction<String, String, String> myBif = (s1,s2) -> s1 + "-" + s2;
		System.out.println(myBif.apply("x", "y"));
		 */
		List<String> l=new ArrayList<>();
		l.add("PRIMO");
		l.add("SECONDO");
		l.add("TERZO");
		l.add("SECONDO");
		l.add("QUARTO");
		l.add("SECONDO");
		l.add("QUINTO");


		System.out.println(l.stream().allMatch(s -> s.startsWith("x")));
		System.out.println(l.stream().anyMatch(s -> s.startsWith("x")));
		System.out.println(l.stream().reduce("INTRO", (s1,s2) -> s1 + "-" + s2));
		System.out.println("--------------" + "DISTINCT: ");
		l.stream().distinct().forEach(s->System.out.println(s));
		List<String> collect = l.stream().collect(Collectors.toList());
		System.out.println("--------------" + "COLLECT1: ");
		collect.forEach(s->System.out.println(s));
		Stream<String> stream = l.stream();
		System.out.println("--------------" + "COLLECT2: ");
		stream.forEach(s->System.out.println(s));


		List<Map<String, String>> ll = new ArrayList<>();
		Map<String, String> m = new HashMap<>();
		m.put("a", "x");
		m.put("b", "y");
		ll.add(m);
		m = new HashMap<>();
		m.put("a", "z");
		m.put("b", "k");
		ll.add(m);
		System.out.println("--------------" + "PRIMA DI MAP: ");
		ll.stream().forEach(s->System.out.println(s));

		Function<Map<String, String>, Map<String, String>> mapToUppercase = (map) -> {
			map.forEach((k,v) -> map.put(k,v.toUpperCase()));
			return map;
		};
		System.out.println("--------------" + "DOPO MAP: ");
		ll.stream().map(mapToUppercase).forEach(s->System.out.println(s));
		
		System.out.println("--------------" + "PAGE: ");
		Page<String> findAll = new PageImpl<>(l);
		findAll.map(s -> s.toUpperCase());
		findAll.forEach(x -> System.out.println(x));

		String[] a = new String[] {"A","B"};
		Stream.of(a).forEach(System.out::println);
		
		
	}

}
