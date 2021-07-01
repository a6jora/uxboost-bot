package botapi.handlers.fillinfad;

public class User {
    public long chatId;
    public String userName;

    public User(long chatId, String userName) {
        this.chatId = chatId;
        this.userName = userName;
    }
}
