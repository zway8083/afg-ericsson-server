package server.task;

public abstract class EmailRunnable implements Runnable {
	Email email;
	
	public EmailRunnable(String id, String password, String host, boolean bodyHTML) {
		super();
		email = new Email(id, password, host, bodyHTML);
	}
}