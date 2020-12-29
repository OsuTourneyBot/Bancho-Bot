package refBot;

import java.util.ArrayList;

import bancho.LobbyHandler;
import irc.IRCClient;
import irc.handlers.IRCEventHandler;

public class RefBot {
    private LobbyHandler lobby;

    public RefBot(LobbyHandler lobby) {
        lobby.addLobbyHandler(new BanchoMessageHandlerGroup(new ArrayList<IRCEventHandler>()));
    }
    
    /*
     * Make a new function called timer
     * takes in int (secs) as args
     * waits for either 2 things (a for when time runs out or b for notify for "this" object) [look at bancho bot line 41]
     */
    public void Timer (int sec) 
    {
    	// start the timer
    	lobby.message("!mp timer " + sec);
    	BanchoTimerHandler handler = new BanchoTimerHandler(this);
    	lobby.addLobbyHandler(handler);
    	
    	synchronized (this) 
    	{
    		try 
    		{
    			this.wait(); // pauses the function
    		} catch (InterruptedException e) 
    		{
    			e.printStackTrace();
    		}
    	}
    	
    	lobby.removeLobbyHanlder(handler);
    	
    }
}


// Example class don't will remove later
class MatchMessage implements IRCEventHandler{

    @Override
    public String[] match(String[] data) {
        // Matches if the message is hello
        if(data[0].equals("Hello")) {
            return data;
        }else {
            return null;
        }
    }

    @Override
    public boolean handle(String[] data, IRCClient client) {
        //Do something with the information
        return false;
    }
    
}




