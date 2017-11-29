package texteditor;

import java.awt.Color;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.util.LinkedList;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Document;
import javax.swing.text.Highlighter;
import javax.swing.text.JTextComponent;

public class HighLite implements KeyListener, MouseListener {
	private Highlighter.HighlightPainter myHighlightPainter;
	private Highlighter hilite;
	private Document doc;
	private JTextArea textArea;
	private String content;
	private LinkedList<String> list;
	private LinkedList<String> wordss;
	private WordList wl;

 	public HighLite(JTextArea textArea, LinkedList<String> wordss) throws FileNotFoundException {
		this.textArea = textArea;
		this.wordss = wordss;
		list = new LinkedList<String>();
		 wl = new WordList();
		
		myHighlightPainter = new MyHighlightPainter(Color.pink);
		setHilite(textArea.getHighlighter());
		setDoc(textArea.getDocument());
		textArea.addKeyListener(this);
		textArea.addMouseListener(this);
		
		// System.out.println(wordss.toString());

	}

	public void Hilite(JTextComponent comp, String pattern) {

		myHighlightPainter = new MyHighlightPainter(Color.pink);
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

	public static boolean isHighlightededUnderMouse(JTextComponent textComp, int textAreaMouseXLocation,
			int textAreaMouseYLocation) {
		boolean isHighlighted = false;
		if (textComp.getText().isEmpty()) {
			return false;
		}

		Point pt = new Point(textAreaMouseXLocation, textAreaMouseYLocation);
		int pos = textComp.viewToModel(pt);

		Highlighter.Highlight[] allHighlights = textComp.getHighlighter().getHighlights();

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

	public void lastWord(KeyEvent e) throws BadLocationException {
		int key = e.getKeyCode();
		if (key == KeyEvent.VK_SPACE) {

			content = textArea.getText();
			String words[] = content.split("\\s");
			for (int i = 0; i < words.length; i++) {
				list.add(words[i]);

			}
			// System.out.println(list.getLast());

			for (int j = 0; j < list.size(); j++) {

				if (!wordss.contains(list.get(j))) {

					Hilite(textArea, list.get(j));
				}
			}

		}
	}

	public void addtext() throws SQLException {
		for (int i = 0; i < list.size(); i++) {
			wordss.add(list.get(i));
			wl.insert(list.get(i));
		
		}

	}

	@Override
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub

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
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseClicked(MouseEvent e) {
		//

		int x = e.getX();
		int y = e.getY();

		// System.out.println(x);
		// System.out.println(y);
		if (isHighlightededUnderMouse(textArea, x, y)) {
			int jo = JOptionPane.showConfirmDialog(null, "add", "add word to dictionary",
					JOptionPane.YES_NO_CANCEL_OPTION);
			if (jo == JOptionPane.YES_OPTION) {
				try {
					addtext();
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				wordss.sort(null);
				dehigh();

			}
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {

	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	public Highlighter getHilite() {
		return hilite;
	}

	public void setHilite(Highlighter hilite) {
		this.hilite = hilite;
	}

	public Document getDoc() {
		return doc;
	}

	public void setDoc(Document doc) {
		this.doc = doc;
	}
}

class MyHighlightPainter extends DefaultHighlighter.DefaultHighlightPainter {
	public MyHighlightPainter(Color color) {
		super(color);
	}

}
