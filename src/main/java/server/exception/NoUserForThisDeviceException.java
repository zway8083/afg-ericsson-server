package server.exception;

public class NoUserForThisDeviceException extends EventTaskException {
    //private static final long serialVersionUID = 1L;
    public NoUserForThisDeviceException(Long userId) { super("le user associé à un id " + userId); }
}
