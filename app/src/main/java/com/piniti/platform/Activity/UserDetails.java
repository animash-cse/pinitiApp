package com.piniti.platform.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.piniti.platform.R;
import com.piniti.platform.ShowProfile;

public class UserDetails extends AppCompatActivity {

    private ImageView mSelectImage;
    private TextView mName, mProfession, mEmail, mAddress, mPhone, mGender;
    private Button chatButton;

    private String URL;
    private String mPost_key = null;

    private DatabaseReference databaseUser;
    private FirebaseUser currentFirebaseUser;
    public Toolbar mToolbar;
    Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_details);

        intent = getIntent();
        mPost_key = intent.getStringExtra("post_id");
      //  mPost_key = getIntent().getExtras().getString("post_id");

        // back Button...
        mToolbar = (Toolbar) findViewById(R.id.show_profile_app_main_tool_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("User Details");

        chatButton = (Button) findViewById(R.id.peopleChat);

        mSelectImage = (ImageView) findViewById(R.id.imageView);
        mGender = (TextView) findViewById(R.id.gender);

        mName = (TextView) findViewById(R.id.name);
        mEmail = (TextView) findViewById(R.id.email);
        mProfession = (TextView) findViewById(R.id.profession);
        mPhone = (TextView) findViewById(R.id.number);
        mAddress = (TextView) findViewById(R.id.address);

        currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();


        chatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserDetails.this, PeopleChat.class);
                intent.putExtra("post_id", mPost_key);
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        databaseUser = FirebaseDatabase.getInstance().getReference().child("Users").child(mPost_key);
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
}
