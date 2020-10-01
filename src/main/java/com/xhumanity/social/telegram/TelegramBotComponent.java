package com.xhumanity.social.telegram;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.BotSession;

import com.xhumanity.social.repository.TelegramUserRepository;
import com.xhumanity.social.service.VideoRegistrationService;

@Component
public class TelegramBotComponent {

	private static final Logger logger = LogManager.getLogger(TelegramBotComponent.class);

	@Autowired
	private VideoRegistrationService videoRegistrationService;
	@Autowired
	private TelegramUserRepository telegramUserRepository;

	@Value("${telegram.token}")
	private String telegramToken;

	@Value("${forum.api.key}")
	private String forumApiKey;
	
	@Value("${youtube.api.key}") 
	private String youtubeApiKey;

	private BotSession botSession = null;

	@PostConstruct
	public void registerBot() throws Exception {
		logger.info("Telegram Bot starting...");
		logger.info("api key: " + telegramToken);

		TelegramBotsApi botsApi = new TelegramBotsApi();

		Thread thread = new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					Thread.sleep(5000);
					ApiContextInitializer.init();
					botSession = botsApi.registerBot(new TelegramBot(videoRegistrationService, telegramUserRepository, telegramToken, forumApiKey, youtubeApiKey));
				} catch (TelegramApiException | InterruptedException e) {
					e.printStackTrace();
				}
			}
		});
		thread.start();
	}

	public void destroy() throws Exception {
		if (botSession != null) {
			botSession.stop();
			logger.info("Telegram Bot stopped.");
		}

		logger.info("Destroying JForum...");
	}
}