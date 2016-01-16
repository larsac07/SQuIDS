import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseException;
import com.github.javaparser.ast.CompilationUnit;

public class MethodsWithDataOrFileOperations {

	private String userName = "user";
	private String password = "password";
	private String dbms = "mysql";
	private String serverName = "localhost";
	private int portNumber = 3306;
	private File file1;
	private File file2;
	private File file3;
	private File file4;
	private File file5;
	private File file6;
	private File file7;
	
	public void method6DbOrIoCalls() {
		File file = new File(".");
		String fileString = "";
		CompilationUnit cu = null;
		try {
			List<String> strings = Files.readAllLines(file.toPath());
			for (String line : strings) {
				fileString += line;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			cu = JavaParser.parse(file);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Connection conn = null;
		Properties connectionProps = new Properties();
		connectionProps.put("user", this.userName);
		connectionProps.put("password", this.password);

		try {
			conn = DriverManager.getConnection(
					"jdbc:" + this.dbms + "://" + this.serverName + ":" + this.portNumber + "/", connectionProps);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Connected to database");
		
		PreparedStatement pstmt;

		try {
			conn.setAutoCommit(false);
			pstmt = conn.prepareStatement("UPDATE COFFEES " + "SET PRICE = ? " + "WHERE COF_NAME = ?");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void method7DbOrIoCalls() {
		File file = new File(".");
		String fileString = "";
		CompilationUnit cu = null;
		try {
			List<String> strings = Files.readAllLines(file.toPath());
			for (String line : strings) {
				fileString += line;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			cu = JavaParser.parse(file);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Connection conn = null;
		Properties connectionProps = new Properties();
		connectionProps.put("user", this.userName);
		connectionProps.put("password", this.password);

		try {
			conn = DriverManager.getConnection(
					"jdbc:" + this.dbms + "://" + this.serverName + ":" + this.portNumber + "/", connectionProps);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Connected to database");
		
		PreparedStatement pstmt;

		try {
			conn.setAutoCommit(false);
			pstmt = conn.prepareStatement("UPDATE COFFEES " + "SET PRICE = ? " + "WHERE COF_NAME = ?");
			pstmt.setString(1, fileString);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void method8DbOrIoCalls() {
		File file = new File(".");
		String fileString = "";
		CompilationUnit cu = null;
		try {
			List<String> strings = Files.readAllLines(file.toPath());
			for (String line : strings) {
				fileString += line;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			cu = JavaParser.parse(file);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Connection conn = null;
		Properties connectionProps = new Properties();
		connectionProps.put("user", this.userName);
		connectionProps.put("password", this.password);

		try {
			conn = DriverManager.getConnection(
					"jdbc:" + this.dbms + "://" + this.serverName + ":" + this.portNumber + "/", connectionProps);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Connected to database");
		
		PreparedStatement pstmt;

		try {
			conn.setAutoCommit(false);
			pstmt = conn.prepareStatement("UPDATE COFFEES " + "SET PRICE = ? " + "WHERE COF_NAME = ?");
			pstmt.setString(1, fileString);
			pstmt.setString(2, cu.toString());
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void javaLibrary() {
		this.file1 = new File(".");
		this.file2 = new File(".");
		this.file3 = new File(".");
		this.file4 = new File(".");
		this.file5 = new File(".");
		this.file6 = new File(".");
		this.file7 = new File(".");
		
		this.file1.toPath();
		this.file2.toPath();
		this.file3.toPath();
		this.file4.toPath();
		this.file5.toPath();
		this.file6.toPath();
		this.file7.toPath();
	}

	public void externalLibrary() {
		try {
			JavaParser.parse(this.file1);
			JavaParser.parse(this.file2);
			JavaParser.parse(this.file3);
			JavaParser.parse(this.file4);
			JavaParser.parse(this.file5);
			JavaParser.parse(this.file6);
			JavaParser.parse(this.file7);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
