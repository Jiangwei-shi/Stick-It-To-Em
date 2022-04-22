package neu.edu.team_mad_sticking_yall;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    private FirebaseDatabase mDatabase;
    private User user;
    private int errorHintVisibility = View.INVISIBLE;

    private String notifiedSender = "";
    private String notifiedRecipient = "";
    private User notifiedUser = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        Log.e(TAG, "extras:" + extras);
        if(extras != null){

            extractDataFromNotification(extras);
        }
    }

    /**
     * This is the method called to extract the payload sent to the device from the server.
     * Notice that the data is present in the extras. Note that this will be executed Only
     * when the message is received when app is in background
     * OnMessageReceived the funtion in the MEssagingservice class is the function called when
     * the message is received and the app is in foreground
     */
    private void extractDataFromNotification(Bundle extras){
        if(extras != null){
            notifiedSender = extras.getString("sender", "");
            notifiedRecipient = extras.getString("recipient", "");
            Log.e(TAG, "title:" + extras.getString("title"));
            Log.e(TAG, "body:" + extras.getString("body"));
            Log.e(TAG, "sender:" + notifiedSender);
            Log.e(TAG, "recipient:" + notifiedRecipient);
            if (!notifiedRecipient.isEmpty()) {
                // login recipient
                mDatabase = FirebaseDatabase.getInstance(Config.FIREBASE_DB_URL);
                getUserFromDatabase(mDatabase.getReference(Config.USER_REF).child(notifiedRecipient), new UserCallback() {
                    @Override
                    public void onCallback(User user) {
                        notifiedUser = user;
                        Log.e(TAG, "notifiedRecipient::" + notifiedUser.toString());
                        if (notifiedSender.isEmpty()) {
                            // go to contacts
                            goToContacts(notifiedUser);
                        } else {
                            // go to messages
                            // get sender
                            getUserFromDatabase(mDatabase.getReference(Config.USER_REF).child(notifiedSender), new UserCallback() {
                                @Override
                                public void onCallback(User user) {
                                    Log.e(TAG, "notifiedSender::" + user.toString());
                                    Intent messageIntent = new Intent(MainActivity.this, MessageActivity.class);
                                    messageIntent.putExtra("sender", notifiedUser);
                                    messageIntent.putExtra("recipient", user);
                                    messageIntent.putExtra("routeFrom", "notify");
                                    startActivity(messageIntent);
                                }
                            });
                        }
                    }
                });
            }
        }
    }

    private void postToastMessage(final String message) {
        Handler handler = new Handler(Looper.getMainLooper());

        handler.post(new Runnable() {

            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * Use local storage to track current username or similar user identifier.
     * @param view
     */
    public void signIn(View view) {
        TextView usernameTextView = findViewById(R.id.username);
        String username = usernameTextView.getText().toString();
        TextView errorHintTextView = findViewById(R.id.errorHint);
        if (!username.isEmpty()) {
            // check username if in use from firebase
            mDatabase = FirebaseDatabase.getInstance(Config.FIREBASE_DB_URL);
            DatabaseReference userRef = mDatabase.getReference(Config.USER_REF);
            userRef.child(username).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    if (!task.isSuccessful()) {
                        Log.e(TAG, "Error getting data", task.getException());
                    } else {
                        Log.d(TAG, "Get " + username + ": " + String.valueOf(task.getResult().getValue(User.class)));
                        boolean getTokenAndRedirect = false;
                        if (task.getResult().getValue() == null) {
                            // new user need to sign up
                            Log.e(TAG, "User not exists...");
                            user = new User(username);
                            // get token and redirect
                            getTokenAndRedirect = true;
                        } else {
                            user = task.getResult().getValue(User.class);
                            // check if user in use
                            if (user.isOnline) {
                                errorHintVisibility = View.VISIBLE;
                                errorHintTextView.setText(R.string.userInUseHint);
                                getTokenAndRedirect = true;
                                // Utils.postToastMessage(String.format("[%s] already logged in!", username), getApplicationContext());
                            } else {
                                getTokenAndRedirect = true;
                            }
                        }
                        if (getTokenAndRedirect) {
                            // get token
                            FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
                                @Override
                                public void onComplete(@NonNull Task<String> task) {
                                    if (!task.isSuccessful()) {
                                        Toast.makeText(MainActivity.this, "Something is wrong!", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Log.e(TAG, user.username + "->Token getResult: " + task.getResult());
                                        if (user.clientToken.isEmpty() || user.clientToken != task.getResult()) {
                                            // update token
                                            user.clientToken = task.getResult();
                                            Log.e(TAG, user.username + "->Update Token: " + user.clientToken);

                                            // update user data
                                            user.isOnline = true;
                                            updateOrCreateUser(userRef, user); // update user
                                        }
                                        Log.e(TAG, String.format("[%s], welcome back! Token: %s", user.username, user.clientToken));
                                        goToContacts(user);
                                    }
                                }
                            });
                        }
                    }
                }
            });
        } else {
            errorHintVisibility = View.VISIBLE;
            errorHintTextView.setText(R.string.emptyNameHint);
        }
        errorHintTextView.setVisibility(errorHintVisibility);
    }

    public void updateOrCreateUser(DatabaseReference databaseReference, User user) {
        databaseReference.child(user.username).setValue(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Log.d(TAG, "Data update successfully!");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Write failed
                Log.e(TAG, "Data update failed!");
            }
        });
        databaseReference.addChildEventListener(new ChildEventListener(){
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
//                Log.d(TAG, "onChildAdded:" + snapshot.getValue().toString());
//                Utils.postToastMessage(String.format("[%s] signed up!", snapshot.getValue(User.class).username),
//                        getApplicationContext());
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
//                Log.d(TAG, "onChildChanged:" + snapshot.getValue().toString());
                // goToContacts(snapshot.getValue(User.class));
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

    public void goToContacts(User user) {
        Intent intent = new Intent(MainActivity.this, ContactsActivity.class);
        intent.putExtra("user", user);
        startActivity(intent);
        finish();
    }

    public interface UserCallback {
        void onCallback(User user);
    }

    public void getUserFromDatabase(DatabaseReference dbRef, final UserCallback userCallback) {
        OnCompleteListener onCompleteListener = new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.getResult().getValue() != null) {
                    User user = task.getResult().getValue(User.class);
                    Log.e(TAG, "getUserFromNotify::" + user.toString());
                    userCallback.onCallback(user);
                }
            }
        };
        dbRef.get().addOnCompleteListener(onCompleteListener);
    }

}