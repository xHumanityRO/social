package com.xhumanity.social.telegram;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Contact;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import com.google.api.services.youtube.model.VideoListResponse;
import com.xhumanity.social.dto.forum.PostDTO;
import com.xhumanity.social.dto.forum.TopicDTO;
import com.xhumanity.social.dto.forum.UserDTO;
import com.xhumanity.social.exception.APIException;
import com.xhumanity.social.model.TelegramUser;
import com.xhumanity.social.repository.TelegramUserRepository;
import com.xhumanity.social.utils.ForumUtils;
import com.xhumanity.social.utils.Utils;
import com.xhumanity.social.youtube.YoutubeUtils;

public class TelegramBot extends TelegramLongPollingBot {
	private static final Logger logger = LogManager.getLogger(TelegramBot.class);

	private static final String BOT_NAME = "xHumanityBotTest";
	private static final int YOUTUBE_CAMPAIGN_ID = 827617;
	private static final int PROMO_TOPIC_ID = 11100297;
	private static final String USERNAME_PREFIX = "H";

	private TelegramUserRepository telegramUserRepository;

	private String telegramToken;

	private String forumApiKey;

	private String youtubeApiKey;

	public TelegramBot(TelegramUserRepository telegramUserRepository, String telegramToken, String forumApiKey, String youtubeApiKey) {
		this.telegramUserRepository = telegramUserRepository;
		this.telegramToken = telegramToken;
		this.forumApiKey = forumApiKey;
		this.youtubeApiKey = youtubeApiKey;
	}

	@Override
	public void onUpdateReceived(Update update) {
		if (update.hasMessage()) {
			TelegramUser telegramUser = createUserIfNotExists(update.getMessage());

			if (update.getMessage().hasContact()) {
				processContact(update, telegramUser);
			} else if (update.getMessage().hasText()) {
				long chatId = update.getMessage().getChatId();
				String messageText = update.getMessage().getText();

				if (messageText.equals("/start")) {
					processStart(update, chatId, messageText);
				} else if (messageText.equals("/forum_sign_up")) {
					createForumAccount(update, telegramUser, chatId, messageText);
				} else if (messageText.equals("/share_phone_number")) {
					displayShareNumberOption(update, chatId, messageText);
				} else if (messageText.equals("/share_email_address")) {
					displayShareEmailText(update, chatId, messageText);
				} else if (YoutubeUtils.isYoutubeLink(messageText)) {
					processYoutubeLink(update, telegramUser, chatId, messageText);
				} else if (Utils.isEmailValid(messageText)) {
					processEmailAddress(update, telegramUser, chatId, messageText);
				} else {
					// Unknown command
					String answer = "I'm instructed to only respond to following commands:\n" +
							"/start\n" +
							"/share_email_address\n" +
							"/forum_sign_up\n" +
							"/share_phone_number\n\n" +
							"And if you copy here a valid youtube link I will post it on your behalf on the forum.";
					SendMessage message = new SendMessage().setChatId(chatId).setText(answer);
					try {
						execute(message);
						logReceivedMessage(update.getMessage().getChat(), messageText, answer);
					} catch (TelegramApiException e) {
						logger.error(e);
					}
				}
			}
		}
	}

	private TelegramUser createUserIfNotExists(Message message) {
		long chatId = message.getChat().getId();
		TelegramUser telegramUser = telegramUserRepository.findByChatId(chatId)
				.orElse(TelegramUser.builder().chatId(message.getChatId()).firstName(message.getChat().getFirstName())
						.lastName(message.getChat().getLastName()).username(message.getChat().getUserName()).build());
		if (telegramUser.getId() == null) {
			telegramUserRepository.save(telegramUser);
		}

		return telegramUser;
	}

	private void processStart(Update update, long chatId, String messageText) {
		String answer = "Hi, xHumanicus! I can’t do much just yet, but soon I will gain new skills. To register on our forum, a valid email address is mandatory. Please click /share_email_address (will be used in case you want to reset the password).\n"
				+ "By cicking on it you agree with our Terms and Conditions below.\n\n"
				+ "While the administrators and moderators of this forum will attempt to remove or edit any generally objectionable material as quickly as possible, it is impossible to review every message. Therefore you acknowledge that all posts made to these forums this forum express the views and opinions of the authors and not the administrators, moderators or webmaster (except for posts made by these people) and hence will not be held liable.\n\n"
				+ "You agree not to post any abusive, obscene, vulgar, slanderous, hateful, threatening, sexually-oriented or any other material that may violate any applicable laws. Doing so may lead to you being immediately and permanently banned (and your service provider being informed). The IP address of all posts is recorded to aid in the enforcement of these conditions. You agree that the webmaster, administrators and moderators of this forum have the right to remove, edit, move or close any topic at any time they see fit. As a user you agree to any information you have entered above being stored in a database. While this information will not be disclosed to any third party without your consent the webmaster, administrators and moderators cannot be held responsible for any hacking attempt that may lead to the data being compromised.\n\n"
				+ "This forum system uses cookies to store information on your local computer. These cookies do not contain any of the information you have entered above; they serve only to improve your viewing pleasure. The e-mail address is used only for confirmation of your registration details and password (and for sending new passwords, should you forget your current one).\n\n"
				+ "/share_email_address";
		SendMessage message = new SendMessage().setChatId(chatId).setText(answer).setParseMode(ParseMode.HTML);
		try {
			execute(message);
			logReceivedMessage(update.getMessage().getChat(), messageText, answer);
		} catch (TelegramApiException e) {
			logger.error(e);
		}
	}

	private void createForumAccount(Update update, TelegramUser telegramUser, long chatId, String messageText) {
		String password = generatePassword();

		String answer = "Account created.";

		try {
			UserDTO user = createForumUser(telegramUser, password, chatId);
			telegramUser.setForumUserId(user.getUserId());
			telegramUserRepository.save(telegramUser);
			answer += " Username: " + telegramUser.getForumUsername() + ", Pass: " + password + "\n"
					+ "To log in please go to https://forum.xhumanity.org/register/login\n\n"
					+ "Now you can send us links to your promotional videos. Just post the link here and we'll do the rest for you...\n\n"
					+ "For a better experience within our comunity /share_phone_number with us";
		} catch (Exception e) {
			logger.error(e);
			answer = e.getMessage();
		}

		SendMessage msg = new SendMessage().setChatId(chatId).setText(answer);
		try {
			execute(msg);
			logReceivedMessage(update.getMessage().getChat(), messageText, answer);
		} catch (TelegramApiException e) {
			logger.error(e);
		}
	}

	/**
	 * Creates a new forum user. Required parameters are "username", "email" and
	 * "password".
	 * 
	 * @throws Exception
	 */
	public UserDTO createForumUser(TelegramUser telegramUser, String password, long chatId) throws Exception {
		UserDTO user = null;
		try {
			Optional<TelegramUser> searchedUser = telegramUserRepository.findByChatIdAndForumuserIdNotNull(chatId);

			if (searchedUser.isPresent()) {
				throw new APIException(
						"You already created an account with username: " + searchedUser.get().getForumUsername());
			}

			String username = USERNAME_PREFIX + String.format("%05d", telegramUser.getId());

			user = ForumUtils.createUser(username, telegramUser.getForumEmail(), password, forumApiKey);

			telegramUser.setForumUserId(user.getUserId());
			telegramUser.setForumUsername(user.getUsername());
			telegramUser.setForumEmail(user.getEmail());
			telegramUserRepository.save(telegramUser);
		} catch (Exception e) {
			logger.error(e);
			throw e;
		}

		return user;
	}

	private void displayShareNumberOption(Update update, long chatId, String messageText) {
		String answer = "To share your phone number please click on the button bellow.";
		SendMessage message = new SendMessage().setChatId(chatId).setText(answer);
		ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
		List<KeyboardRow> keyboard = new ArrayList<>();
		KeyboardRow row = new KeyboardRow();
		KeyboardButton requestContact = new KeyboardButton("Send my phone number");
		requestContact.setRequestContact(true);
		row.add(requestContact);
		keyboard.add(row);
		keyboardMarkup.setKeyboard(keyboard);
		keyboardMarkup.setResizeKeyboard(true);
		message.setReplyMarkup(keyboardMarkup);
		try {
			execute(message);
			logReceivedMessage(update.getMessage().getChat(), messageText, answer);
		} catch (TelegramApiException e) {
			logger.error(e);
		}
	}

	private void displayShareEmailText(Update update, long chatId, String messageText) {
		String answer = "Enter your email address.";
		SendMessage message = new SendMessage().setChatId(chatId).setText(answer);
		try {
			execute(message);
			logReceivedMessage(update.getMessage().getChat(), messageText, answer);
		} catch (TelegramApiException e) {
			logger.error(e);
		}
	}

	private void processContact(Update update, TelegramUser telegramUser) {
		long chatId = update.getMessage().getChatId();
		String messageText = update.getMessage().getText();

		Contact contact = update.getMessage().getContact();
		logReceivedContact(contact);
		telegramUser.setPhoneNumber(contact.getPhoneNumber());
		telegramUserRepository.save(telegramUser);

		String answer = "Your phone number was added to your profile.";
		SendMessage message = new SendMessage().setChatId(chatId).setText(answer).setParseMode(ParseMode.MARKDOWN);
		message.setReplyMarkup(new ReplyKeyboardRemove());
		try {
			execute(message);
			logReceivedMessage(update.getMessage().getChat(), messageText, answer);
		} catch (TelegramApiException e) {
			logger.error(e);
		}
	}

	private void processYoutubeLink(Update update, TelegramUser telegramUser, long chatId, String videoUrl) {
		final StringBuffer answer = new StringBuffer(
				"Your clip has been taken into account. You can visit our forum to check its status:");
		if (telegramUser.getForumUserId() == null) {
			Utils.replace(answer,
					"We cannot take into account your link. You first need to create an account on our forum /forum_sign_up");
		} else {
			String videoId = YoutubeUtils.getVideoIdFromYoutubeUrl(videoUrl);
			logger.info("videoId = " + videoId);
			try {
				VideoListResponse response = YoutubeUtils.getVideoDetails(videoId, youtubeApiKey);
				logger.info(response);
			} catch (GeneralSecurityException | IOException e) {
				logger.error(e);
			}

			try {
				String postLink = createPost(telegramUser, videoUrl, forumApiKey);
				answer.append(" ").append(postLink);
				logger.info(postLink);
			} catch (Exception e) {
				logger.error(e);
				Utils.replace(answer, "Error occurred while processing your link");
			}
		}
		try {
			SendMessage message = new SendMessage().setChatId(chatId).setText(answer.toString());
			execute(message);
		} catch (TelegramApiException e) {
			logger.error(e);
		}
		logReceivedMessage(update.getMessage().getChat(), videoUrl, answer.toString());
	}

	public String createTopic(TelegramUser user, String url, String forumApiKey) throws Exception {
		int categoryId = YOUTUBE_CAMPAIGN_ID;
		final String title = user.getFirstName() + "'s promotional video";
		final String content = "This is my video. Waiting for your reaction...\\n" + url;

		String topicLink = "Error creating automated post";
		try {
			TopicDTO topic = ForumUtils.createTopic(user.getForumUsername(), categoryId, title, content, forumApiKey);
			topicLink = topic.getURL();
		} catch (Exception e) {
			logger.error(e);
		}
		return topicLink;
	}

	public String createPost(TelegramUser user, String url, String forumApiKey) throws Exception {
		int topicId = PROMO_TOPIC_ID;
		final String subject = user.getFirstName() + "'s promotional video";
		final String message = "This is my video. Waiting for your reaction...\\n" + url;

		String postLink = "Error creating automated post";
		try {
			PostDTO post = ForumUtils.createPost(user.getForumUsername(), topicId, subject, message, forumApiKey);
			postLink = post.getURL();
		} catch (Exception e) {
			logger.error(e);
		}
		return postLink;
	}

	private void processEmailAddress(Update update, TelegramUser telegramUser, long chatId, String email) {
		String answer = "Your e-mail address will be linked to your forum account.\n"
				+ "To create an account click on /forum_sign_up";
		Optional<TelegramUser> existingUser = telegramUserRepository.findByForumEmail(email);
		if (existingUser.isPresent() && !existingUser.get().getId().equals(telegramUser.getId())) {
			answer = "This email address is already registered by another user. Please try with another one.";
		} else {
			logger.info("registering email address = " + email);
			telegramUser.setForumEmail(email);
			telegramUserRepository.save(telegramUser);
		}
		try {
			SendMessage message = new SendMessage().setChatId(chatId).setText(answer);
			execute(message);
		} catch (TelegramApiException e) {
			logger.error(e);
		}
		logReceivedMessage(update.getMessage().getChat(), email, answer);
	}

	@Override
	public String getBotUsername() {
		return BOT_NAME;
	}

	@Override
	public String getBotToken() {
		return telegramToken;
	}

	private String generatePassword() {
		int leftLimit = 48; // numeral '0'
		int rightLimit = 122; // letter 'z'
		int targetStringLength = 10;
		Random random = new Random();

		String generatedString = random.ints(leftLimit, rightLimit + 1)
				.filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97)).limit(targetStringLength)
				.collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append).toString();

		return generatedString;
	}

	private void logReceivedMessage(Chat chat, String txt, String botAnswer) {
		long userId = chat.getId();
		String firstName = chat.getFirstName();
		String lastName = chat.getLastName();
		String userName = chat.getUserName();

		logger.info("\n ----------------------------");
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Date date = new Date();
		logger.info(dateFormat.format(date));
		logger.info("Message from " + firstName + " " + lastName + ". (id = " + userId + " userName = " + userName
				+ ") \n Text - " + txt);
		logger.info("Bot answer: \n Text - " + botAnswer);
	}

	private void logReceivedContact(Contact contact) {
		long userId = contact.getUserID();
		String firstName = contact.getFirstName();
		String lastName = contact.getLastName();
		String phoneNumber = contact.getPhoneNumber();

		logger.info("\n ----------------------------");
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Date date = new Date();
		logger.info(dateFormat.format(date));
		logger.info("Message from " + firstName + " " + lastName + ". (userId = " + userId + ") \n Phone number - "
				+ phoneNumber);
	}
}
