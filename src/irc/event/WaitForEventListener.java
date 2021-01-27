package irc.event;

public class WaitForEventListener implements EventListener {

	private EventType type;
	private Event event;

	public WaitForEventListener(EventType type) {
		this.type = type;
	}

	public void listen() {
		if (event == null) {
			synchronized (this) {
				try {
					this.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public void trigger(Event event) {
		this.event = event;
		synchronized (this) {
			this.notify();
		}
	}

	@Override
	public EventType getEventType() {
		return type;
	}

	public Event getEvent() {
		return event;
	}

}
