package botapi;

import botapi.handlers.askad.AskAdHandler;
import botapi.handlers.fillinfad.FillingAdHandler;
import cache.UserAdCache;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BotStateContext {
    private Map<BotState, InputMessageHandler> messageHandlers = new HashMap<>();
    private UserAdCache userAdCache = new UserAdCache();

    public UserAdCache getUserAdCache() {
        return userAdCache;
    }

    public void setUserAdCache(UserAdCache userAdCache) {
        this.userAdCache = userAdCache;
    }

    public BotStateContext(){
        List<InputMessageHandler> messageHandlers = new ArrayList<>();
        messageHandlers.add(new AskAdHandler(userAdCache));
        messageHandlers.add(new FillingAdHandler(userAdCache));
        messageHandlers.forEach(handler -> this.messageHandlers.put(handler.getHandlerName(),handler));
    }
    public ArrayList<SendMessage> processInputMessage(BotState currentState, Update update){
        InputMessageHandler currentMessageHandler = findMessageHandler(currentState);
        return currentMessageHandler.handle(update);
    }

    private InputMessageHandler findMessageHandler(BotState currentState){
        if (isFillingAd(currentState)){
            return messageHandlers.get(BotState.ASK_START);
        }
        return messageHandlers.get(currentState);
    }

    private boolean isFillingAd(BotState currentState){
        switch (currentState){
            case ASK_START:
            case ASK_OPTION:
            case ASK_TO_SEND:
            case ASK_AD:
            case ASK_GLADS:
            case ASK_TIME_FOR:
            case ASK_TIME_SLOTS:
            case ASK_REST:
            case ASK_CONTACTS:
            case ASK_DEADLINE:
            case ASK_TO_POST:
            case ASK_TO_BAN:
            case ASK_TO_UNBAN:
            case WAITING:
                return true;
            default:
                return false;
        }
    }
}
