
public class Rule {
	
	final String set; // required
	final int wincondition; //require 1 = normal, 2 = accuracy, 3 = scoreV2
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
	
	
}
