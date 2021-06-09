package botapi;

import cache.UserAdCache;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.ArrayList;

public class TelegramFacade {
    private BotStateContext botStateContext;
    private UserAdCache userAdCache;

    public TelegramFacade(BotStateContext botStateContext, UserAdCache userAdCache) {
        this.botStateContext = botStateContext;
        this.userAdCache = userAdCache;
    }

    public ArrayList<SendMessage> handleUpdate(Update update){
        SendMessage replyMessage = null;
        ArrayList<SendMessage> messageArrayList = new ArrayList<>();
        Message message = update.getMessage();

        if (message != null && message.hasText()&&(!message.isSuperGroupMessage()||message.isReply())||update.hasCallbackQuery()) {
            messageArrayList = handleInputMessage(update);
        }


        return messageArrayList;
    }

    private ArrayList<SendMessage> handleInputMessage(Update update) {
        ArrayList<SendMessage> messagesList = new ArrayList<>();
        Message message = update.getMessage();
        String inputMsg = "null";
        int userId ;
        if (update.hasCallbackQuery()){
            userId = update.getCallbackQuery().getFrom().getId();
        }
        else {
            message.getText();
            userId = message.getFrom().getId();
        }
        BotState botState = BotState.ASK_START;
        SendMessage replyMessage = null;

        switch (inputMsg) {
            case "/start":
                botState = BotState.ASK_START;
                break;
            case "/ad":
                botState = BotState.ASK_AD;
                break;
            default:
                botState = userAdCache.getUsersCurrentBotState(userId);
                break;
        }

        userAdCache.setUserCurrentBotState(userId, botState);

        messagesList = botStateContext.processInputMessage(botState, update);

        return messagesList;
    }
}
