package tournamentData;

public class RuleBuilder {

	final String set; // required

<<<<<<< Updated upstream
	final MapPool mappool;
=======
>>>>>>> Stashed changes
	final int firstTo;
	final String[] players; // The players in this tourney.
	final int teamSize; // Number of players in a team.
	final int numbans; // number of bans per team.

	int teamMode = 2;
	int scoreMode = 3; // require 1 = normal, 2 = accuracy, 3 = scoreV2, ScoreV2 by default
	int readytime = 120; // The time given to warmup, default at 120 seconds.
	int picktime = 120; // The time given to pick maps, default at 120 seconds.
	int bantime = 120; // The time given to ban, default at 120 seconds.
	boolean warmup = true; // Check if warmup is enabled true by default.
	int maxWarmupLength = 300; // The max length of a warmup map, defaults at 300 seconds.

	/**
	 * Constructor for rule builder
	 * 
	 * @param set     Name of the rule set
	 * @param wincon  Win condition for tourney
	 * @param pool    the arraylist containing all the maps
	 * @param best    what the tourney is out of
	 * @param players an arraylist containing the names of all participating players
	 */
<<<<<<< Updated upstream
	public RuleBuilder(String set, MapPool pool, int best, String[] players, int teamSize, int numbans,
			int teamMode, int wincon) {
		this.set = set;
		this.teamMode = teamMode;
		this.scoreMode = wincon;
		this.mappool = pool;
		this.firstTo = best;
=======
	public RuleBuilder(String setName, int firstTo, String[][] players, int teamSize, int numBans,
			int teamMode, int scoreMode) {
		this.setName = setName;
		this.teamMode = teamMode;
		this.scoreMode = scoreMode;
		this.firstTo = firstTo;
>>>>>>> Stashed changes
		this.players = players;
		this.teamSize = teamSize;
		this.numbans = numbans;
	}

	/**
	 * Setter method for type of win condition. 1 = normal, 2 = accuracy , 3 =
	 * ScoreV2
	 * 
	 * @param cond
	 * @return
	 */
	public RuleBuilder scoreMode(int cond) {
		this.scoreMode = cond;
		return this;
	}

	/**
	 * Setter for ready up time
	 * 
	 * @param time Time in second for length
	 * @return
	 */
	public RuleBuilder readytime(int time) {
		this.readytime = time;
		return this;
	}

	/**
	 * Setter for pick up time
	 * 
	 * @param time Time in second for length
	 * @return
	 */
	public RuleBuilder picktime(int time) {
		this.picktime = time;
		return this;
	}

	/**
	 * Setter for ban up time
	 * 
	 * @param time Time in second for length
	 * @return
	 */
	public RuleBuilder bantime(int time) {
		this.bantime = time;
		return this;
	}

	/**
	 * Setter method to tell if a warm up exists
	 * 
	 * @param bool True for yes, false for no.
	 * @return
	 */
	public RuleBuilder warmup(boolean bool) {
		this.warmup = bool;
		;
		return this;
	}

	/**
	 * Setter for warm up length
	 * 
	 * @param time time in seconds.
	 * @return
	 */
	public RuleBuilder maxWarmupLength(int time) {
		this.maxWarmupLength = time;
		return this;
	}

	/**
	 * Setter for team mode
	 * 
	 * @param 0 = Head to Head, 1 = Tag Coop, 2 = Team Vs, 3 = Tag Team Vs
	 * @return
	 */
	public RuleBuilder teamMode(int teamMode) {
		this.teamMode = teamMode;
		return this;
	}

	/**
	 * Builder method that creates rule set.
	 * 
	 * @return
	 */
	protected Rule build() {
		Rule set = new Rule(this);
		return set;
	}

}
