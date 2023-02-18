package shellyEM.lambda;

public class LambdaExample {

	public static void main(String[] args) {
		
		RunnableClass r = new RunnableClass();
		Thread t1 = new Thread(r);
		t1.start();
		
		
		Thread t2 = new Thread(new Runnable() {
			@Override
			public void run() {
				System.out.println("2");
			}
		});
		t2.start();

		Thread t3 = new Thread(() -> System.out.println(3));
		t3.start();

		MyFunctionalInterface m = () -> System.out.println(4);
		m.myMethod();
		
		onTheFly(()-> System.out.println(5));
		
	}
	
	public static void onTheFly(MyFunctionalInterface m) {
		m.myMethod();
	}
	
}
