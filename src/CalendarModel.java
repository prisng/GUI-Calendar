import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

enum MONTHS {
	January, February, March, April, May, June, July, August, September, October, November, December;
}

enum DAYS {
	Sun, Mon, Tue, Wed, Thu, Fri, Sat;
}

/**
 * A calendar model that holds events using a tree map with a date of the event as the key and
 * an array list of events as the value. Functionalities include the creation and deletion
 * of events, viewing of events on specific dates, a listing of all events of the calendar
 * and the loading/creation of an events.txt file that holds a list of all events.
 * 
 * @author Priscilla Ng
 *
 */
public class CalendarModel {
	private Map<String, ArrayList<Event>> dateToEvents;	// data structure holding events
	private int daysInMonth;
	private Calendar c = new GregorianCalendar();
	private MONTHS[] arrayOfMonths = MONTHS.values();
	private DAYS[] arrayOfDays = DAYS.values();
	private ArrayList<ChangeListener> listeners = new ArrayList<>(); // data structure for Views
	private DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
	private static final String EVENTFILE = "events.txt";
	
	/**
	 * Constructs a calendar model.
	 */
	public CalendarModel() {
		this.dateToEvents = new TreeMap<String, ArrayList<Event>>();
		this.daysInMonth = c.getActualMaximum(Calendar.DAY_OF_MONTH);
	}

	/**
	 * Attaches a ChangeListener to this calendar model. (CalendarView)
	 * @param l		the ChangeListener to be attached
	 */
	public void attach(ChangeListener l) {
		listeners.add(l);
	}

	/**
	 * Notifies the View (observer) of the changes in the calendar model.
	 */
	public void notifyView() {
		for (ChangeListener l : listeners) {
			l.stateChanged(new ChangeEvent(this));
		}
	}
	
	/**
	 * Gets the Calendar of this calendar model.
	 * @return	the Calendar of this calendar model
	 */
	public Calendar getCalendar() {
		return this.c;
	}
	
	/**
	 * Gets the year of this calendar model.
	 * @return	the year of this calendar model
	 */
	public String getYear() {
		int thisYear = c.get(Calendar.YEAR);
		return Integer.toString(thisYear);
	}
	
	/**
	 * Gets the month of this calendar model.
	 * @return	the month of this calendar model
	 */
	public String getMonth() {
		int thisMonthInt = c.get(Calendar.MONTH);
		String thisMonth = arrayOfMonths[thisMonthInt].toString();
		return thisMonth;
	}
	
	/**
	 * Gets the month in integer form of this calendar model.
	 * @return	the month in integer form
	 */
	public int getMonthInt() {
		return c.get(Calendar.MONTH);
	}
	
	/**
	 * Gets the day in integer form of this calendar model.
	 * @return	the day in integer form
	 */
	public int getDayInt() {
		return c.get(Calendar.DATE);
	}
	
	/**
	 * Sets the day of this calendar model.
	 * @param day	the day to be set
	 */
	public void setDay(int day) {
		c.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH), day);
		// Notify View of the changes
		notifyView();
	}
	
	/**
	 * Gets the date of the calendar in MM/dd/yyyy format.
	 * @return	the date of the calendar in MM/dd/yyyy format
	 */
	public String getDate() {
		Date date = c.getTime();
		return dateFormat.format(date);
	}
	
	/**
	 * Gets the number of days in the current month.
	 * @return	the number of days in the current month
	 */
	public int getDaysInMonth() {
		return daysInMonth;
	}
	
	/**
	 * Sets the number of days in the current month
	 * @param numberOfDays	the number of days to be set
	 */
	public void setDaysInMonth(int numberOfDays) {
		this.daysInMonth = numberOfDays;
	}
	
	/**
	 * Gets the days of the week.
	 * @return	the days of the week
	 */
	public String getDaysOfWeek() {
		String s = "";
		for (DAYS d : arrayOfDays) {
			s = s + "\t\t\t\t\t\t\t\t\t\t\t\t\t\t" + d;
		}
		return s;
	}
	
	/**
	 * Gets the starting day of the current month of this calendar model.
	 * @return	the starting day of the calendar model
	 */
	public int getStartingDay() {
		// Make a temporary calendar to import current year/month and set day to 1
		GregorianCalendar temp = new GregorianCalendar(c.get(Calendar.YEAR), c.get(Calendar.MONTH), 1);
		// Get the day of the week (which is the first day of the month)
		int startingDay = temp.get(Calendar.DAY_OF_WEEK) - 1;
		return startingDay;
	}
	
	/**
	 * Moves to the previous day in the calendar model.
	 */
	public void previousDay() {
		c.add(Calendar.DAY_OF_MONTH, -1);
		setDaysInMonth(c.getActualMaximum(Calendar.DAY_OF_MONTH));
		
		// Outputs the month, day, and year in the console
		int thisDay = c.get(Calendar.DAY_OF_MONTH);
		Integer.toString(thisDay);
		System.out.println("Month: \t" + getMonth());
		System.out.println("Day: \t" + thisDay);
		System.out.println("Year: \t" + getYear());

		// Notify View of the changes
		notifyView();
	}
	
	/**
	 * Moves to the next day in the calendar model.
	 */
	public void nextDay() {
		c.add(Calendar.DAY_OF_MONTH, 1);
		setDaysInMonth(c.getActualMaximum(Calendar.DAY_OF_MONTH));
		
		// Outputs the month, day, and year in the console
		int thisDay = c.get(Calendar.DAY_OF_MONTH);
		Integer.toString(thisDay);
		System.out.println("Month: \t" + getMonth());
		System.out.println("Day: \t" + thisDay);
		System.out.println("Year: \t" + getYear());

		// Notify View of the changes
		notifyView();
	}
	
	/**
	 * Gets the day of the week.
	 * @return	the day of the week
	 * 			(Sun, Mon, Tues, etc.)
	 */
	public String getDayOfWeek() {
		// Day enums as array
		DAYS[] arrayOfDays = DAYS.values();

		return arrayOfDays[c.get(Calendar.DAY_OF_WEEK) - 1] + "";
	}
	
	/**
	 * Loads a text file events.txt to populate calendar with events (if any).
	 * @throws IOException		if stream to events.txt cannot be written to
	 * @throws ParseException	if the date of events are not written in the correct format MM/dd/yyyy
	 */
	public void load() throws IOException, ParseException {
		BufferedReader br = new BufferedReader(new FileReader(EVENTFILE));

			String line = "";
			// For each line in the text file, create a new event
			while ((line = br.readLine()) != null) {
				String inputDate = line.substring(0, 10);
				String startTime = line.substring(11, 16);
				String endTime = line.substring(19, 24);
				String title = line.substring(25);
				
				// Create an event based on that line
				Event e = new Event(title, inputDate, startTime, endTime);
				create(inputDate, e);
			}
		System.out.println("Events loaded onto the calendar.");
		br.close();
	}
	
	/**
	 * Creates an event and adds it to the tree map of events.
	 * @param d		the date of the event to create
	 * @param e		the event to create
	 */
	public void create(String d, Event e) {
		if (checkConflict(d, e) == false) {
			// Create a non-conflicting event that day
			if (dateToEvents.containsKey(d)) {
				ArrayList<Event> events = dateToEvents.get(d);
				events.add(e);
				
			}
			// Otherwise, create an event that day
			else {
				ArrayList<Event> events = new ArrayList<Event>();
				events.add(e);
				dateToEvents.put(d, events);
			}
		}
		
		// Notify View of the changes
		notifyView();
	}
	
	/**
	 * Checks for conflicting events in the tree map of events.
	 * @param d		the date of the event to check
	 * @param e		the event to check
	 * @return
	 */
	public boolean checkConflict(String d, Event e) {
		boolean conflict = false;
		// If there is an event that day, check if there are conflicting events existing
		if (dateToEvents.containsKey(d)) {
			ArrayList<Event> events = dateToEvents.get(d);
			for (int i = 0; i < events.size(); i++) {
				Event e2 = dateToEvents.get(d).get(i);
				// Check for time conflict
				if (e.getStartTimeInt() <= e2.getEndTimeInt() && e2.getStartTimeInt() <= e.getEndTimeInt()) {
					System.out.println("The event you are trying to create is conflicting with an existing event.");
					System.out.println("Cannot create overlapping event. Redirecting to main menu.");
					conflict = true;
				}
			}
		}
		return conflict;
	}
	
	/**
	 * Gets the events on this calendar model's current date.
	 * @return	the events on this calendar model's current date
	 * 			format: MM/dd/yyyy
	 * 					start time - end time	title of event 1
	 * 					start time - end time	title of event 2
	 * 					etc.
	 */
	public String getEvents() {
		StringBuilder events = new StringBuilder();
		if (dateToEvents.containsKey(this.getDate())) {
			for (Event e : dateToEvents.get(this.getDate())) {
				events.append(e.getStartTime() + " - " + e.getEndTime() + "\t" + e.getTitle());
				events.append("\n");
			}
		}
		return this.getDate() + "\n" + events.toString();
	}
	
	/**
	 * Outputs the events on this calendar model in the console.
	 * @return	the list of events on this calendar model
	 */
	public String eventList() {
		String list = "";

		System.out.println("List of all scheduled events: ");
		
		if (dateToEvents.isEmpty()) {
			System.out.println("\tThere are no scheduled events to show.");
		}
		
		// Iterate through event tree map to print events
		for (Map.Entry<String, ArrayList<Event>> entry : dateToEvents.entrySet()) {
			ArrayList<Event> eventList = entry.getValue();
			for (int i = 0; i < eventList.size(); i++) {
				list = list + eventList.get(i).printEvent() + "\n";
			}
		}
		return list;
	}
	
	/**
	 * Exits the calendar application and saves all created events into events.txt.
	 */
	public void quit() {
		// Formatting date
		String pattern = "MM/dd/yyyy";
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
		
		// BufferedWriter used to write content of event map into text file
		BufferedWriter writer = null;
		
		try {
		    writer = new BufferedWriter(new FileWriter(EVENTFILE));
		    
		    if (!dateToEvents.isEmpty()) {
				for (Map.Entry<String, ArrayList<Event>> entry : dateToEvents.entrySet()) {
					String key = entry.getKey();
					ArrayList<Event> eventList = entry.getValue();
					for (int i = 0; i < eventList.size(); i++) {
						writer.write(eventList.get(i).printEvent() + "\n");
					}
				}
		    }
		    
		} catch (IOException e) {
			e.printStackTrace();
		}
		finally {
			try {
				if (writer != null) {
					writer.close();
					System.out.println("File successfully created. (events.txt)");
				}
		    } catch (IOException e) {
		    	e.printStackTrace();
		    }
		}
		
	}	// end of quit method
	
}	// end of CalendarModel class
