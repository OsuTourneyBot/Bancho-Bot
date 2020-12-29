package tournamentData;
import java.util.ArrayList;
public class RuleBuilder {
	
	final String set; // required
	final int wincondition; //require 1 = normal, 2 = accuracy, 3 = scoreV2
	final MapPool mappool;
	final int bestoutof;
	final ArrayList<String> players; //The players in this tourney.
	final int teamsize; // Number of players in a team 
	int readytime, picktime, bantime; // for the amount of time given
	//to ready up, pick map and ban maps
	int numbans; // optional
	boolean warmup;
	/**
	 * Constructor for rule builer
	 * @param set Name of the rule set
	 * @param wincon Win condition for tourney
	 * @param pool the arraylist containing all the maps
	 * @param best what the tourney is out of
	 * @param players an arraylist containing the names of all participating players
	 */
	public RuleBuilder(String set, int wincon, MapPool pool, int best, ArrayList<String> players, int team) {
		this.set = set;
		this.wincondition = wincon;
		this.mappool = pool;
		this.bestoutof = best;
		this.players = players;
		this.teamsize = team;
	}

	/**
	 * Setter method for number of bans
	 * 
	 * @param number of bands
	 * @return This object
	 */
	protected RuleBuilder numbans (int num) {
		this.numbans = num;
		return this;
	}

	/**
	 * Setter methods for Map pool
	 * @param pool
	 * @return
	 */
	protected RuleBuilder mappool(ArrayList<OsuMap> pool) {
		this.mappool = pool;
		return this;
	}
	
	/**
	 * Setter method for best out of
	 * @param best
	 * @return
	 */
	protected RuleBuilder bestoutof(int best) {
		this.bestoutof = best;
		return this;
	}
	
	/**
	 * Setter method for the player array
	 * @param player array of player names
	 * @return
	 */
	protected RuleBuilder players(ArrayList<String> player) {
		this.players = player;
		return this;
	}
	
	/**
	 * Setter for ready up time
	 * @param time Time in second for length
	 * @return 
	 */
	protected RuleBuilder readytime(int time) {
		this.readytime = time;
		return this;
	}
	/**
	 * Setter for pick up time
	 * @param time Time in second for length
	 * @return 
	 */
	protected RuleBuilder picktime(int time) {
		this.picktime = time;
		return this;
	}
	/**
	 * Setter for ban up time
	 * @param time Time in second for length
	 * @return 
	 */
	protected RuleBuilder bantime(int time) {
		this.bantime = time;
		return this;
	}
	/**
	 * Setter method to tell if a warm up exists
	 * @param bool True for yes, false for no.
	 * @return
	 */
	protected RuleBuilder warmup(boolean bool) {
		this.warmup = bool;;
		return this;
	}
	/**
	 * Builder method that creates rule set.
	 * @return
	 */
	protected Rule build() {
		Rule set = new Rule(this);
		return set;
	}
	
} 
