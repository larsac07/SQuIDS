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
	public String country;
	
	public Person(String firstName, String[] middleNames, int birthYear, int birthMonth, int birthDate, String streetName, int streetNo, String zipCode, String city, String country) {
		this.firstName = firstName;
		this.middleNames = middleNames;
		this.birthYear = birthYear;
		this.birthMonth = birthMonth;
		this.birthDate = birthDate;
		this.streetName = streetName;
		this.streetNo = streetNo;
		this.zipCode = zipCode;
		this.city = city;
		this.country = country;
	}
	
	public Person getPerson10Params(String firstName, String[] middleNames, int birthYear, int birthMonth, int birthDate, String streetName, int streetNo, String zipCode, String city, String country) {
		return new Person(firstName, middleNames, birthYear, birthMonth, birthDate, streetName, streetNo, zipCode, city, country);
	}
	
	public Person getPerson8Params(String firstName, String[] middleNames, int birthYear, int birthMonth, int birthDate, String streetName, int streetNo, String zipCode) {
		return new Person(firstName, middleNames, birthYear, birthMonth, birthDate, streetName, streetNo, zipCode, null, null);
	}
	
	public static Person createPerson10Params(String firstName, String[] middleNames, int birthYear, int birthMonth, int birthDate, String streetName, int streetNo, String zipCode, String city, String country) {
		return new Person(firstName, middleNames, birthYear, birthMonth, birthDate, streetName, streetNo, zipCode, city, country);
	}
	
	public static Person createPerson8Params(String firstName, String[] middleNames, int birthYear, int birthMonth, int birthDate, String streetName, int streetNo, String zipCode) {
		return new Person(firstName, middleNames, birthYear, birthMonth, birthDate, streetName, streetNo, zipCode, null, null);
	}
	
	public static Person createPerson7Params(String firstName, String[] middleNames, int birthYear, int birthMonth, int birthDate, String streetName, int streetNo) {
		return new Person(firstName, middleNames, birthYear, birthMonth, birthDate, streetName, streetNo, null, null, null);
	}
	
	public static Person createPerson6Params(String firstName, String[] middleNames, int birthYear, int birthMonth, int birthDate, String streetName) {
		return new Person(firstName, middleNames, birthYear, birthMonth, birthDate, streetName, 0, null, null, null);
	}
	
	public Person getPersonFanOut12(String firstName, String[] middleNames, int birthYear, int birthMonth, int birthDate, String streetName, int streetNo, String zipCode, String city, String country) {
		this.firstName = firstName;
		this.middleNames = middleNames;
		this.birthYear = birthYear;
		this.birthMonth = birthMonth;
		int a = 0;
		a = 5;
		this.birthDate = birthDate;
		this.streetName = streetName;
		String b = "b";
		this.streetNo = streetNo;
		this.zipCode = zipCode;
		this.city = city;
		this.country = country;
		Person person = createPerson(firstName, middleNames, birthYear, birthMonth, birthDate, streetName, streetNo, zipCode, city, country);
		return person.getPerson10Params(firstName, middleNames, birthYear, birthMonth, birthDate, streetName, streetNo, zipCode, city, country);
		
	}
	
	public Person getPersonFanOut10(String firstName, String[] middleNames, int birthYear, int birthMonth, int birthDate, String streetName, int streetNo, String zipCode) {
		this.firstName = firstName;
		this.middleNames = middleNames;
		this.birthYear = birthYear;
		this.birthMonth = birthMonth;
		int a = 0;
		a = 5;
		this.birthDate = birthDate;
		this.streetName = streetName;
		String b = "b";
		this.streetNo = streetNo;
		this.zipCode = zipCode;
		Person person = createPerson(firstName, middleNames, birthYear, birthMonth, birthDate, streetName, streetNo, zipCode, city, country);
		return person.getPerson10Params(firstName, middleNames, birthYear, birthMonth, birthDate, streetName, streetNo, zipCode, city, country);
	}
	
	public static Person createPersonFanOut12(String firstName, String[] middleNames, int birthYear, int birthMonth, int birthDate, String streetName, int streetNo, String zipCode, String city, String country) {
		Person person = new Person();
		person.firstName = firstName;
		person.middleNames = middleNames;
		person.birthYear = birthYear;
		person.birthMonth = birthMonth;
		int a = 0;
		a = 5;
		person.birthDate = birthDate;
		person.streetName = streetName;
		String b = "b";
		person.streetNo = streetNo;
		person.zipCode = zipCode;
		person.city = city;
		person.country = country;
		person = createEmptyPerson();
		return person.getPerson10Params(firstName, middleNames, birthYear, birthMonth, birthDate, streetName, streetNo, zipCode, city, country);
	}
	
	public static Person createPersonFanOut11(String firstName, String[] middleNames, int birthYear, int birthMonth, int birthDate, String streetName, int streetNo, String zipCode, String city) {
		Person person = new Person();
		person.firstName = firstName;
		person.middleNames = middleNames;
		person.birthYear = birthYear;
		person.birthMonth = birthMonth;
		int a = 0;
		a = 5;
		person.birthDate = birthDate;
		person.streetName = streetName;
		String b = "b";
		person.streetNo = streetNo;
		person.zipCode = zipCode;
		person.city = city;
		person = createEmptyPerson();
		return person.getPerson10Params(firstName, middleNames, birthYear, birthMonth, birthDate, streetName, streetNo, zipCode, city, null);
	}
	
	public static Person createPersonFanOut10(String firstName, String[] middleNames, int birthYear, int birthMonth, int birthDate, String streetName, int streetNo, String zipCode) {
		Person person = new Person();
		person.firstName = firstName;
		person.middleNames = middleNames;
		person.birthYear = birthYear;
		person.birthMonth = birthMonth;
		int a = 0;
		a = 5;
		person.birthDate = birthDate;
		person.streetName = streetName;
		String b = "b";
		person.streetNo = streetNo;
		person.zipCode = zipCode;
		person = createEmptyPerson();
		return person.getPerson10Params(firstName, middleNames, birthYear, birthMonth, birthDate, streetName, streetNo, zipCode, null, null);
	}
	
	public static Person createPersonFanOut9(String firstName, String[] middleNames, int birthYear, int birthMonth, int birthDate, String streetName, int streetNo) {
		Person person = new Person();
		person.firstName = firstName;
		person.middleNames = middleNames;
		person.birthYear = birthYear;
		person.birthMonth = birthMonth;
		int a = 0;
		a = 5;
		person.birthDate = birthDate;
		person.streetName = streetName;
		String b = "b";
		person.streetNo = streetNo;
		person = createEmptyPerson();
		return person.getPerson10Params(firstName, middleNames, birthYear, birthMonth, birthDate, streetName, streetNo, null, null, null);
	}
	
	public static createEmptyPerson() {
		return new Person(null, new String[10], 0, 0, 0, null, 0, 0, 0, 0);
	}

	public void setStreetName(String streetName) {
		this.streetName = streetName;
	}
	public void setStreetNo(String streetNo) {
		this.streetNo = streetNo;
	}
	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	
}
