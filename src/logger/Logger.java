package logger;

public class Logger {
	private static Logger logger;

	public static Logger getLogger() {
		if (logger == null) {
			logger = new Logger();
		}
		return logger;
	}

	private Logger() {
	}

	private void write(Object object, String data) {
		System.out.print("["+object.getClass().getCanonicalName()+"]: " + data);
	}

	public void print(Object object, String data) {
		write(object, data);
	}

	public void println(Object object, String data) {
		write(object, data + "\n");
	}
}
