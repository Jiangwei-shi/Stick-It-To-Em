package neu.edu.team_mad_sticking_yall;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONObject;

import java.util.Map;


// This class simulated what happens in the backend server
// Check meta-data in AndroidManifest, very important!!!
public class MessagingService extends FirebaseMessagingService {
    private static final String TAG = MessagingService.class.getSimpleName();
    private static final String CHANNEL_ID = "CHANNEL_ID";
    private static final String CHANNEL_NAME = "CHANNEL_NAME";
    private static final String CHANNEL_DESCRIPTION = "CHANNEL_DESCRIPTION";

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onNewToken(String newToken) {
        //super.onNewToken(newToken);

        Log.d(TAG, "Refreshed token: " + newToken);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        // sendRegistrationToServer(newToken);
    }

    /**
     * Called when message is received.
     * Mainly what you need to implement
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]
    //
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // [START_EXCLUDE]
        // There are two types of messages data messages and notification messages. Data messages are handled
        // here in onMessageReceived whether the app is in the foreground or background. Data messages are the type
        // traditionally used with GCM. Notification messages are only received here in onMessageReceived when the app
        // is in the foreground. When the app is in the background an automatically generated notification is displayed.
        // When the user taps on the notification they are returned to the app. Messages containing both notification
        // and data payloads are treated as notification messages. The Firebase console always sends notification
        // messages. For more see: https://firebase.google.com/docs/cloud-messaging/concept-options
        // [END_EXCLUDE]


        /* NOTICE!!!
         * Message types are inevitable in recent Android version.
         * remoteMessage.getData() Method will return null for 'topic-subscribed messages' from FCMActivity
         *
         * remoteMessage.getFrom() Method will recognize topic-subscribed messages
         * remoteMessage.getNotification() Method will show the raw-data of topic-subscribed messages
         */

        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.e(TAG, "From: " + remoteMessage.getFrom());
        Log.e(TAG, "msgId: " + remoteMessage.getSenderId());
        Log.e(TAG, "senderId: " + remoteMessage.getMessageId());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.e(TAG, "Message data payload: " + remoteMessage.getData());
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.e(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }

        String identificator = remoteMessage.getFrom();
        Log.e(TAG, "identificator->"+identificator);
        Log.e(TAG, "remoteMessage.getData()->" + remoteMessage.getData().size());
        if (identificator != null) {
            Log.e(TAG, "myClassifier-> Topic? "+identificator.contains("topic"));
            showNotification(remoteMessage);
        }
    }

    /**
     * Create and show a simple notification containing the received FCM message.
     * Notification for new sticker, more than just text, will display the received stickers.
     */
    private void showNotification(RemoteMessage remoteMessage) {
        String title = remoteMessage.getNotification().getTitle();
        String body = remoteMessage.getNotification().getBody();
        Map<String, String> dataMap = remoteMessage.getData();
        // JSONObject jData = new JSONObject(remoteMessage.getData());
        Log.e(TAG, "dataMap->" + dataMap.toString());

        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("title", title);
        intent.putExtra("body", body);
        intent.putExtra("sender", dataMap.get("sender"));
        intent.putExtra("recipient", dataMap.get("recipient"));
        // intent.putExtra("jData", (Parcelable) remoteMessage.getData());

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 , intent,
                PendingIntent.FLAG_MUTABLE); // FLAG_IMMUTABLE | FLAG_UPDATE_CURRENT

        Notification notification;
        NotificationCompat.Builder builder;
        NotificationManager notificationManager = getSystemService(NotificationManager.class);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
            // Configure the notification channel
            notificationChannel.setDescription(CHANNEL_DESCRIPTION);
            notificationManager.createNotificationChannel(notificationChannel);
            Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.user_profile_icon); ;
            if(body.equals("Sticker: OKEY")) {
                largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.okay);
            } else if (body.equals("Sticker: HAPPY")) {
                largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.happy);
            } else if (body.equals("Sticker: SAD")) {
                largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.sad);
            }

            builder = new NotificationCompat.Builder(this, CHANNEL_ID).setLargeIcon(largeIcon);

        } else {
            builder = new NotificationCompat.Builder(this);
        }


        notification = builder.setContentTitle(title)
                .setContentText(body)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .build();
        notificationManager.notify(0, notification);

    }

}