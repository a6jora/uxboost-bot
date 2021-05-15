import org.telegram.api.auth.TLCheckedPhone;
import org.telegram.api.chat.TLAbsChat;
import org.telegram.api.chat.channel.TLChannel;
import org.telegram.api.engine.ApiCallback;
import org.telegram.api.engine.AppInfo;
import org.telegram.api.engine.TelegramApi;
import org.telegram.api.engine.storage.AbsApiState;
import org.telegram.api.functions.auth.TLRequestAuthCheckPhone;
import org.telegram.api.message.TLAbsMessage;
import org.telegram.api.message.TLMessage;
import org.telegram.api.messages.TLMessages;
import org.telegram.api.updates.TLAbsUpdates;

import org.telegram.bot.kernel.engine.MemoryApiState;
import org.telegram.bot.structure.Chat;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.ApiContext;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;
import org.telegram.tl.TLVector;

import java.math.BigInteger;
import java.nio.channels.Channels;
import java.security.acl.Group;

public class App {


    public static void main(String[] args) throws TelegramApiRequestException {

        ApiContextInitializer.init();

        TelegramBotsApi botsApi = new TelegramBotsApi();
        RaisingBot bot = new RaisingBot();
        botsApi.registerBot(bot);


    }
}