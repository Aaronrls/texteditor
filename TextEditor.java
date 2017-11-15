package textEditor;

import java.awt.*;
import java.awt.event.*;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Stack;

import javax.swing.*;  
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;




public class TextEditor extends JFrame
{
	private JTextArea textArea= new JTextArea(20,60);
	
	private JFileChooser fc= new JFileChooser();
	JFrame frame= new JFrame("Text Editor");
	private Stack<String> textHistory= new Stack<String>();
	private Stack <String> undoHistory= new Stack<String>();
	
	
	
		/*
		 * Constructor for Text Editor Frame
		 */
		public TextEditor()
		{
			
		JScrollPane scrollPane= new JScrollPane(textArea, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		
		
		//Adds a filter for text files
		FileFilter txtFilter= new FileNameExtensionFilter("plain text", "txt");
		fc.setFileFilter(txtFilter);
		ActionListener exitListener= new ExitListener();
		ActionListener saveListener= new SaveListener();
		ActionListener openListener= new SaveListener();
		
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
		
		JToolBar toolBar= new JToolBar("ToolBar", JToolBar.HORIZONTAL);
		
		JButton cutButton= new JButton ("Cut");
		JButton copyButton= new JButton ("Copy");
		JButton pasteButton= new JButton ("Paste");
		JButton undoButton= new JButton ("Undo");
		JButton redoButton= new JButton ("Redo");
		
		toolBar.add(cutButton);
		toolBar.add(copyButton);
		toolBar.add(pasteButton);
		toolBar.add(undoButton);
		toolBar.add(redoButton);
		add(toolBar, BorderLayout.NORTH);
		
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		pack();
		setLocationRelativeTo(null);
		setVisible(true);
		
		exit.addActionListener(exitListener);
		save.addActionListener(saveListener);
		open.addActionListener(openListener);
		
		String currentText=textArea.getText();
		currentText="";	
		 
		
		}
		
		
		private class ExitListener implements ActionListener
	 	{
			public void actionPerformed(ActionEvent e)
			{
				System.exit(0);
			}
		}
		
		private class SaveListener implements ActionListener
		{
			public void actionPerformed(ActionEvent e)
			{
				saveFile();
				
			}
		}
		
		private class OpenListener implements ActionListener
		{
			public void actionPerformed(ActionEvent e)
			{
				if(fc.showSaveDialog(null)==JFileChooser.APPROVE_OPTION){
					openFile(fc.getSelectedFile().getAbsolutePath());
				}
			}
		}
		
		public void saveFile()
		{
		if(fc.showSaveDialog(null)==JFileChooser.APPROVE_OPTION){
			FileWriter fw= null;
			
			try{
				fw=new FileWriter(fc.getSelectedFile().getAbsolutePath());
				textArea.write(fw);
				fw.close();
			}
			catch(IOException e){
				e.printStackTrace();
			}
		
			}
		}
		
		public void openFile(String filename)
		{
			FileReader fr= null;
			try{
				fr=new FileReader(filename);
				textArea.read(fr, null);
				fr.close();
				setTitle(filename);
			}
			catch(IOException e){
				e.printStackTrace();
			}
			
			
		}
	
	public static void main (String[] arg) 
	{
		JToolBar t= new JToolBar();
		t.setVisible(true);
		
		new TextEditor();
	}

}
