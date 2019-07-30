package com.piniti.platform.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.piniti.platform.Adapters.filterAdapter;
import com.piniti.platform.MainActivity;
import com.piniti.platform.R;
import com.piniti.platform.UserAdapter;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class AllPeople extends AppCompatActivity {

    private RecyclerView mPeopleList;
    private DatabaseReference mDatabase;
    private Toolbar mToolbar;
    private List<AddPeople> peoples;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_people);

        // back Button...
        mToolbar = findViewById(R.id.all_people_app_main_tool_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("All People");

        peoples = new ArrayList<>();

        mPeopleList = findViewById(R.id.recycler);
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        mPeopleList.setHasFixedSize(true);
        mPeopleList.setLayoutManager(new LinearLayoutManager(this));

        loadAllUser();

    }

    private void loadAllUser(){
        //mDatabase.keepSynced(true);
        FirebaseRecyclerAdapter<AddPeople, AllPeopleViewHolder> peopleRecyclerAdapter = new FirebaseRecyclerAdapter<AddPeople, AllPeopleViewHolder>(
                AddPeople.class,
                R.layout.people_view,
                AllPeople.AllPeopleViewHolder.class,
                mDatabase
        ) {
            @Override
            protected void populateViewHolder(AllPeopleViewHolder viewHolder, AddPeople model, int position) {

                final String post_key = getRef(position).getKey();

                viewHolder.setName(model.getName());
                viewHolder.setProfession(model.getProfession());

                viewHolder.setThumbImage(getApplicationContext(), model.getThumb_image());

                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

//                        Toast.makeText(CseTeacherActivity.this, post_key, Toast.LENGTH_LONG).show();
                        Intent ditailsIntent = new Intent(AllPeople.this, UserDetails.class);
                        ditailsIntent.putExtra("post_id", post_key);

                        startActivity(ditailsIntent);
                    }
                });

            }
        };
        mPeopleList.setAdapter(peopleRecyclerAdapter);
    }
    public static class AllPeopleViewHolder extends RecyclerView.ViewHolder {
        View mView;
        public AllPeopleViewHolder(View itemView) {
            super(itemView);

            mView = itemView;
        }

        public void setName(String name){
            TextView teacherName = mView.findViewById(R.id.name);
            teacherName.setText(name);
        }

        public void setThumbImage(final Context ctx, final String thumb_image){
            final ImageView peopleImage = mView.findViewById(R.id.imagePeople);

            Picasso.with(ctx).load(thumb_image).networkPolicy(NetworkPolicy.OFFLINE).into(peopleImage, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError() {
                    Picasso.with(ctx).load(thumb_image).into(peopleImage);
                }
            });
        }

        public void setProfession(String profession) {
            TextView peopleCategory = mView.findViewById(R.id.category);
            peopleCategory.setText(profession);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_filter, menu);

        MenuItem searchItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) searchItem.getActionView();

        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // filter recycler view when query submitted
                //mAdapter.getFilter().filter(query);

                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                // filter recycler view when text is changed
                //mAdapter.getFilter().filter(query); //ContactsAdapter
                searchQuery(query);
                return false;
            }
        });
        return true;

    }

    private void searchQuery(String query) {
        Query familySearchQuery = mDatabase.orderByChild("tag").startAt(query).endAt(query+"\uf8ff");
        FirebaseRecyclerAdapter<AddPeople, AllPeopleViewHolder> peopleRecyclerAdapter = new FirebaseRecyclerAdapter<AddPeople, AllPeopleViewHolder>(
                AddPeople.class,
                R.layout.people_view,
                AllPeople.AllPeopleViewHolder.class,
                familySearchQuery
        ) {
            @Override
            protected void populateViewHolder(AllPeopleViewHolder viewHolder, AddPeople model, int position) {

                final String post_key = getRef(position).getKey();

                viewHolder.setName(model.getName());
                viewHolder.setProfession(model.getProfession());

                viewHolder.setThumbImage(getApplicationContext(), model.getThumb_image());

                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

//                        Toast.makeText(CseTeacherActivity.this, post_key, Toast.LENGTH_LONG).show();
                        Intent ditailsIntent = new Intent(AllPeople.this, UserDetails.class);
                        ditailsIntent.putExtra("post_id", post_key);

                        startActivity(ditailsIntent);
                    }
                });

            }
        };
        mPeopleList.setAdapter(peopleRecyclerAdapter);

    }


}
