package neu.edu.team_mad_sticking_yall;

import java.io.Serializable;

public class StickerMessage implements Serializable {

    public String sender_username;
    public String receiver_username;
    public String sticker_file_name;
    public String text_message;
    public String message_date;

    public StickerMessage() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public StickerMessage(String sender_username, String receiver_username, String sticker_file_name,
                          String message_date)
    {
        this.sender_username = sender_username;
        this.receiver_username = receiver_username;
        this.sticker_file_name = sticker_file_name;
        this.message_date = message_date;
    }
}
