package tournamentData;

public class Beatmap 
{
	// stores the song ID, song link, and mod
	private int ID;
	private String link;
	private int mod;
	private boolean freeMod;
	
	public Beatmap(int ID, int mod, boolean freeMod)
	{
		this.ID = ID;
		this.mod = mod;
		this.freeMod = freeMod;
	}

	// getters
	public String getLink() {
		return link;
	}

	public int getMod() {
		return mod;
	}

	public boolean isFreeMod() {
		return freeMod;
	}

	public int getID() {
		return ID;
	}
	
	public String getTitle() {
		return "WIP";
	}
}
