package tournamentData;

public class Rule {

	private final String ruleSetName; // required

	private final int firstTo; // how many games to be played
	private final String[][] players; // The players in this tourney.
	private final String[] teamNames;
	private final int teamSize; // The size of a team

	private int teamMode;
	private int scoreMode; // require 1 = normal, 2 = accuracy, 3 = scoreV2
	private int readyTime, pickTime, banTime; // for the amount of time given to ready up, pick map and ban maps
	private int numBans; // optional
	private boolean warmUp; // optional
	private int maxWarmupLength; // optional
	private boolean tieBreaker; // optional

	/**
	 * The actual ruleset that takes in a builder
	 * 
	 * @param builder The builder for this rule set.
	 */
	public Rule(RuleBuilder builder) {
		this.warmUp = builder.warmUp;
		this.readyTime = builder.readyTime;
		this.pickTime = builder.pickTime;
		this.banTime = builder.banTime;
		this.teamSize = builder.teamSize;
		this.teamMode = builder.teamMode;
		this.ruleSetName = builder.setName;
		this.scoreMode = builder.scoreMode;
		this.numBans = builder.numBans;
		this.players = builder.players;
		this.firstTo = builder.firstTo;
		this.maxWarmupLength = builder.maxWarmupLength;
		this.tieBreaker = builder.tieBreaker;

		this.teamNames = new String[players.length];
		for (int i = 0; i < players.length; i++) {
			teamNames[i] = players[i][0];
		}
	}

	public int getMaxWarmupLength() {
		return maxWarmupLength;
	}

	public String getRuleSetName() {
		return ruleSetName;
	}

	public int getScoreMode() {
		return scoreMode;
	}

	public int getFirstTo() {
		return firstTo;
	}

	public String[][] getPlayers() {
		return players;
	}

	public String[] getTeamNames() {
		return teamNames;
	}

	public int getTeamSize() {
		return teamSize;
	}

	public int getReadyTime() {
		return readyTime;
	}

	public int getPickTime() {
		return pickTime;
	}

	public int getBanTime() {
		return banTime;
	}

	public int getNumBans() {
		return numBans;
	}

	public boolean hasWarmUp() {
		return warmUp;
	}

	public int getTeamMode() {
		return teamMode;
	}

	public boolean getTieBreaker() {
		return tieBreaker;
	}

}
