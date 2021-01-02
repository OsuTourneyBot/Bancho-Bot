package api;

import java.io.IOException;
import org.json.JSONObject;

public class APIToken {

	private static JSONObject parent = new JSONObject();
	private static JSONObject obj = new JSONObject();
	private static String tokenCodes;
	private static long initiatedTime;
	private static int expiraryTime;

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
	private static String pullData() {
		return tokenCodes;
	}

	/**
	 * Checks if the current token is expired
	 * 
	 * @return True if Expired, False if not expired.
	 */
	private static boolean isExpired() {
		return ((initiatedTime + (1000 * expiraryTime) <= System.currentTimeMillis()));
	}

	/**
	 * Generates the Token and code and starts the timer for when the token will
	 * expire
	 * 
	 * @throws IOException
	 */
	private static void GenerateCode() throws IOException {
		String[] Header = new String[] { "Accept", "Content-Type" };
		String[] Body = new String[] { "application/json", "application/json ;charset=UTF-8" };
		String body = API.POST("https://osu.ppy.sh/oauth/token", obj.toString(), Header, Body);
		String[] number = body.split(",");
		String expirary = number[1].split(":")[1];
		expiraryTime = Integer.parseInt(expirary);
		tokenCodes = number[2].split(":")[1].substring(1, number[2].split(":")[1].length() - 1);
		initiatedTime = System.currentTimeMillis();

	}

	public static JSONObject APIMapInfo(int id) throws IOException {
		if (isExpired()) {
			GenerateCode();
		}
		String AccessString = pullData();
		String bearer = "Bearer " + AccessString;
		String[] header = new String[] { "Authorization", "Accept" };
		String[] body = new String[] { bearer, "application/json" };
		String stringBody = API.GET("https://osu.ppy.sh/api/v2/beatmaps/" + id, header, body);
		JSONObject data = new JSONObject(stringBody);
		return data;
	}

}
