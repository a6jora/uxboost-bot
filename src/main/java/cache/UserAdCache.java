package cache;

import botapi.BotState;
import botapi.handlers.fillinfad.UserAd;

import java.util.HashMap;
import java.util.Map;

public class UserAdCache implements AdCache{
    private Map<Integer, BotState> usersBotStates = new HashMap<>();
    private Map<Integer, UserAd> usersAds = new HashMap<>();
    @Override
    public void setUserCurrentBotState(int userId, BotState botState) {
        usersBotStates.put(userId, botState);
    }

    @Override
    public BotState getUsersCurrentBotState(int userId) {
        BotState botState = usersBotStates.get(userId);
        if (botState == null){
            botState = BotState.ASK_OPTION;
        }
        return botState;
    }

    @Override
    public UserAd getUserAd(int userId) {
        UserAd userAd = usersAds.get(userId);
        if (userAd == null){
            userAd = new UserAd();
        }
        return userAd;
    }

    @Override
    public void saveUserAd(int userId, UserAd userAd) {
        usersAds.put(userId, userAd);
    }
}
