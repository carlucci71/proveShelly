package shellyEM.lambda;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class D {

	public static void main(String[] args) {
		Thread t = new Thread(new Runnable() {

			@Override
			public void run() {

				System.out.println("fa cose");
			}
			
		});
	t.start();
	
	Thread t2 = new Thread(()-> System.out.println("con lambda"));
	t2.start();
	Fx fx = new Fx();
	Thread t3 = new Thread(fx);
	fx.run();
	List<Mia> l = new ArrayList<>();
	l.add(new Mia(53));
	l.add(new Mia(13));
	l.add(new Mia(71));
	System.out.println(l);
	Collections.sort(l, (o1,o2) -> o1.getCampo().compareTo(o2.getCampo()));
	System.out.println(l);
	
	Map<String, String> map = new HashMap<>();
	map.put("1", "1");
	map.put("2", "2");

	List<Integer> collect = map.values().stream().map((el) -> Integer.parseInt(el)).collect(Collectors.toList());
	System.out.println(collect);
	
	map = new HashMap<>();
	map.put("a", "c");
	map.put("b", "d");
	
	System.out.println("************");

	List<String> titles = map.entrySet().stream()
			  .map(Map.Entry::getValue)
			  .collect(Collectors.toList());	
	System.out.println(titles);
	}
	
	static class Fx implements Runnable{
		public void run() {
			System.out.println("xxx");
		}
	}
	
	static class Mia {
		
		@Override
		public String toString() {
			return "campo: " + campo;
		}
		
		private Integer campo;

		Mia(Integer campo){
			this.campo=campo;
		}
		
		public Integer getCampo() {
			return campo;
		}

		public void setCampo(Integer campo) {
			this.campo = campo;
		}
		
	}
	
	
	
}
