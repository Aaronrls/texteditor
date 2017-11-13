package textEditor;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;




public class TextEditor extends JFrame
{
	private JTextArea textArea= new JTextArea(20,60);
	private JFileChooser fc= new JFileChooser();
	
		/*
		 * Constructor for Text Editor Frame
		 */
		public TextEditor()
		{
			
		JScrollPane scrollPane= new JScrollPane(textArea, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		
		//Adds a filter for text files
		FileFilter txtFilter= new FileNameExtensionFilter("plain text", "txt");
		fc.setFileFilter(txtFilter);
		
		//Adds a menu bar and menu items
		add(scrollPane);
		JMenuBar menuBar= new JMenuBar();
		setJMenuBar(menuBar);
		JMenu file= new JMenu("File");
		menuBar.add(file);
		
		//Adds items to File menu
		JMenuItem open= new JMenuItem("Open");
		JMenuItem save= new JMenuItem("SAve");
		JMenuItem exit= new JMenuItem("Exit");
		
		file.add(open);
		file.add(save);
		file.addSeparator();
		file.add(exit);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		pack();
		setLocationRelativeTo(null);
		setVisible(true);
		

		
		
		
		
		
		
		}
	
	
	public static void main (String[] arg) 
	{
		new TextEditor();
	}

}
