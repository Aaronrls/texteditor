package texteditor;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;

@SuppressWarnings("serial")
public class Frame extends JFrame {

	private JMenu editMenu, fileMenu;
	private JScrollPane jScrollPane;
	private JPopupMenu.Separator jSeparator1;
	private JMenuBar menuBar;
	private JMenuItem newButton, openButton, quitButton, redoButton, saveButton, undoButton,wordCountButton;
	private JTextField status;
	static JTextArea textArea;

	public Frame() {
		initComponents();
	}

	private void initComponents() {

		jScrollPane = new JScrollPane();
		textArea = new JTextArea();
		status = new JTextField();
		menuBar = new JMenuBar();
		fileMenu = new JMenu();
		saveButton = new JMenuItem();
		newButton = new JMenuItem();
		openButton = new JMenuItem();
		jSeparator1 = new JPopupMenu.Separator();
		quitButton = new JMenuItem();
		editMenu = new JMenu();
		undoButton = new JMenuItem();
		redoButton = new JMenuItem();
		wordCountButton = new JMenuItem();
		
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

		textArea.setColumns(20);
		textArea.setLineWrap(true);
		textArea.setRows(5);
		textArea.setWrapStyleWord(true);
		jScrollPane.setViewportView(textArea);

		status.setEditable(false);
		status.setToolTipText("");

		fileMenu.setText("File");

		saveButton.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK));
		saveButton.setText("Save");
		saveButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				saveButtonActionPerformed(evt);
			}
		});
		fileMenu.add(saveButton);

		newButton.setText("New File");
		newButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				newButtonActionPerformed(evt);
			}
		});
		fileMenu.add(newButton);

		openButton.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_MASK));
		openButton.setText("Open");
		openButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				openButtonActionPerformed(evt);
			}
		});
		fileMenu.add(openButton);
		fileMenu.add(jSeparator1);

		quitButton.setText("Exit");
		quitButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				quitButtonActionPerformed(evt);
			}
		});
		wordCountButton.setText("Word Count");
		wordCountButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				wordCountButtonActionPerformed(evt);
			}
});
		fileMenu.add(quitButton);

		menuBar.add(fileMenu);

		editMenu.setText("Edit");

		undoButton.setText("Undo");
		undoButton.setToolTipText("");
		editMenu.add(undoButton);

		redoButton.setText("Redo");
		editMenu.add(redoButton);

		
		menuBar.add(wordCountButton);
		
		
		menuBar.add(editMenu);

		setJMenuBar(menuBar);

		GroupLayout layout = new GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addComponent(jScrollPane, GroupLayout.DEFAULT_SIZE, 558, Short.MAX_VALUE)
				.addGroup(GroupLayout.Alignment.TRAILING,
						layout.createSequentialGroup().addContainerGap().addComponent(status).addContainerGap()));
		layout.setVerticalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(layout
				.createSequentialGroup()
				.addComponent(jScrollPane, GroupLayout.PREFERRED_SIZE, 273, GroupLayout.PREFERRED_SIZE)
				.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
				.addComponent(status, GroupLayout.PREFERRED_SIZE, 20, GroupLayout.PREFERRED_SIZE).addContainerGap()));

		pack();
	}// </editor-fold>//GEN-END:initComponents

	private void quitButtonActionPerformed(ActionEvent evt) {// GEN-FIRST:event_quitButtonActionPerformed
		// exit program
		System.exit(0);
	}// GEN-LAST:event_quitButtonActionPerformed

	private void openButtonActionPerformed(ActionEvent evt) {// GEN-FIRST:event_openButtonActionPerformed
		JFileChooser chooser = new JFileChooser();
		int chooserValue = chooser.showOpenDialog(this);
		if (chooserValue == JFileChooser.APPROVE_OPTION) {
			try {
				Scanner fileIn = new Scanner(chooser.getSelectedFile());
				// read one line at a time into area
				String buffer = "";
				while (fileIn.hasNext()) {
					buffer += fileIn.nextLine() + "\n";
				}
				textArea.setText(buffer);
				fileIn.close();
				status.setText("Loaded " + chooser.getSelectedFile().getAbsolutePath());
			} catch (FileNotFoundException ex) {
				JOptionPane.showMessageDialog(this, "File not found!");

			}
		}
	}// GEN-LAST:event_openButtonActionPerformed

	private void newButtonActionPerformed(ActionEvent evt) {// GEN-FIRST:event_newButtonActionPerformed
		// clear text area
		textArea.setText("");
		status.setText("New File");
	}// GEN-LAST:event_newButtonActionPerformed

	private void saveButtonActionPerformed(ActionEvent evt) {// GEN-FIRST:event_saveButtonActionPerformed
		// open file chooser dialog
		JFileChooser chooser = new JFileChooser();
		int chooserValue = chooser.showSaveDialog(this);
		if (chooserValue == JFileChooser.APPROVE_OPTION) {
			try {
				// write contents of text area to file
				PrintWriter fileOut = new PrintWriter(chooser.getSelectedFile());
				fileOut.print(textArea.getText());
				fileOut.close();
				status.setText("Saved " + chooser.getSelectedFile().getAbsolutePath());
			} catch (FileNotFoundException ex) {
				Logger.getLogger(Frame.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
	}
	private void wordCountButtonActionPerformed(ActionEvent evt) {
		String doc = textArea.getText();
		int words = countWords(doc);
		status.setText("Words: " + words );
		
	}
	private static int countWords(String doc) {
		int index =0;
		boolean beginning = true;
		if(doc.equals("")) {
			return 0;
		}else {
			while(true) {
				try {
					if(doc.charAt(index)!=' ') {
						beginning = false;
						index++;
					}
					else if(!beginning)
							return 1+countWords(doc.substring(++index));
					else {
						return countWords(doc.substring(++index));
					}
				}catch(StringIndexOutOfBoundsException e) {
					if(!beginning) {
						return 1;
					}else {
						return 0;
					}
				}
			}
		}
		
}
	
	// GEN-LAST:event_saveButtonActionPerformed

	/**
	 * @param args
	 *            the command line arguments
	 */
	public static void main(String args[]) {
		/* Set the Nimbus look and feel */
		// <editor-fold defaultstate="collapsed" desc=" Look and feel setting
		// code (optional) ">
		/*
		 * If Nimbus (introduced in Java SE 6) is not available, stay with the
		 * default look and feel. For details see
		 * http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.
		 * html
		 */
		try {
			for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
				if ("Nimbus".equals(info.getName())) {
					UIManager.setLookAndFeel(info.getClassName());
					break;
				}
			}
		} catch (ClassNotFoundException ex) {
			Logger.getLogger(Frame.class.getName()).log(Level.SEVERE, null, ex);
		} catch (InstantiationException ex) {
			Logger.getLogger(Frame.class.getName()).log(Level.SEVERE, null, ex);
		} catch (IllegalAccessException ex) {
			Logger.getLogger(Frame.class.getName()).log(Level.SEVERE, null, ex);
		} catch (UnsupportedLookAndFeelException ex) {
			Logger.getLogger(Frame.class.getName()).log(Level.SEVERE, null, ex);
		}
		// </editor-fold>

		/* Create and display the form */
		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				new Frame().setVisible(true);
			}
		});
	}

}