package irc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import logger.Logger;

public class MessageHandler extends Thread {

	private IRCClient client;
	private BufferedReader reader;
	private Logger logger;
	private ArrayList<IRCEventHandler> handlers;

	public MessageHandler(IRCClient client, InputStream inputStream) {
		this.client = client;
		this.reader = new BufferedReader(new InputStreamReader(inputStream));
		this.handlers = new ArrayList<IRCEventHandler>();
		this.logger = Logger.getLogger();
		addHandler(new PingPongHandler());
	}

	public void addHandler(IRCEventHandler handler) {
		synchronized (handlers) {
			handlers.add(handler);
		}
	}

	@Override
	public void run() {
		try {
			String line;
			while ((line = reader.readLine()) != null) {
				boolean handled = false;
				synchronized (handlers) {
					for (IRCEventHandler handler : handlers) {
						// Check if the handler matches and if it does then process it
						if ((handled |= handler.match(line)) && handler.handle(line, client)) {
							// If the handler says that nothing else should process it, await next message
							break;
						}
					}
				}
				if (!handled) {
					logger.println(this, "Message not handled --");
					logger.println(this, line);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
