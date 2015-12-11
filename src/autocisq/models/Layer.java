package autocisq.models;

import java.util.LinkedList;
import java.util.List;

public class Layer {
	private String name;
	private List<String> classes;

	public Layer(String name, List<String> classes) {
		this.name = name;
		this.classes = classes;
	}

	public Layer(String name) {
		this.name = name;
		this.classes = new LinkedList<>();
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<String> getClasses() {
		return this.classes;
	}

	public void setClasses(List<String> classes) {
		this.classes = classes;
	}

	public void add(String classURI) {
		this.classes.add(classURI);
	}

}
