package neu.edu.team_mad_sticking_yall;


public class MessageCard implements MessageCardListener {

    public String getSenderName() {
        return senderName;
    }

    private final String senderName;
    private final int imageSource;
    private final String stickName;
    private final MessageGravity gravity;
    private final String messageTime;

    //Constructor
//    public MessageCard(User sender, String stickName, string messageTime) {
//        this(sender, stickName, "");
//    }

    public MessageCard(Message message, MessageGravity gravity) {
        this.senderName = message.getSender();
        this.stickName = message.getStickName();
        this.gravity = gravity;
        this.messageTime = message.getMessageTime();

        int stickResId = R.drawable.happy;;
        switch (Stick.valueOf(this.stickName)) {
            case HAPPY:
                stickResId = R.drawable.happy;
                break;
            case SAD:
                stickResId = R.drawable.sad;
                break;
            case OKEY:
                stickResId = R.drawable.okay;
                break;
        }
        this.imageSource = stickResId;
    }

    @Override
    public void onItemClick(int position) {
//        isChecked = !isChecked;
    }

    //Getters for the imageSource, itemName and itemDesc
    public int getImageSource() {
        return imageSource;
    }

    public MessageGravity getGravity() {
        return gravity;
    }

    public String getStickName() {
        return stickName;// + (isChecked ? "(checked)" : "");
    }

    public String getMessageTime() {
        return messageTime;
    }

}
