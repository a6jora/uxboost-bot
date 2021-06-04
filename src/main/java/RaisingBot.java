
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
        System.out.println("update getted");

        ArrayList<SendMessage> replyMessageToUser = telegramFacade.handleUpdate(update);

        try {

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


//    public void sendAd(Message adMessage) {
//        try {
//            SendMessage outMessageToChannel = new SendMessage();
//
//            BigInteger id = new BigInteger("-1001256495856");
//            outMessageToChannel.setChatId(id.longValue());
//            outMessageToChannel.setText(adMessage.getText().substring(4));
//            execute(outMessageToChannel);
//            SendMessage outMessageToUser = new SendMessage();
//            outMessageToChannel.setChatId(adMessage.getChatId());
//            outMessageToChannel.setText(adMessage.getText().substring(4));
//            execute(outMessageToChannel);
//            comments.put(outMessageToChannel.getText(), new ArrayList<>());
//            ads.put(outMessageToChannel.getText(), adMessage.getChatId());
//            System.out.println("ad is added:" + outMessageToChannel.getText());
//        } catch (TelegramApiException e) {
//            e.printStackTrace();
//        }
//    }

//    public void getComments(Message inMessage) {
//        System.out.println("problem part");
//        SendMessage outMessage = new SendMessage();
//        outMessage.setChatId(inMessage.getChatId());
//        try {
//            for (Map.Entry<String, Long> adWithId :
//                    ads.entrySet()) {
//                System.out.println("ad: "+ adWithId.getKey()+ " id: "+ adWithId.getValue());
//                if (adWithId.getValue().equals(inMessage.getChatId())) {
//                    ArrayList<String> list = comments.get(adWithId.getKey());
//                    StringBuilder row = new StringBuilder(adWithId.getKey());
//                    row.append(":");
//                    for (String comment :
//                            list) {
//                        System.out.println(comment);
//                        row.append("\n-");
//                        row.append(comment);
//                    }
//                    outMessage.setText(row.toString());
//                    execute(outMessage);
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//    }


//проверяем есть ли сообщение и текстовое ли оно
//        if (update.hasMessage() && update.getMessage().hasText()) {
//                Message inMessage = update.getMessage();
//                if (update.getMessage().getText().toLowerCase().startsWith("/ad ")) {
//                //Извлекаем объект входящего сообщения
//                  try {
//                     sendAd(inMessage);
//                   } catch (Exception e) {
//                    e.printStackTrace();
//                  }
//                }
//                if (update.getMessage().getText().toLowerCase().startsWith("/wtf ")) {
    //                System.out.println("use /comments");
    //                try{
    //                for (int i = 0; i < 20; i++) {
    //                  String string = update.getChannelPost().getEntities().get(i).getText();
    //                  SendMessage outMessage = new SendMessage();
    //                  outMessage.setChatId(inMessage.getChatId());
    //                  outMessage.setText(string);
    //                  execute(outMessage);
//                    }
            //        }catch (Exception e ){
            //        e.printStackTrace();
            //        }
//              }
//        if (inMessage.isReply()) {
//        System.out.println(inMessage);
//        String reply = inMessage.getReplyToMessage().getText();
//        try {
//        comments.get(reply).add(inMessage.getText());
//        }
//        catch (Exception ex){}
//        System.out.println("comment: "+ inMessage.getText() +" to: "+ reply + " is added" );
//        }
//
//        }
