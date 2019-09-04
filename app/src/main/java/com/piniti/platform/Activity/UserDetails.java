package com.piniti.platform.Activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;

import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.piniti.platform.Models.AddPeople;
import com.piniti.platform.Notification.APIService;
import com.piniti.platform.Notification.Client;
import com.piniti.platform.Notification.Data;
import com.piniti.platform.Notification.MyResponse;
import com.piniti.platform.Notification.Sender;
import com.piniti.platform.Notification.Token;
import com.piniti.platform.R;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserDetails extends AppCompatActivity {

    // Declare Button, textview, string, database, toolbar, Intent
    private ImageView mSelectImage;
    private TextView mName, mProfession, mEmail, mAddress, mPhone, mGender;
    private Button chatButton, followButton, addButton;

    private String URL;
    private String userKey = null;

    private APIService apiService;

    private DatabaseReference databaseUser,reference, notify, adding;
    private FirebaseUser fuser;
    public Toolbar mToolbar;
    private Intent intent;

    // Pop Up Menu
    private Dialog popAddPeople;
    private Spinner pCategory, pRelation;
    private Button pAdd;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_details);

        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference();

        // Get extra String from AllPeople.java
        intent = getIntent();
        userKey = intent.getStringExtra("post_id");
        notify = FirebaseDatabase.getInstance().getReference("Notification").child(userKey).push();
        adding = FirebaseDatabase.getInstance().getReference("AddPeople").child(userKey).push();

        // Create toolbar and back Button...
        mToolbar = findViewById(R.id.show_profile_app_main_tool_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("User Details");

        // Initialize Buttons, Text and Image Views
        chatButton = findViewById(R.id.peopleChat);
        followButton = findViewById(R.id.follow);
        addButton = findViewById(R.id.peopleAdd);

        mSelectImage =  findViewById(R.id.imageView);
        mGender = findViewById(R.id.gender);

        mName = findViewById(R.id.name);
        mEmail = findViewById(R.id.email);
        mProfession = findViewById(R.id.profession);
        mPhone = findViewById(R.id.number);
        mAddress = findViewById(R.id.address);



        // Chat Button Click
        chatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserDetails.this, PeopleChat.class);
                intent.putExtra("post_id", userKey);
                startActivity(intent);
            }
        });
        // Follow button click
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
                        //sendNotification(userKey, user.getName(), "Following you");
                        addNotification(userKey, fuser.getUid());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });

        // Add button functionality
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popAddPeople.show();
            }
        });
        popUp();
    }

    // Add notification in notification layout
    private void addNotification(String userid, String fuserid) {

        notify.child("from").setValue(fuserid);
        notify.child("to").setValue(userid);
        notify.child("time").setValue(ServerValue.TIMESTAMP);
        notify.child("text").setValue(" Following you");
    }

    // Add notification in notification layout
    private void addPeople(String userid, String fuserid) {

        notify.child("from").setValue(fuserid);
        notify.child("to").setValue(userid);
        notify.child("time").setValue(ServerValue.TIMESTAMP);
        notify.child("text").setValue(" Adding you");
    }

    @Override
    protected void onStart() {
        super.onStart();

        // When User details page open, instant load user information
        databaseUser = FirebaseDatabase.getInstance().getReference().child("Users").child(userKey);
        databaseUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mName.setText(dataSnapshot.child("name").getValue(String.class));
                mEmail.setText(dataSnapshot.child("email").getValue(String.class));
                mProfession.setText(dataSnapshot.child("profession").getValue(String.class));
                mPhone.setText(dataSnapshot.child("number").getValue(String.class));
                mAddress.setText(dataSnapshot.child("address").getValue(String.class));
                URL = (dataSnapshot.child("thumb_image").getValue(String.class));

                mGender.setText(dataSnapshot.child("gender").getValue(String.class));
                Glide.with(getApplicationContext()).load(URL).into(mSelectImage);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    // Send Notification to the receiver user notification penal
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

    private void popUp() {
        // Popup Manu inti..
        popAddPeople = new Dialog(this);
        popAddPeople.setContentView(R.layout.popup_add_people);
       // popAddPeople.getWindow().setBackgroundDrawable(new ColorDrawable(Color.GRAY));
        popAddPeople.getWindow().setLayout(Toolbar.LayoutParams.MATCH_PARENT,Toolbar.LayoutParams.WRAP_CONTENT);
        popAddPeople.getWindow().getAttributes().gravity = Gravity.BOTTOM;


        //Popup Widgets
        pCategory = popAddPeople.findViewById(R.id.spinner_category);
        pRelation = popAddPeople.findViewById(R.id.spinner_relation);

        final String sGroup[] = {"Select Group","Family Members", "Relatives", "Friends", "Neighbors",
                "Co-Workers", "Important People", "Special People", "Following", "Followers"};
        final String sFamilyMember[] = {"Select Family Member","Father", "Mother", "Sister", "Brother", "Spouse", "Son", "Daughter"};
        final String sRelatives[] = {"Select Relatives","Grand Father", "Grand Mother", "Uncle", "Aunt", "Cousin", "Nephew", "Niece", "Father-in-law",
                "Mother-in-law", "Son-in-law", "Daughter-in-law", "Brother-in-law", "Sister-in-law"};
        final String sOthers[] = {"Select Relationship", "Friends", "Neighbors", "Co-Workers", "Important People", "Special People", "Following", "Followers"};

        final ArrayAdapter<String> group = new ArrayAdapter<String>(UserDetails.this, android.R.layout.simple_spinner_dropdown_item, sGroup);
        pCategory.setAdapter(group);

        pCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String pos = sGroup[position];
                if(position == 1){
                    ArrayAdapter<String> family = new ArrayAdapter<>(UserDetails.this, android.R.layout.simple_spinner_dropdown_item, sFamilyMember);
                    pRelation.setAdapter(family);
                }
                else if(position == 2){
                    ArrayAdapter<String> family = new ArrayAdapter<>(UserDetails.this, android.R.layout.simple_spinner_dropdown_item, sRelatives);
                    pRelation.setAdapter(family);
                }else{
                    ArrayAdapter<String> family = new ArrayAdapter<>(UserDetails.this, android.R.layout.simple_spinner_dropdown_item, sOthers);
                    pRelation.setAdapter(family);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // People add Button
        pAdd = popAddPeople.findViewById(R.id.add);
        pAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPosting();
            }
        });
    }

    private void startPosting() {
        final ProgressDialog progressBar = new ProgressDialog(UserDetails.this, R.style.AppTheme_Dark_Dialog);
        progressBar.setIndeterminate(true);
        progressBar.setCanceledOnTouchOutside(false);
        progressBar.setMessage("Adding People...");
        progressBar.show();

        String GroupValue = pCategory.getSelectedItem().toString();
        String relationValue = pRelation.getSelectedItem().toString();

        if(GroupValue.equals("Select Group") && relationValue.equals("Select Family Member") && relationValue.equals("Select Relatives") && relationValue.equals("Select Relationship")){
            Toast.makeText(UserDetails.this, "Select a Category and Relationship", Toast.LENGTH_LONG).show();
            progressBar.dismiss();
        }else{

            final String categoryV = GroupValue;
            final String relationV = relationValue;



        }
    }
}
