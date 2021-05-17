package botapi.handlers.fillinfad;

import botapi.BotState;
import botapi.InputMessageHandler;
import cache.UserAdCache;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Handler;

public class FillingAdHandler implements InputMessageHandler {
    private UserAdCache userAdCache;
    private HashMap<String, ArrayList<String>> comments = new HashMap<>();
    private HashMap<String, Long> ads = new HashMap<>();

    public FillingAdHandler(UserAdCache userAdCache) {
        this.userAdCache = userAdCache;
    }

    @Override
    public ArrayList<SendMessage> handle(Update update) {
        if (update.hasCallbackQuery()) {
            if (userAdCache.getUsersCurrentBotState(update.getCallbackQuery().getFrom().getId()).equals(BotState.ASK_START)) {
                userAdCache.setUserCurrentBotState(update.getCallbackQuery().getFrom().getId(), BotState.ASK_OPTION);
            }

            return processUsersInput(update);
        }
        if (userAdCache.getUsersCurrentBotState(update.getMessage().getFrom().getId()).equals(BotState.ASK_START)) {
            userAdCache.setUserCurrentBotState(update.getMessage().getFrom().getId(), BotState.ASK_OPTION);
        }
        return processUsersInput(update);
    }

    @Override
    public BotState getHandlerName() {
        return BotState.ASK_START;
    }

    private ArrayList<SendMessage> processUsersInput(Update update) {
        ArrayList<SendMessage> messageList = new ArrayList<>();
        SendMessage replyToUser = null;

        if (update.hasCallbackQuery()) {
            int userId = update.getCallbackQuery().getFrom().getId();
            long chatId = update.getCallbackQuery().getMessage().getChatId();
            CallbackQuery callbackQuery = update.getCallbackQuery();
            if (callbackQuery.getData().equals("buttonYes")) {
                replyToUser = new SendMessage(chatId,
                        "Ваше объявление:\n" + userAdCache.getUserAd(userId).toString() + "\nразмещено");
                messageList.add(replyToUser);
                messageList.add(sendAdToChannel(userAdCache.getUserAd(userId).toString(), chatId));
                userAdCache.setUserCurrentBotState(userId, BotState.ASK_START);
                replyToUser = new SendMessage(chatId, "Выберите команду:");
                replyToUser.setReplyMarkup(getInlineAskMessageButton());

            } else if (callbackQuery.getData().equals("buttonAd")) {

                replyToUser = new SendMessage(chatId, "Текст объявления");
                userAdCache.setUserCurrentBotState(userId, BotState.ASK_GLADS);
            } else if (callbackQuery.getData().equals("buttonComment")) {
                userAdCache.setUserCurrentBotState(userId, BotState.ASK_OPTION);
                messageList = getComments(chatId);
                replyToUser = new SendMessage(chatId, "Выберите команду:");
                replyToUser.setReplyMarkup(getInlineAskMessageButton());
            } else {
                replyToUser = new SendMessage(chatId, "Выберите команду:");

                userAdCache.setUserCurrentBotState(userId, BotState.ASK_OPTION);
            }
            messageList.add(replyToUser);
            return messageList;
        }

        Message inputMsg = update.getMessage();
        String usersAnswer = inputMsg.getText();


        if (inputMsg.isReply()) {
            System.out.println("=+++===" + inputMsg.getText());
            String reply = inputMsg.getReplyToMessage().getText()+"\n";
            System.out.println("\n+===" + reply+"****\n");
            try {
                comments.get(reply).add(inputMsg.getText());
            } catch (Exception ex) {
                System.out.println("didnt work");
            }
            System.out.println("comment: " + inputMsg.getText() + " to: " + reply + " is added");
            replyToUser = new SendMessage(ads.get(reply), inputMsg.getText());
            messageList.add(replyToUser);
            return messageList;
        }
        int userId = inputMsg.getFrom().getId();
        long chatId = inputMsg.getChatId();



        UserAd ad = userAdCache.getUserAd(userId);
        BotState botState = userAdCache.getUsersCurrentBotState(userId);


        if (botState.equals(BotState.ASK_OPTION)) {
            replyToUser = new SendMessage(chatId, "Выберите команду:");
            replyToUser.setReplyMarkup(getInlineAskMessageButton());
        }
        if (botState.equals(BotState.ASK_AD)) {
            replyToUser = new SendMessage(chatId, "Текст объявления");

            userAdCache.setUserCurrentBotState(userId, BotState.ASK_GLADS);
        }
        if (botState.equals(BotState.ASK_GLADS)) {
            replyToUser = new SendMessage(chatId, "Пожелания к респондентам и что хотите сделать на созвоне");
            ad.setAdText(inputMsg.getText());
            userAdCache.setUserCurrentBotState(userId, BotState.ASK_TIME_FOR);
        }
        if (botState.equals(BotState.ASK_TIME_FOR)) {
            replyToUser = new SendMessage(chatId,
                    "Время, которое по вашему мнению нужно на интервью/опрос/тест — чтобы ваш респондент мог планировать свой график");
            ad.setGlads(inputMsg.getText());
            userAdCache.setUserCurrentBotState(userId, BotState.ASK_TIME_SLOTS);
        }
        if (botState.equals(BotState.ASK_TIME_SLOTS)) {
            replyToUser = new SendMessage(chatId,
                    "Временные слоты в которые хотите провести созвон — чтобы другим участникам сразу было легче ориентироваться");
            ad.setTimeFor(inputMsg.getText());
            userAdCache.setUserCurrentBotState(userId, BotState.ASK_REST);
        }
        if (botState.equals(BotState.ASK_REST)) {
            replyToUser = new SendMessage(chatId,
                    "Если для вас важно гео, или есть другие ограничения — не забудьте указать");
            ad.setTimeSlots(inputMsg.getText());
            userAdCache.setUserCurrentBotState(userId, BotState.ASK_CONTACTS);
        }
        if (botState.equals(BotState.ASK_CONTACTS)) {
            replyToUser = new SendMessage(chatId,
                    "Контакты — куда откликаться, где заполнять вашу анкету и пр.");
            ad.setRest(inputMsg.getText());
            userAdCache.setUserCurrentBotState(userId, BotState.ASK_DEADLINE);
        }
        if (botState.equals(BotState.ASK_DEADLINE)) {
            replyToUser = new SendMessage(chatId,
                    "До какого числа актуально");
            ad.setContacts(inputMsg.getText());
            userAdCache.setUserCurrentBotState(userId, BotState.ASK_TO_POST);
        }
        if (botState.equals(BotState.ASK_TO_POST)) {
            ad.setDeadline(inputMsg.getText());
            replyToUser = new SendMessage(chatId,
                    "Ваше объявление:\n" + userAdCache.getUserAd(userId).toString() + "\nРазместить?");
            replyToUser.setReplyMarkup(getInlineSendMessageButton());

        }

        userAdCache.saveUserAd(userId, ad);
        messageList.add(replyToUser);
        return messageList;
    }


    private InlineKeyboardMarkup getInlineSendMessageButton() {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        InlineKeyboardButton buttonYes = new InlineKeyboardButton().setText("Да");
        InlineKeyboardButton buttonNo = new InlineKeyboardButton().setText("Нет");
        buttonYes.setCallbackData("buttonYes");
        buttonNo.setCallbackData("ButtonNo");

        List<InlineKeyboardButton> row = new ArrayList<>();
        row.add(buttonNo);
        row.add(buttonYes);
        List<List<InlineKeyboardButton>> list = new ArrayList<>();
        list.add(row);
        inlineKeyboardMarkup.setKeyboard(list);
        return inlineKeyboardMarkup;
    }

    private InlineKeyboardMarkup getInlineAskMessageButton() {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        InlineKeyboardButton buttonAd = new InlineKeyboardButton().setText("Добавить Объявление");
        InlineKeyboardButton buttonComment = new InlineKeyboardButton().setText("Комментарии");
        buttonAd.setCallbackData("buttonAd");
        buttonComment.setCallbackData("buttonComment");

        List<InlineKeyboardButton> row = new ArrayList<>();
        row.add(buttonComment);
        row.add(buttonAd);
        List<List<InlineKeyboardButton>> list = new ArrayList<>();
        list.add(row);
        inlineKeyboardMarkup.setKeyboard(list);
        return inlineKeyboardMarkup;
    }

    public SendMessage sendAdToChannel(String adText, Long chatId) {

        SendMessage outMessageToChannel = new SendMessage();
        BigInteger id = new BigInteger("-1001256495856");
        outMessageToChannel.setChatId(id.longValue());
        outMessageToChannel.setText(adText);

        comments.put(adText, new ArrayList<>());
        System.out.println("===\n" + adText+"====\n");
        ads.put(adText, chatId);
        System.out.println("ad is added:" + adText+" with" + chatId);
        return outMessageToChannel;
    }

    private ArrayList<SendMessage> getComments(long chatId) {
        ArrayList<SendMessage> messages = new ArrayList<>();
        System.out.println("problem part");
        SendMessage outMessage = new SendMessage();
        outMessage.setChatId(chatId);
        try {
            for (Map.Entry<String, Long> adWithId :
                    ads.entrySet()) {
                System.out.println("ad: "+ adWithId.getKey()+ " id: "+ adWithId.getValue());
                if (adWithId.getValue().equals(chatId)) {
                    ArrayList<String> list = comments.get(adWithId.getKey());
                    StringBuilder row = new StringBuilder(adWithId.getKey());
                    row.append(":");
                    for (String comment :
                            list) {
                        System.out.println(comment);
                        row.append("\n-");
                        row.append(comment);
                    }
                    outMessage.setText(row.toString());
                    messages.add(outMessage);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return messages;
    }
}
