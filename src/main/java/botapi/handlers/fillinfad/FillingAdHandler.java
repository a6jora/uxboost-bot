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

public class FillingAdHandler implements InputMessageHandler {
    private UserAdCache userAdCache;
    private HashMap<String, ArrayList<String>> comments = new HashMap<>();
    private HashMap<String, Long> ads = new HashMap<>();
    private ArrayList<Long> banList = new ArrayList<>();
    private final String[] texts = {"Выберите команду:", "Вы добавлены в бан-лист. Обратитесь к администратору","Введите текст объявления:",
            "Ваши пожелания к респондентам и вопросы к опросу:",
            "Предполагаемая продолжительность опроса:","Временные слоты для проведения опроса:",
            "Ограничения для респондентов (местоположение, возраст и т.п.)",
            "Контакты — куда откликаться, где заполнять вашу анкету и пр.:","Дата, до которой объявление актуально:",""};


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
                messageList.add(sendAdToChannel(userAdCache.getUserAd(userId).toString().trim(), chatId));
                userAdCache.setUserCurrentBotState(userId, BotState.ASK_START);
                replyToUser = new SendMessage(chatId, texts[0]);
                replyToUser.setReplyMarkup(getInlineAskMessageButton());

            } else if (callbackQuery.getData().equals("buttonAd")) {

                replyToUser = new SendMessage(chatId, texts[2]);
                userAdCache.setUserCurrentBotState(userId, BotState.ASK_GLADS);
                messageList.add(replyToUser);
                return messageList;
            } else if (callbackQuery.getData().equals("buttonComment")) {
                userAdCache.setUserCurrentBotState(userId, BotState.ASK_OPTION);
                messageList = getComments(chatId);
                replyToUser = new SendMessage(chatId, texts[0]);
                replyToUser.setReplyMarkup(getInlineAskMessageButton());
                if (update.getCallbackQuery().getFrom().getUserName().equals("Stlts")) {
                    replyToUser.setReplyMarkup(getInlineAdminMessageButton());
                }
            } else if (callbackQuery.getData().equals("buttonBanHammer")) {
                userAdCache.setUserCurrentBotState(userId, BotState.ASK_TO_BAN);

                messageList.add(new SendMessage(chatId, "Введите полный текст объявления пользователя для бана"));
                return messageList;
            } else if (callbackQuery.getData().equals("buttonUnBanHammer")) {
                userAdCache.setUserCurrentBotState(userId, BotState.ASK_TO_UNBAN);

                messageList.add(new SendMessage(chatId, "Введите id чата:"));
                return messageList;
            } else if (callbackQuery.getData().equals("buttonBanList")) {
                userAdCache.setUserCurrentBotState(userId, BotState.ASK_OPTION);
                String str ="Бан-лист:\n";
                if (banList.isEmpty())
                {
                    str = "Бан-лист пуст";
                }
                for (long i :
                        banList) {
                    str+="- " +i + "\n";
                }
                messageList.add(new SendMessage(chatId, str));
                return messageList;
            } else {
                replyToUser = new SendMessage(chatId, texts[0]);

                userAdCache.setUserCurrentBotState(userId, BotState.ASK_START);
                replyToUser.setReplyMarkup(getInlineAskMessageButton());
            }
            if (update.getCallbackQuery().getFrom().getUserName().equals("Stlts")) {
                replyToUser.setReplyMarkup(getInlineAdminMessageButton());
            }
            messageList.add(replyToUser);
            return messageList;
        }

        Message inputMsg = update.getMessage();
        String usersAnswer = inputMsg.getText();


        if (inputMsg.isReply()) {

            String reply = inputMsg.getReplyToMessage().getText();

            try {
                comments.get(reply).add(inputMsg.getText());
            } catch (Exception ex) {

            }
            replyToUser = new SendMessage(ads.get(reply), inputMsg.getText());
            messageList.add(replyToUser);
            return messageList;
        }
        int userId = inputMsg.getFrom().getId();
        long chatId = inputMsg.getChatId();


        UserAd ad = userAdCache.getUserAd(userId);
        BotState botState = userAdCache.getUsersCurrentBotState(userId);
        try {

            for (long i :
                    banList) {
                if (i == update.getMessage().getChatId()) {
                    messageList.add(new SendMessage(chatId, texts[1]));
                    return messageList;
                }
            }
        }
        catch (Exception e){
        }
        if (botState.equals(BotState.ASK_TO_BAN)) {
            try {

                long chatToBan = ads.get(update.getMessage().getText());

                banList.add(chatToBan);
                messageList.add(new SendMessage(chatId, "Пользователь с id: " + chatToBan + " добавлен в бан-лист"));
                userAdCache.setUserCurrentBotState(userId, BotState.ASK_OPTION);
                botState = BotState.ASK_OPTION;
            }
            catch (Exception e){
                messageList.add( new SendMessage(chatId,"Некорректный id"));
                userAdCache.setUserCurrentBotState(userId, BotState.ASK_OPTION);
                botState = BotState.ASK_OPTION;
            }
        }
        if (botState.equals(BotState.ASK_TO_UNBAN)) {
            try {
                int chatToBan = Integer.parseInt(update.getMessage().getText());

                for (int i = 0; i < banList.size(); i++) {
                    if (banList.get(i) == chatToBan) {
                        banList.remove(i);
                        i--;
                    }
                }
                messageList.add(new SendMessage(chatId, "Пользователь с id: " + chatToBan + " удален из бан-листа"));
                userAdCache.setUserCurrentBotState(userId, BotState.ASK_OPTION);
                botState = BotState.ASK_OPTION;
            } catch (Exception ex){
                messageList.add( new SendMessage(chatId,"Некорректный id"));
                userAdCache.setUserCurrentBotState(userId, BotState.ASK_OPTION);
                botState = BotState.ASK_OPTION;
            }

        }
        if (botState.equals(BotState.ASK_OPTION)) {

            replyToUser = new SendMessage(chatId, texts[0]);
            replyToUser.setReplyMarkup(getInlineAskMessageButton());
            for (long i :
                    banList) {
                if (i == update.getMessage().getChatId()) {
                    messageList.add(new SendMessage(chatId, texts[1]));
                    return messageList;
                }
            }
            if (update.getMessage().getFrom().getUserName().equals("Stlts")) {
                replyToUser.setReplyMarkup(getInlineAdminMessageButton());
            }
        }
        if (botState.equals(BotState.ASK_AD)) {
            replyToUser = new SendMessage(chatId, texts[2]);

            userAdCache.setUserCurrentBotState(userId, BotState.ASK_GLADS);
        }
        if (botState.equals(BotState.ASK_GLADS)) {
            replyToUser = new SendMessage(chatId, texts[3]);
            ad.setAdText(inputMsg.getText());
            userAdCache.setUserCurrentBotState(userId, BotState.ASK_TIME_FOR);
        }
        if (botState.equals(BotState.ASK_TIME_FOR)) {
            replyToUser = new SendMessage(chatId,
                    texts[4]);
            ad.setGlads(inputMsg.getText());
            userAdCache.setUserCurrentBotState(userId, BotState.ASK_TIME_SLOTS);
        }
        if (botState.equals(BotState.ASK_TIME_SLOTS)) {
            replyToUser = new SendMessage(chatId,
                    texts[5]);
            ad.setTimeFor(inputMsg.getText());
            userAdCache.setUserCurrentBotState(userId, BotState.ASK_REST);
        }
        if (botState.equals(BotState.ASK_REST)) {
            replyToUser = new SendMessage(chatId,
                    texts[6]);
            ad.setTimeSlots(inputMsg.getText());
            userAdCache.setUserCurrentBotState(userId, BotState.ASK_CONTACTS);
        }
        if (botState.equals(BotState.ASK_CONTACTS)) {
            replyToUser = new SendMessage(chatId,
                    texts[7]);
            ad.setRest(inputMsg.getText());
            userAdCache.setUserCurrentBotState(userId, BotState.ASK_DEADLINE);
        }
        if (botState.equals(BotState.ASK_DEADLINE)) {
            replyToUser = new SendMessage(chatId,
                    texts[8]);
            ad.setContacts(inputMsg.getText());
            userAdCache.setUserCurrentBotState(userId, BotState.ASK_TO_POST);
        }
        if (botState.equals(BotState.ASK_TO_POST)) {
            ad.setDeadline(inputMsg.getText());
            replyToUser = new SendMessage(chatId,
                    "Ваше объявление:\n" + userAdCache.getUserAd(userId).toString() + "\nРазместить?");
            replyToUser.setReplyMarkup(getInlineSendMessageButton());
            userAdCache.setUserCurrentBotState(userId, BotState.WAITING);
        }
        if (botState.equals(BotState.WAITING)) {
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

    private InlineKeyboardMarkup getInlineAdminMessageButton() {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        InlineKeyboardButton buttonAd = new InlineKeyboardButton().setText("Добавить Объявление");
        InlineKeyboardButton buttonComment = new InlineKeyboardButton().setText("Комментарии");
        buttonAd.setCallbackData("buttonAd");
        buttonComment.setCallbackData("buttonComment");
        List<InlineKeyboardButton> row1 = new ArrayList<>();
        row1.add(buttonComment);
        row1.add(buttonAd);

        InlineKeyboardButton buttonBanlist = new InlineKeyboardButton().setText("Бан-лист");
        InlineKeyboardButton buttonBanhammer = new InlineKeyboardButton().setText("Забанить");
        InlineKeyboardButton buttonUnBanhammer = new InlineKeyboardButton().setText("Разбанить");
        buttonBanlist.setCallbackData("buttonBanList");
        buttonBanhammer.setCallbackData("buttonBanHammer");
        buttonUnBanhammer.setCallbackData("buttonUnBanHammer");

        List<InlineKeyboardButton> row2 = new ArrayList<>();
        row2.add(buttonBanlist);
        row2.add(buttonBanhammer);
        row2.add(buttonUnBanhammer);
        List<List<InlineKeyboardButton>> list = new ArrayList<>();
        list.add(row1);
        list.add(row2);
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
        ads.put(adText, chatId);
        return outMessageToChannel;
    }

    private ArrayList<SendMessage> getComments(long chatId) {
        ArrayList<SendMessage> messages = new ArrayList<>();
        SendMessage outMessage = new SendMessage();
        outMessage.setChatId(chatId);
        try {
            for (Map.Entry<String, Long> adWithId :
                    ads.entrySet()) {
                if (adWithId.getValue().equals(chatId)) {
                    ArrayList<String> list = comments.get(adWithId.getKey());
                    StringBuilder row = new StringBuilder(adWithId.getKey());
                    row.append("\nКомментарии:");
                    if (list.isEmpty()){
                        row.append("\n(комментарии отсутствуют)");
                    }
                    for (String comment :
                            list) {
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
