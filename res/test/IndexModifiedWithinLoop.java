
public class IndexModifiedWithinLoop {
	
	private int indexA;
	private int[] someArray = new int[10];

	public void method() {
		for (int i = 0; i < 100; i++) {
			System.out.println("index=" + (i + 5));
		}
		
		for (int j = 0; j < 100; j++) {
			System.out.println("item=" + this.someArray[j + 1]);
		}
		
		while (this.indexA > 0 && this.indexA != 5) {
			this.indexA += 1;
			System.out.println("item=" + this.someArray[this.indexA]);
		}
		
		do {
			int item = this.someArray[this.indexA];
			System.out.println("item=" + item);
			this.indexA += 1;
		} while (this.indexA > 0);
		
	}

}
