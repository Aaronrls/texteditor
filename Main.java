
import javax.swing.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import java.awt.Color;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.sql.SQLException;

import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Document;
import javax.swing.text.Highlighter;

import javax.swing.text.JTextComponent;

@SuppressWarnings("serial")
public class Main extends JFrame implements DocumentListener, KeyListener, MouseListener {
	private Connex con;
	private WordTable wt;
	private LinkedList<String> wordss;
	private LinkedList<String> list;
	private String content;
	private Highlighter.HighlightPainter myHighlightPainter;
	/////////////////////////
	private JMenu editMenu;
	private JMenu fileMenu;
	private JScrollPane jScrollPane;
	private JPopupMenu.Separator jSeparator1;
	private JMenuBar menuBar;
	private JMenuItem newButton;
	private JMenuItem openButton;
	private JMenuItem quitButton;
	private JMenuItem redoButton;
	private JMenuItem saveButton;
	private JTextField status;
	private JTextArea textArea;
	private JMenuItem undoButton;
	////////////////////////////////////////////////////////////////
	private static final String COMMIT_ACTION = "commit";

	private static enum Mode {
		INSERT, COMPLETION
	};

	// lets program know if it should suggest words or move the carot after
	// inserting a suggestion
	private Mode mode = Mode.INSERT;

	public Main() throws FileNotFoundException, SQLException {
		super("Project");
		initComponents();

		textArea.getDocument().addDocumentListener(this);
		textArea.addKeyListener(this);
		textArea.addMouseListener(this);
		InputMap im = textArea.getInputMap();
		ActionMap am = textArea.getActionMap();
		// this is where we take control of the enter button. so we can
		// quick insert the suggested completion

		im.put(KeyStroke.getKeyStroke("ENTER"), COMMIT_ACTION);
		am.put(COMMIT_ACTION, new CommitAction());

		///// our custom list
		wordss = new LinkedList<String>();
		wordss = WordTable.wordss;

	}

	private void initComponents() throws FileNotFoundException, SQLException {

		myHighlightPainter = new MyHighlightPainter(Color.pink);
		list = new LinkedList<String>();
		//// connection
		con = new Connex();
		con.start();
		// words list updates from data base
		wt = new WordTable();
		wt.getWords(con.setCon());
		wt.listsort();
		con.close();
		////

		textArea = new JTextArea();
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		textArea.setColumns(20);
		textArea.setLineWrap(true);
		textArea.setRows(5);
		textArea.setWrapStyleWord(true);

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
		fileMenu.add(quitButton);

		menuBar.add(fileMenu);

		editMenu.setText("Edit");

		undoButton.setText("Undo");
		undoButton.setToolTipText("");
		editMenu.add(undoButton);

		redoButton.setText("Redo");
		editMenu.add(redoButton);

		menuBar.add(editMenu);

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

	public void changedUpdate(DocumentEvent ev) {
	}

	public void removeUpdate(DocumentEvent ev) {
	}

	public void insertUpdate(DocumentEvent ev) {
		// if the event change happens it returns a 1
		// this says if it dont return a 1 for some reason dont do nutthin

		// System.out.println(ev.getLength());
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
		///// ////////////////////// add word
		///// shit//////////////////////////////////////////////////
		// String[] wordsArray = textArea.getText().split("\\s+");
		// String lastWord = wordsArray[wordsArray.length -1];
		//
		//
		// System.out.println(lastWord);

		////////////////////////////////////////////////////////////////////////////
	}

	@Override
	public void keyPressed(KeyEvent e) {
		// lastWord(e);

	}

	@Override
	public void keyReleased(KeyEvent e) {

		try {
			lastWord(e);
		} catch (BadLocationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// lastWord(e);

	}

	public void lastWord(KeyEvent e) throws BadLocationException {
		int key = e.getKeyCode();
		if (key == KeyEvent.VK_SPACE) {

			content = textArea.getText();
			String words[] = content.split("\\s");
			for (int i = 0; i < words.length; i++) {
				list.add(words[i]);

			}
			System.out.println(list.getLast());

			for (int j = 0; j < list.size(); j++) {

				if (!wordss.contains(list.get(j))) {

					highlight(textArea, list.get(j));
				}
			}

		}
	}

	public void highlight(JTextComponent comp, String pattern) {

		Highlighter hilite = comp.getHighlighter();
		Document doc = comp.getDocument();

		try {

			String text = doc.getText(0, doc.getLength());
			int pos = 0;
			while ((pos = text.toUpperCase().indexOf(pattern.toUpperCase(), pos)) >= 0) {
				hilite.addHighlight(pos, pos + pattern.length(), myHighlightPainter);
				pos += pattern.length();
			}

		} catch (Exception e) {

		}

	}

	public void dehigh() {
		textArea.getHighlighter().removeAllHighlights();
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// addtext();
		// wordss.sort(null);
		// dehigh();
		//

		int x = e.getX();
		int y = e.getY();

		System.out.println(x);
		System.out.println(y);
		if (isHighlightededUnderMouse(textArea, x, y)) {
			int jo = JOptionPane.showConfirmDialog(null, "add", "add word to dictionary",
					JOptionPane.YES_NO_CANCEL_OPTION);
			if (jo == JOptionPane.YES_OPTION) {
				addtext();
				wordss.sort(null);
				dehigh();

			}
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {

		//

		//
		int textAreaMouseXLocation = e.getXOnScreen();
		int textAreaMouseYLocation = e.getYOnScreen();

		Point pt = new Point(textAreaMouseXLocation, textAreaMouseYLocation);
		int pos = textArea.viewToModel(pt);
		System.out.println(pos);

	}

	@Override
	public void mouseExited(MouseEvent e) {
		// int textAreaMouseXLocation =e.getX();
		// int textAreaMouseYLocation = e.getY();
		//
		// Point pt = new Point(textAreaMouseXLocation, textAreaMouseYLocation);
		// int pos = textArea.viewToModel(pt);
		// System.out.println(pos);
	}

	@Override
	public void mousePressed(MouseEvent e) {

	}

	@Override
	public void mouseReleased(MouseEvent e) {

	}

	public static boolean isHighlightededUnderMouse(JTextComponent textComp, int textAreaMouseXLocation,
			int textAreaMouseYLocation) {
		boolean isHighlighted = false;
		if (textComp.getText().isEmpty()) {
			return false;
		}

		Point pt = new Point(textAreaMouseXLocation, textAreaMouseYLocation);
		int pos = textComp.viewToModel(pt);

		Highlighter.Highlight[] allHighlights = textComp.getHighlighter().getHighlights();
		String strg = "";
		for (int i = 0; i < allHighlights.length; i++) {
			int start = (int) allHighlights[i].getStartOffset();
			int end = (int) allHighlights[i].getEndOffset();
			if (pos >= start && pos <= end) {
				isHighlighted = true;
				break;
			}
		}
		return isHighlighted;
	}

	public void addtext() {
		for (int i = 0; i < list.size(); i++) {
			wordss.add(list.get(i));

		}

	}

	class MyHighlightPainter extends DefaultHighlighter.DefaultHighlightPainter {
		public MyHighlightPainter(Color color) {
			super(color);
		}

	}

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
	// when the word is completed the carrot needs to be reset with a space at
	// the
	// end of the word

	private class CommitAction extends AbstractAction {

		public void actionPerformed(ActionEvent ev) {
			if (mode == Mode.COMPLETION) {
				int pos = textArea.getSelectionEnd();
				textArea.insert(" ", pos);
				textArea.setCaretPosition(pos + 1);
				mode = Mode.INSERT;
			} else {
				// since the word replace requires us to take control of the
				// return
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

}