
public class ContinuesAndBreaks {
	public void continueOutsideSwitch() {
		String searchMe = "peter piper picked a " + "peck of pickled peppers";
		int max = searchMe.length();
		int numPs = 0;
		
		for (int i = 0; i < max; i++) {
			// interested only in p's
			if (searchMe.charAt(i) != 'p')
				continue;
			
			// process p's
			numPs++;
		}
		System.out.println("Found " + numPs + " p's in the string.");
	}
	
	public void breakOutsideSwitch() {
		int[] arrayOfInts = 
			{ 32, 87, 3, 589,
					12, 1076, 2000,
					8, 622, 127 };
		int searchfor = 12;
		
		int i;
		boolean foundIt = false;
		
		for (i = 0; i < arrayOfInts.length; i++) {
			if (arrayOfInts[i] == searchfor) {
				foundIt = true;
				break;
			}
		}
		
		if (foundIt) {
			System.out.println("Found " + searchfor + " at index " + i);
		} else {
			System.out.println(searchfor + " not in the array");
		}
	}
	
	public void continueInsideSwitch() {
		loop: for (int i=0; i<10; i++) {
			switch (i) {
				case 1:
				case 3:
				case 5:
				case 7:
				case 9:
					continue loop;
				default:
					continue;
			}
			
			System.out.println(i);
		}
	}
	
	public void breakInsideSwitch() {
		int month = 8;
		String monthString;
		switch (month) {
			case 1:  monthString = "January";
			break;
			case 2:  monthString = "February";
			break;
			case 3:  monthString = "March";
			break;
			case 4:  monthString = "April";
			break;
			case 5:  monthString = "May";
			break;
			case 6:  monthString = "June";
			break;
			case 7:  monthString = "July";
			break;
			case 8:  monthString = "August";
			break;
			case 9:  monthString = "September";
			break;
			case 10: monthString = "October";
			break;
			case 11: monthString = "November";
			break;
			case 12: monthString = "December";
			break;
			default: monthString = "Invalid month";
			break;
		}
		System.out.println(monthString);
	}
}
