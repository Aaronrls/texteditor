package texteditor;

import java.io.FileNotFoundException;
import java.sql.SQLException;

import javax.swing.JFrame;

public class Main {

	@SuppressWarnings("static-access")
	public static void main(String[] args)  throws FileNotFoundException, SQLException  {

		
		Connect con = new Connect();
		Frame frame = new Frame();
		WordList wl = new WordList();
		
		con.start();
		wl.getWords(con.setCon());
		wl.listsort();
		con.close();
		new WordComp(frame.textArea,WordList.wordss);
		new HighLite(frame.textArea,WordList.wordss);
		
		// exit on close
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		// center
		frame.setLocationRelativeTo(null);

		frame.setVisible(true);
		
	}

}
