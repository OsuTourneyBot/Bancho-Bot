import java.util.ArrayList;
import java.util.Map;

public class Rule {
	
	final String set; // required
	final int wincondition; //require 1 = normal, 2 = accuracy, 3 = scoreV2
	final ArrayList<OsuMap> mappool; // required mappool
	final int bestoutof; // how many games to be played
	final ArrayList<String> players; //The players in this tourney.
	boolean bans; // opitonal 
	int numbans; // optional
	boolean mods; //optional
	
	/**
	 * The actual ruleset that takes in a builder
	 * @param builder The builder for this rule set.
	 */
	public Rule(RuleBuilder builder) {
		
		this.set = builder.set;
		this.wincondition = builder.wincondition;
		this.bans = builder.bans;
		this.numbans = builder.numbans;
		this.mods = builder.mods;
		this.mappool = builder.mappool;
		this.players = builder.players;
		
	}
	public int getwincon() {
		return this.wincondition;
	}
	
	public boolean getbans() {
		return this.bans;
	}
	
	public boolean getmods() {
		return this.mods;
	}
	
	/**
	 * Method for banning an osu map.
	 * Removes map from pool if banned.
	 * @return Returns true if map has been banned false if not.
	 */
	public boolean ban(OsuMap) {
		return mappool.remove(OsuMap);
			
		}
	}
	
	
}
