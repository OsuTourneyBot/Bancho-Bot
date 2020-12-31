package tournamentData;

public class Rule {

	final String RuleSetName; // required

	final MapPool mappool; // required mappool
	final int firstTo; // how many games to be played
	final String[] players; // The players in this tourney.
	final int teamsize; // The size of a team
	final int teamMode;

	final int scoreMode; // require 1 = normal, 2 = accuracy, 3 = scoreV2
	int readytime, picktime, bantime; // for the amount of time given to ready up, pick map and ban maps
	int numbans; // optional
	boolean warmup; // optional
	int maxWarmupLength; // optional

	/**
	 * The actual ruleset that takes in a builder
	 * 
	 * @param builder The builder for this rule set.
	 */
	public Rule(RuleBuilder builder) {

		this.warmup = builder.warmup;
		this.readytime = builder.readytime;
		this.picktime = builder.picktime;
		this.bantime = builder.bantime;
		this.teamsize = builder.teamSize;
		this.RuleSetName = builder.set;
		this.teamMode = builder.teamMode;
		this.scoreMode = builder.scoreMode;
		this.numbans = builder.numbans;
		this.mappool = builder.mappool;
		this.players = builder.players;
		this.firstTo = builder.firstTo;
		this.maxWarmupLength = builder.maxWarmupLength;
	}

	public int getMaxWarmupLength() {
		return maxWarmupLength;
	}

	public String getRuleSetName() {
		return RuleSetName;
	}

	public int getTeamMode() {
		return teamMode;
	}

	public int getScoreMode() {
		return scoreMode;
	}

	public MapPool getMappool() {
		return mappool;
	}

	public int getFirstTo() {
		return firstTo;
	}

	public String[] getPlayers() {
		return players;
	}

	public int getTeamsize() {
		return teamsize;
	}

	public int getReadytime() {
		return readytime;
	}

	public int getPicktime() {
		return picktime;
	}

	public int getBantime() {
		return bantime;
	}

	public int getNumbans() {
		return numbans;
	}

	public boolean isWarmup() {
		return warmup;
	}

	public int getBans() {
		return this.numbans;
	}

}
