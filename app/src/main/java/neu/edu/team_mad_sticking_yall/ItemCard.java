package neu.edu.team_mad_sticking_yall;


public class ItemCard implements ItemCardListener {

    private final int imageSource;
    private final String itemName;
    private final String itemDesc;
    private boolean isChecked;
    public boolean isOnline = false;
    public User user;

    //Constructor
    public ItemCard(User user) {
        this.user = user;
        this.imageSource = R.drawable.user_profile_icon;
        this.itemName = user.username;
        this.itemDesc = user.clientToken;
        this.isChecked = false;
        this.isOnline = user.isOnline;
    }

    @Override
    public void onItemClick(int position) {
//        isChecked = !isChecked;
    }

    @Override
    public void onCheckBoxClick(int position) {
        isChecked = !isChecked;
    }

    //Getters for the imageSource, itemName and itemDesc
    public int getImageSource() {
        return imageSource;
    }

    public String getItemDesc() {
        return itemDesc;
    }

    public String getItemName() {
        return itemName;// + (isChecked ? "(checked)" : "");
    }

    public boolean getStatus() {
        return isChecked;
    }
}
