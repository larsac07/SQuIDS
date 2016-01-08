
public class Supervisor {
	private String name;
	public int id;
	private Person person;
	public static int count;
	
	public Supervisor(String name, int id, Person person) {
		this.name = name;
		this.id = id;
		this.person = person;
	}
	
	public void changePersonAddress(Person person, String streetName, int streetNo, String zipCode, String city, String country) {
		person.streetName = streetName;
		person.setStreetNo(streetNo);
		person.zipCode = zipCode;
		person.setCity(city);
		person.country = country;
		this.name = person.firstName;
		Supervisor.count++;
	}
	
	public void changePersonCountry(Person person, String country) {
		person.setCountry(country);
	}
	
	public void changePersonAddress2(Person person, String streetName, int streetNo, String zipCode, String city, String country) {
		person.setStreetName(streetName);
		person.setStreetNo(streetNo);
		person.setZipCode(zipCode);
		person.country = country;
		person.setCity(city);
	}
	
	public static void changePersonAddress2(Person person, String streetName, int streetNo, String zipCode, String city, String country) {
		person.streetName = streetName;
		person.setStreetNo(streetNo);
		person.zipCode = zipCode;
		person.setCity(city);
		person.country = country;
	}
	
	public static void changePersonCountry2(Person person, String country) {
		person.setCountry(country);
	}
	
	public static void changePersonAddress2Point2(Person person, String streetName, int streetNo, String zipCode, String city, String country) {
		person.setStreetName(streetName);
		person.setStreetNo(streetNo);
		person.setZipCode(zipCode);
		person.country = country;
		person.setCity(city);
	}
}
