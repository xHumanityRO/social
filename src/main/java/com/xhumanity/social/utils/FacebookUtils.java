package com.xhumanity.social.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

public class FacebookUtils {

	public static final String LOGGED_USER_ID = "me";

	public static final String USER_FIELD_ID = "id";

	public static final String USER_FIELD_EMAIL = "email";

	public static final String USER_FIELD_FIRST_NAME = "first_name";

	public static final String USER_FIELD_LAST_NAME = "last_name";
	
	public static String getLongLivedTokenURL(String facebookAppId, String facebookSecret, String token) {
		String url = "https://graph.facebook.com/v8.0/oauth/access_token?" +
		    "grant_type=fb_exchange_token&" +
		    "client_id=" + facebookAppId + "&" +
		    "client_secret=" + facebookSecret + "&" +
		    "fb_exchange_token=" + token;
	    return url;
	}

	public static String readURL(URL url) throws IOException {
	    InputStream is = url.openStream();
	    InputStreamReader inStreamReader = new InputStreamReader(is);
	    BufferedReader reader = new BufferedReader(inStreamReader);
	    String s = "";
	    int r;
		while ((r = is.read()) != -1) {
	        s = reader.readLine();
	    }
	    reader.close();
	    return s;
	}

}
