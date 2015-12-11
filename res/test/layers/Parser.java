package no.uib.lca092.rtms.io;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

public class Parser {

	private File inputTSV;
	private File outputTXT;
	private List<String> columnsToSkip;
	private String[] headers;
	private List<String[]> rows;
	private List<String> rowHTMLList;

	/**
	 * Initializes a Parser with an input, output and filter
	 *
	 * @param inputTSV
	 *            the .tsv-file to parse
	 * @param outputTXT
	 *            the output file. Can be any file-ending.
	 * @param skipColumns
	 *            a list of column names to skip
	 */
	public Parser(File inputTSV, File outputTXT, String... columnsToSkip) {
		this.inputTSV = inputTSV;
		this.outputTXT = outputTXT;
		this.columnsToSkip = new ArrayList<String>();
		this.rowHTMLList = new ArrayList<String>();
		for (String columnToSkip : columnsToSkip) {
			this.columnsToSkip.add(columnToSkip);
		}
	}

	public Parser() {
		this.columnsToSkip = new ArrayList<String>();
		this.rowHTMLList = new ArrayList<String>();
	}

	// public static void main(String[] args) {
	// Parser parser = new Parser(new File("res/retting.tsv"), new
	// File("res/comments.txt"), "Rettet av");
	// try {
	// parser.parse();
	// } catch (IOException e) {
	// System.err.println("Something went wrong when trying to read from file  \""
	// + parser.getInputTSV() + "\"");
	// e.printStackTrace();
	// }
	//
	// try {
	// parser.write();
	// } catch (IOException e) {
	// System.err.println("Something went wrong when trying to write to file  \""
	// + parser.getOutputTXT() + "\"");
	// e.printStackTrace();
	// }
	// }

	/**
	 * Parses the .tsv-file into the String[] headers and List<String[]> rows
	 *
	 * @throws IOException
	 */
	public void parse() throws IOException {
		System.out.println("Trying to read from file \"" + this.inputTSV + "\"");
		// .tsv to a buffered reader
		FileReader fileReader = new FileReader(this.inputTSV);
		BufferedReader reader = new BufferedReader(fileReader);

		// Get the row headers (the first row)
		this.headers = reader.readLine().split("\t");

		this.rows = new ArrayList<>(); // This will be the list of
		// data rows

		// A complete row not yet split into cells
		String unsplitRow;

		// Assign a row to unsplitRow, and continue if it is not null
		while ((unsplitRow = reader.readLine()) != null) {
			// Split the row by tab into String[] and add the array to the list
			// of rows
			String[] row = unsplitRow.split("\t");
			this.rows.add(row);
		}

		reader.close();
		System.out.println("Successfully read from file \"" + this.inputTSV + "\"");
	}

	/**
	 * Writes the lists of rows and corresponding headers to a string, and adds
	 * said string to the rowHTMLList
	 */
	public void toRowHTML() {
		String nl = System.lineSeparator();

		// For each row:
		for (String[] row : this.rows) {
			String rowHTML = "";
			// For each cell with a header:
			for (int i = 0; i < this.headers.length; i++) {
				if (skipColumn(this.headers[i])) {
					continue;
				}

				// <h1>Header</h1>
				rowHTML += writeHeading(this.headers, i);

				// <p>Paragraph</p>
				rowHTML += writeParahraph(row, i);

				System.out.println();
				rowHTML += nl;
			}
			String rowEnd = ""
					+ "####################################################################################################################";
			System.out.println(rowEnd);
			System.out.println();
			rowHTML += nl;
			this.rowHTMLList.add(rowHTML);
		}
	}

	/**
	 * Writes the lists of rows and corresponding headers to set file
	 *
	 * @param headers
	 * @param rows
	 * @throws IOException
	 */
	public void write() throws IOException {
		String nl = System.lineSeparator();
		System.out.println("Trying to write to file \"" + this.outputTXT + "\"");
		System.out.println();
		FileOutputStream fileOut = new FileOutputStream(this.outputTXT);
		OutputStreamWriter outputStream = new OutputStreamWriter(fileOut, "utf-8");
		Writer writer = new BufferedWriter(outputStream);

		// For each row:
		for (String row : this.rowHTMLList) {
			writer.write(row);

			String rowEnd = ""
					+ "####################################################################################################################";
			writer.write(rowEnd + nl + nl);
		}
		writer.close();
		System.out.println("Successfully wrote to file \"" + this.outputTXT + "\"");
	}

	/**
	 * Writes a column header as a heading.
	 *
	 * @param writer
	 * @param headerString
	 * @throws IOException
	 */
	@SuppressWarnings("unused")
	private void writeHeading(Writer writer, String[] headers, int cellIndex) throws IOException {
		String headerStringTagged = "<h1>" + headers[cellIndex] + "</h1>";
		System.out.println(headerStringTagged);
		writer.write(headerStringTagged + "\n");
	}

	/**
	 * Formats a header to an HTML heading
	 *
	 * @param headers
	 * @param cellIndex
	 * @return HTML heading
	 */
	private String writeHeading(String[] headers, int cellIndex) {
		String headerStringTagged = "<h1>" + headers[cellIndex] + "</h1>";
		System.out.println(headerStringTagged);
		return headerStringTagged + System.lineSeparator();
	}

	/**
	 * Writes a cell as a paragraph. If there is no data, an empty paragraph is
	 * written.
	 *
	 * @param writer
	 * @param cellString
	 * @throws IOException
	 */
	@SuppressWarnings("unused")
	private void writeParahraph(Writer writer, String[] row, int cellIndex) throws IOException {
		try {
			String cellStringTagged = "<p>" + row[cellIndex] + "</p>";
			System.out.println(cellStringTagged);
			writer.write(cellStringTagged + "\n");
		} catch (ArrayIndexOutOfBoundsException e) {
			// If there is no data, print out an empty paragraph for the cell
			String emptyParagraph = "<p></p>";
			System.out.println(emptyParagraph);
			writer.write(emptyParagraph + "\n");
		}
	}

	/**
	 * Formats a cell as a paragraph. If there is no data, an empty paragraph is
	 * returned
	 *
	 * @param row
	 * @param cellIndex
	 * @return HTML paragraph
	 */
	private String writeParahraph(String[] row, int cellIndex) {
		try {
			String cellStringTagged = "<p>" + row[cellIndex] + "</p>";
			System.out.println(cellStringTagged);
			return cellStringTagged + System.lineSeparator();
		} catch (ArrayIndexOutOfBoundsException e) {
			// If there is no data, print out an empty paragraph for the cell
			String emptyParagraph = "<p></p>";
			System.out.println(emptyParagraph);
			return emptyParagraph + System.lineSeparator();
		}
	}

	/**
	 * Checks the list of columns to skip to see if a column should be skipped
	 * or not.
	 *
	 * @param columName
	 * @return true if column should be skipped; false if not.
	 */
	private boolean skipColumn(String columName) {
		for (String columnToSkip : this.columnsToSkip) {
			if (columnToSkip.equals(columName)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Gets input .tsv-file.
	 *
	 * @return input .tsv-file
	 */
	public File getInputTSV() {
		return this.inputTSV;
	}

	/**
	 * Sets input .tsv-file.
	 *
	 * @param inputTSV
	 *            input .tsv-file
	 */
	public void setInputTSV(File inputTSV) {
		this.inputTSV = inputTSV;
	}

	/**
	 * Gets output file.
	 *
	 * @return output file
	 */
	public File getOutputTXT() {
		return this.outputTXT;
	}

	/**
	 * Sets output file.
	 *
	 * @param outputTXT
	 *            output file
	 */
	public void setOutputTXT(File outputTXT) {
		this.outputTXT = outputTXT;
	}

	/**
	 * Gets columns to skip.
	 *
	 * @return columns to skip
	 */
	public List<String> getColumnsToSkip() {
		return this.columnsToSkip;
	}

	/**
	 * Sets columns to skip.
	 *
	 * @param columnsToSkip
	 */
	public void setColumnsToSkip(List<String> columnsToSkip) {
		this.columnsToSkip = columnsToSkip;
	}

	/**
	 * Gets headers.
	 *
	 * @return headers
	 */
	public String[] getHeaders() {
		return this.headers;
	}

	/**
	 * Sets headers.
	 *
	 * @param headers
	 */
	public void setHeaders(String[] headers) {
		this.headers = headers;
	}

	/**
	 * Gets rows.
	 *
	 * @return rows
	 */
	public List<String[]> getRows() {
		return this.rows;
	}

	/**
	 * Sets rows.
	 *
	 * @param rows
	 */
	public void setRows(List<String[]> rows) {
		this.rows = rows;
	}

	public List<String> getRowHTMLList() {
		return this.rowHTMLList;
	}

	public void setRowHTMLList(List<String> rowHTMLList) {
		this.rowHTMLList = rowHTMLList;
	}
}
