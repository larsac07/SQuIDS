package squids.handlers;

import static org.junit.Assert.assertEquals;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

public class HandlerTest {

	private Handler handler;

	@Before
	public void setUp() throws Exception {
		this.handler = new Handler();
	}

	@Test
	public void testLinesToListOfSets() {
		String string = "some.package.id.Class1, 1\n" + "some.package.id.Class2, 2\n" + "some.package.id.Class3, 2\n"
				+ "some.package.id.Class4, 3";
		List<Set<String>> expected = new LinkedList<>();
		Set<String> layer1 = new HashSet<>();
		layer1.add("some.package.id.Class1");
		Set<String> layer2 = new HashSet<>();
		layer2.add("some.package.id.Class2");
		layer2.add("some.package.id.Class3");
		Set<String> layer3 = new HashSet<>();
		layer3.add("some.package.id.Class4");
		expected.add(layer1);
		expected.add(layer2);
		expected.add(layer3);
		List<Set<String>> actual = this.handler.linesToStringListOfSets(string, ",");
		assertEquals(expected, actual);
	}

}
