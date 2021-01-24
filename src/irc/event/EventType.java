package irc.event;

import java.util.HashMap;

public interface EventType {
	public default Event toEvent() {
		return new Event(this, new HashMap<String, Object>());
	}
}
