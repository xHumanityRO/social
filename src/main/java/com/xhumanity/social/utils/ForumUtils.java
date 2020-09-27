package com.xhumanity.social.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xhumanity.social.dto.forum.PostDTO;
import com.xhumanity.social.dto.forum.TopicDTO;
import com.xhumanity.social.dto.forum.UserDTO;

public class ForumUtils {

	private static final Logger logger = LogManager.getLogger(ForumUtils.class);

	private static final String USER_AGENT = "Mozilla/5.0";

	public static UserDTO createUser(String username, String email, String password, String forumApiKey) throws IOException {
		URL obj = new URL("https://api.websitetoolbox.com/v1/api/users");
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		con.setRequestMethod("POST");
		con.setRequestProperty("User-Agent", USER_AGENT);
		con.setDoOutput(true);
		con.setInstanceFollowRedirects(false);
		con.setRequestProperty("Content-Type", "application/json");
		con.setRequestProperty("Accept", "application/json");
		con.setRequestProperty("x-api-key", forumApiKey);
		logger.info("forumApiKey: " + forumApiKey);
		OutputStreamWriter osw = new OutputStreamWriter(con.getOutputStream());
		String payload = "{\n" + "  \"username\": \"" + username + "\",\n" + "  \"password\": \"" + password + "\",\n"
				+ "  \"email\": \"" + email + "\"\n" + "}";
		logger.info("payload:" + payload);
		osw.write(payload);
		osw.flush();
		osw.close();

		int responseCode = con.getResponseCode();
		logger.info("POST Response Code : " + responseCode);
		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();
		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();
		//logger.info("response: " + response.toString());

		UserDTO user = new ObjectMapper().readValue(response.toString(), UserDTO.class);
		logger.info(user);

		return user;
	}

	public static TopicDTO createTopic(String username, Integer categoryId, String title, String content, String forumApiKey)
			throws IOException {
		URL obj = new URL("https://api.websitetoolbox.com/v1/api/topics");
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		con.setRequestMethod("POST");
		con.setRequestProperty("User-Agent", USER_AGENT);
		con.setDoOutput(true);
		con.setInstanceFollowRedirects(false);
		con.setRequestProperty("Content-Type", "application/json");
		con.setRequestProperty("Accept", "application/json");
		con.setRequestProperty("x-api-key", forumApiKey);
		OutputStreamWriter osw = new OutputStreamWriter(con.getOutputStream());
		String payload = "{\n" + 
				"  \"title\": \"" + title + "\",\n" + 
				"  \"content\": \"" + content + "\",\n" + 
				"  \"username\": \"" + username + "\",\n" + 
				"  \"categoryId\": " + categoryId + "\n" + 
				"}";
		logger.info("payload:" + payload);
		osw.write(payload);
		osw.flush();
		osw.close();

		int responseCode = con.getResponseCode();
		logger.info("POST Response Code : " + responseCode);
		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();
		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();
		//logger.info("Posting through API response: " + response.toString());

		TopicDTO topic = new ObjectMapper().readValue(response.toString(), TopicDTO.class);
		logger.info(topic);

		return topic;
	}

	public static PostDTO createPost(String username, Integer topicId, String subject, String message, String forumApiKey)
			throws IOException {
		URL obj = new URL("https://api.websitetoolbox.com/v1/api/posts");
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		con.setRequestMethod("POST");
		con.setRequestProperty("User-Agent", USER_AGENT);
		con.setDoOutput(true);
		con.setInstanceFollowRedirects(false);
		con.setRequestProperty("Content-Type", "application/json");
		con.setRequestProperty("Accept", "application/json");
		con.setRequestProperty("x-api-key", forumApiKey);
		OutputStreamWriter osw = new OutputStreamWriter(con.getOutputStream());
		String payload = "{\n" + "  \"content\": \"" + message + "\",\n" + "  \"username\": \"" + username + "\",\n"
				+ "  \"topicId\": " + topicId + "\n" + "}";
		logger.info("payload:" + payload);
		osw.write(payload);
		osw.flush();
		osw.close();

		int responseCode = con.getResponseCode();
		logger.info("POST Response Code : " + responseCode);
		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();
		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();
		//logger.info("Posting through API response: " + response.toString());

		PostDTO post = new ObjectMapper().readValue(response.toString(), PostDTO.class);
		logger.info(post);

		return post;
	}
}
