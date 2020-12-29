package irc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import irc.handlers.IRCCommandGroup;
import irc.handlers.IRCEventGroup;
import irc.handlers.IRCEventHandler;
import irc.handlers.IgnoreHandler;
import irc.handlers.JoinChannelHandler;
import irc.handlers.MOTDHandler;
import irc.handlers.PingPongHandler;
import logger.Logger;

public class MessageHandler extends Thread {

	private IRCClient client;
	private BufferedReader reader;
	private Logger logger;
	private ArrayList<IRCEventHandler> handlers;
	private IRCEventGroup eventGroup;

	public MessageHandler(IRCClient client, InputStream inputStream) {
		this.client = client;
		this.reader = new BufferedReader(new InputStreamReader(inputStream));
		this.handlers = new ArrayList<IRCEventHandler>();
		this.logger = Logger.getLogger();

		IRCCommandGroup commandGroup = new IRCCommandGroup();

		handlers.add(commandGroup);
		handlers.add(new PingPongHandler());
		handlers.add(new MOTDHandler());
		handlers.add(new IgnoreHandler());

		// Accept everything since this is the base group.
		eventGroup = new IRCEventGroup(handlers) {
			@Override
			public String[] match(String[] data) {
				return data;
			}
		};
	}

	public void addHandler(IRCEventHandler handler) {
		synchronized (handlers) {
			eventGroup.addHandler(handler);
		}
	}

	@Override
	public void run() {
		try {
			String line;
			while ((line = reader.readLine()) != null) {
				boolean handled;
				String[] lineArr = new String[] { line };
				synchronized (handlers) {
					handled = eventGroup.handle(lineArr, client);
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
