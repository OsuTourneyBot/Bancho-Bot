package tournamentData;

import java.io.IOException;

import org.json.JSONObject;

import api.OSUAPI;

public class Beatmap 
{
	// stores the song ID, song link, and mod
	private int ID;
	private int mod;
	private boolean freeMod;
	
	private String url;
	private String title;
	private String version;
	private int total_length;
	private String artist;
	private String creator;
	
	
	public Beatmap(int ID, int mod, boolean freeMod)
	{
		this.ID = ID;
		this.mod = mod;
		this.freeMod = freeMod;
		
		try {
			// getting the map info from the ID
			JSONObject jo = OSUAPI.APIMapInfo(this.ID);
			
			// getting the stuff from JSON
			url = jo.getString("url");
			title = jo.getJSONObject("beatmapset").getString("title");
			artist = jo.getJSONObject("beatmapset").getString("artist");
			creator = jo.getJSONObject("beatmapset").getString("creator");
			version = jo.getString("version");
			total_length = jo.getInt("total_length");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// getters
	public String getLink() {
		return url;
	}

	public int getTotal_length() {
		return total_length;
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
	
	public String getVersion() {
		return version;
	}

	public String getTitle() {
		return title;
	}
	
	public String getFullTitle()
	{
		return artist + " - " + title + " {" + version + "} (" + creator + ")";
	}

	public String getCreator() {
		return creator;
	}
	

}
