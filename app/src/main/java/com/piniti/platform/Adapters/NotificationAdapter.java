package com.piniti.platform.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.piniti.platform.Models.NotificationModel;
import com.piniti.platform.R;

import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {


    private Context mContext;
    private List<NotificationModel> mChat;
    private DatabaseReference getUserDetails;

    FirebaseUser fuser;

    public NotificationAdapter(Context mContext, List<NotificationModel> mChat){
        this.mChat = mChat;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public NotificationAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.notification_view, parent, false);
        return new NotificationAdapter.ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull final NotificationAdapter.ViewHolder holder, int position) {

        NotificationModel chat = mChat.get(position);
        NotificationModel data = mChat.get(position);
        getUserDetails = FirebaseDatabase.getInstance().getReference().child("Users").child(chat.getTo());
        getUserDetails.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                holder.name.setText(dataSnapshot.child("name").getValue(String.class));
                String URL = (dataSnapshot.child("image").getValue(String.class));
                Glide.with(mContext).load(URL).into(holder.profile_image);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        holder.subject.setText(chat.getText());
       // holder.time.setText(chat.getTime());

       // Glide.with(mContext).load(imageurl).into(holder.profile_image);


    }

    @Override
    public int getItemCount() {
        return mChat.size();
    }

    public  class ViewHolder extends RecyclerView.ViewHolder{

        public TextView name;
        public ImageView profile_image;
        public TextView time;
        public TextView subject;

        public ViewHolder(View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.name);
            profile_image = itemView.findViewById(R.id.imagePeople);
            time = itemView.findViewById(R.id.time);
            subject = itemView.findViewById(R.id.subject);
        }
    }

    /*@Override
    public int getItemViewType(int position) {
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        if (mChat.get(position).getTo().equals(fuser.getUid())){

        }
    }*/
}