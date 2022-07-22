package com.nyan.cckmenubot.bots;

import java.util.List;

import com.nyan.cckmenubot.config.BotConfig;
import com.nyan.cckmenubot.handlers.MainHandler;
import com.nyan.cckmenubot.repositories.PhotoRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMediaGroup;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
public class CCKMenuBot extends TelegramLongPollingBot{
	
	@Autowired
	private MainHandler handler;
	@Autowired
	private PhotoRepository photoRepository;
	
	@Override
	public String getBotUsername() {
		return BotConfig.BOT_USERNAME;
	}

	@Override
	public String getBotToken() {
		return BotConfig.BOT_TOKEN;
	}
	
	@Override
	public void onUpdateReceived(Update update) {
		// SendMessage message = mainHandler.handleUpdate(update);
		System.out.println("Update received!");
				
		if (update.hasCallbackQuery()) {
			if(update.getCallbackQuery().getData().contains("location")) {
				EditMessageText message = handler.handleLocationUpdate(update);
				try {
					execute(message);
				} catch (TelegramApiException e) {
					e.printStackTrace();
				}
			} else {
				String callData = update.getCallbackQuery().getData().substring(7);
				if(photoRepository.findByStallName(callData).size() < 2) {
					SendPhoto message = handler.handleStallUpdate(update);
					try {
						execute(message);
					} catch (TelegramApiException e) {
						e.printStackTrace();
					}
				} else {
					SendMediaGroup message = handler.handleStallUpdateMultiple(update);
					try {
						execute(message);
					} catch (TelegramApiException e) {
						e.printStackTrace();
					}
				}
			}
			
		} else if(update.hasMessage() && update.getMessage().hasText()) {
			SendMessage message = handler.handleTextUpdate(update);
			try {
				execute(message);
			} catch (TelegramApiException e) {
				e.printStackTrace();
			} 
		} 

		System.out.println("Update processed!");
	}



}