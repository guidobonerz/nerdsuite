package de.drazil.nerdsuite;

//Code revised from
/*
The Definitive Guide to SWT and JFace
by Robert Harris and Rob Warner 
Apress 2004
*/
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Stack;
import java.util.StringTokenizer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ExtendedModifyEvent;
import org.eclipse.swt.custom.ExtendedModifyListener;
import org.eclipse.swt.custom.LineStyleEvent;
import org.eclipse.swt.custom.LineStyleListener;
import org.eclipse.swt.custom.ST;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.custom.StyledTextPrintOptions;
import org.eclipse.swt.events.ArmEvent;
import org.eclipse.swt.events.ArmListener;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.printing.Printer;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

public class PmpEditor {
// The number of operations that can be undone
	private static final int UNDO_LIMIT = 500;

// Contains a reference to this application
	private static PmpEditor app;

// Contains a reference to the main window
	private Shell shell;

// Displays the file
	private StyledText st;

// The full path of the current file
	private String filename;

// The font for the StyledText
	private Font font;

// The label to display statistics
	private Label status;

// The print options and printer
	private StyledTextPrintOptions options;

	private Printer printer;

// The stack used to store the undo information
	private Stack changes;

// Flag to set before performaing an undo, so the undo
// operation doesn't get stored with the rest of the undo
// information
	private boolean ignoreUndo = false;

// Syntax data for the current extension
	private SyntaxData sd;

// Line style listener
	private PmpeLineStyleListener lineStyleListener;

	/**
	 * Gets the reference to this application
	 * 
	 * @return HexEditor
	 */
	public static PmpEditor getApp() {
		return app;
	}

	/**
	 * Constructs a PmpEditor
	 */
	public PmpEditor() {
		app = this;
		changes = new Stack();

		// Set up the printing options
		options = new StyledTextPrintOptions();
		options.footer = StyledTextPrintOptions.SEPARATOR + StyledTextPrintOptions.PAGE_TAG
				+ StyledTextPrintOptions.SEPARATOR + "Confidential";
	}

	/**
	 * Runs the application
	 */
	public void run() {
		Display display = new Display();
		shell = new Shell(display);
		// Choose a monospaced font
		font = new Font(display, "Terminal", 12, SWT.NONE);

		createContents(shell);
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		font.dispose();
		display.dispose();
		if (printer != null)
			printer.dispose();
	}

	/**
	 * Creates the main window's contents
	 * 
	 * @param shell the main window
	 */
	private void createContents(Shell shell) {
		// Set the layout and the menu bar
		shell.setLayout(new FormLayout());
		shell.setMenuBar(new PmpEditorMenu(shell).getMenu());

		// Create the status bar
		status = new Label(shell, SWT.BORDER);
		FormData data = new FormData();
		data.left = new FormAttachment(0, 0);
		data.right = new FormAttachment(100, 0);
		data.bottom = new FormAttachment(100, 0);
		data.height = status.computeSize(SWT.DEFAULT, SWT.DEFAULT).y;
		status.setLayoutData(data);

		// Create the styled text
		st = new StyledText(shell, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		data = new FormData();
		data.left = new FormAttachment(0);
		data.right = new FormAttachment(100);
		data.top = new FormAttachment(0);
		data.bottom = new FormAttachment(status);
		st.setLayoutData(data);

		// Set the font
		st.setFont(font);

		// Add Brief delete next word
		// Use SWT.MOD1 instead of SWT.CTRL for portability
		st.setKeyBinding('k' | SWT.MOD1, ST.DELETE_NEXT);

		// Add vi end of line (kind of)
		// Use SWT.MOD1 instead of SWT.CTRL for portability
		// Use SWT.MOD2 instead of SWT.SHIFT for portability
		// Shift+4 is $
		st.setKeyBinding('4' | SWT.MOD1 | SWT.MOD2, ST.LINE_END);

		// Handle key presses
		st.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent event) {
				// Update the status bar
				updateStatus();
			}
		});

		// Handle text modifications
		st.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent event) {
				// Update the status bar
				updateStatus();

				// Update the comments
				if (lineStyleListener != null) {
					lineStyleListener.refreshMultilineComments(st.getText());
					st.redraw();
				}
			}
		});

		// Store undo information
		st.addExtendedModifyListener(new ExtendedModifyListener() {
			public void modifyText(ExtendedModifyEvent event) {
				if (!ignoreUndo) {
					// Push this change onto the changes stack
					changes.push(new TextChange(event.start, event.length, event.replacedText));
					if (changes.size() > UNDO_LIMIT)
						changes.remove(0);
				}
			}
		});

		// Update the title bar and the status bar
		updateTitle();
		updateStatus();
	}

	/**
	 * Opens a file
	 */
	public void openFile() {
		FileDialog dlg = new FileDialog(shell);
		String temp = dlg.open();
		if (temp != null) {
			try {
				// Get the file's contents
				String text = PmpeIoManager.getFile(temp);
				// File loaded, so save the file name
				filename = temp;

				// Update the syntax properties to use
				updateSyntaxData();

				// Put the new file's data in the StyledText
				st.setText(text);

				// Update the title bar
				updateTitle();

				// Delete any undo information
				changes.clear();
			} catch (IOException e) {
				showError(e.getMessage());
			}
		}
	}

	/**
	 * Saves a file
	 */
	public void saveFile() {
		if (filename == null) {
			saveFileAs();
		} else {
			try {
				// Save the file and update the title bar based on the new file name
				PmpeIoManager.saveFile(filename, st.getText().getBytes());
				updateTitle();
			} catch (IOException e) {
				showError(e.getMessage());
			}
		}
	}

	/**
	 * Saves a file under a different name
	 */
	public void saveFileAs() {
		FileDialog dlg = new FileDialog(shell);
		if (filename != null) {
			dlg.setFileName(filename);
		}
		String temp = dlg.open();
		if (temp != null) {
			filename = temp;

			// The extension may have changed; update the syntax data accordingly
			updateSyntaxData();
			saveFile();
		}
	}

	/**
	 * Prints the document to the default printer
	 */
	public void print() {
		if (printer == null)
			printer = new Printer();
		options.header = StyledTextPrintOptions.SEPARATOR + filename + StyledTextPrintOptions.SEPARATOR;
		st.print(printer, options).run();
	}

	/**
	 * Cuts the current selection to the clipboard
	 */
	public void cut() {
		st.cut();
	}

	/**
	 * Copies the current selection to the clipboard
	 */
	public void copy() {
		st.copy();
	}

	/**
	 * Pastes the clipboard's contents
	 */
	public void paste() {
		st.paste();
	}

	/**
	 * Selects all the text
	 */
	public void selectAll() {
		st.selectAll();
	}

	/**
	 * Undoes the last change
	 */
	public void undo() {
		// Make sure undo stack isn't empty
		if (!changes.empty()) {
			// Get the last change
			TextChange change = (TextChange) changes.pop();

			// Set the flag. Otherwise, the replaceTextRange call will get placed
			// on the undo stack
			ignoreUndo = true;
			// Replace the changed text
			st.replaceTextRange(change.getStart(), change.getLength(), change.getReplacedText());

			// Move the caret
			st.setCaretOffset(change.getStart());

			// Scroll the screen
			st.setTopIndex(st.getLineAtOffset(change.getStart()));
			ignoreUndo = false;
		}
	}

	/**
	 * Toggles word wrap
	 */
	public void toggleWordWrap() {
		st.setWordWrap(!st.getWordWrap());
	}

	/**
	 * Gets the current word wrap settings
	 * 
	 * @return boolean
	 */
	public boolean getWordWrap() {
		return st.getWordWrap();
	}

	/**
	 * Shows an about box
	 */
	public void about() {
		MessageBox mb = new MessageBox(shell, SWT.ICON_INFORMATION | SWT.OK);
		mb.setMessage("Poor Man's Programming Editor");
		mb.open();
	}

	/**
	 * Updates the title bar
	 */
	private void updateTitle() {
		String fn = filename == null ? "Untitled" : filename;
		shell.setText(fn + " -- PmPe");
	}

	/**
	 * Updates the status bar
	 */
	private void updateStatus() {
		// Show the offset into the file, the total number of characters in the
		// file,
		// the current line number (1-based) and the total number of lines
		StringBuffer buf = new StringBuffer();
		buf.append("Offset: ");
		buf.append(st.getCaretOffset());
		buf.append("\tChars: ");
		buf.append(st.getCharCount());
		buf.append("\tLine: ");
		buf.append(st.getLineAtOffset(st.getCaretOffset()) + 1);
		buf.append(" of ");
		buf.append(st.getLineCount());
		status.setText(buf.toString());
	}

	/**
	 * Updates the syntax data based on the filename's extension
	 */
	private void updateSyntaxData() {
		// Determine the extension of the current file
		String extension = "";
		if (filename != null) {
			int pos = filename.lastIndexOf(".");
			if (pos > -1 && pos < filename.length() - 2) {
				extension = filename.substring(pos + 1);
			}
		}

		// Get the syntax data for the extension
		sd = SyntaxManager.getSyntaxData(extension);

		// Reset the line style listener
		if (lineStyleListener != null) {
			st.removeLineStyleListener(lineStyleListener);
		}
		lineStyleListener = new PmpeLineStyleListener(sd);
		st.addLineStyleListener(lineStyleListener);

		// Redraw the contents to reflect the new syntax data
		st.redraw();
	}

	/**
	 * Shows an error message
	 * 
	 * @param error the text to show
	 */
	private void showError(String error) {
		MessageBox mb = new MessageBox(shell, SWT.ICON_ERROR | SWT.OK);
		mb.setMessage(error);
		mb.open();
	}

	/**
	 * The application entry point
	 * 
	 * @param args the command line arguments
	 */
	public static void main(String[] args) {
		new PmpEditor().run();
	}
}

class PmpEditorMenu {
// The underlying menu this class wraps
	Menu menu = null;

	/**
	 * Constructs a PmpEditorMenu
	 * 
	 * @param shell the parent shell
	 */
	public PmpEditorMenu(final Shell shell) {
		// Create the menu
		menu = new Menu(shell, SWT.BAR);

		// Create the File top-level menu
		MenuItem item = new MenuItem(menu, SWT.CASCADE);
		item.setText("File");
		Menu dropMenu = new Menu(shell, SWT.DROP_DOWN);
		item.setMenu(dropMenu);

		// Create File->Open
		item = new MenuItem(dropMenu, SWT.NULL);
		item.setText("Open...\tCtrl+O");
		item.setAccelerator(SWT.CTRL + 'O');
		item.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				PmpEditor.getApp().openFile();
			}
		});

		// Create File->Save
		item = new MenuItem(dropMenu, SWT.NULL);
		item.setText("Save\tCtrl+S");
		item.setAccelerator(SWT.CTRL + 'S');
		item.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				PmpEditor.getApp().saveFile();
			}
		});

		// Create File->Save As
		item = new MenuItem(dropMenu, SWT.NULL);
		item.setText("Save As...");
		item.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				PmpEditor.getApp().saveFileAs();
			}
		});

		new MenuItem(dropMenu, SWT.SEPARATOR);

		// Create File->Print
		item = new MenuItem(dropMenu, SWT.NULL);
		item.setText("Print\tCtrl+P");
		item.setAccelerator(SWT.CTRL + 'P');
		item.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				PmpEditor.getApp().print();
			}
		});

		new MenuItem(dropMenu, SWT.SEPARATOR);

		// Create File->Exit
		item = new MenuItem(dropMenu, SWT.NULL);
		item.setText("Exit\tAlt+F4");
		item.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				shell.close();
			}
		});

		// Create Edit
		item = new MenuItem(menu, SWT.CASCADE);
		item.setText("Edit");
		dropMenu = new Menu(shell, SWT.DROP_DOWN);
		item.setMenu(dropMenu);

		// Create Edit->Cut
		item = new MenuItem(dropMenu, SWT.NULL);
		item.setText("Cut\tCtrl+X");
		item.setAccelerator(SWT.CTRL + 'X');
		item.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				PmpEditor.getApp().cut();
			}
		});

		// Create Edit->Copy
		item = new MenuItem(dropMenu, SWT.NULL);
		item.setText("Copy\tCtrl+C");
		item.setAccelerator(SWT.CTRL + 'C');
		item.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				PmpEditor.getApp().copy();
			}
		});

		// Create Edit->Paste
		item = new MenuItem(dropMenu, SWT.NULL);
		item.setText("Paste\tCtrl+V");
		item.setAccelerator(SWT.CTRL + 'V');
		item.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				PmpEditor.getApp().paste();
			}
		});

		new MenuItem(dropMenu, SWT.SEPARATOR);

		// Create Select All
		item = new MenuItem(dropMenu, SWT.NULL);
		item.setText("Select All\tCtrl+A");
		item.setAccelerator(SWT.CTRL + 'A');
		item.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				PmpEditor.getApp().selectAll();
			}
		});

		new MenuItem(dropMenu, SWT.SEPARATOR);

		// Create Undo
		item = new MenuItem(dropMenu, SWT.NULL);
		item.setText("Undo\tCtrl+Z");
		item.setAccelerator(SWT.CTRL + 'Z');
		item.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				PmpEditor.getApp().undo();
			}
		});

		new MenuItem(dropMenu, SWT.SEPARATOR);
		// Create Word Wrap
		final MenuItem wwItem = new MenuItem(dropMenu, SWT.CHECK);
		wwItem.setText("Word Wrap\tCtrl+W");
		wwItem.setAccelerator(SWT.CTRL + 'W');
		wwItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				PmpEditor.getApp().toggleWordWrap();
			}
		});
		wwItem.addArmListener(new ArmListener() {
			public void widgetArmed(ArmEvent event) {
				wwItem.setSelection(PmpEditor.getApp().getWordWrap());
			}
		});

		// Create Help
		item = new MenuItem(menu, SWT.CASCADE);
		item.setText("Help");
		dropMenu = new Menu(shell, SWT.DROP_DOWN);
		item.setMenu(dropMenu);

		// Create Help->About
		item = new MenuItem(dropMenu, SWT.NULL);
		item.setText("About\tCtrl+A");
		item.setAccelerator(SWT.CTRL + 'A');
		item.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				PmpEditor.getApp().about();
			}
		});
	}

	/**
	 * Gets the underlying menu
	 * 
	 * @return Menu
	 */
	public Menu getMenu() {
		return menu;
	}
}

class PmpeIoManager {
	/**
	 * Gets a file (loads it) from the filesystem
	 * 
	 * @param filename the full path of the file
	 * @return String
	 * @throws IOException if file cannot be loaded
	 */
	public static String getFile(String filename) throws IOException {
		InputStream in = new BufferedInputStream(new FileInputStream(filename));
		StringBuffer buf = new StringBuffer();
		int c;
		while ((c = in.read()) != -1) {
			buf.append((char) c);
		}
		return buf.toString();
	}

	/**
	 * Saves a file
	 * 
	 * @param filename the full path of the file to save
	 * @param data     the data to save
	 * @throws IOException if file cannot be saved
	 */
	public static void saveFile(String filename, byte[] data) throws IOException {
		File outputFile = new File(filename);
		FileOutputStream out = new FileOutputStream(outputFile);
		out.write(data);
		out.close();
	}
}

class TextChange {
// The starting offset of the change
	private int start;

// The length of the change
	private int length;

// The replaced text
	String replacedText;

	/**
	 * Constructs a TextChange
	 * 
	 * @param start        the starting offset of the change
	 * @param length       the length of the change
	 * @param replacedText the text that was replaced
	 */
	public TextChange(int start, int length, String replacedText) {
		this.start = start;
		this.length = length;
		this.replacedText = replacedText;
	}

	/**
	 * Returns the start
	 * 
	 * @return int
	 */
	public int getStart() {
		return start;
	}

	/**
	 * Returns the length
	 * 
	 * @return int
	 */
	public int getLength() {
		return length;
	}

	/**
	 * Returns the replacedText
	 * 
	 * @return String
	 */
	public String getReplacedText() {
		return replacedText;
	}
}

/**
 * This class contains information for syntax coloring and styling for an
 * extension
 */
class SyntaxData {
	private String extension;

	private Collection keywords;

	private String punctuation;

	private String comment;

	private String multiLineCommentStart;

	private String multiLineCommentEnd;

	/**
	 * Constructs a SyntaxData
	 * 
	 * @param extension the extension
	 */
	public SyntaxData(String extension) {
		this.extension = extension;
	}

	/**
	 * Gets the extension
	 * 
	 * @return String
	 */
	public String getExtension() {
		return extension;
	}

	/**
	 * Gets the comment
	 * 
	 * @return String
	 */
	public String getComment() {
		return comment;
	}

	/**
	 * Sets the comment
	 * 
	 * @param comment The comment to set.
	 */
	public void setComment(String comment) {
		this.comment = comment;
	}

	/**
	 * Gets the keywords
	 * 
	 * @return Collection
	 */
	public Collection getKeywords() {
		return keywords;
	}

	/**
	 * Sets the keywords
	 * 
	 * @param keywords The keywords to set.
	 */
	public void setKeywords(Collection keywords) {
		this.keywords = keywords;
	}

	/**
	 * Gets the multiline comment end
	 * 
	 * @return String
	 */
	public String getMultiLineCommentEnd() {
		return multiLineCommentEnd;
	}

	/**
	 * Sets the multiline comment end
	 * 
	 * @param multiLineCommentEnd The multiLineCommentEnd to set.
	 */
	public void setMultiLineCommentEnd(String multiLineCommentEnd) {
		this.multiLineCommentEnd = multiLineCommentEnd;
	}

	/**
	 * Gets the multiline comment start
	 * 
	 * @return String
	 */
	public String getMultiLineCommentStart() {
		return multiLineCommentStart;
	}

	/**
	 * Sets the multiline comment start
	 * 
	 * @param multiLineCommentStart The multiLineCommentStart to set.
	 */
	public void setMultiLineCommentStart(String multiLineCommentStart) {
		this.multiLineCommentStart = multiLineCommentStart;
	}

	/**
	 * Gets the punctuation
	 * 
	 * @return String
	 */
	public String getPunctuation() {
		return punctuation;
	}

	/**
	 * Sets the punctuation
	 * 
	 * @param punctuation The punctuation to set.
	 */
	public void setPunctuation(String punctuation) {
		this.punctuation = punctuation;
	}
}

/**
 * This class manages the syntax coloring and styling data
 */
class SyntaxManager {
// Lazy cache of SyntaxData objects
	private static Map data = new Hashtable();

	/**
	 * Gets the syntax data for an extension
	 */
	public static synchronized SyntaxData getSyntaxData(String extension) {
		// Check in cache
		SyntaxData sd = (SyntaxData) data.get(extension);
		if (sd == null) {
			// Not in cache; load it and put in cache
			sd = loadSyntaxData(extension);
			if (sd != null)
				data.put(sd.getExtension(), sd);
		}
		return sd;
	}

	/**
	 * Loads the syntax data for an extension
	 * 
	 * @param extension the extension to load
	 * @return SyntaxData
	 */
	private static SyntaxData loadSyntaxData(String extension) {
		SyntaxData sd = null;

		sd = new SyntaxData(extension);
		sd.setComment("//");
		sd.setMultiLineCommentStart("/*");
		sd.setMultiLineCommentEnd("*/");

		// Load the keywords
		Collection<String> keywords = new ArrayList<>();
		keywords.add("guido");
		keywords.add("bonerz");
		sd.setKeywords(keywords);

		// Load the punctuation
		sd.setPunctuation(".");

		return sd;
	}
}

/**
 * This class performs the syntax highlighting and styling for Pmpe
 */
class PmpeLineStyleListener implements LineStyleListener {
// Colors
	private static final Color COMMENT_COLOR = Display.getCurrent().getSystemColor(SWT.COLOR_DARK_GREEN);

	private static final Color COMMENT_BACKGROUND = Display.getCurrent().getSystemColor(SWT.COLOR_GRAY);

	private static final Color PUNCTUATION_COLOR = Display.getCurrent().getSystemColor(SWT.COLOR_DARK_CYAN);

	private static final Color KEYWORD_COLOR = Display.getCurrent().getSystemColor(SWT.COLOR_DARK_MAGENTA);

// Holds the syntax data
	private SyntaxData syntaxData;

// Holds the offsets for all multiline comments
	List commentOffsets;

	/**
	 * PmpeLineStyleListener constructor
	 * 
	 * @param syntaxData the syntax data to use
	 */
	public PmpeLineStyleListener(SyntaxData syntaxData) {
		this.syntaxData = syntaxData;
		commentOffsets = new LinkedList();
	}

	/**
	 * Refreshes the offsets for all multiline comments in the parent StyledText.
	 * The parent StyledText should call this whenever its text is modified. Note
	 * that this code doesn't ignore comment markers inside strings.
	 * 
	 * @param text the text from the StyledText
	 */
	public void refreshMultilineComments(String text) {
		// Clear any stored offsets
		commentOffsets.clear();

		if (syntaxData != null) {
			// Go through all the instances of COMMENT_START
			for (int pos = text.indexOf(syntaxData.getMultiLineCommentStart()); pos > -1; pos = text
					.indexOf(syntaxData.getMultiLineCommentStart(), pos)) {
				// offsets[0] holds the COMMENT_START offset
				// and COMMENT_END holds the ending offset
				int[] offsets = new int[2];
				offsets[0] = pos;

				// Find the corresponding end comment.
				pos = text.indexOf(syntaxData.getMultiLineCommentEnd(), pos);

				// If no corresponding end comment, use the end of the text
				offsets[1] = pos == -1 ? text.length() - 1 : pos + syntaxData.getMultiLineCommentEnd().length() - 1;
				pos = offsets[1];
				// Add the offsets to the collection
				commentOffsets.add(offsets);
			}
		}
	}

	/**
	 * Checks to see if the specified section of text begins inside a multiline
	 * comment. Returns the index of the closing comment, or the end of the line if
	 * the whole line is inside the comment. Returns -1 if the line doesn't begin
	 * inside a comment.
	 * 
	 * @param start  the starting offset of the text
	 * @param length the length of the text
	 * @return int
	 */
	private int getBeginsInsideComment(int start, int length) {
		// Assume section doesn't being inside a comment
		int index = -1;

		// Go through the multiline comment ranges
		for (int i = 0, n = commentOffsets.size(); i < n; i++) {
			int[] offsets = (int[]) commentOffsets.get(i);

			// If starting offset is past range, quit
			if (offsets[0] > start + length)
				break;
			// Check to see if section begins inside a comment
			if (offsets[0] <= start && offsets[1] >= start) {
				// It does; determine if the closing comment marker is inside
				// this section
				index = offsets[1] > start + length ? start + length
						: offsets[1] + syntaxData.getMultiLineCommentEnd().length() - 1;
			}
		}
		return index;
	}

	/**
	 * Called by StyledText to get styles for a line
	 */
	public void lineGetStyle(LineStyleEvent event) {
		// Only do styles if syntax data has been loaded
		if (syntaxData != null) {
			// Create collection to hold the StyleRanges
			List styles = new ArrayList();

			int start = 0;
			int length = event.lineText.length();

			// Check if line begins inside a multiline comment
			int mlIndex = getBeginsInsideComment(event.lineOffset, event.lineText.length());
			if (mlIndex > -1) {
				// Line begins inside multiline comment; create the range
				styles.add(new StyleRange(event.lineOffset, mlIndex - event.lineOffset, COMMENT_COLOR,
						COMMENT_BACKGROUND));
				start = mlIndex;
			}
			// Do punctuation, single-line comments, and keywords
			while (start < length) {
				// Check for multiline comments that begin inside this line
				if (event.lineText.indexOf(syntaxData.getMultiLineCommentStart(), start) == start) {
					// Determine where comment ends
					int endComment = event.lineText.indexOf(syntaxData.getMultiLineCommentEnd(), start);

					// If comment doesn't end on this line, extend range to end of line
					if (endComment == -1)
						endComment = length;
					else
						endComment += syntaxData.getMultiLineCommentEnd().length();
					styles.add(new StyleRange(event.lineOffset + start, endComment - start, COMMENT_COLOR,
							COMMENT_BACKGROUND));

					// Move marker
					start = endComment;
				}
				// Check for single line comments
				else if (event.lineText.indexOf(syntaxData.getComment(), start) == start) {
					// Comment rest of line
					styles.add(new StyleRange(event.lineOffset + start, length - start, COMMENT_COLOR,
							COMMENT_BACKGROUND));

					// Move marker
					start = length;
				}
				// Check for punctuation
				else if (syntaxData.getPunctuation().indexOf(event.lineText.charAt(start)) > -1) {
					// Add range for punctuation
					styles.add(new StyleRange(event.lineOffset + start, 1, PUNCTUATION_COLOR, null));
					++start;
				} else if (Character.isLetter(event.lineText.charAt(start))) {
					// Get the next word
					StringBuffer buf = new StringBuffer();
					int i = start;
					// Call any consecutive letters a word
					for (; i < length && Character.isLetter(event.lineText.charAt(i)); i++) {
						buf.append(event.lineText.charAt(i));
					}
					// See if the word is a keyword
					if (syntaxData.getKeywords().contains(buf.toString())) {
						// It's a keyword; create the StyleRange
						styles.add(new StyleRange(event.lineOffset + start, i - start, KEYWORD_COLOR, null, SWT.BOLD));
					}
					// Move the marker to the last char (the one that wasn't a letter)
					// so it can be retested in the next iteration through the loop
					start = i;
				} else
					// It's nothing we're interested in; advance the marker
					++start;
			}

			// Copy the StyleRanges back into the event
			event.styles = (StyleRange[]) styles.toArray(new StyleRange[0]);
		}
	}
}