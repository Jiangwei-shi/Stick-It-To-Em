package neu.edu.team_mad_sticking_yall;


public interface Config {
    String FIREBASE_DB_URL = "https://mad-sticking-default-rtdb.firebaseio.com";
    String SERVER_KEY = "key=AAAA2-iXU3s:APA91bF59IeuAuNL6lXbtMscZvjAdfN5m3vgGw37smyroxRmCkaNv5bt4GjFTYseeS_Rb3ThpzEJDfHFnxbO6IKXIMEw0XX-MJQ_YRV2skdrDzmtNa_9A8B1qauSuYF_U2nfFrggo5uE";
    String USER_REF = "userList";
    String MESSAGE_REF = "userMessages";
    String MESSAGE_NODE = "messages";
    String dataTimePattern = "dd/MM/yyyy hh:mm:ss";
}

enum Stick {
    HAPPY, SAD, OKEY
}
enum MessageGravity {
    RIGHT, LEFT
}
