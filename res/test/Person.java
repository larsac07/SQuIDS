public class Person {

	public String firstName;
	public String[] middleNames;
	public int birthYear;
	public int birthMonth;
	public int birthDate;
	public String streetName;
	public int streetNo;
	public String zipCode;
	public String city;
	public String region;
	public String country;
	
	public Person(String firstName, String[] middleNames, int birthYear, int birthMonth, int birthDate, String streetName, int streetNo, String zipCode, String city, String region, String country) {
		this.firstName = firstName;
		this.middleNames = middleNames;
		this.birthYear = birthYear;
		this.birthMonth = birthMonth;
		this.birthDate = birthDate;
		this.streetName = streetName;
		this.streetNo = streetNo;
		this.zipCode = zipCode;
		this.city = city;
		this.region = region;
		this.country = country;
	}
	
	public Person getPerson10Params(String firstName, String[] middleNames, int birthYear, int birthMonth, int birthDate, String streetName, int streetNo, String zipCode, String city, String country) {
		return new Person(firstName, middleNames, birthYear, birthMonth, birthDate, streetName, streetNo, zipCode, city, country);
	}
	
	public Person getPerson8Params(String firstName, String[] middleNames, int birthYear, int birthMonth, int birthDate, String streetName, int streetNo, String zipCode) {
		return new Person(firstName, middleNames, birthYear, birthMonth, birthDate, streetName, streetNo, zipCode, city, country);
	}
	
	public static Person createPerson10Params(String firstName, String[] middleNames, int birthYear, int birthMonth, int birthDate, String streetName, int streetNo, String zipCode, String city, String country) {
		return new Person(firstName, middleNames, birthYear, birthMonth, birthDate, streetName, streetNo, zipCode, city, country);
	}
	
	public static Person createPerson8Params(String firstName, String[] middleNames, int birthYear, int birthMonth, int birthDate, String streetName, int streetNo, String zipCode) {
		return new Person(firstName, middleNames, birthYear, birthMonth, birthDate, streetName, streetNo, zipCode, city, country);
	}
	
	public static Person createPerson7Params(String firstName, String[] middleNames, int birthYear, int birthMonth, int birthDate, String streetName, int streetNo) {
		return new Person(firstName, middleNames, birthYear, birthMonth, birthDate, streetName, streetNo, null, null, null);
	}
	
	public static Person createPerson6Params(String firstName, String[] middleNames, int birthYear, int birthMonth, int birthDate, String streetName) {
		return new Person(firstName, middleNames, birthYear, birthMonth, birthDate, streetName, 0, null, null, null);
	}
}
