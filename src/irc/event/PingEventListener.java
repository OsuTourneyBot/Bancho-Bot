package irc.event;

import irc.IRCClient;

public class PingEventListener implements EventListener {

	public IRCClient client;

	public PingEventListener(IRCClient client) {
		this.client = client;
	}

	@Override
	public void trigger(Event event) {
		String data = (String) event.getData().get("data");
		client.write("PONG " + data);
		client.flush();
	}

	@Override
	public EventType[] getEventType() {
		return new EventType[] { IRCEvent.PING };
	}

}
