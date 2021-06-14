
import botapi.BotStateContext;
import botapi.TelegramFacade;


import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.*;

public class RaisingBot extends TelegramLongPollingBot {



    private BotStateContext botStateContext = new BotStateContext();
    private TelegramFacade telegramFacade = new TelegramFacade(botStateContext, botStateContext.getUserAdCache());

    @Override
    public String getBotToken() {
        return "1781189373:AAEGPKeKuP1jdYROBhDxQ8zHsSEV7ZC8Wao";
    }

    @Override
    public void onUpdateReceived(Update update) {

        ArrayList<SendMessage> replyMessageToUser = telegramFacade.handleUpdate(update);

        try {

            System.out.println(update);
            try{
                System.out.println(update.getMessage().getFrom().getUserName());
            } catch (Exception e){
                System.out.println("message error");
            }
            try{
                System.out.println(update.getCallbackQuery().getFrom().getUserName());
            } catch (Exception e){
                System.out.println("callbackquery error");
            }
            for (SendMessage message :
                    replyMessageToUser) {
                execute(message);
            }

        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getBotUsername() {
        return "RaisingBot";
    }
}
