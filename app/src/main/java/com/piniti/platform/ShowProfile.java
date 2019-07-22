package com.piniti.platform;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

public class ShowProfile extends AppCompatActivity {

    private ImageView mSelectImage;
    private TextView mName, mProfession, mEmail, mAddress, mPhone, mGender;
    private Button mUpdate;

    private String URL;
    private DatabaseReference databaseUser;
    private FirebaseUser currentFirebaseUser;
    public Toolbar mToolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_profile);

        // back Button...
        mToolbar = (Toolbar) findViewById(R.id.show_profile_app_main_tool_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Profile");

        mSelectImage = (ImageView) findViewById(R.id.imageView);
        mGender = (TextView) findViewById(R.id.gender);

        mName = (TextView) findViewById(R.id.name);
        mEmail = (TextView) findViewById(R.id.email);
        mProfession = (TextView) findViewById(R.id.profession);
        mPhone = (TextView) findViewById(R.id.number);
        mAddress = (TextView) findViewById(R.id.address);
        mUpdate = (Button) findViewById(R.id.edit);

        currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseUser = FirebaseDatabase.getInstance().getReference().child("Users").child(currentFirebaseUser.getUid());


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
        mUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ShowProfile.this, EditProfile.class);
                startActivity(intent);
            }
        });
    }
}
