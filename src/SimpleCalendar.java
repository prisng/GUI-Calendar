/**
 * Tester for the calendar.
 * @author Priscilla Ng
 *
 */
public class SimpleCalendar {
	public static void main(String[] args) {
		CalendarModel m = new CalendarModel();
		CalendarView v = new CalendarView(m);
		m.attach(v);
	}
}
