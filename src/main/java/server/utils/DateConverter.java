package server.utils;

import java.sql.Date;
import java.sql.Time;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class DateConverter {
	static private final DateTimeFormatter DATE_FORMATTER = DateTimeFormat.forPattern("dd/MM/yyyy");
	static private final DateTimeFormatter TIME_FORMATTER = DateTimeFormat.forPattern("HH:mm");
	
	static public Date toSQLDate(String date) throws IllegalArgumentException {
		return new Date(toDateTime(date).getMillis());
	}
	
	static public DateTime toDateTime(String date) throws IllegalArgumentException {
		return DATE_FORMATTER.parseDateTime(date);
	}

	static public String toFormatString(DateTime dateTime) {
		return DATE_FORMATTER.print(dateTime);
	}
	
	static public String toFormatString(Date date) {
		return toFormatString(new DateTime(date));
	}
	
	static public String toFormatTime(Time time) {
		if (time == null)
			return null;
		return TIME_FORMATTER.print(new DateTime(time.getTime()));
	}
	
	static public Time toTime(String time) {
		return new Time(TIME_FORMATTER.parseDateTime(time).getMillis());
	}
}
