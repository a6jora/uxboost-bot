package botapi.handlers.askad;

import botapi.BotState;
import botapi.InputMessageHandler;
import cache.UserAdCache;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

public class AskAdHandler implements InputMessageHandler {
    private UserAdCache userAdCache;

    public AskAdHandler(UserAdCache userAdCache){
        this.userAdCache = userAdCache;
    }


    @Override
    public SendMessage handle(Message message) {
        return processUsersInput(message);
    }

    @Override
    public BotState getHandlerName() {
        return BotState.ASK_START;
    }

    private SendMessage processUsersInput(Message inputMsg) {
        int userId = inputMsg.getFrom().getId();
        long chatId = inputMsg.getChatId();

        SendMessage replyToUser = new SendMessage(chatId,
                "Бот для размещения объявлений");
        userAdCache.setUserCurrentBotState(userId,BotState.ASK_START);

        return replyToUser;
    }
}
