import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class CyclomaticComplexity {
	public CyclomaticComplexity() {
		int a = 4 > 5 ? 0 : 7;
		if (a > 0 || a < -2) {
			break;
		}
		for (int i = 0; i < 10; i++) {
			break;			
		}
		for (String string : new String[]{"asfd", "sdfg"}) {
			break;
		}
		while (a > 0) {
			a--;
			break;
		}
		do {
			a++;
		} while (a < 2);
		switch (a) {
			case 0:
				break;
			default:
				break;
		}
	}
	
	public void cc9() {
		int a = 4 > 5 ? 0 : 7;
		if (a > 0 || a < -2) {
			break;
		}
		for (int i = 0; i < 10; i++) {
			break;			
		}
		for (String string : new String[]{"asfd", "sdfg"}) {
			break;
		}
		while (a > 0) {
			a--;
			break;
		}
		do {
			a++;
		} while (a < 2);
	}

	public void cc10() {
		int a = 4 > 5 ? 0 : 7;
		if (a > 0 || a < -2) {
			break;
		}
		for (int i = 0; i < 10; i++) {
			break;			
		}
		for (String string : new String[]{"asfd", "sdfg"}) {
			break;
		}
		while (a > 0) {
			a--;
			break;
		}
		do {
			a++;
		} while (a < 2);
		switch (a) {
			case 0:
				break;
			default:
				break;
		}
	}

	public void cc11() {
		int a = 4 > 5 ? 0 : 7;
		if (a > 0 || a < -2) {
			break;
		}
		for (int i = 0; i < 10; i++) {
			break;			
		}
		for (String string : new String[]{"asfd", "sdfg"}) {
			break;
		}
		while (a > 0) {
			a--;
			break;
		}
		do {
			a++;
		} while (a < 2);
		switch (a) {
			case 0:
				break;
			case 1:
				break;
			default:
				break;
		}
	}
	
	public void allCases() { // entry point: 1
		int a = 4 > 5 ? 0 : 7; // ternary: 2
		if (a > 0 || a < -2) { // if and ||-operator: 4
			break;
		}
		for (int i = 0; i < 10; i++) { // for loop: 5
			break;			
		}
		for (String string : new String[]{"asfd", "sdfg"}) { // foreach loop: 6
			break;
		}
		while (a > 0) { // while loop: 7
			a--;
			break;
		}
		do { // do while loop: 8
			a++;
		} while (a < 2 && a < 10); // &&-operator: 9
		switch (a) {
			case 0: // case: 10
				break;
			default: // default: 11
				break;
		}
		File f = new File("asdf.txt");
		FileWriter fw;
		try {
			fw = new FileWriter(f);
			fw.write("adfg");
		} catch (IOException e) { // catch block: 12
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			
		}
	}
}
