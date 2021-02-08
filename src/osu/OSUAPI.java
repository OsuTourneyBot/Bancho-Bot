package osu;

import java.io.IOException;

import org.json.JSONObject;

import api.API;

public class OSUAPI {

	private static JSONObject parent = new JSONObject();
	private static JSONObject obj = new JSONObject();
	private static boolean APITokenSet = false;
	private static String tokenCodes = null;
	private static long initiatedTime;
	private static int expiraryTime;

	public static boolean hasAPITokenSet() {
		return APITokenSet;
	}

	/**
	 * The constructor for trying to get an API Token.
	 * 
	 * @param Clientid     The client ID gotten from OSU API
	 * @param ClientSecret The Client Secret Gotten from OSU API
	 * @throws IOException
	 */
	public static void setCredentials(int Clientid, String ClientSecret) {
		// All of our info, creating
		obj.put("client_id", Clientid);
		obj.put("client_secret", ClientSecret);
		obj.put("grant_type", "client_credentials");
		obj.put("scope", "public");

		parent.put("obj", obj.toString());
		APITokenSet = true;
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
		JSONObject bodyJSON = new JSONObject(body);
		if (bodyJSON.getString("token_type").equals("Bearer")) {
			initiatedTime = System.currentTimeMillis();
			expiraryTime = bodyJSON.getInt("expires_in");
			tokenCodes = bodyJSON.getString("access_token");
		} else {
			System.err.println("Invalid API key.");
		}

	}

	public static JSONObject APIMapInfo(int id) throws IOException {
		if (tokenCodes == null || isExpired()) {
			GenerateCode();
		}
		String bearer = "Bearer " + tokenCodes;
		String[] header = new String[] { "Authorization", "Accept" };
		String[] body = new String[] { bearer, "application/json" };
		String stringBody = API.GET("https://osu.ppy.sh/api/v2/beatmaps/" + id, header, body);
		JSONObject data = new JSONObject(stringBody);
		return data;
	}

}
