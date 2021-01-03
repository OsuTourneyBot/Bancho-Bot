package irc;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.LinkedList;
import java.util.Queue;

import logger.Logger;

public class RateLimitedPrintStream {

	private static final int DELAY = 500;

	private Queue<String> buffer;
	private PrintStream printStream;
	private long lastWrite = System.currentTimeMillis() - DELAY;
	private Logger logger;

	private boolean waitingToWrite = false;

	public RateLimitedPrintStream(OutputStream os) {
		buffer = new LinkedList<String>();
		logger = Logger.getLogger();
		printStream = new PrintStream(os, false);
	}

	public void write(String data) {
		buffer.add(data);
		logger.println(this,data);
	}

	public void flush() {
		if (waitingToWrite || buffer.isEmpty()) {
			return;
		}
		Thread t = new Thread(() -> {
			waitingToWrite = true;
			long currentTime = System.currentTimeMillis();
			if (currentTime - lastWrite < DELAY) {
				try {
					Thread.sleep(DELAY - currentTime + lastWrite);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			while (!buffer.isEmpty()) {
				printStream.println(buffer.poll());
			}
			printStream.flush();
			waitingToWrite = false;
			lastWrite = System.currentTimeMillis();
		});
		t.start();
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
