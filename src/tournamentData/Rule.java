package tournamentData;
import java.util.ArrayList;
import java.util.Map;

public class Rule {
	
	final String RuleSetName; // required
	
	final MapPool mappool; // required mappool
	final int firstto; // how many games to be played
	final ArrayList<String> players; //The players in this tourney.
	final int teamsize; // The size of a team
	
	int wincondition; //require 1 = normal, 2 = accuracy, 3 = scoreV2
	int readytime, picktime, bantime; // for the amount of time given to ready up, pick map and ban maps
	int numbans; // optional
	boolean warmup; // optional
	int maxWarmupLength; //optional
	
	/**
	 * The actual ruleset that takes in a builder
	 * @param builder The builder for this rule set.
	 */
	public Rule(RuleBuilder builder) {
		
		this.warmup = builder.warmup;
		this.readytime = builder.readytime;
		this.picktime = builder.picktime;
		this.bantime = builder.bantime;
		this.teamsize = builder.teamsize;
		this.RuleSetName = builder.set;
		this.wincondition = builder.wincondition;
		this.numbans = builder.numbans;
		this.mappool = builder.mappool;
		this.players = builder.players;
		this.firstto = builder.firstto;
		this.maxWarmupLength = builder.maxWarmupLength;
	}
	private int getwincon() {
		return this.wincondition;
	}
	
	private int getmaxWarmupLength() {
		return maxWarmupLength;
	}
	
	private String getRuleSetName() {
		return RuleSetName;
	}
	private int getWincondition() {
		return wincondition;
	}
	private MapPool getMappool() {
		return mappool;
	}
	private int getfirstto() {
		return firstto;
	}
	private ArrayList<String> getPlayers() {
		return players;
	}
	private int getTeamsize() {
		return teamsize;
	}
	private int getReadytime() {
		return readytime;
	}
	private int getPicktime() {
		return picktime;
	}
	private int getBantime() {
		return bantime;
	}
	private int getNumbans() {
		return numbans;
	}
	private boolean isWarmup() {
		return warmup;
	}
	private int getbans() {
		return this.numbans;
	}
	
	
}
