package tournamentData;
import java.util.ArrayList;
public class RuleBuilder {
	
	final String set; // required
	
	final MapPool mappool;
	final int firstto;
	final ArrayList<String> players; //The players in this tourney.
	final int teamsize; // Number of players in a team.
	final int numbans; // number of bans per team.
	
	int wincondition = 3; //require 1 = normal, 2 = accuracy, 3 = scoreV2, ScoreV2 by default
	int readytime = 120; // The time given to warmup, default at 120 seconds.
	int picktime = 120; // The time given to pick maps, default at 120 seconds.
	int bantime = 120; // The time given to ban, default at 120 seconds.
	boolean warmup = true; //Check if warmup is enabled true by default.
	int maxWarmupLength = 300; //The max length of a warmup map, defaults at 300 seconds.
	/**
	 * Constructor for rule builer
	 * @param set Name of the rule set
	 * @param wincon Win condition for tourney
	 * @param pool the arraylist containing all the maps
	 * @param best what the tourney is out of
	 * @param players an arraylist containing the names of all participating players
	 */
	public RuleBuilder(String set, MapPool pool, int best, ArrayList<String> players, int team, int numbans, int wincon) {
		this.set = set;
		this.wincondition = wincon;
		this.mappool = pool;
		this.firstto = best;
		this.players = players;
		this.teamsize = team;
		this.numbans = numbans;
	}
	/**
	 * Setter method for type of win condition.
	 * 1 = normal, 2 = accuracy , 3 = ScoreV2
	 * @param cond
	 * @return
	 */
	protected RuleBuilder wincondition(int cond) {
		this.wincondition = cond;
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
	 * Setter for warm up length
	 * @param time time in seconds.
	 * @return
	 */
	protected RuleBuilder maxWarmupLength(int time) {
		this.maxWarmupLength = time;
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
