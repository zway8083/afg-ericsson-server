package server.utils;

import java.sql.Date;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class DateConverter {
	static public DateTimeFormatter formatter = DateTimeFormat.forPattern("dd/MM/yyyy");
	
	static public Date toSQLDate(String date) throws IllegalArgumentException {
		return new Date(toDateTime(date).getMillis());
	}
	
	static public DateTime toDateTime(String date) throws IllegalArgumentException {
		return formatter.parseDateTime(date);
	}
	
	static public String toFormatString(DateTime dateTime) {
		return formatter.print(dateTime);
	}
}
