package tournamentData;

public class RuleBuilder {

	final String setName; // required

	final int firstTo;
	final String[][] players; // The players in this tourney.
	final int teamSize; // Number of players in a team.
	final int numBans; // number of bans per team.

	int teamMode = 2;
	int scoreMode = 3; // require 1 = normal, 2 = accuracy, 3 = scoreV2, ScoreV2 by default
	int readyTime = 120; // The time given to warmup, default at 120 seconds.
	int pickTime = 120; // The time given to pick maps, default at 120 seconds.
	int banTime = 120; // The time given to ban, default at 120 seconds.
	boolean warmUp = true; // Check if warmup is enabled true by default.
	int maxWarmupLength = 300; // The max length of a warmup map, defaults at 300 seconds.
	boolean tieBreaker = false;

	/**
	 * Constructor for the rule builder
	 * 
	 * @param setName   The name for this rule set
	 * @param firstTo   The number of points required to win
	 * @param players
	 * @param teamSize
	 * @param numBans
	 * @param teamMode
	 * @param scoreMode
	 */
	public RuleBuilder(String setName, int firstTo, String[][] players, int teamSize, int numBans,
			int teamMode, int scoreMode) {
		this.setName = setName;
		this.teamMode = teamMode;
		this.scoreMode = scoreMode;
		this.firstTo = firstTo;
		this.players = players;
		this.teamSize = teamSize;
		this.numBans = numBans;
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
		this.readyTime = time;
		return this;
	}

	/**
	 * Setter for pick up time
	 * 
	 * @param time Time in second for length
	 * @return
	 */
	public RuleBuilder pickTime(int time) {
		this.pickTime = time;
		return this;
	}

	/**
	 * Setter for ban up time
	 * 
	 * @param time Time in second for length
	 * @return
	 */
	public RuleBuilder banTime(int time) {
		this.banTime = time;
		return this;
	}

	/**
	 * Setter method to tell if a warm up exists
	 * 
	 * @param bool True for yes, false for no.
	 * @return
	 */
	public RuleBuilder warmUp(boolean bool) {
		this.warmUp = bool;
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

	public RuleBuilder tieBreaker(boolean tieBreaker) {
		this.tieBreaker = tieBreaker;
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
