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
		String[] titles = new String[] {"Accept", "Content-Type"};
		String[] body = new String[] {"application/json","application/json"};
		
		String info = API.POST(URL, tourneyJSON.toString(),titles , body);
		
		return info;
		
	}
	
	
	public static void main(String[] args) throws IOException {
		System.out.print(CHALLONGEAPI.createTournament("lvEr5HHJHdKXaSZD4tUYAl2QaFAFbtrwlpjTJARS", "Osu_Tourney", false,"swiss"));
		
	}

}
