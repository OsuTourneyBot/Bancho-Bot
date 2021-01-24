package test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Queue;

/**
 * This class is used for testing the IRC connection
 */
public class SimpleIRCServer extends Thread {
	private ServerSocket server;
	private Socket connection;
	private Queue<String> messages = new LinkedList<String>();
	private BufferedWriter writer;

	public SimpleIRCServer(int port) throws IOException {
		server = new ServerSocket(port);

	}

	@Override
	public void run() {
		try {
			connection = server.accept();
			writer = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));
			listen();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void listen() throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		Thread t = new Thread(() -> {
			String line;
			try {
				while ((line = reader.readLine()) != null) {
					synchronized (messages) {
						messages.add(line);
						messages.notify();
					}
				}
			} catch (Exception e) {
			} finally {
				try {
					reader.close();
				} catch (Exception e) {
				}
			}
		});
		t.start();
	}

	public void write(String message) {
		try {
			writer.write(message);
			writer.flush();
		} catch (Exception e) {
		}
	}

	public String getMessage() {
		if (messages.isEmpty()) {
			try {
				synchronized (messages) {
					messages.wait();
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		synchronized (messages) {
			return messages.poll();
		}
	}

	public void close() {
		try {
			server.close();
			connection.close();
		} catch (Exception e) {
		}
	}

}
