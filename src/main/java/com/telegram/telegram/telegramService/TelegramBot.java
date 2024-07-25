package com.telegram.telegram.telegramService;

import com.telegram.telegram.Service.linkService;
import com.telegram.telegram.entitys.link;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.util.HashMap;
import java.util.Map;

@Service
public class TelegramBot extends TelegramLongPollingBot {
    @Autowired
    private linkService linkService;

    private Map<Long, ChatState> chatStates = new HashMap<>();

    private static class ChatState {
        String step;
        link link;
    }

    @Override
    public void onUpdateReceived(Update update) {
        final long chatId = update.getMessage().getChatId();
        String messageText = update.getMessage().getText();
        chatStates.putIfAbsent(chatId, new ChatState());

        ChatState chatState = chatStates.get(chatId);

        if ("INITIAL".equals(chatState.step) || chatState.step == null) {
            String mainMessage = """
                Mensaje Desde MARCELO
                Saludo =>Escribe 1
                Guardar SX =>Escribe 2
                Listar sx =>Escribe 3
                """;
            EnviarMensajeGold(chatId, mainMessage);
            chatState.step = "MAIN_MENU";
        } else {
            handleStep(chatId, messageText, chatState);
        }
    }

    private void handleStep(long chatId, String messageText, ChatState chatState) {
        switch (chatState.step) {
            case "MAIN_MENU":
                switch (messageText) {
                    case "1":
                        EnviarMensajeGold(chatId, "Ingresa tu nombre BRO 🐇 🐇");
                        chatState.step = "ASK_NAME";
                        break;
                    case "2":
                        chatState.link = new link();
                        EnviarMensajeGold(chatId, "Ingresa el nombre de tu video");
                        chatState.step = "ASK_VIDEO_NAME";
                        break;
                    case "3":
                        EnviarMensajeGold(chatId, "Listando videos");
                        EnviarMensajeGold(chatId, linkService.getLinks().toString());
                        chatState.step = "INITIAL";
                        break;
                    default:
                        EnviarMensajeGold(chatId, "Comando no válido, prueba con 1, 2 o 3");
                }
                break;
            case "ASK_NAME":
                EnviarMensajeGold(chatId, "Hola Amigo " + messageText + " Yo soy Marcelo El mas veloz");
                chatState.step = "INITIAL";
                break;
            case "ASK_VIDEO_NAME":
                chatState.link.setNombre(messageText);
                EnviarMensajeGold(chatId, "Ingrese la url de tu video");
                chatState.step = "ASK_VIDEO_URL";
                break;
            case "ASK_VIDEO_URL":
                chatState.link.setLink(messageText);
                linkService.Guardar(chatState.link);
                EnviarMensajeGold(chatId, "Video guardado correctamente.");
                chatState.step = "INITIAL";
                break;
        }
    }

    private void EnviarMensajeGold(long chatId, String mensaje) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(mensaje);
        try {
            execute(message);
        } catch (TelegramApiException t) {
            System.out.println("Error executing");
        }
    }

    @Override
    public String getBotUsername() {
        return "marcelocaramelo1_bot";
    }

    @Override
    public String getBotToken() {
        // Regenera y coloca tu nuevo token aquí
        return "7277026924:AAEUCRfSlUvIzzptqyhpJ9-QGXTENgf0U1E";
    }

    @PostConstruct
    public void registerBot() {
        try {
            TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
            telegramBotsApi.registerBot(this);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
