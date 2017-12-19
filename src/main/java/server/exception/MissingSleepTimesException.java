package server.exception;

public class MissingSleepTimesException extends EventTaskException {
	private static final long serialVersionUID = -6524219031864116156L;

	public MissingSleepTimesException(Long userId) {
		super("User id=" + userId + ": no sleep times found in database");
	}
}
