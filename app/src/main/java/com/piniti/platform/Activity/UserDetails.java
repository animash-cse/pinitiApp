package com.piniti.platform.Activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.piniti.platform.Notification.APIService;
import com.piniti.platform.Notification.Client;
import com.piniti.platform.Notification.Data;
import com.piniti.platform.Notification.MyResponse;
import com.piniti.platform.Notification.Sender;
import com.piniti.platform.Notification.Token;
import com.piniti.platform.R;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserDetails extends AppCompatActivity {

    private ImageView mSelectImage;
    private TextView mName, mProfession, mEmail, mAddress, mPhone, mGender;
    private Button chatButton, followButton;

    private String URL;
    private String userKey = null;

    APIService apiService;

    private DatabaseReference databaseUser,reference, notify;
    private FirebaseUser fuser;
    public Toolbar mToolbar;
    Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_details);

        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference();

        notify = FirebaseDatabase.getInstance().getReference("Notification").child(fuser.getUid()).push();

        intent = getIntent();
        userKey = intent.getStringExtra("post_id");
      //  userKey = getIntent().getExtras().getString("post_id");

        // back Button...
        mToolbar = (Toolbar) findViewById(R.id.show_profile_app_main_tool_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("User Details");

        chatButton = (Button) findViewById(R.id.peopleChat);
        followButton = findViewById(R.id.follow);

        mSelectImage = (ImageView) findViewById(R.id.imageView);
        mGender = (TextView) findViewById(R.id.gender);

        mName = (TextView) findViewById(R.id.name);
        mEmail = (TextView) findViewById(R.id.email);
        mProfession = (TextView) findViewById(R.id.profession);
        mPhone = (TextView) findViewById(R.id.number);
        mAddress = (TextView) findViewById(R.id.address);



        chatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserDetails.this, PeopleChat.class);
                intent.putExtra("post_id", userKey);
                startActivity(intent);
            }
        });

        followButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //sendNotification(fuser.getUid(), userKey, "Test Follow notification");

                final DatabaseReference chatRef = FirebaseDatabase.getInstance().getReference("Follow")
                        .child(fuser.getUid())
                        .child(userKey);
                chatRef.child("id").setValue(userKey);

                final DatabaseReference chatRefReceiver = FirebaseDatabase.getInstance().getReference("Follow")
                        .child(userKey)
                        .child(fuser.getUid());
                chatRefReceiver.child("id").setValue(fuser.getUid());
                chatRefReceiver.child("by").setValue("me");


                reference = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid());
                reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        AddPeople user = dataSnapshot.getValue(AddPeople.class);
                        sendNotification(userKey, user.getName(), "Following you");
                        addNotification(userKey, fuser.getUid());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });
    }

    private void addNotification(String userid, String fuserid) {

        notify.child("from").setValue(fuserid);
        notify.child("to").setValue(userid);
        notify.child("time").setValue(ServerValue.TIMESTAMP);
        notify.child("text").setValue(" Following you");
    }

    @Override
    protected void onStart() {
        super.onStart();

        databaseUser = FirebaseDatabase.getInstance().getReference().child("Users").child(userKey);
        databaseUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mName.setText(dataSnapshot.child("name").getValue(String.class));
                mEmail.setText(dataSnapshot.child("email").getValue(String.class));
                mProfession.setText(dataSnapshot.child("profession").getValue(String.class));
                mPhone.setText(dataSnapshot.child("number").getValue(String.class));
                mAddress.setText(dataSnapshot.child("address").getValue(String.class));
                URL = (dataSnapshot.child("image").getValue(String.class));

                mGender.setText(dataSnapshot.child("gender").getValue(String.class));
                Glide.with(getApplicationContext()).load(URL).into(mSelectImage);



            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void sendNotification(String receiver, final String username, final String message){
        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
        Query query = tokens.orderByKey().equalTo(receiver);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Token token = snapshot.getValue(Token.class);
                    Data data = new Data(fuser.getUid(), R.mipmap.ic_launcher, username+" "+message, "New Notification",
                            userKey);

                    Sender sender = new Sender(data, token.getToken());

                    apiService.sendNotification(sender)
                            .enqueue(new Callback<MyResponse>() {
                                @Override
                                public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                    if (response.code() == 200){
                                        if (response.body().success != 1){
                                            Toast.makeText(UserDetails.this, "Failed!", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }

                                @Override
                                public void onFailure(Call<MyResponse> call, Throwable t) {

                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
