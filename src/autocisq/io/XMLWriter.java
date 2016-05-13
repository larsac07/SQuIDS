package autocisq.io;

import java.io.File;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.eclipse.core.runtime.Path;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import autocisq.models.FileIssue;
import autocisq.models.Issue;

public class XMLWriter {

	private final static String EL_ITEMS = "items";
	private final static String EL_ITEM = "item";
	private final static String EL_METRIC = "metric";
	private final static String EL_FILE = "file";
	private final static String EL_LINE = "line";
	private final static String EL_DETAIL = "detail";
	private final static String UNKNOWN = "XXX";
	private final static String FOLDER = "/home/lars/Development/java_workspace/SQuIDS/compare/SQuIDS/";
	private final static String XML_FILE = "result.xml";
	private final static String SUCCESS = "File saved!";
	private final static String ERROR = "An error occured!";
	private final static String YES = "yes";
	private final static String INDENT_AMOUNT_KEY = "{http://xml.apache.org/xslt}indent-amount";
	private final static String INDENT_AMOUNT = 2 + "";

	public XMLWriter() {

	}

	public void writeIssues(String projectName, Map<File, List<Issue>> fileIssuesMap) {
		try {

			Document doc = createItems(fileIssuesMap);

			DOMSource source = new DOMSource(doc);
			File xmlFile = new File(FOLDER + projectName + Path.SEPARATOR + XML_FILE);
			if (!xmlFile.exists()) {
				xmlFile.getParentFile().mkdirs();
			}
			StreamResult result = new StreamResult(xmlFile);

			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, YES);
			transformer.setOutputProperty(INDENT_AMOUNT_KEY, INDENT_AMOUNT);
			transformer.transform(source, result);

			System.out.println(SUCCESS);
			System.out.println(new File(""));
		} catch (ParserConfigurationException pce) {
			System.err.println(ERROR);
			pce.printStackTrace();
		} catch (TransformerException tfe) {
			System.err.println(ERROR);
			tfe.printStackTrace();
		}
	}

	/**
	 * @param fileIssuesMap
	 * @return
	 * @throws ParserConfigurationException
	 */
	private Document createItems(Map<File, List<Issue>> fileIssuesMap) throws ParserConfigurationException {
		Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
		Element rootElement = doc.createElement(EL_ITEMS);
		doc.appendChild(rootElement);

		for (File fileKey : fileIssuesMap.keySet()) {
			List<Issue> issues = fileIssuesMap.get(fileKey);
			for (Issue issue : issues) {
				Element item = createItem(doc, fileKey, issue);
				rootElement.appendChild(item);
			}
		}
		return doc;
	}

	/**
	 * @param doc
	 * @param rootElement
	 * @param fileKey
	 * @param issue
	 */
	private Element createItem(Document doc, File fileKey, Issue issue) {
		Element item = doc.createElement(EL_ITEM);

		Element metric = doc.createElement(EL_METRIC);
		metric.appendChild(doc.createTextNode(notEmpty(issue.getMeasure().getClass().getSimpleName())));
		item.appendChild(metric);

		String filePathString;
		String lineString;
		if (issue instanceof FileIssue) {
			filePathString = fileKey.getPath();
			lineString = ((FileIssue) issue).getBeginLine() + "";
		} else {
			filePathString = UNKNOWN;
			lineString = UNKNOWN;
		}
		Element file = doc.createElement(EL_FILE);
		file.appendChild(doc.createTextNode(notEmpty(filePathString)));
		item.appendChild(file);

		Element line = doc.createElement(EL_LINE);
		line.appendChild(doc.createTextNode(notEmpty(lineString)));
		item.appendChild(line);

		Element detail = doc.createElement(EL_DETAIL);
		detail.appendChild(doc.createTextNode(notEmpty(issue.getMessage())));
		item.appendChild(detail);

		return item;
	}

	private String notEmpty(String string) {
		if (string == null || string.isEmpty()) {
			return UNKNOWN;
		} else {
			return string;
		}
	}

}
