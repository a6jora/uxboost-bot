package botapi;

import cache.UserAdCache;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

public class TelegramFacade {
    private BotStateContext botStateContext;
    private UserAdCache userAdCache;

    public TelegramFacade(BotStateContext botStateContext, UserAdCache userAdCache) {
        this.botStateContext = botStateContext;
        this.userAdCache = userAdCache;
    }

    public SendMessage handleUpdate(Update update){
        SendMessage replyMessage = null;

        Message message = update.getMessage();
        if (message != null && message.hasText()||update.hasCallbackQuery()) {
            replyMessage = handleInputMessage(update);
        }
        System.out.println("message HandleUpdate");
        return replyMessage;
    }

    private SendMessage handleInputMessage(Update update) {
        Message message = update.getMessage();
        System.out.println("message HandleInput");
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
                System.out.println("ad added");
                break;
            default:
                botState = userAdCache.getUsersCurrentBotState(userId);
                break;
        }

        userAdCache.setUserCurrentBotState(userId, botState);

        replyMessage = botStateContext.processInputMessage(botState, update);

        return replyMessage;
    }
}
