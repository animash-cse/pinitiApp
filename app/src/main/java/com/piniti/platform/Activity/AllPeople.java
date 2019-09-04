package com.piniti.platform.Activity;

import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.piniti.platform.Models.AddPeople;
import com.piniti.platform.R;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AllPeople extends AppCompatActivity {

    private RecyclerView mPeopleList;
    private DatabaseReference mDatabase;
    private Toolbar mToolbar;
    private List<AddPeople> peoples;

    //-------

    private Toolbar toolbar;
    private EditText searchInput;
    private ImageView backButton;
    private TextView notFoundTV;

    private RecyclerView peoples_list;
    private DatabaseReference peoplesDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_people);

        // back Button...
        mToolbar = findViewById(R.id.all_people_app_main_tool_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().setTitle("People");

        final ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setTitle("People");

        /*peoples = new ArrayList<>();

        mPeopleList = findViewById(R.id.recycler);
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        mPeopleList.setHasFixedSize(true);
        mPeopleList.setLayoutManager(new LinearLayoutManager(this));

        loadAllUser();*/
        //-----------------

        LayoutInflater layoutInflater = (LayoutInflater)
                this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.appbar_search, null);
        actionBar.setCustomView(view);

        peoplesDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Users");
        peoplesDatabaseReference.keepSynced(true); // for offline

        searchInput = findViewById(R.id.serachInput);
        notFoundTV = findViewById(R.id.notFoundTV);
        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchPeopleProfile(searchInput.getText().toString().toLowerCase());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        // Setup recycler view
        peoples_list = findViewById(R.id.recycler);
        peoples_list.setHasFixedSize(true);
        peoples_list.setLayoutManager(new LinearLayoutManager(this));

        FirebaseRecyclerOptions<AddPeople> recyclerOptions = new FirebaseRecyclerOptions.Builder<AddPeople>()
                .setQuery(peoplesDatabaseReference, AddPeople.class)
                .build();

        FirebaseRecyclerAdapter<AddPeople, SearchPeopleVH> adapter = new FirebaseRecyclerAdapter<AddPeople, SearchPeopleVH>(recyclerOptions) {
            @Override
            protected void onBindViewHolder(@NonNull SearchPeopleVH holder, final int position, @NonNull AddPeople model) {
                holder.name.setText(model.getName());
                holder.profession.setText(model.getProfession());

                Picasso.get()
                        .load(model.getThumb_image())
                        .networkPolicy(NetworkPolicy.OFFLINE) // for Offline
                        .placeholder(R.drawable.ic_launcher_foreground)
                        .into(holder.profile_pic);

                /**on list >> clicking item, then, go to single user profile*/
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String visit_user_id = getRef(position).getKey();
                        Intent ditailsIntent = new Intent(AllPeople.this, UserDetails.class);
                        ditailsIntent.putExtra("post_id", visit_user_id);
                        startActivity(ditailsIntent);
                    }
                });

            }

            @NonNull
            @Override
            public SearchPeopleVH onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.people_view, viewGroup, false);
                return new SearchPeopleVH(view);
            }
        };
        peoples_list.setAdapter(adapter);
        adapter.startListening();


    }


    private void searchPeopleProfile(final String searchString) {
        final Query searchQuery = peoplesDatabaseReference.orderByChild("tag")
                .startAt(searchString).endAt(searchString + "\uf8ff");
        //final Query searchQuery = peoplesDatabaseReference.orderByChild("search_name").equalTo(searchString);


        FirebaseRecyclerOptions<AddPeople> recyclerOptions = new FirebaseRecyclerOptions.Builder<AddPeople>()
                .setQuery(searchQuery, AddPeople.class)
                .build();

        FirebaseRecyclerAdapter<AddPeople, SearchPeopleVH> adapter = new FirebaseRecyclerAdapter<AddPeople, SearchPeopleVH>(recyclerOptions) {
            @Override
            protected void onBindViewHolder(@NonNull SearchPeopleVH holder, final int position, @NonNull AddPeople model) {
                holder.name.setText(model.getName());
                holder.profession.setText(model.getProfession());

                /*Picasso.with(getApplicationContext())
                        .load(model.getThumb_image())
                        .networkPolicy(NetworkPolicy.OFFLINE)
                        .into(holder.profile_pic);*/
                Picasso.get()
                        .load(model.getThumb_image())
                        .networkPolicy(NetworkPolicy.OFFLINE) // for Offline
                        .placeholder(R.drawable.ic_launcher_foreground)
                        .into(holder.profile_pic);

                /**on list >> clicking item, then, go to single user profile*/
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String visit_user_id = getRef(position).getKey();
                        Intent ditailsIntent = new Intent(AllPeople.this, UserDetails.class);
                        ditailsIntent.putExtra("post_id", visit_user_id);
                        startActivity(ditailsIntent);
                    }
                });


            }

            @NonNull
            @Override
            public SearchPeopleVH onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.people_view, viewGroup, false);
                return new SearchPeopleVH(view);
            }
        };
        peoples_list.setAdapter(adapter);
        adapter.startListening();
    }

    public static class SearchPeopleVH extends RecyclerView.ViewHolder{
        TextView name, profession;
        CircleImageView profile_pic;
        public SearchPeopleVH(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            profession = itemView.findViewById(R.id.category);
            profile_pic = itemView.findViewById(R.id.imagePeopleView);
        }
    }


    /*
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
    */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_filter, menu);

        final MenuItem filterItem = menu.findItem(R.id.filter);
        final MenuItem searchItem = menu.findItem(R.id.search);

        searchItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                searchInput.setVisibility(View.VISIBLE);
                filterItem.setVisible(false);
                searchItem.setVisible(false);
                return true;
            }
        });
        /*SearchView searchView = (SearchView) searchItem.getActionView();

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
                //searchQuery(query);
                return false;
            }
        });*/
        return true;

    }

    /*private void searchQuery(String query) {
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

    }*/

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
