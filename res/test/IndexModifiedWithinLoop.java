
public class IndexModifiedWithinLoop {
	
	private int indexA;

	public void method() {
		for (int i = 0; i < 100; i++) {
			System.out.println("index=" + i);
		}
		
		for (this.indexA = 0; this.indexA < 100; this.indexA++) {
			System.out.println("index=" + this.indexA);
			indexA++;
			--indexA;
		}
		
		while (this.indexA > 0 && this.indexA != 5) {
			indexA -= 2;
			indexA += 2;
			indexA = this.indexA - 2;
		}
		
		do {
			this.indexA -= 2;
			this.indexA += 2;
			this.indexA = this.indexA - 2;
		} while (this.indexA > 0);
		
	}

}
