package neu.edu.team_mad_sticking_yall;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SendActivity extends AppCompatActivity {
    private static final String SERVER_KEY = "key=AAAA2-iXU3s:APA91bF59IeuAuNL6lXbtMscZvjAdfN5m3vgGw37smyroxRmCkaNv5bt4GjFTYseeS_Rb3ThpzEJDfHFnxbO6IKXIMEw0XX-MJQ_YRV2skdrDzmtNa_9A8B1qauSuYF_U2nfFrggo5uE";
    static String targetToken;
    private static String CLIENT_REGISTRATION_TOKEN;
    public String userToBeSent;
    int mesCnt;
    private String pendingSentFile = "";
    private String currentUser = "";
    private Spinner spinner;
    private DatabaseReference mDatabase;
    private List<String> nameList;
    private Button history;
    private View selectedImg;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_message);
//        currentUser = MainActivity.username;
        TextView sentMesCnt = findViewById(R.id.sentMessageCount);
        mDatabase = FirebaseDatabase.getInstance("https://mad-sticking-default-rtdb.firebaseio.com").getReference();
        // adding the send record to db history
        mDatabase.child("users").child(currentUser).child("history").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mesCnt = 0;
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    if (Objects.requireNonNull(dataSnapshot.child("sender_username").getValue(String.class)).compareTo(currentUser) == 0) {
                        mesCnt++;
                    }
                }
                sentMesCnt.setText("Stickers you sent: " + mesCnt);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        // getting the users you can send to from server
        mDatabase.child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                spinner = findViewById(R.id.userSpinner);
                nameList = new ArrayList<>();
                //nameList.add(0, "Please select: ");
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String usernameTmp = dataSnapshot.child("username").getValue(String.class);
                    nameList.add(usernameTmp);
                }
                // set up the spinner to choose a user
                // citation this part is from https://stackoverflow.com/questions/867518/how-to-make-an-android-spinner-with-initial-text-select-one
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(SendActivity.this, android.R.layout.simple_spinner_item, nameList);
                arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(arrayAdapter);
                // get the desired user
                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        userToBeSent = adapterView.getItemAtPosition(i).toString();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Toast.makeText(SendActivity.this, "Error code 2!", Toast.LENGTH_SHORT).show();
            } else {
                if (CLIENT_REGISTRATION_TOKEN == null) {
                    CLIENT_REGISTRATION_TOKEN = task.getResult();
                    //System.out.print(CLIENT_REGISTRATION_TOKEN);
                }
                Log.e("CLIENT_REGISTRATION_TOKEN", CLIENT_REGISTRATION_TOKEN);
            }
        });
    }
    // selectedImg the img choose from the three
    // pendingSentFile the file name pending sent
    @SuppressLint("NonConstantResourceId")
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imgBnt1:
                selectedImg = findViewById(R.id.imgBnt1);
                pendingSentFile = "happy";
                break;
            case R.id.imgBnt2:
                selectedImg = findViewById(R.id.imgBnt2);
                pendingSentFile = "sad";
                break;
            case R.id.imgBnt3:
                selectedImg = findViewById(R.id.imgBnt3);
                pendingSentFile = "okay";
                break;
            case R.id.sendMessage:
                if (!pendingSentFile.equals("") && userToBeSent != null) {
                    sendMessageToOtherUser(view);
                }
                break;
            //TODO
            //            case R.id.showHistory:
            //                startActivity(new Intent(this, History.class));
            //                break;
            default:
                throw new IllegalStateException("Unexpected value: " + view.getId());
        }
    }

    private void sendMessageToOtherUser(View view) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (userToBeSent != null) {
                    sendMessageToOtherUser(userToBeSent);
                }
            }
        }).start();
    }

    private void sendMessageToOtherUser(String target_username) {

        JSONObject jPayload = new JSONObject();
        JSONObject jNotification = new JSONObject();
        JSONObject jdata = new JSONObject();
        try {
            jNotification.put("title", "You have received a sticker message from " + currentUser);
            jNotification.put("body", pendingSentFile);
            jNotification.put("sound", "default");
            jNotification.put("icon", R.drawable.happy);
            jNotification.put("badge", "1");
            jNotification.put("click_action", "OPEN_ACTIVITY_1");


            jdata.put("title", "You have received a sticker message from " + currentUser);
            jdata.put("content", pendingSentFile);

            StickerMessage chat = new StickerMessage(currentUser, target_username, pendingSentFile, Utils.date_time());

            // update sender history
            DatabaseReference History_Array_List_ref = mDatabase.child("users").child(currentUser).child("history");
            History_Array_List_ref.addListenerForSingleValueEvent(new com.google.firebase.database.ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    ArrayList<StickerMessage> temp_array_list;
                    temp_array_list = new ArrayList<>();

                    StickerMessage temp_message;

                    for (DataSnapshot pss : dataSnapshot.getChildren()) {
                        temp_message = new StickerMessage((String) pss.child("sender_username").getValue(),
                                (String) pss.child("receiver_username").getValue(),
                                (String) pss.child("sticker_file_name").getValue(),
                                (String) pss.child("message_date").getValue());
                        temp_array_list.add(temp_message);

                    }

                    temp_array_list.add(chat);
                    User user;
                    user = new User(currentUser, CLIENT_REGISTRATION_TOKEN);
//                    user.history = temp_array_list;
                    mDatabase.child("users").child(currentUser).setValue(user);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });


            // get target token
            DatabaseReference username_list_ref = mDatabase.child("users").child(target_username).child("CLIENT_REGISTRATION_TOKEN");
            username_list_ref.addListenerForSingleValueEvent(new com.google.firebase.database.ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    targetToken = (String)dataSnapshot.getValue();
                    Log.e("target_token", targetToken);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            // update receiver history
            DatabaseReference sender_History_Array_List_ref = mDatabase.child("users").child(target_username).child("history");
            sender_History_Array_List_ref.addListenerForSingleValueEvent(new com.google.firebase.database.ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    ArrayList<StickerMessage> temp_array_list;
                    temp_array_list = new ArrayList<StickerMessage>();

                    StickerMessage temp_message;

                    for (DataSnapshot pss : dataSnapshot.getChildren()) {
                        temp_message = new StickerMessage((String) pss.child("sender_username").getValue(),
                                (String) pss.child("receiver_username").getValue(),
                                (String) pss.child("sticker_file_name").getValue(),
                                (String) pss.child("message_date").getValue());
                        temp_array_list.add(temp_message);

                    }

                    temp_array_list.add(chat);
                    User user;
                    user = new User(target_username, targetToken);
//                    user.history = temp_array_list;
                    Task update_sender_chat_history = mDatabase.child("users").child(target_username).setValue(user);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
                @Override
                public void onComplete(@NonNull Task<String> task) {
                    if (!task.isSuccessful()) {
                        Toast.makeText(SendActivity.this, "Something is wrong, please check your Internet connection!", Toast.LENGTH_SHORT).show();
                    } else {
                        if (CLIENT_REGISTRATION_TOKEN.length() < 1) {
                            CLIENT_REGISTRATION_TOKEN = task.getResult();
                        }
                        Log.e("CLIENT_REGISTRATION_TOKEN", CLIENT_REGISTRATION_TOKEN);
                    }

                }
            });



            jPayload.put("to", targetToken);
            jPayload.put("priority", "high");
            jPayload.put("notification", jNotification);
            jPayload.put("data", jdata);


        } catch (JSONException  e) {
            e.printStackTrace();
        }

        final String resp = Utils.fcmHttpConnection(SERVER_KEY, jPayload);

    }
}
