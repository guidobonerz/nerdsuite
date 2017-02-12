package de.drazil.test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

/**
 * This class demonstrates TableViewers
 */
public class PlayerTable extends ApplicationWindow
{
	// The data model
	private PlayerTableModel model;

	// The table viewer
	private TableViewer tv;

	/**
	 * Constructs a PlayerTable
	 */
	public PlayerTable()
	{
		super(null);
		model = new PlayerTableModel();
	}

	/**
	 * Runs the application
	 */
	public void run()
	{
		// Don't return from open() until window closes
		setBlockOnOpen(true);

		// Open the main window
		open();

		// Dispose the display
		Display.getCurrent().dispose();
	}

	/**
	 * Configures the shell
	 * 
	 * @param shell
	 *          the shell
	 */
	protected void configureShell(Shell shell)
	{
		super.configureShell(shell);
		shell.setSize(400, 400);
	}

	/**
	 * Creates the main window's contents
	 * 
	 * @param parent
	 *          the main window
	 * @return Control
	 */
	protected Control createContents(Composite parent)
	{
		// Create the composite to hold the controls
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(1, false));

		// Create the combo to hold the team names
		Combo combo = new Combo(composite, SWT.READ_ONLY);
		combo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		// Create the table viewer to display the players
		tv = new TableViewer(composite);

		// Set the content and label providers
		tv.setContentProvider(new PlayerContentProvider());
		tv.setLabelProvider(new PlayerLabelProvider());
		tv.setSorter(new PlayerViewerSorter());

		// Set up the table
		Table table = tv.getTable();
		table.setLayoutData(new GridData(GridData.FILL_BOTH));

		// Add the first name column
		TableColumn tc = new TableColumn(table, SWT.LEFT);
		tc.setText("First Name");
		tc.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent event)
			{
				((PlayerViewerSorter) tv.getSorter()).doSort(PlayerConst.COLUMN_FIRST_NAME);
				tv.refresh();
			}
		});

		// Add the last name column
		tc = new TableColumn(table, SWT.LEFT);
		tc.setText("Last Name");
		tc.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent event)
			{
				((PlayerViewerSorter) tv.getSorter()).doSort(PlayerConst.COLUMN_LAST_NAME);
				tv.refresh();
			}
		});

		// Add the points column
		tc = new TableColumn(table, SWT.RIGHT);
		tc.setText("Points");
		tc.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent event)
			{
				((PlayerViewerSorter) tv.getSorter()).doSort(PlayerConst.COLUMN_POINTS);
				tv.refresh();
			}
		});

		// Add the rebounds column
		tc = new TableColumn(table, SWT.RIGHT);
		tc.setText("Rebounds");
		tc.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent event)
			{
				((PlayerViewerSorter) tv.getSorter()).doSort(PlayerConst.COLUMN_REBOUNDS);
				tv.refresh();
			}
		});

		// Add the assists column
		tc = new TableColumn(table, SWT.RIGHT);
		tc.setText("Assists");
		tc.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent event)
			{
				((PlayerViewerSorter) tv.getSorter()).doSort(PlayerConst.COLUMN_ASSISTS);
				tv.refresh();
			}
		});

		// Add the team names to the combo
		for (int i = 0, n = model.teams.length; i < n; i++)
		{
			combo.add(model.teams[i].getName());
		}

		// Add a listener to change the tableviewer's input
		combo.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent event)
			{
				update(model.teams[((Combo) event.widget).getSelectionIndex()]);
			}
		});

		// Select the first item
		combo.select(0);
		update(model.teams[0]);

		// Pack the columns
		for (int i = 0, n = table.getColumnCount(); i < n; i++)
		{
			table.getColumn(i).pack();
		}

		// Turn on the header and the lines
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		return composite;
	}

	/**
	 * Updates the application with the selected team
	 * 
	 * @param team
	 *          the team
	 */
	private void update(Team team)
	{
		// Update the window's title bar with the new team
		getShell().setText(team.getYear() + " " + team.getName());

		// Set the table viewer's input to the team
		tv.setInput(team);
	}

	/**
	 * The application entry point
	 * 
	 * @param args
	 *          the command line arguments
	 */
	public static void main(String[] args)
	{
		new PlayerTable().run();
	}
}

/**
 * This class contains the data model for the PlayerTable
 */

class PlayerTableModel
{
	public Team[] teams;

	/**
	 * Constructs a PlayerTableModel Fills the model with data
	 */
	public PlayerTableModel()
	{
		teams = new Team[3];

		teams[0] = new Team("Celtics", "1985-86");
		teams[0].add(new Player("Larry", "Bird", 25.8f, 9.8f, 6.8f));
		teams[0].add(new Player("Kevin", "McHale", 21.3f, 8.1f, 2.7f));
		teams[0].add(new Player("Robert", "Parish", 16.1f, 9.5f, 1.8f));
		teams[0].add(new Player("Dennis", "Johnson", 15.6f, 3.4f, 5.8f));
		teams[0].add(new Player("Danny", "Ainge", 10.7f, 2.9f, 5.1f));
		teams[0].add(new Player("Scott", "Wedman", 8.0f, 2.4f, 1.1f));
		teams[0].add(new Player("Bill", "Walton", 7.6f, 6.8f, 2.1f));
		teams[0].add(new Player("Jerry", "Sichting", 6.5f, 1.3f, 2.3f));
		teams[0].add(new Player("David", "Thirdkill", 3.3f, 1.4f, 0.3f));
		teams[0].add(new Player("Sam", "Vincent", 3.2f, 0.8f, 1.2f));
		teams[0].add(new Player("Sly", "Williams", 2.8f, 2.5f, 0.3f));
		teams[0].add(new Player("Rick", "Carlisle", 2.6f, 1.0f, 1.4f));
		teams[0].add(new Player("Greg", "Kite", 1.3f, 2.0f, 1.3f));

		teams[1] = new Team("Bulls", "1995-96");
		teams[1].add(new Player("Michael", "Jordan", 30.4f, 6.6f, 4.3f));
		teams[1].add(new Player("Scottie", "Pippen", 19.4f, 6.4f, 5.9f));
		teams[1].add(new Player("Toni", "Kukoc", 13.1f, 4.0f, 3.5f));
		teams[1].add(new Player("Luc", "Longley", 9.1f, 5.1f, 1.9f));
		teams[1].add(new Player("Steve", "Kerr", 8.4f, 1.3f, 2.3f));
		teams[1].add(new Player("Ron", "Harper", 7.4f, 2.7f, 2.6f));
		teams[1].add(new Player("Dennis", "Rodman", 5.5f, 14.9f, 2.5f));
		teams[1].add(new Player("Bill", "Wennington", 5.3f, 2.5f, 0.6f));
		teams[1].add(new Player("Jack", "Haley", 5.0f, 2.0f, 0.0f));
		teams[1].add(new Player("John", "Salley", 4.4f, 3.3f, 1.3f));
		teams[1].add(new Player("Jud", "Buechler", 3.8f, 1.5f, 0.8f));
		teams[1].add(new Player("Dickey", "Simpkins", 3.6f, 2.6f, 0.6f));
		teams[1].add(new Player("James", "Edwards", 3.5f, 1.4f, 0.4f));
		teams[1].add(new Player("Jason", "Caffey", 3.2f, 1.9f, 0.4f));
		teams[1].add(new Player("Randy", "Brown", 2.7f, 1.0f, 1.1f));

		teams[2] = new Team("Lakers", "1987-1988");
		teams[2].add(new Player("Magic", "Johnson", 23.9f, 6.3f, 12.2f));
		teams[2].add(new Player("James", "Worthy", 19.4f, 5.7f, 2.8f));
		teams[2].add(new Player("Kareem", "Abdul-Jabbar", 17.5f, 6.7f, 2.6f));
		teams[2].add(new Player("Byron", "Scott", 17.0f, 3.5f, 3.4f));
		teams[2].add(new Player("A.C.", "Green", 10.8f, 7.8f, 1.1f));
		teams[2].add(new Player("Michael", "Cooper", 10.5f, 3.1f, 4.5f));
		teams[2].add(new Player("Mychal", "Thompson", 10.1f, 4.1f, 0.8f));
		teams[2].add(new Player("Kurt", "Rambis", 5.7f, 5.8f, 0.8f));
		teams[2].add(new Player("Billy", "Thompson", 5.6f, 2.9f, 1.0f));
		teams[2].add(new Player("Adrian", "Branch", 4.3f, 1.7f, 0.5f));
		teams[2].add(new Player("Wes", "Matthews", 4.2f, 0.9f, 2.0f));
		teams[2].add(new Player("Frank", "Brickowski", 4.0f, 2.6f, 0.3f));
		teams[2].add(new Player("Mike", "Smrek", 2.2f, 1.1f, 0.1f));
	}
}

/**
 * This class represents a player
 */

class Player
{
	private Team team;

	private String lastName;

	private String firstName;

	private float points;

	private float rebounds;

	private float assists;

	/**
	 * Constructs an empty Player
	 */
	public Player()
	{
		this(null, null, 0.0f, 0.0f, 0.0f);
	}

	/**
	 * Constructs a Player
	 * 
	 * @param firstName
	 *          the first name
	 * @param lastName
	 *          the last name
	 * @param points
	 *          the points
	 * @param rebounds
	 *          the rebounds
	 * @param assists
	 *          the assists
	 */
	public Player(String firstName, String lastName, float points, float rebounds, float assists)
	{
		setFirstName(firstName);
		setLastName(lastName);
		setPoints(points);
		setRebounds(rebounds);
		setAssists(assists);
	}

	/**
	 * Sets the team for theo player
	 * 
	 * @param team
	 *          the team
	 */
	public void setTeam(Team team)
	{
		this.team = team;
	}

	/**
	 * Gets the assists
	 * 
	 * @return float
	 */
	public float getAssists()
	{
		return assists;
	}

	/**
	 * Sets the assists
	 * 
	 * @param assists
	 *          The assists to set.
	 */
	public void setAssists(float assists)
	{
		this.assists = assists;
	}

	/**
	 * Gets the first name
	 * 
	 * @return String
	 */
	public String getFirstName()
	{
		return firstName;
	}

	/**
	 * Sets the first name
	 * 
	 * @param firstName
	 *          The firstName to set.
	 */
	public void setFirstName(String firstName)
	{
		this.firstName = firstName;
	}

	/**
	 * Gets the last name
	 * 
	 * @return String
	 */
	public String getLastName()
	{
		return lastName;
	}

	/**
	 * Sets the last name
	 * 
	 * @param lastName
	 *          The lastName to set.
	 */
	public void setLastName(String lastName)
	{
		this.lastName = lastName;
	}

	/**
	 * Gets the points
	 * 
	 * @return float
	 */
	public float getPoints()
	{
		return points;
	}

	/**
	 * Sets the points
	 * 
	 * @param points
	 *          The points to set.
	 */
	public void setPoints(float points)
	{
		this.points = points;
	}

	/**
	 * Gets the rebounds
	 * 
	 * @return float
	 */
	public float getRebounds()
	{
		return rebounds;
	}

	/**
	 * Sets the rebounds
	 * 
	 * @param rebounds
	 *          The rebounds to set.
	 */
	public void setRebounds(float rebounds)
	{
		this.rebounds = rebounds;
	}

	/**
	 * Gets the team
	 * 
	 * @return Team
	 */
	public Team getTeam()
	{
		return team;
	}

	/**
	 * Returns whether this player led the team in the specified category
	 * 
	 * @param column
	 *          the column (category)
	 * @return boolean
	 */
	public boolean ledTeam(int column)
	{
		return team.led(this, column);
	}
}

/**
 * This class represents a team
 */

class Team
{
	private String name;

	private String year;

	private List players;

	/**
	 * Constructs a Team
	 * 
	 * @param name
	 *          the name
	 * @param year
	 *          the year
	 */
	public Team(String name, String year)
	{
		this.name = name;
		this.year = year;
		players = new LinkedList();
	}

	/**
	 * Gets the name
	 * 
	 * @return String
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Gets the year
	 * 
	 * @return String
	 */
	public String getYear()
	{
		return year;
	}

	/**
	 * Adds a player
	 * 
	 * @param player
	 *          the player to add
	 * @return boolean
	 */
	public boolean add(Player player)
	{
		boolean added = players.add(player);
		if (added)
			player.setTeam(this);
		return added;
	}

	/**
	 * Gets the players
	 * 
	 * @return List
	 */
	public List getPlayers()
	{
		return Collections.unmodifiableList(players);
	}

	/**
	 * Returns whether the specified player led his team in the specified category
	 * 
	 * @param player
	 *          the player
	 * @param column
	 *          the category
	 * @return boolean
	 */
	public boolean led(Player player, int column)
	{
		boolean led = true;

		// Go through all the players on the team, comparing the specified
		// player's
		// stats with each other player.
		for (int i = 0, n = players.size(); i < n && led; i++)
		{
			Player test = (Player) players.get(i);
			if (player == test)
				continue;
			switch (column)
			{
			case PlayerConst.COLUMN_POINTS:
				if (player.getPoints() < test.getPoints())
					led = false;
				break;
			case PlayerConst.COLUMN_REBOUNDS:
				if (player.getRebounds() < test.getRebounds())
					led = false;
				break;
			case PlayerConst.COLUMN_ASSISTS:
				if (player.getAssists() < test.getAssists())
					led = false;
				break;
			}
		}
		return led;
	}
}

/**
 * This class implements the sorting for the Player Table
 */

class PlayerViewerSorter extends ViewerSorter
{
	private static final int ASCENDING = 0;

	private static final int DESCENDING = 1;

	private int column;

	private int direction;

	/**
	 * Does the sort. If it's a different column from the previous sort, do an
	 * ascending sort. If it's the same column as the last sort, toggle the sort
	 * direction.
	 * 
	 * @param column
	 */
	public void doSort(int column)
	{
		if (column == this.column)
		{
			// Same column as last sort; toggle the direction
			direction = 1 - direction;
		}
		else
		{
			// New column; do an ascending sort
			this.column = column;
			direction = ASCENDING;
		}
	}

	/**
	 * Compares the object for sorting
	 */
	public int compare(Viewer viewer, Object e1, Object e2)
	{
		int rc = 0;
		Player p1 = (Player) e1;
		Player p2 = (Player) e2;

		// Determine which column and do the appropriate sort
		switch (column)
		{
		case PlayerConst.COLUMN_FIRST_NAME:
			rc = collator.compare(p1.getFirstName(), p2.getFirstName());
			break;
		case PlayerConst.COLUMN_LAST_NAME:
			rc = collator.compare(p1.getLastName(), p2.getLastName());
			break;
		case PlayerConst.COLUMN_POINTS:
			rc = p1.getPoints() > p2.getPoints() ? 1 : -1;
			break;
		case PlayerConst.COLUMN_REBOUNDS:
			rc = p1.getRebounds() > p2.getRebounds() ? 1 : -1;
			break;
		case PlayerConst.COLUMN_ASSISTS:
			rc = p1.getAssists() > p2.getAssists() ? 1 : -1;
			break;
		}

		// If descending order, flip the direction
		if (direction == DESCENDING)
			rc = -rc;

		return rc;
	}
}

/**
 * This class contains constants for the PlayerTable application
 */

class PlayerConst
{
	// Column constants
	public static final int COLUMN_FIRST_NAME = 0;

	public static final int COLUMN_LAST_NAME = 1;

	public static final int COLUMN_POINTS = 2;

	public static final int COLUMN_REBOUNDS = 3;

	public static final int COLUMN_ASSISTS = 4;
}

/**
 * This class provides the labels for the person table
 */

class PersonLabelProvider implements ITableLabelProvider
{
	/**
	 * Returns the image
	 * 
	 * @param element
	 *          the element
	 * @param columnIndex
	 *          the column index
	 * @return Image
	 */
	public Image getColumnImage(Object element, int columnIndex)
	{
		return null;
	}

	/**
	 * Returns the column text
	 * 
	 * @param element
	 *          the element
	 * @param columnIndex
	 *          the column index
	 * @return String
	 */
	public String getColumnText(Object element, int columnIndex)
	{
		Person person = (Person) element;
		switch (columnIndex)
		{
		case 0:
			return person.getName();
		case 1:
			return Boolean.toString(person.isMale());
		case 2:
			return AgeRange.INSTANCES[person.getAgeRange().intValue()];
		case 3:
			return person.getShirtColor().toString();
		}
		return null;
	}

	/**
	 * Adds a listener
	 * 
	 * @param listener
	 *          the listener
	 */
	public void addListener(ILabelProviderListener listener)
	{
		// Ignore it
	}

	/**
	 * Disposes any created resources
	 */
	public void dispose()
	{
		// Nothing to dispose
	}

	/**
	 * Returns whether altering this property on this element will affect the
	 * label
	 * 
	 * @param element
	 *          the element
	 * @param property
	 *          the property
	 * @return boolean
	 */
	public boolean isLabelProperty(Object element, String property)
	{
		return false;
	}

	/**
	 * Removes a listener
	 * 
	 * @param listener
	 *          the listener
	 */
	public void removeListener(ILabelProviderListener listener)
	{
		// Ignore
	}
}

/**
 * This class represents a person
 */

class Person
{
	private String name;

	private boolean male;

	private Integer ageRange;

	private RGB shirtColor;

	/**
	 * @return Returns the ageRange.
	 */
	public Integer getAgeRange()
	{
		return ageRange;
	}

	/**
	 * @param ageRange
	 *          The ageRange to set.
	 */
	public void setAgeRange(Integer ageRange)
	{
		this.ageRange = ageRange;
	}

	/**
	 * @return Returns the male.
	 */
	public boolean isMale()
	{
		return male;
	}

	/**
	 * @param male
	 *          The male to set.
	 */
	public void setMale(boolean male)
	{
		this.male = male;
	}

	/**
	 * @return Returns the name.
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * @param name
	 *          The name to set.
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 * @return Returns the shirtColor.
	 */
	public RGB getShirtColor()
	{
		return shirtColor;
	}

	/**
	 * @param shirtColor
	 *          The shirtColor to set.
	 */
	public void setShirtColor(RGB shirtColor)
	{
		this.shirtColor = shirtColor;
	}
}

/**
 * This class encapsulates age ranges
 */

class AgeRange
{
	public static final String NONE = "";

	public static final String BABY = "0 - 3";

	public static final String TODDLER = "4 - 7";

	public static final String CHILD = "8 - 12";

	public static final String TEENAGER = "13 - 19";

	public static final String ADULT = "20 - ?";

	public static final String[] INSTANCES = { NONE, BABY, TODDLER, CHILD, TEENAGER, ADULT };
}

/**
 * This class provides the labels for PlayerTable
 */

class PlayerLabelProvider implements ITableLabelProvider
{
	// Image to display if the player led his team
	private Image ball;

	// Constructs a PlayerLabelProvider
	public PlayerLabelProvider()
	{
		// Create the image
		try
		{
			ball = new Image(null, new FileInputStream("images/ball.png"));
		}
		catch (FileNotFoundException e)
		{
			// Swallow it
		}
	}

	/**
	 * Gets the image for the specified column
	 * 
	 * @param arg0
	 *          the player
	 * @param arg1
	 *          the column
	 * @return Image
	 */
	public Image getColumnImage(Object arg0, int arg1)
	{
		Player player = (Player) arg0;
		Image image = null;
		switch (arg1)
		{
		// A player can't lead team in first name or last name
		case PlayerConst.COLUMN_POINTS:
		case PlayerConst.COLUMN_REBOUNDS:
		case PlayerConst.COLUMN_ASSISTS:
			if (player.ledTeam(arg1))
				// Set the image
				image = ball;
			break;
		}
		return image;
	}

	/**
	 * Gets the text for the specified column
	 * 
	 * @param arg0
	 *          the player
	 * @param arg1
	 *          the column
	 * @return String
	 */
	public String getColumnText(Object arg0, int arg1)
	{
		Player player = (Player) arg0;
		String text = "";
		switch (arg1)
		{
		case PlayerConst.COLUMN_FIRST_NAME:
			text = player.getFirstName();
			break;
		case PlayerConst.COLUMN_LAST_NAME:
			text = player.getLastName();
			break;
		case PlayerConst.COLUMN_POINTS:
			text = String.valueOf(player.getPoints());
			break;
		case PlayerConst.COLUMN_REBOUNDS:
			text = String.valueOf(player.getRebounds());
			break;
		case PlayerConst.COLUMN_ASSISTS:
			text = String.valueOf(player.getAssists());
			break;
		}
		return text;
	}

	/**
	 * Adds a listener
	 * 
	 * @param arg0
	 *          the listener
	 */
	public void addListener(ILabelProviderListener arg0)
	{
		// Throw it away
	}

	/**
	 * Dispose any created resources
	 */
	public void dispose()
	{
		// Dispose the image
		if (ball != null)
			ball.dispose();
	}

	/**
	 * Returns whether the specified property, if changed, would affect the label
	 * 
	 * @param arg0
	 *          the player
	 * @param arg1
	 *          the property
	 * @return boolean
	 */
	public boolean isLabelProperty(Object arg0, String arg1)
	{
		return false;
	}

	/**
	 * Removes the specified listener
	 * 
	 * @param arg0
	 *          the listener
	 */
	public void removeListener(ILabelProviderListener arg0)
	{
		// Do nothing
	}
}

/**
 * This class provides the content for the table
 */

class PlayerContentProvider implements IStructuredContentProvider
{

	/**
	 * Gets the elements for the table
	 * 
	 * @param arg0
	 *          the model
	 * @return Object[]
	 */
	public Object[] getElements(Object arg0)
	{
		// Returns all the players in the specified team
		return ((Team) arg0).getPlayers().toArray();
	}

	/**
	 * Disposes any resources
	 */
	public void dispose()
	{
		// We don't create any resources, so we don't dispose any
	}

	/**
	 * Called when the input changes
	 * 
	 * @param arg0
	 *          the parent viewer
	 * @param arg1
	 *          the old input
	 * @param arg2
	 *          the new input
	 */
	public void inputChanged(Viewer arg0, Object arg1, Object arg2)
	{
		// Nothing to do
	}
}