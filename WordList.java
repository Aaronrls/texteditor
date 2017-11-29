package texteditor;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;

import java.io.*;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collections;
import java.util.Deque;
import java.util.LinkedList;

public class WordList {
	static LinkedList<String> wordss;
	private LinkedList<String> list;
	private Deque <String> insertList;

	private String inString;
	Statement stmnt2,stmnt3;
	ResultSet rset;
	ResultSetMetaData rsmd;
	int colnum;

	public WordList() throws FileNotFoundException {

		wordss = new LinkedList<String>();
		list = new LinkedList<String>();
		insertList = new LinkedList<String>();
	}
	public void insertToDataBase(Connection conn) throws SQLException{
	this.setInString(inString);
		stmnt3 = conn.createStatement();
		
		System.out.println(wordss.size());
		
		for (int i =0;i<wordss.size();i++){
			
		stmnt3.executeUpdate(  "INSERT INTO wordslist(word)  VALUES ( '"+wordss.poll()+ "' )");
		
		}
		
	}


	public void getWords(Connection conn) throws SQLException {
		stmnt2 = conn.createStatement();
		rset = stmnt2.executeQuery("SELECT word FROM wordslist WHERE LENGTH(word) >= 3 ");
		rsmd = rset.getMetaData();
		colnum = rsmd.getColumnCount();

		while (rset.next()) {
			for (int i = 1; i <= colnum; i++) {
				String colval = rset.getString(i);
				wordss.add(colval);

			}

		}

	}

	public void listsort() {

		Collections.sort(wordss);

		
	}

	public void insert(String in) throws SQLException {
		
		//make different data type
	
		wordss.add(in);
		insertList.addFirst(in);
		
	
	}
	
	public void addtext() {
		for (int i = 0; i < list.size(); i++) {
			wordss.add(list.get(i));

		}

	}
	public String getInString() {
		return inString;
	}
	public void setInString(String inString) {
		this.inString = inString;
	}
	

}