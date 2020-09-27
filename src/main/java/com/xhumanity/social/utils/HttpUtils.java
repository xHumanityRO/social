package com.xhumanity.social.utils;


import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class HttpUtils {

	private static final Logger logger = LogManager.getLogger(HttpUtils.class);

	private static final String USER_AGENT = "Mozilla/5.0";

	public static String sendPOST(URI uri, String subject, String message) throws IOException, URISyntaxException {
		String urlParameters = "subject=" + subject + "&message=" + message;
		String responseBody = null;
		URL obj = uri.toURL();
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		con.setRequestMethod("POST");
		con.setRequestProperty("User-Agent", USER_AGENT);
		con.setDoOutput(true);
		con.setInstanceFollowRedirects(false);
		con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		con.setRequestProperty("charset", "utf-8");

		byte[] postData = urlParameters.getBytes(StandardCharsets.UTF_8);
		int postDataLength = postData.length;
		con.setRequestProperty("Content-Length", Integer.toString(postDataLength));
		con.setUseCaches(false);
		try (DataOutputStream wr = new DataOutputStream(con.getOutputStream())) {
			wr.write(postData);
		}

		int responseCode = con.getResponseCode();
		logger.info("POST Response Code :: " + responseCode);
		if (responseCode == HttpURLConnection.HTTP_OK) {
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();

			responseBody = response.toString();
		}
		return responseBody;
	}

	public static String extractPostPath(String response) {
		String searchedString = "<post link=\"/";
		int fromIndex = response.indexOf(searchedString);
		int endIndex = response.indexOf("\"", fromIndex + searchedString.length());
		String path = response.substring(fromIndex + searchedString.length() - 1, endIndex);
		return path;
	}

}