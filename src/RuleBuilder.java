
public class RuleBuilder {
	
	final String set; // required
	final int wincondition; //require 1 = normal, 2 = accuracy, 3 = scoreV2
	boolean bans; // opitonal 
	int numbans; // optional
	boolean mods; //optional
	
	/**
	 * Constructor for rule builer
	 * @param set Name of the rule set
	 * @param wincon Win condition for tourney
	 */
	public RuleBuilder(String set, int wincon) {
		this.set = set;
		this.wincondition = wincon;
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
	
	public Rule build() {
		Rule set = new Rule(this);
		return set;
	}
}
