package irc.event;

public interface EventListener {

	public default void listen() {

	};

	public void trigger(Event event);

	public EventType[] getEventType();
}
