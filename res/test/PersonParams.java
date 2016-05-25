public class Person {

	public String firstName;
	public String[] middleNames;
	public int birthYear;
	public int birthMonth;
	public int birthDate;
	public String streetName;
	public int streetNo;
	public String zipCode;
	
	// 8 params
	public Person(String firstName, String[] middleNames, int birthYear, int birthMonth, int birthDate, String streetName, int streetNo, String zipCode) {
		this.firstName = firstName;
		this.middleNames = middleNames;
		this.birthYear = birthYear;
		this.birthMonth = birthMonth;
		this.birthDate = birthDate;
		this.streetName = streetName;
		this.streetNo = streetNo;
		this.zipCode = zipCode;
	}
	
	// 7 params
	public Person(String firstName, String[] middleNames, int birthYear, int birthMonth, int birthDate, String streetName, int streetNo) {
		this.firstName = firstName;
		this.middleNames = middleNames;
		this.birthYear = birthYear;
		this.birthMonth = birthMonth;
		this.birthDate = birthDate;
		this.streetName = streetName;
		this.streetNo = streetNo;
	}
	
	// 6 params
	public Person(String firstName, String[] middleNames, int birthYear, int birthMonth, int birthDate, String streetName) {
		this.firstName = firstName;
		this.middleNames = middleNames;
		this.birthYear = birthYear;
		this.birthMonth = birthMonth;
		this.birthDate = birthDate;
		this.streetName = streetName;
	}
	
	public Person getPerson8Params(String firstName, String[] middleNames, int birthYear, int birthMonth, int birthDate, String streetName, int streetNo, String zipCode) {
		return new Person(firstName, middleNames, birthYear, birthMonth, birthDate, streetName, streetNo, zipCode);
	}
	
	public Person getPerson7Params(String firstName, String[] middleNames, int birthYear, int birthMonth, int birthDate, String streetName, int streetNo) {
		return new Person(firstName, middleNames, birthYear, birthMonth, birthDate, streetName, streetNo);
	}
	
	public Person getPerson6Params(String firstName, String[] middleNames, int birthYear, int birthMonth, int birthDate, String streetName) {
		return new Person(firstName, middleNames, birthYear, birthMonth, birthDate, streetName);
	}	
}
