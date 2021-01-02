/*
 * 1. Figure out how to make API calls like C#
 * 2. JSON (so find a JSON library that works)
 * 3. once you learn how to make API calls, learn how to oauth2
 * 4. Figure out how to make actual calls to the actual API and make sure it works
 * 
 * 
 * 
 * Oauth2 example
 * https://developer.byu.edu/docs/consume-api/use-api/oauth-20/oauth-20-java-sample-code
 * 
 * API calls 
 * https://www.youtube.com/watch?v=qzRKa8I36Ww 1:52 method 1, 10:33 method 2 (HttpClient)
 * https://jsonplaceholder.typicode.com/albums // data
 * 
 * https://jsonplaceholder.typicode.com/ // website for JSON data
 * 
 * how to make POST request
 * https://stackoverflow.com/questions/3324717/sending-http-post-request-in-java
 */

package api;

import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONObject;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class API
{	
	// OkHttp
	static OkHttpClient client = new OkHttpClient();

	static String GET(String url) throws IOException {
	  Request request = new Request.Builder()
	      .url(url)
	      .build();

	  try (Response response = client.newCall(request).execute()) {
	    return response.body().string();
	  }
	}
	
	static String GET(String url, String Header, String Header2, String body1, String body2) throws IOException {
		  Request request = new Request.Builder()
			  .addHeader(Header, body1)
			  .addHeader(Header2, body2)
		      .url(url)
		      .build();

		  try (Response response = client.newCall(request).execute()) {
		    return response.body().string();
		  }
		}
		
	
	public static final MediaType JSON
			= MediaType.get("application/json; charset=utf-8");

	static String POST(String url, String json, String Header, String Header2, String body1, String body2) throws IOException {
		RequestBody body = RequestBody.create(json, JSON);
		Request request = new Request.Builder().url(url).post(body)
				.addHeader(Header, body1)
				.addHeader(Header2, body2)
				.build();
		try (Response response = client.newCall(request).execute()) {
			return response.body().string();
		}
	}

	static String POST(String url, String json) throws IOException {
		RequestBody body = RequestBody.create(json, JSON);
		Request request = new Request.Builder().url(url).post(body).build();
		try (Response response = client.newCall(request).execute()) {
			return response.body().string();
		}
	}
	
	
	public static void main(String[] args) throws IOException
	{
		String body = GET("https://jsonplaceholder.typicode.com/albums");
		System.out.println(body);
		
		// parse
		parse(body);
		
		String post = POST("https://7dd1c7548bfe2ad013c39d35f5f60e20.m.pipedream.net/", body);
		System.out.println(post);
	}
	
	// Time to parse data
	public static String parse(String responseBody) {
		JSONArray albums = new JSONArray(responseBody);
		for (int i = 0; i < albums.length(); i++) {
			JSONObject album = albums.getJSONObject(i);

			// extract the ID and stuff
			int id = album.getInt("id"); // no idea if key: is needed
			int userId = album.getInt("userId");
			String title = album.getString("title");

			System.out.println(id + " " + title + " " + userId);
		}

		// modify to add data instead of printing the stuff
		return null;
	}
}












