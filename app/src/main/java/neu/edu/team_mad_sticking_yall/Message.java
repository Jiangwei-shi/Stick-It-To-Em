package neu.edu.team_mad_sticking_yall;

import java.io.Serializable;
import java.util.UUID;

public class Message implements Serializable {
    private String id;
    private String sender;
    private String recipient;
    private String stickName;
    private String messageTime;

    public Message() {

    }

    public Message(String sender, String recipient, String stickName, String messageTime) {
        this.id = Utils.timeId();
        this.sender = sender;
        this.recipient = recipient;
        this.stickName = stickName;
        this.messageTime = messageTime;
//        UUID uuid = UUID.randomUUID();
//        this.id = uuid.toString().replace("-", "").toUpperCase();
    }

    public String getSender() {
        return sender;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getStickName() {
        return stickName;
    }

    public void setStickName(String stickName) {
        this.stickName = stickName;
    }

    public String getMessageTime() {
        return messageTime;
    }

    public String getId() {
        return id;
    }

    public void setMessageTime(String messageTime) {
        this.messageTime = messageTime;
    }

    public boolean equals(Message message) {
        return this.id.equals(message.getId());
    }

    @Override
    public String toString() {
        return "Message{" +
                "id='" + id + '\'' +
                ", sender='" + sender + '\'' +
                ", recipient='" + recipient + '\'' +
                ", stickName='" + stickName + '\'' +
                ", messageTime='" + messageTime + '\'' +
                '}';
    }
}
