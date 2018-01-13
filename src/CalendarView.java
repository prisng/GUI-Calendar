import java.text.ParseException;
import java.util.regex.Pattern;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.Calendar;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;

import javax.swing.Box;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * View/Controller of the calendar.
 * Controller takes user input (e.g. creating events, scrolling through the calendar, etc.)
 * and updates the calendar model. View outputs the calendar model with changes made 
 * accordingly.
 * 
 * @author Priscilla Ng
 * 
 */
public class CalendarView implements ChangeListener {
	private CalendarModel model;
	private int daysInMonth;
	private String currentMonth;
	private int currentMonthInt;
	private String currentYear;
	private Dimension d = new Dimension(75, 50);		// to change button size in the future
	private ArrayList<JButton> dayButtons;				// buttons for each day of the month
	private JPanel dayPanel = new JPanel();				// panel to hold the day buttons
	private ArrayList<JTextField> textFields = new ArrayList<>(); // for create button fields
	private JLabel monthYearLabel;
	private JTextArea eventList;
	private JFrame calendarFrame;
	private JPanel calendarPanel;

	/**
	 * Constructs the GUI View of the calendar model.
	 * @param model		the CalendarModel to be constructed and represented
	 */
	public CalendarView(CalendarModel model) {
		// Initialize variables from the calendar model
		this.model = model;
		this.currentMonthInt = model.getMonthInt();
		this.daysInMonth = model.getDaysInMonth();
		
		// Create the frame for the entire calendar
		calendarFrame = new JFrame("My Calendar");
		calendarFrame.setLayout(new FlowLayout());
		calendarFrame.setSize(900, 450);
		
		// Load events onto calendar
		try {
			model.load();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		// Outputs list of events in the console
		System.out.println(model.eventList());

		// Create the label for the month and year
		currentMonth = model.getMonth();
		currentYear = model.getYear();
		monthYearLabel = new JLabel(currentMonth + " " + currentYear);
		monthYearLabel.setFont(new Font("Helvetica", Font.BOLD, 25));
		monthYearLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		// Create a label for the days of the week
		JLabel daysOfWeekLabel = new JLabel(model.getDaysOfWeek());
		daysOfWeekLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		daysOfWeekLabel.setHorizontalAlignment(SwingConstants.LEFT);
		
		// Create a text area for displaying events on that day
		eventList = new JTextArea();
		eventList.setEditable(false);
		eventList.setBorder(BorderFactory.createLineBorder(Color.GRAY));
		eventList.setPreferredSize(new Dimension(300, 150));
		eventList.setLineWrap(true);
		showEvents();	// display the events on the current day
		
		// Panel for the day buttons, back/forward buttons, create event button
		dayPanel.setLayout(new GridLayout(0, 7, 3, 3)); // 7 columns, 3 pixel gap
		dayPanel.setBorder(BorderFactory.createEtchedBorder());
		
		// Initialize an array list for holding buttons for each day
		dayButtons = new ArrayList<JButton>();	// create an array list of buttons
		createDayButtons();		// create the day buttons for the current month
		addDayButtons();		// add buttons to the day panel
		selectDayButton(model.getDayInt());		// select and highlight today
		
		// Create the calendar buttons
		JPanel calendarButtons = new JPanel();
		calendarButtons.setLayout(new BoxLayout(calendarButtons, BoxLayout.X_AXIS));
		JButton back = new JButton("<");
		JButton forward = new JButton(">");
		JButton create = new JButton("Create");
		back.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				model.previousDay();
			}
		});
		forward.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				model.nextDay();
			}
		});
		create.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				createEvent();
			}
		});
		JButton quit = new JButton("Quit");
		quit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				model.quit();
				// Create a JDialog for quitting
				JDialog quit = new JDialog();
				quit.setLayout(new BorderLayout());
				quit.setTitle("Quit");
				JLabel success = new JLabel("Events saved onto calendar.");
				JButton ok = new JButton("OK");
				ok.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						quit.dispose();
						System.exit(0);
					}
				});
				quit.add(success, BorderLayout.CENTER);
				quit.add(ok, BorderLayout.SOUTH);
				quit.pack();
				quit.setVisible(true);
			}
		});
		calendarButtons.add(back);
		calendarButtons.add(forward);
		calendarButtons.add(create);
		calendarButtons.add(Box.createRigidArea(new Dimension(175, 0)));
		calendarButtons.add(quit);
		
		// Create a panel that holds the month/year/days of week labels & calendar buttons
		calendarPanel = new JPanel();
		calendarPanel.setLayout(new BoxLayout(calendarPanel, BoxLayout.Y_AXIS));
		calendarPanel.add(monthYearLabel);
		calendarPanel.add(Box.createRigidArea(new Dimension(0, 20)));
		calendarPanel.add(calendarButtons);
		calendarPanel.add(Box.createRigidArea(new Dimension(0, 10)));
		calendarPanel.add(daysOfWeekLabel);
		calendarPanel.add(dayPanel);

		// Add all components to the calendar frame
		calendarFrame.add(calendarPanel);
		calendarFrame.add(eventList);
		calendarFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		calendarFrame.setVisible(true);
	}
	
    /**
     * Creates day buttons for each day of the month.
     */
	public void createDayButtons() {
		// Create empty buttons to fill gaps before starting day
		for (int i = 0; i < model.getStartingDay(); i++) {
			JButton blankButton = new JButton();
			blankButton.setBorder(BorderFactory.createRaisedBevelBorder());
			dayButtons.add(blankButton);
		}
		for (int i = 0; i < daysInMonth; i++) {
			JButton dayButton = new JButton(Integer.toString(i + 1) + " ");

			// Look of button
			dayButton.setHorizontalAlignment(SwingConstants.LEFT);	// align number top left
			dayButton.setVerticalAlignment(SwingConstants.TOP);
			dayButton.setPreferredSize(d);
			dayButton.setFont(new Font("Helvetica", Font.PLAIN, 12));
			dayButton.setOpaque(true);
			dayButton.setBackground(Color.WHITE);
			dayButton.setBorder(BorderFactory.createRaisedBevelBorder());
			
			// Action listener when selecting button
			dayButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					// show the events on that date in the text field
					// highlight the daybutton
					// set the date of the calendar to the selected day
					model.setDay(Integer.valueOf(dayButton.getText().trim()));
					deselectDayButtons();
					selectDayButton(Integer.valueOf(dayButton.getText().trim()));
				}
			});
			dayButtons.add(dayButton);
		}
	}
	
	/**
	 * Adds the buttons in the dayButtons array list onto the dayPanel.
	 */
	public void addDayButtons() {
		for (JButton b : dayButtons) {
			dayPanel.add(b);
		}
	}
	
	/**
	 * JDialog for creating an event on this calendar. Invoked when "Create" button
	 * is clicked.
	 */
	public void createEvent() {
		// Create an event dialog
		JDialog eventDialog = new JDialog();
		eventDialog.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		eventDialog.setSize(new Dimension(500, 100));
		eventDialog.setTitle("Create Event");
		
		JTextField title = new JTextField("Untitled event");
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 3;
		c.ipady = 5;
		c.ipadx = 100;
		eventDialog.add(title, c);
		
		JTextField date = new JTextField(model.getDayOfWeek() + " " + model.getDate());
		c.gridwidth = 1;
		c.gridy = 1;
		c.ipadx = 10;
		eventDialog.add(date, c);
		date.setEditable(false);	// user doesn't have to enter date
		
		JTextField startTime = new JTextField("Start time");
		c.gridx = 1;
		c.gridy = 1;
		c.ipadx = 20;
		eventDialog.add(startTime, c);
		
		JTextField endTime = new JTextField("End time");
		c.gridx = 2;
		c.gridy = 1;
		c.anchor = GridBagConstraints.LINE_END;
		eventDialog.add(endTime, c);

		JButton cancelButton = new JButton("Cancel");
		c.gridx = 1;
		c.gridy = 3;
		c.ipady = 1;
		c.ipadx = 1;
		eventDialog.add(cancelButton, c);
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				eventDialog.dispose();
			}
		});
		
		JButton createButton = new JButton("Create");
		c.gridx = 2;
		c.gridy = 3;
		c.ipady = 1;
		c.ipadx = 1;
		eventDialog.add(createButton, c);
		createButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
					String date = model.getDate();
					Event ev = new Event(title.getText(), model.getDate(), startTime.getText(), endTime.getText());
					Pattern timePattern = Pattern.compile("\\d{2}:\\d{2}");
					boolean startTimeMatch = timePattern.matcher(startTime.getText()).matches();
					boolean endTimeMatch = timePattern.matcher(endTime.getText()).matches();
					// If the event time is conflicting, open conflict dialog
					if (model.checkConflict(date, ev) == true) {
						JDialog conflict = new JDialog();
						conflict.setLayout(new BorderLayout());
						JLabel conflictMessage = new JLabel("The event you are trying to create "
								+ "is conflicting with an existing event.");
						JButton backButton = new JButton("Back");
						backButton.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent e) {
								conflict.dispose();
							}
						});
						conflict.setTitle("Error");
						conflict.add(conflictMessage, BorderLayout.NORTH);
						conflict.add(backButton, BorderLayout.SOUTH);
						conflict.pack();
						conflict.setVisible(true);
					}
					// If start time or end time aren't entered properly
					else if (startTimeMatch == false || endTimeMatch == false) {
						JDialog formatError = new JDialog();
						formatError.setLayout(new BorderLayout());
						JLabel errorMessage = new JLabel("Please enter a valid time in "
								+ "HH:mm format");
						JButton backButton2 = new JButton("Back");
						backButton2.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent e) {
								formatError.dispose();
							}
						});
						formatError.setTitle("Error");
						formatError.add(errorMessage, BorderLayout.NORTH);
						formatError.add(backButton2, BorderLayout.SOUTH);
						formatError.pack();
						formatError.setVisible(true);
					}
					else {
						// If there's no conflict, create the event
						model.create(date, ev);
						eventDialog.dispose();
					}
			}
		});
		
		// Add to textfield array list for making default text disappear on click
		textFields.add(title);
		textFields.add(startTime);
		textFields.add(endTime);
		
		// Making default text disappear on click
		MyMouseHandler m = new MyMouseHandler();
		title.addMouseListener(m);
		startTime.addMouseListener(m);
		endTime.addMouseListener(m);
		
		eventDialog.pack();
		eventDialog.setVisible(true);
	}
	
	/**
	 * Class for clearing the JTextFields in the JDialog for creating events.
	 * @author prisng
	 *
	 */
    private class MyMouseHandler extends MouseAdapter {
    	/**
    	 * Sets the text of the JTextField to an empty string when clicked on.
    	 */
        public void mouseClicked(MouseEvent e) {
        	JTextField f = (JTextField) e.getSource();
            for (JTextField textField : textFields) {
                if (textField == f) {
                	textField.setText("");
                }
            }
        }
    }
	
	/**
	 * Displays the events of the currently selected date of the calendar model
	 * in the event list text field.
	 */
	public void showEvents() {
		eventList.setText(model.getEvents());
	}
	
	/**
	 * Selects and highlights a day button.
	 * @param date	the date of the button to be selected and highlighted
	 */
	public void selectDayButton(int date) {
		Border buttonBorder = BorderFactory.createBevelBorder(1);
		for (JButton b : dayButtons) {
			if (b.getText().trim().equals(String.valueOf(date))) {
				b.setBorder(buttonBorder);
			}
		}
	}
	
	/**
	 * Deselects all of the buttons on the calendar.
	 */
	public void deselectDayButtons() {
		Border buttonBorderDeselect = BorderFactory.createRaisedBevelBorder(); // repeated code?
		for (int i = 0; i < dayButtons.size(); i++) {
			JButton button = dayButtons.get(i);
			// for all the buttons, set the border to the original border
			button.setBorder(buttonBorderDeselect);
		}
	}

	/**
	 * (Method overridden from implementing ChangeListener)
	 * Updates the calendar view when information from the calendar model gets changed.
	 */
	public void stateChanged(ChangeEvent e) {
		int modelMonth = model.getCalendar().get(Calendar.MONTH);
		// Change month views if necessary
		if (modelMonth != currentMonthInt) {
			currentMonthInt = modelMonth;
			daysInMonth = model.getDaysInMonth();
			dayPanel.removeAll();
			dayButtons.clear();
			createDayButtons();
			addDayButtons();
			monthYearLabel.setText(model.getMonth() + " " + model.getYear());
		}
		deselectDayButtons();				// Deselects the previously selected day
		selectDayButton(model.getDayInt());	// Highlights the currently selected day
		showEvents();						// Show events on currently selected day
	}
	
}	// end of CalendarView class
