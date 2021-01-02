package api;

import java.io.IOException;
import org.json.JSONObject;

public class APIToken {

	private static JSONObject parent = new JSONObject();
	JSONObject obj = new JSONObject();
	private static String tokenCodes;
	long initiatedTime;
	int expiraryTime;

	/**
	 * The constructor for trying to get an API Token.
	 * 
	 * @param Clientid     The client ID gotten from OSU API
	 * @param ClientSecret The Client Secret Gotten from OSU API
	 * @throws IOException
	 */
	public APIToken(int Clientid, String ClientSecret) {
		// All of our info, creating
		obj.put("client_id", Clientid);
		obj.put("client_secret", ClientSecret);
		obj.put("grant_type", "client_credentials");
		obj.put("scope", "public");

		parent.put("obj", obj.toString());
	}

	/**
	 * Returns the Tokens.
	 * 
	 * @return JSON Object containing the token.
	 */
	public String pullData() {
		return tokenCodes;
	}

	/**
	 * Checks if the current token is expired
	 * 
	 * @return True if Expired, False if not expired.
	 */
	public boolean isExpired() {
		return ((initiatedTime + (1000 * expiraryTime) <= System.currentTimeMillis()));
	}

	/**
	 * Generates the Token and code and starts the timer for when the token will
	 * expire
	 * 
	 * @throws IOException
	 */
	public void GenerateCode() throws IOException {
		String[] Header = new String[] { "Accept", "Content-Type" };
		String[] Body = new String[] { "application/json", "application/json ;charset=UTF-8" };
		String body = API.POST("https://osu.ppy.sh/oauth/token", obj.toString(), Header, Body);
		String[] number = body.split(",");
		String expirary = number[1].split(":")[1];
		expiraryTime = Integer.parseInt(expirary);
		tokenCodes = number[2].split(":")[1].substring(1, number[2].split(":")[1].length() - 1);
		initiatedTime = System.currentTimeMillis();

	}

}
