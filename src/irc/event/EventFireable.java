package irc.event;

import java.util.ArrayList;
import java.util.HashMap;

public abstract class EventFireable {
	private HashMap<EventType, ArrayList<EventListener>> eventListeners;

	public EventFireable() {
		this.eventListeners = new HashMap<EventType, ArrayList<EventListener>>();
	}

	public void addEventListener(EventListener listener) {
		addEventListener(listener, listener.getEventType());
	}

	public void addEventListener(EventListener listener, EventType... types) {
		for (EventType type : types) {
			if (!eventListeners.containsKey(type)) {
				eventListeners.put(type, new ArrayList<EventListener>());
			}
			eventListeners.get(type).add(listener);
		}
	}

	public void removeEventListener(EventListener listener) {
		removeEventListener(listener, listener.getEventType());
	}

	public void removeEventListener(EventListener listener, EventType... types) {
		for (EventType type : types) {
			if (eventListeners.containsKey(type)) {
				eventListeners.get(type).remove(listener);
			}
		}
	}

	public void fireEvent(Event event) {
		if (eventListeners.containsKey(event.getType())) {
			for (EventListener listener : eventListeners.get(event.getType())) {
				listener.trigger(event);
			}
		}
	}

	public WaitForEventListener createWaitForEvent(EventType eventType) {
		WaitForEventListener listener = new WaitForEventListener(eventType);
		addEventListener(listener);
		return listener;
	}

	public Event waitForEvent(EventType eventType) {
		WaitForEventListener listener = new WaitForEventListener(eventType);
		addEventListener(listener);
		listener.listen();
		removeEventListener(listener);
		return listener.getEvent();
	}
}
