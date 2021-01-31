package irc.event;

import irc.IRCClient;

public class JoinEventListener implements EventListener {

	private IRCClient client;

	public JoinEventListener(IRCClient client) {
		this.client = client;
	}

	@Override
	public void trigger(Event event) {
		String channelName = (String) event.getData().get("channelName");
		client.addChannel(channelName);
	}

	@Override
	public EventType[] getEventType() {
		return new EventType[] { IRCEvent.JOIN };
	}

}
