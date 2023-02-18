package shellyEM.lambda;

public class LambdaSottoIlCofano {
	public static void main(String[] args) {
		
		MyFunctionalInterface m1 = new MyFunctionalInterface() {
			@Override
			public void myMethod() {
				System.out.println("IMPL 1");
			}
		};
	}

	MyFunctionalInterface m2 = new MyFunctionalInterface() {
		@Override
		public void myMethod() {
			System.out.println("IMPL 2");
		}
	};
		/*
	MyFunctionalInterface m3 = () -> System.out.println("IMPL 3");
	MyFunctionalInterface m4 = () -> System.out.println("IMPL 4");
*/
}
