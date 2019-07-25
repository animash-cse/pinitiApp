package com.piniti.platform.Activity;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.piniti.platform.Adapters.MessageAdapter;
import com.piniti.platform.Adapters.NotificationAdapter;
import com.piniti.platform.Models.Chat;
import com.piniti.platform.Models.NotificationModel;
import com.piniti.platform.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class Notification extends AppCompatActivity {

    private RecyclerView mNotificationRecycler;
    private Toolbar mToolbar;
    private DatabaseReference mNotificationData, reference;
    private FirebaseUser fuser;
    private List<NotificationModel> mchat;
    private NotificationAdapter notificationAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);


        // Setup Toolbar
        mToolbar = findViewById(R.id.all_people_app_main_tool_bar);
        setSupportActionBar(mToolbar);

        // Setup Back Button and Layout name
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Notifications");

        //  Get user ID and initialize notification database
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        mNotificationData = FirebaseDatabase.getInstance().getReference().child("Notification").child(fuser.getUid());

        // Initialize recycler view
        mNotificationRecycler = findViewById(R.id.recyclerView);
        mNotificationRecycler.setHasFixedSize(true);
        mNotificationRecycler.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onStart() {
        super.onStart();

        reference = FirebaseDatabase.getInstance().getReference("Notification");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //mchat.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    NotificationModel chat = snapshot.getValue(NotificationModel.class);
                    if (chat.getTo().equals(fuser)){
                        mchat.add(chat);
                    }

                    notificationAdapter = new NotificationAdapter(Notification.this, mchat);
                    mNotificationRecycler.setAdapter(notificationAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        FirebaseRecyclerAdapter<AddPeople, Notification.NotificationViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<AddPeople, Notification.NotificationViewHolder>(
                AddPeople.class,
                R.layout.notification_view,
                Notification.NotificationViewHolder.class,
                mNotificationData
        ) {
            @Override
            protected void populateViewHolder(Notification.NotificationViewHolder viewHolder, AddPeople model, int position) {

                //final String post_key = getRef(position).getKey();

                viewHolder.setName(model.getName());
                viewHolder.setRelation(model.getRelation());

                viewHolder.setImage(getApplicationContext(), model.getImage());

            }
        };
        mNotificationRecycler.setAdapter(firebaseRecyclerAdapter);
    }

    private static class NotificationViewHolder extends RecyclerView.ViewHolder {
        View mView;
        public NotificationViewHolder(View itemView) {
            super(itemView);

            mView = itemView;
        }

        public void setName(String name){
            TextView teacherName = mView.findViewById(R.id.name);
            teacherName.setText(name);
        }

        public void setImage(final Context ctx, final String image){
            final ImageView peopleImage = mView.findViewById(R.id.imagePeople);

            Picasso.with(ctx).load(image).networkPolicy(NetworkPolicy.OFFLINE).into(peopleImage, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError() {
                    Picasso.with(ctx).load(image).into(peopleImage);
                }
            });
        }

        public void setRelation(String relation) {
            TextView peopleCategory = mView.findViewById(R.id.category);
            peopleCategory.setText(relation);
        }

    }
}
