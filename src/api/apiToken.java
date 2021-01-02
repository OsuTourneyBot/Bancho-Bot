package api;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONObject;

public class APIToken {

	
	private static JSONObject parent = new JSONObject();
	JSONObject obj = new JSONObject();
	private static String tokencodes;
	long initiatedTime;
	int expiraryTime;
	
	/**
	 * The constructor for trying to get an API Token.
	 * @param Clientid The client ID gotten from OSU API
	 * @param ClientSecret The Client Secret Gotten from OSU API
	 * @throws IOException
	 */
	public APIToken(int Clientid, String ClientSecret){
	// All of our info, creating 
	obj.put("client_id",Clientid);
	obj.put("client_secret",ClientSecret );
	obj.put("grant_type", "client_credentials");
	obj.put("scope", "public");
	
	parent.put("obj", obj.toString());
	}
	/**
	 * Returns the Tokens.
	 * @return JSON Object containing the token.
	 */
	public String pullData() {
		return tokencodes;
	}
	/**
	 * Checks if the current token is expired
	 * @return True if Expired, False if not expired.
	 */
	public boolean isExpired() {
		return((initiatedTime + (1000 * expiraryTime) <= System.currentTimeMillis()));
			
	}
	/**
	 * Generates the Token and code and starts the timer for when the token will expire
	 * @throws IOException 
	 */
	public void GenerateCode() throws IOException {
		
		String body = API.POST("https://osu.ppy.sh/oauth/token", obj.toString(),"Accept", "application/json","Content-Type", "application/json; charset=UTF-8");
		
		String[] number = body.split(",");
		String expirary = number[1].split(":")[1];
		expiraryTime = Integer.parseInt(expirary);
		tokencodes = number[2].split(":")[1].substring(1, number[2].split(":")[1].length() - 1);
		initiatedTime  = System.currentTimeMillis();
		
		
	}
	

}
