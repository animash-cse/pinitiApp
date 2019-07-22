package com.piniti.platform;

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
import com.piniti.platform.Activity.PeopleList;

public class ProfileActivity extends AppCompatActivity {


    private ImageView mSelectImage;
    private TextView mName, mProfession;
    private Button mOverview, mPeople;
    private String URL;
    private DatabaseReference databaseUser;
    private FirebaseUser currentFirebaseUser;

    public Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // back Button...
        mToolbar = (Toolbar) findViewById(R.id.profile_app_main_tool_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Profile");

        mSelectImage = (ImageView) findViewById(R.id.imageView);
        mName = (TextView)findViewById(R.id.name);
        mProfession=(TextView)findViewById(R.id.profession);
        mOverview = (Button) findViewById(R.id.overview);
        mPeople = (Button)findViewById(R.id.people);

        currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseUser = FirebaseDatabase.getInstance().getReference().child("Users").child(currentFirebaseUser.getUid());

        mOverview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, ShowProfile.class);
                startActivity(intent);
            }
        });
        mPeople.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, PeopleList.class);
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        databaseUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mName.setText(dataSnapshot.child("name").getValue(String.class));
                mProfession.setText(dataSnapshot.child("profession").getValue(String.class));
                URL = (dataSnapshot.child("image").getValue(String.class));
                Glide.with(getApplicationContext()).load(URL).into(mSelectImage);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}