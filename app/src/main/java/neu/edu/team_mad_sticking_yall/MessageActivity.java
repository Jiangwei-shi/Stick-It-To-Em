package neu.edu.team_mad_sticking_yall;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MessageActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    private static final String TAG = MessageActivity.class.getSimpleName();

    private DatabaseReference userRef;
    private DatabaseReference messageRef;

    private User sender;
    private User recipient;

    private ArrayList<MessageCard> messageCards;
    private ArrayList<Message> messageList;

    private SwipeRefreshLayout swipeLayout;
    private RecyclerView messageRecyclerView;
    private MessageViewAdapter messageViewAdapter;
    private RecyclerView.LayoutManager recyclerLayoutManager;
    private String routeFrom = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if(extras != null){
            Log.e(TAG, "intentGetExtras:" + extras);
//            extractDataFromNotification(extras);
        }

        sender = (User) intent.getSerializableExtra("sender");
        recipient = (User) intent.getSerializableExtra("recipient");
        routeFrom = extras.getString("routeFrom", "");

        // database reference
        FirebaseDatabase mDatabase = FirebaseDatabase.getInstance(Config.FIREBASE_DB_URL);
        userRef = mDatabase.getReference(Config.USER_REF);
        messageRef = mDatabase.getReference(Config.MESSAGE_REF);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(recipient.username);
        actionBar.setDisplayHomeAsUpEnabled(true);

        swipeLayout = findViewById(R.id.messageSwipeLayout);
        swipeLayout.setOnRefreshListener(this);

        messageCards = new ArrayList<>();
        messageList = new ArrayList<>();
        createMessageListView();

        loadMessages();
    }

    @Override
    public void onRefresh() {
        swipeLayout.setRefreshing(false);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                //Log.e(TAG, "[ActionBar]home:");
                if (routeFrom.isEmpty()) {
                    this.finish();
                } else if ("notify".equalsIgnoreCase(routeFrom)) {
                    Intent intent = new Intent(MessageActivity.this, ContactsActivity.class);
                    intent.putExtra("user", sender);
                    startActivity(intent);
                    finish();
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // selectedImg the img choose from the three
    public void onStickClick(View view) {
        String stickName = "";
        switch (view.getId()) {
            case R.id.stickHappy:
                stickName = Stick.HAPPY.name();
                break;
            case R.id.stickSad:
                stickName = Stick.SAD.name();
                break;
            case R.id.stickOkey:
                stickName = Stick.OKEY.name();
                break;
        }
        if (!stickName.isEmpty()) {
            Message message = new Message(sender.username, recipient.username, stickName, Utils.dateTime());
            // save to database
            // sender side
            messageRef.child(sender.username).child(recipient.username).child(message.getId()).setValue(message);
            // recipient side
            messageRef.child(recipient.username).child(sender.username).child(message.getId()).setValue(message);
            //
            new Thread(new Runnable() {
                @Override
                public void run() {
                    sendMessageToDevice(recipient.clientToken, message);
                }
            }).start();
        }
    }

    /**
     * Stickers sent & history is displayed
     */
    private void createMessageListView() {
        recyclerLayoutManager = new LinearLayoutManager(this);

        messageRecyclerView = findViewById(R.id.messageRecyclerView);
        messageRecyclerView.setHasFixedSize(true);

        messageViewAdapter = new MessageViewAdapter(messageCards);
        MessageCardListener messageCardListener = new MessageCardListener() {
            @Override
            public void onItemClick(int position) {
                //attributions bond to the item has been changed
//                messageList.get(position).onItemClick(position);

//                Intent messageIntent = new Intent(ContactsActivity.this, MessageActivity.class);
//                messageIntent.putExtra("sender", user);
//                messageIntent.putExtra("recipient", itemList.get(position).user);
//                startActivity(messageIntent);
//
//                messageViewAdapter.notifyItemChanged(position);
            }
        };
        messageViewAdapter.setOnItemClickListener(messageCardListener);

        messageRecyclerView.setAdapter(messageViewAdapter);
        messageRecyclerView.setLayoutManager(recyclerLayoutManager);
    }

    private void loadMessages() {
        messageRef.child(sender.username).child(recipient.username).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Log.e(TAG,  "loadMessages[onChildAdded]key>> " + snapshot.getKey());
                Log.e(TAG,  "loadMessages[onChildAdded]value>> " + snapshot.getValue());
                int position = messageCards.size();
                Message addedMsg = snapshot.getValue(Message.class);
                messageList.add(addedMsg);
                messageCards.add(position, new MessageCard(addedMsg,
                        sender.username.equals(addedMsg.getSender()) ? MessageGravity.RIGHT : MessageGravity.LEFT));
                messageViewAdapter.notifyItemInserted(position);
                messageRecyclerView.scrollToPosition(messageViewAdapter.getItemCount() - 1);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    /**
     * Pushes a notification to a given device-- in particular, this device,
     * because that's what the instanceID token is defined to be.
     */
    private void sendMessageToDevice(String targetToken, Message message) {

        Log.e(TAG,  "sendMessageToDevice >> " + targetToken);
        // Prepare data
        JSONObject jPayload = new JSONObject();
        JSONObject jNotification = new JSONObject();
        JSONObject jdata = new JSONObject();
        try {
            String dataTitle = "Message from: " + sender.username;
            String dataBody = "Sticker: " + message.getStickName();
            jNotification.put("title", dataTitle);
            jNotification.put("body", dataBody);
            jNotification.put("sound", "default");
            jNotification.put("badge", "1");

            /*
            // We can add more details into the notification if we want.
            // We happen to be ignoring them for this demo.
            jNotification.put("click_action", "OPEN_ACTIVITY_1");
            */
            jdata.put("title", dataTitle);
            jdata.put("content", dataBody);
            jdata.put("sender", sender.username);
            jdata.put("recipient", recipient.username);

            /***
             * The Notification object is now populated.
             * Next, build the Payload that we send to the server.
             */

            // If sending to a single client
            jPayload.put("to", targetToken); // CLIENT_REGISTRATION_TOKEN);

            /*
            // If sending to multiple clients (must be more than 1 and less than 1000)
            JSONArray ja = new JSONArray();
            ja.put(CLIENT_REGISTRATION_TOKEN);
            // Add Other client tokens
            ja.put(FirebaseInstanceId.getInstance().getToken());
            jPayload.put("registration_ids", ja);
            */

            jPayload.put("priority", "high");
            jPayload.put("notification", jNotification);
            jPayload.put("data", jdata);

        } catch (JSONException e) {
            e.printStackTrace();
        }


        final String resp = Utils.fcmHttpConnection(Config.SERVER_KEY, jPayload);
//        Utils.postToastMessage("Status from Server: " + resp, getApplicationContext());

    }
}