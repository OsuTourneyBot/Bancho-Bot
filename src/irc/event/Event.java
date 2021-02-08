package irc.event;

import java.util.HashMap;

public class Event {
	private EventType type;
	private HashMap<String, Object> data;

	public Event(EventType type) {
		this(type, new HashMap<String, Object>());
	}

	public Event(EventType type, HashMap<String, Object> data) {
		this.type = type;
		this.data = data;
	}

	public EventType getType() {
		return type;
	}

	public void addData(String id, Object object) {
		data.put(id, object);
	}

	public HashMap<String, Object> getData() {
		return data;
	}
}
