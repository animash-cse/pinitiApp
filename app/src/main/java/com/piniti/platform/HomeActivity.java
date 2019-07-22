package com.piniti.platform;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
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
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.piniti.platform.Notification.Token;

import java.util.HashMap;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private FirebaseAuth mAuth;
    private FirebaseUser user, currentFirebaseUser;
    private DatabaseReference getUserId, mDatabase, databaseUser, reference;
    private StorageReference mStorage;

    ValueEventListener seenListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.action_bar_title);

        mAuth = FirebaseAuth.getInstance();


       // userID = (TextView)findViewById(R.id.uid);
        //userID2 = (TextView)findViewById(R.id.uid2);
        user = mAuth.getCurrentUser();
        getUserId = FirebaseDatabase.getInstance().getReference();

        currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseUser = FirebaseDatabase.getInstance().getReference().child("Users").child(currentFirebaseUser.getUid());

        //Toast.makeText(this, "" + currentFirebaseUser.getUid(), Toast.LENGTH_SHORT).show();

        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                getUserId.child("AllUserID").child(user.getUid()).setValue("userid: "+user.getUid());
            }
        });*/

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        navHeaderProfile();

        //default fragment for home
        android.support.v4.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.flMain,new MenuFragment());
        ft.commit();

        navigationView.setCheckedItem(R.id.nav_home);
        updateToken(FirebaseInstanceId.getInstance().getToken());
       // userID.setText("User ID: " + user.getUid());

    }

    private void updateToken(String token){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Tokens");
        Token token1 = new Token(token);
        reference.child(currentFirebaseUser.getUid()).setValue(token1);
    }

    public void setActionBarTitle(String title) {
        getSupportActionBar().setTitle(title);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.notification) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            android.support.v4.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.flMain,new MenuFragment());
            ft.commit();

        } else if (id == R.id.nav_profile) {
            Intent intent = new Intent(HomeActivity.this, ProfileActivity.class);
            startActivity(intent);
            /*android.support.v4.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.flMain,new UserFragment());
            ft.commit();*/

        } else if (id == R.id.nav_platform) {
            android.support.v4.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.flMain,new PlatformFragment());
            ft.commit();
        } else if (id == R.id.nav_report) {

        } else if (id == R.id.nav_notification) {

        } else if (id == R.id.nav_setting){

        }else if (id == R.id.log_out) {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(HomeActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void navHeaderProfile(){
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        final TextView navUserName = headerView.findViewById(R.id.headerNameView);
        final TextView navUserProfe = headerView.findViewById(R.id.headerProfeView);
        final ImageView navUserImage = headerView.findViewById(R.id.headerImageView);

        databaseUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                navUserName.setText(dataSnapshot.child("name").getValue(String.class));
                navUserProfe.setText(dataSnapshot.child("profession").getValue(String.class));
                final String URL = (dataSnapshot.child("image").getValue(String.class));

                // mGender.setText(dataSnapshot.child("gender").getValue(String.class));
                Glide.with(getApplicationContext()).load(URL).into(navUserImage);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void currentUser(String userid){
        SharedPreferences.Editor editor = getSharedPreferences("PREFS", MODE_PRIVATE).edit();
        editor.putString("currentuser", userid);
        editor.apply();
    }

    private void status(String status){
        reference = FirebaseDatabase.getInstance().getReference("Users").child(currentFirebaseUser.getUid());

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("status", status);

        reference.updateChildren(hashMap);
    }

    @Override
    protected void onStart() {
        super.onStart();
        status("online");
        currentUser(currentFirebaseUser.getUid());
    }

    @Override
    protected void onStop() {
        super.onStop();
       // reference.removeEventListener(seenListener);
        status("offline");
        currentUser("none");
    }

    /*

    pass(Key store password): PinitiProject1234

    Key alias: piniti

    Kye pass(Key password): PinitiProject123

    */
}
