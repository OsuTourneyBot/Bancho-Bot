import java.util.ArrayList;
public class RuleBuilder {
	
	final String set; // required
	final int wincondition; //require 1 = normal, 2 = accuracy, 3 = scoreV2
	final ArrayList<Map> mappool;
	final int bestoutof;
	final ArrayList<String> players; //The players in this tourney.
	boolean bans; // opitonal 
	int numbans; // optional
	boolean mods; //optional
	
	/**
	 * Constructor for rule builer
	 * @param set Name of the rule set
	 * @param wincon Win condition for tourney
	 * @param pool the arraylist containing all the maps
	 * @param best what the tourney is out of
	 * @param players an arraylist containing the names of all participating players
	 */
	public RuleBuilder(String set, int wincon, ArrayList<OsuMap> pool, int best, ArrayList<String> players) {
		this.set = set;
		this.wincondition = wincon;
		this.mappool = pool;
		this.bestoutof = best;
		this.players = players;
	}
	/**
	 * Setter method for changing bans
	 * @param bool
	 * @return this object
	 */
	public RuleBuilder bans(boolean bool) {
		this.bans = bool;
		return this;
	}
	
	/**
	 * Setter method for number of bans
	 * 
	 * @param number of bands
	 * @return This object
	 */
	public RuleBuilder numbans (int num) {
		this.numbans = num;
		return this;
	}
	/**
	 * Setter method for mods
	 * 
	 * @param true or false for mods.
	 * @return This object
	 */
	public RuleBuilder mods(boolean bool) {
		this.mods = bool;
		return this;
	}
	/**
	 * Setter methods for Map pool
	 * @param pool
	 * @return
	 */
	public RuleBuilder mappool(ArrayList<OsuMap> pool) {
		this.mappool = pool;
		return this;
	}
	
	/**
	 * Setter method for best out of
	 * @param best
	 * @return
	 */
	public RuleBuilder bestoutof(int best) {
		this.bestoutof = best;
		return this;
	}
	
	/**
	 * Setter method for the player array
	 * @param player array of player names
	 * @return
	 */
	public RuleBuilder players(ArrayList<String> player) {
		this.players = player;
		return this;
	}
	/**
	 * Builder method that creates rule set.
	 * @return
	 */
	public Rule build() {
		Rule set = new Rule(this);
		return set;
	}
}
