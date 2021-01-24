package irc.event;

public interface EventListener {
	
	public void listen();
	
	public void trigger(Event event);
	
	public EventType getEventType();
}
