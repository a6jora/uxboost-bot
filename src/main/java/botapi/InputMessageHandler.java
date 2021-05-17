package botapi;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.ArrayList;

public interface InputMessageHandler {
    ArrayList<SendMessage> handle(Update update);
    BotState getHandlerName();
}
