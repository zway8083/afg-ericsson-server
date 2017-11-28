package server.utils;

import java.sql.Date;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class DateConverter {
	static public Date toSQLDate(String date) throws IllegalArgumentException {
		DateTimeFormatter formatter = DateTimeFormat.forPattern("dd/MM/yyyy");
		return new Date(formatter.parseDateTime(date).getMillis());
	}
}
