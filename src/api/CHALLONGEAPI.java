package api;

import java.io.IOException;
import org.json.JSONObject;


public class CHALLONGEAPI {
	
	/**
	 * Creates a tournament 
	 * @param key The API key for the tourney 
	 * @param name The name of the tournament 
	 * @param openSignUp Make it so the tourney is open for sign up
	 * @param tourneyType Define what kind of tourney type it is.
	 * @return
	 * @throws IOException
	 */
	public static String createTournament(String key, String name, boolean openSignUp, String tourneyType ) throws IOException {
		String[] titlesJSON = new String[]{"api_key","name","open_signup","tournament_type" };
		Object[] dataJSON = new Object[] {key, name, openSignUp, tourneyType};
		JSONObject tourneyJSON = API.createJSON(titlesJSON, dataJSON);
		System.out.print(tourneyJSON);
		String URL = "https://api.challonge.com/v1/tournaments.json";
		String[] empty = new String[0];
		
		String info = API.POST(URL, tourneyJSON.toString(),empty , empty);
		
		return info;
		
	}
	
	
	public static void main(String[] args) throws IOException {
		System.out.println(CHALLONGEAPI.createTournament("Pssw", "Osu_Tourney", false,"swiss"));
		
	}

}
