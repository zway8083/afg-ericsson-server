package server.exception;

import org.joda.time.DateTime;

import server.utils.DateConverter;

public class NoMotionException extends EventTaskException {
	private static final long serialVersionUID = -347090372733523826L;

	public NoMotionException(Long userId, DateTime start, DateTime end) {
		super("User id=" + userId + ": no motion events found between " + DateConverter.toFormatString(start) + " and "
				+ DateConverter.toFormatString(end));
	}
}
