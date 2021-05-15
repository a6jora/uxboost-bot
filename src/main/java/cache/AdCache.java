package cache;

import botapi.BotState;
import botapi.handlers.fillinfad.UserAd;

public interface AdCache {
    void setUserCurrentBotState(int userId, BotState botState);
    BotState getUsersCurrentBotState(int userId);
    UserAd getUserAd(int userId);
    void saveUserAd(int userId, UserAd userAd);
}
