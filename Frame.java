package texteditor;

import java.awt.Color;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.*;
import java.sql.SQLException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.undo.UndoManager;

@SuppressWarnings("serial")
public class Frame extends JFrame implements WindowListener {

	@SuppressWarnings("rawtypes")
	private JComboBox fonts;
	private String[] names;
	@SuppressWarnings("unused")
	private String current;

	private WordList wl;
	private Connect con;
	private JMenu editMenu, fileMenu;
	private JScrollPane jScrollPane;
	private JPopupMenu.Separator jSeparator1;
	private JMenuBar menuBar;
	@SuppressWarnings("unused")
	private JMenuItem newButton, openButton, quitButton, redoButton, saveButton, undoButton, wordCountButton;
	private JTextField status;
	static JTextArea textArea;
	private UndoManager undoManager;
	private UndoAction undoAction;
	private RedoAction redoAction;

	public Frame() throws FileNotFoundException {
		initComponents();
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void initComponents() throws FileNotFoundException {
		undoManager = new UndoManager();
		undoAction = new UndoAction();
		redoAction = new RedoAction();
		textArea = new JTextArea();

		UndoManager editManager = new UndoManager();
		//add undo listener to main text area
		textArea.getDocument().addUndoableEditListener(new UndoableEditListener() {
			public void undoableEditHappened(UndoableEditEvent e) {
				editManager.addEdit(e.getEdit());
			}
		});

		con = new Connect();
		wl = new WordList();
		jScrollPane = new JScrollPane();

		textArea.getDocument().addUndoableEditListener(new UndoListener());

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

		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		addWindowListener(this);

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
		
		//control + o
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

		names = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
		fonts = new JComboBox(names);
		fonts.setBackground(Color.WHITE);

		fonts.addItemListener(new ItemListener() {

			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					textArea.setFont(new Font((String) fonts.getSelectedItem(), Font.ITALIC, 16));
				}
			}
		});
		fileMenu.add(quitButton);

		menuBar.add(fileMenu);
		menuBar.add(fonts);
		editMenu.setText("Edit");

		menuBar.add(wordCountButton);

		menuBar.add(editMenu);
		editMenu.setText("Edit");
		editMenu.add(undoAction);
		editMenu.add(redoAction);
		wordCountButton.setText("Word Count");
		menuBar.add(wordCountButton);

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
	}

	private void quitButtonActionPerformed(ActionEvent evt) {
		// exit program
		System.exit(0);
	}

	private void openButtonActionPerformed(ActionEvent evt) {
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
	}

	private void newButtonActionPerformed(ActionEvent evt) {
		// clear text area
		textArea.setText("");
		status.setText("New File");
	}

	private void saveButtonActionPerformed(ActionEvent evt) {
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
		status.setText("Words: " + words);

	}
	

	private static int countWords(String doc) {
		int index = 0;
		boolean beginning = true; //check if beginning of line
		if (doc.equals("")) {
			return 0;
		} else {
			while (true) {
				try {
					if (doc.charAt(index) != ' ') {
						//if the character is not empty, you're not at beginning
						beginning = false;
						index++;
					} else if (!beginning)
						return 1 + countWords(doc.substring(++index));
					else {
						return countWords(doc.substring(++index));
					}
				} catch (StringIndexOutOfBoundsException e) {
					if (!beginning) {
						return 1;
					} else {
						return 0;
					}
				}
			}
		}

	}

	class UndoListener implements UndoableEditListener {
		public void undoableEditHappened(UndoableEditEvent e) {
			undoManager.addEdit(e.getEdit()); //add undoable edit to edit list
			undoAction.update();
			redoAction.update();
		}
	}

	class UndoAction extends AbstractAction {
		public UndoAction() {
			this.putValue(Action.NAME, undoManager.getUndoPresentationName());
			this.setEnabled(false); //undo action initally disabled
		}

		public void actionPerformed(ActionEvent e) {
			if (this.isEnabled()) {
				undoManager.undo();
				undoAction.update();
				redoAction.update();
			}
		}

		public void update() {
			this.putValue(Action.NAME, undoManager.getUndoPresentationName());
			this.setEnabled(undoManager.canUndo());
		}
	}

	class RedoAction extends AbstractAction {
		public RedoAction() {
			this.putValue(Action.NAME, undoManager.getRedoPresentationName());
			this.setEnabled(false); //redo button initailly disabled
		}

		public void actionPerformed(ActionEvent e) {
			if (this.isEnabled()) {
				undoManager.redo();
				undoAction.update();
				redoAction.update();
			}
		}

		public void update() {
			this.putValue(Action.NAME, undoManager.getRedoPresentationName());
			this.setEnabled(undoManager.canRedo());
		}
	}

	public static void main(String args[]) {

		// try {
		// for (UIManager.LookAndFeelInfo info :
		// UIManager.getInstalledLookAndFeels()) {
		// if ("Nimbus".equals(info.getName())) {
		// UIManager.setLookAndFeel(info.getClassName());
		// break;
		// }
		// }
		// } catch (ClassNotFoundException ex) {
		// Logger.getLogger(Frame.class.getName()).log(Level.SEVERE, null, ex);
		// } catch (InstantiationException ex) {
		// Logger.getLogger(Frame.class.getName()).log(Level.SEVERE, null, ex);
		// } catch (IllegalAccessException ex) {
		// Logger.getLogger(Frame.class.getName()).log(Level.SEVERE, null, ex);
		// } catch (UnsupportedLookAndFeelException ex) {
		// Logger.getLogger(Frame.class.getName()).log(Level.SEVERE, null, ex);
		// }
		//

		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					new Frame().setVisible(true);
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}

	@Override
	public void windowActivated(WindowEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowClosed(WindowEvent e) {

		con.start();
		try {
			wl.insertToDataBase(con.setCon());
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		con.close();

		System.out.println("closed program");
		System.exit(0);

	}

	@Override
	public void windowClosing(WindowEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowDeactivated(WindowEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowDeiconified(WindowEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowIconified(WindowEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowOpened(WindowEvent e) {
		// TODO Auto-generated method stub

	}

}