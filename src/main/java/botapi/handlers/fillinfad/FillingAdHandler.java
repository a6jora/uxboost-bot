package botapi.handlers.fillinfad;

import botapi.BotState;
import botapi.InputMessageHandler;
import cache.UserAdCache;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

public class FillingAdHandler implements InputMessageHandler {
    private UserAdCache userAdCache;

    public FillingAdHandler(UserAdCache userAdCache) {
        this.userAdCache = userAdCache;
    }

    @Override
    public SendMessage handle(Update update) {
        if (userAdCache.getUsersCurrentBotState(update.getMessage().getFrom().getId()).equals(BotState.ASK_START)) {
            userAdCache.setUserCurrentBotState(update.getMessage().getFrom().getId(), BotState.ASK_AD);
        }
        return processUsersInput(update);
    }

    @Override
    public BotState getHandlerName() {
        return BotState.ASK_START;
    }

    private SendMessage processUsersInput(Update update) {
        Message inputMsg = update.getMessage();
        String usersAnswer = inputMsg.getText();
        int userId = inputMsg.getFrom().getId();
        long chatId = inputMsg.getChatId();

        UserAd ad = userAdCache.getUserAd(userId);
        BotState botState = userAdCache.getUsersCurrentBotState(userId);

        SendMessage replyToUser = null;

        if (botState.equals(BotState.ASK_OPTION)) {
            replyToUser = new SendMessage(chatId, "Выберите команду:");

            userAdCache.setUserCurrentBotState(userId, BotState.ASK_AD);
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
                    "Ваше объявление:\n"+userAdCache.getUserAd(userId).toString()+"\nразмещено");
            replyToUser.setReplyMarkup(getInlineMessageButton());
            userAdCache.setUserCurrentBotState(userId, BotState.ASK_TO_SEND);
        }
        if (botState.equals(BotState.ASK_TO_SEND)) {
            ad.setDeadline(inputMsg.getText());
            replyToUser = new SendMessage(chatId,
                    "Ваше объявление:\n"+userAdCache.getUserAd(userId).toString()+"\nразмещено");

            userAdCache.setUserCurrentBotState(userId, BotState.ASK_START);
        }
        userAdCache.saveUserAd(userId, ad);
        return replyToUser;
    }

    private InlineKeyboardMarkup getInlineMessageButton(){
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
}
