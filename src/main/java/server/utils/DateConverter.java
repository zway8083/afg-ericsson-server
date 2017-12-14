package server.utils;

import java.sql.Date;
import java.sql.Time;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class DateConverter {
	static public DateTimeFormatter formatter = DateTimeFormat.forPattern("dd/MM/yyyy");
	static private DateTimeFormatter timeFormatter = DateTimeFormat.forPattern("HH:mm");
	
	static public Date toSQLDate(String date) throws IllegalArgumentException {
		return new Date(toDateTime(date).getMillis());
	}
	
	static public DateTime toDateTime(String date) throws IllegalArgumentException {
		return formatter.parseDateTime(date);
	}
	
	static public String toFormatString(DateTime dateTime) {
		return formatter.print(dateTime);
	}
	
	static public String toFormatTime(Time time) {
		if (time == null)
			return null;
		return timeFormatter.print(new DateTime(time.getTime()));
	}
	
	static public Time toTime(String time) {
		return new Time(timeFormatter.parseDateTime(time).getMillis());
	}
}
