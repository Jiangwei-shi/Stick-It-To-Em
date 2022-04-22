package neu.edu.team_mad_sticking_yall;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ContactsActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    private static final String TAG = ContactsActivity.class.getSimpleName();

    private User user;
    private DatabaseReference userRef;

    private ArrayList<ItemCard> itemList = new ArrayList<>();

    private SwipeRefreshLayout swipeLayout;
    private RecyclerView recyclerView;
    private RecyclerViewAdapter recyclerViewAdapter;
    private RecyclerView.LayoutManager recyclerLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        // getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        swipeLayout = findViewById(R.id.swipeLayout);
        swipeLayout.setOnRefreshListener(this);

        createRecyclerView();

        // database reference
        userRef = FirebaseDatabase.getInstance(Config.FIREBASE_DB_URL).getReference(Config.USER_REF);

        Intent intent = getIntent();
        user = (User) intent.getSerializableExtra("user");

        Log.e(TAG, "[ContactsActivity]User:" + user.toString());

        TextView currentUserText = findViewById(R.id.currentUsername);
        currentUserText.setText(user.username);
        ImageView currentUserIcon = findViewById(R.id.currentUserIcon);
        currentUserIcon.setImageResource(R.drawable.user_profile_icon);
        TextView currentUserToken = findViewById(R.id.currentUserToken);
        currentUserToken.setText("Token: " + user.clientToken);

        loadUserList();
    }

    @Override
    public void onRefresh() {
        Log.e(TAG, "[SwipeRefreshLayout]loadUserList");
//        Toast.makeText(this, "Refresh", Toast.LENGTH_SHORT).show();
//        loadUserList();
        swipeLayout.setRefreshing(false);
    }

    public void logOut(View view) {
        user.isOnline = false;
        userRef.child(user.username).setValue(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d(TAG, "Data update successfully!");
                        backToHome();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Write failed
                Log.e(TAG, "Data update failed!");
                logOutFailed();
            }
        });
    }

    public void backToHome() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void logOutFailed() {
        Utils.postToastMessage(String.format("Logout failed, Please try again!"),
                getApplicationContext());
    }

    public void loadUserList() {
        itemList.clear();
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds : snapshot.getChildren()) {
                    String username = (String) ds.getKey();

                    if (!username.equalsIgnoreCase(user.username)) {
                        userRef.child(username).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DataSnapshot> task) {
                                if (!task.isSuccessful()) {
                                    Log.e(TAG, "Error getting data", task.getException());
                                } else {
                                    User updatedUser = task.getResult().getValue(User.class);
//                                    Log.e(TAG, "GetTmpUser: " + itemList.size() + " -> " + updatedUser.username);
                                    addOrUpdateUser(0, updatedUser);

                                }
                            }
                        });
                    }
                }
                swipeLayout.setRefreshing(false);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void addOrUpdateUser(int position, User updatedUser) {
        ItemCard userCardFound = itemList.stream()
                .filter((itemCard) -> updatedUser.username.equals(itemCard.getItemName()))
                .findAny().orElse(null);
        if (userCardFound == null) {
            // insert
            itemList.add(position, new ItemCard(updatedUser));
            recyclerViewAdapter.notifyItemInserted(position);
        } else {
            // update
            position = itemList.indexOf(userCardFound);
            Log.e(TAG, "UpdateUser: " + position + " -> " + updatedUser.toString());
            itemList.set(position, new ItemCard(updatedUser));
            recyclerViewAdapter.notifyItemChanged(position);
        }
    }

    private void createRecyclerView() {
        recyclerLayoutManager = new LinearLayoutManager(this);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);

        recyclerViewAdapter = new RecyclerViewAdapter(itemList);
        ItemCardListener itemClickListener = new ItemCardListener() {
            @Override
            public void onItemClick(int position) {
                //attributions bond to the item has been changed
                itemList.get(position).onItemClick(position);

                Intent messageIntent = new Intent(ContactsActivity.this, MessageActivity.class);
                messageIntent.putExtra("sender", user);
                messageIntent.putExtra("recipient", itemList.get(position).user);
                startActivity(messageIntent);

                recyclerViewAdapter.notifyItemChanged(position);
            }

            @Override
            public void onCheckBoxClick(int position) {
                //attributions bond to the item has been changed
                itemList.get(position).onCheckBoxClick(position);

                recyclerViewAdapter.notifyItemChanged(position);
            }
        };
        recyclerViewAdapter.setOnItemClickListener(itemClickListener);

        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerView.setLayoutManager(recyclerLayoutManager);
    }


}