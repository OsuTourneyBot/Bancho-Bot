package api;

import java.io.IOException;
import java.net.URL;

public class APIMapInfo {
	private String accessString;
	private String[] parent;
	URL beatmapLink;

	/**
	 * The constructor for the Get Map object.
	 * 
	 * @param link  Takes in the link of the beat map
	 * @param token The JSON object for the token.
	 * @throws IOException
	 */
	public APIMapInfo(String link, APIToken token) throws IOException {

		if (token.isExpired()) {
			token.GenerateCode();
		} else {
			String AccessString = token.pullData();
			String bearer = "Bearer " + AccessString;
			String[] header = new String[] { "Authorization", "Accept" };
			String[] body = new String[] { bearer, "application/json" };
			String stringBody = API.GET(link, header, body);
			parent = stringBody.split(",");
		}
	}

	/**
	 * Returns the title of the beatmap
	 * 
	 * @return
	 */
	public String GetTitle() {
		return parent[43].split(":")[1];
	}

	/**
	 * returns the Map ID
	 * 
	 * @return
	 */
	public Object GetID() {
		return parent[1].split(":")[1];
	}

	/**
	 * Returns the length in seconds
	 * 
	 * @return
	 */
	public String GetLength() {
		return parent[3].split(":")[1];
	}

	/**
	 * Returns the mode of the map
	 * 
	 * @return
	 */
	public String GetMode() {
		return parent[2].split(":")[1];
	}

}
