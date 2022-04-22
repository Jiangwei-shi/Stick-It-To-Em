package neu.edu.team_mad_sticking_yall;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class User implements Serializable {

    public String username;
    public boolean isOnline = false;
    public String clientToken = ""; // CLIENT_REGISTRATION_TOKEN
    public List<Message> messages = new ArrayList<>();
    public Integer sticker_count = 0;


    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String username) {
        this(username, "");
    }

    public User(String username, String clientToken) {
        this.username = username;
        messages = new ArrayList<>();
    }

//    public void Add_ChatHistory(StickerMessage current_chat) {
//        history.add(current_chat);
//    }

    public void addMessage(Message message) {
        messages.add(message);
    }

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", isOnline=" + isOnline +
                ", clientToken='" + clientToken + '\'' +
                ", sticker_count=" + sticker_count +
                '}';
    }
}
