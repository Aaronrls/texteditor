

import javax.swing.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.BadLocationException;
import javax.swing.undo.*;



import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.font.TextAttribute;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.sql.SQLException;




@SuppressWarnings("serial")
public class Main extends JFrame implements DocumentListener, KeyListener {
	private Connex con;
	private WordTable wt;
	private LinkedList<String> wordss;
	 private UndoManager undoManager = new UndoManager();
	 private UndoAction undoAction = new UndoAction();
	  private RedoAction redoAction = new RedoAction();
	/////////////////////////

	private JMenu fileMenu;
	private JScrollPane jScrollPane;
	private JPopupMenu.Separator jSeparator1;
	private JMenuBar menuBar;
	private JMenuItem newButton;
	private JMenuItem openButton;
	private JMenuItem quitButton;
	private JMenu editMenu;
	private JMenuItem saveButton;
	private JMenuItem wordCountButton;
	private JTextField status;
	private JTextArea textArea;
	
	
	////////////////////////////////////////////////////////////////
	private static final String COMMIT_ACTION = "commit";

	private static enum Mode {
		INSERT, COMPLETION
	};

	// lets program know if it should suggest words or move the carot after
	// inserting a suggestion
	private Mode mode = Mode.INSERT;
	

	public Main() throws FileNotFoundException, SQLException  {
		super("Project");
		initComponents();

		textArea.getDocument().addDocumentListener(this);
		textArea.addKeyListener(this);
		InputMap im = textArea.getInputMap();
		ActionMap am = textArea.getActionMap();
		// this is where we take control of the enter button. so we can
		// quick insert the suggested completion

		im.put(KeyStroke.getKeyStroke("ENTER"), COMMIT_ACTION);
		am.put(COMMIT_ACTION, new CommitAction());
		
		///// our custom list
		wordss = new LinkedList<String>();
		//wordss = WordTable.wordss;

	}

	private void initComponents() throws FileNotFoundException, SQLException {

		//// connection
		con = new Connex();
		con.start();
		// words list updates from data base
		wt = new WordTable();
		wt.getWords(con.setCon());
		wt.listsort();
		con.close();
		////

		JMenuItem redoEdit = new JMenuItem("Redo");
		JMenuItem undoEdit = new JMenuItem("Undo");
		UndoManager editManager = new UndoManager();
		textArea = new JTextArea();
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		textArea.setColumns(20);
		textArea.setLineWrap(true);
		textArea.setRows(5);
		textArea.setWrapStyleWord(true);
		textArea.getDocument().addUndoableEditListener(new UndoableEditListener() {
			public void undoableEditHappened(UndoableEditEvent e) {
				editManager.addEdit(e.getEdit());
			}
		});
		
		
		jScrollPane = new JScrollPane();
		textArea = new JTextArea();
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
		
		wordCountButton = new JMenuItem();

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
		wordCountButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				wordCountButtonActionPerformed(evt);
			}
		});
		fileMenu.add(quitButton);

		menuBar.add(fileMenu);
		menuBar.add(editMenu);
		
		
	    editMenu.setText("Edit");
		editMenu.add(undoAction);
	    editMenu.add(redoAction);
		wordCountButton.setText("Word Count");
		menuBar.add(wordCountButton);
	
	

		setJMenuBar(menuBar);
		
	

		GroupLayout layout = new GroupLayout(getContentPane());
		getContentPane().setLayout(layout);

		jScrollPane = new JScrollPane(textArea);

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
	// Listener methods
	
	
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
				Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
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

	class UndoListener implements UndoableEditListener {
	    public void undoableEditHappened(UndoableEditEvent e) {
	      undoManager.addEdit(e.getEdit());
	      undoAction.update();
	      redoAction.update();
	    }
	  }
	  class UndoAction extends AbstractAction {
	    public UndoAction() {
	      this.putValue(Action.NAME, undoManager.getUndoPresentationName());
	      this.setEnabled(false);
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
	      this.setEnabled(false);
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

	public void changedUpdate(DocumentEvent ev) {
	}

	public void removeUpdate(DocumentEvent ev) {
	}

	public void insertUpdate(DocumentEvent ev) {
		// if the event change happens it returns a 1
		// this says if it dont return a 1 for some reason dont do nutthin
		
		//System.out.println(ev.getLength());
		if (ev.getLength() != 1) {
			
			return;
		}

		int pos = ev.getOffset();
		// System.out.println(ev.getLength());
		//// gets element pos starting at 0
		// System.out.println(ev.getOffset());
		String content = null;

		try {
			//// gets the letters being typed in the event change sarting
			// at elemnt space 0 and adding 1 to pos to get full length
			content = textArea.getText(0, pos + 1);
			// System.out.println(content);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}

		// Find where the word starts
		int w;
		// pos = offset still
		for (w = pos; w >= 0; w--) {
			// makes sur its not a number
			if (!Character.isLetter(content.charAt(w))) {
				break;
			}
		}
		// here we can determin how many chars to wait before it starts matching
		// the prefix
		if (pos - w < 2) {
			// Too few chars
			return;
		}
		// ok so here is the meat of the program
		String prefix = content.substring(w + 1).toLowerCase();
		
		///////////////////////////
		// System.out.println(prefix);
		// finds words in list based on matching prefix
		int n = Collections.binarySearch(wordss, prefix);
		if (n < 0 && -n <= wordss.size()) {

			String match = wordss.get(-n - 1);
			// System.out.println(wordss.get(-n - 1));

			if (match.startsWith(prefix)) {
				// A completion is found
				// completion is the last half or suffix of the string.
				String completion = match.substring(pos - w);
				// System.out.println(match.substring(pos - w));

				// We cannot modify Document from within notification,
				// so we submit a task that does the change later

				// calls a completion task which is further down
				SwingUtilities.invokeLater(new CompletionTask(completion, pos + 1));
				// System.out.println(content);
				
				
				
			}
		} else {
			// Nothing found
			
			
			mode = Mode.INSERT;

		}
	/////	////////////////////// add word shit//////////////////////////////////////////////////
//		String[] wordsArray = textArea.getText().split("\\s+");
//				String lastWord = wordsArray[wordsArray.length -1];
//				
//				
//				System.out.println(lastWord);
		
		
		
		
////////////////////////////////////////////////////////////////////////////
	}

	@Override
	public void keyPressed(KeyEvent e) {
		//lastWord(e);
		
	}



	@Override
	public void keyTyped(KeyEvent e) {
		//lastWord(e);
		
	}
	

	
	
//	private void checkLastWord() {
//        try {
//            int start = Utilities.getWordStart(textArea, textArea.getCaretPosition());
//            int end = Utilities.getWordEnd(textArea, textArea.getCaretPosition());
//            String text = textArea.getDocument().getText(start, end - start);
//           System.out.println(text);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//  }

private class CompletionTask implements Runnable {

	String completion;
	int position;
	// takes the suffix suggestion and adds it upon pushing enter

	CompletionTask(String completion, int position) {
		this.completion = completion;
		this.position = position;

	}

	public void run() {
		textArea.insert(completion, position);
		textArea.setCaretPosition(position + completion.length());
		textArea.moveCaretPosition(position);
		mode = Mode.COMPLETION;
	}
}
// when the word is completed the carrot needs to be reset with a space at the
// end of the word

private class CommitAction extends AbstractAction {

	public void actionPerformed(ActionEvent ev) {
		if (mode == Mode.COMPLETION) {
			int pos = textArea.getSelectionEnd();
			textArea.insert(" ", pos);
			textArea.setCaretPosition(pos + 1);
			mode = Mode.INSERT;
		} else {
			// since the word replace requires us to take control of the return
			// button , we need to
			// redefine its original use.
			textArea.replaceSelection("\n");
		}
	}

	}


	public static void main(String args[]) {
		
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {

				try {
					new Main().setVisible(true);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}
	
	
	public void keyReleased(KeyEvent e) {
		
		
	}

	

}