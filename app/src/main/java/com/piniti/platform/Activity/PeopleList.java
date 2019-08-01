package com.piniti.platform.Activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
import android.support.v7.widget.Toolbar;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import com.piniti.platform.Adapters.PeopleAdapter;
import com.piniti.platform.Models.AddPeople;
import com.piniti.platform.R;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.List;

public class PeopleList extends AppCompatActivity {

    private Uri imageUri = null;

    private Dialog popAddPeople;
    private EditText pName, pProfession, pNumber;
    private Spinner pCategory, pRelation;
    private ImageButton pImage;
    private Button pAdd;

    private DatabaseReference databaseDirectory, mDatabase, familyDatabase;
    private StorageReference storageDirectory;
    private FirebaseUser currentFirebaseUser;

    private RecyclerView mTeacherList, mRelatives;
    PeopleAdapter peopleAdapter;
    List<PeopleAdapter> peopleList;
    SharedPreferences mSheredPre;

    public Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.people_list);

        // back Button...
        mToolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.people_list_app_main_tool_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("My People");

       // pCategory = (Spinner) findViewById(R.id.spinner_category);
       // pRelation = (Spinner) findViewById(R.id.spinner_relation);



        currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        String id = currentFirebaseUser.toString();
        storageDirectory = FirebaseStorage.getInstance().getReference().child("Users").child(currentFirebaseUser.getUid()).child("Peoples");
       // mDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        databaseDirectory = FirebaseDatabase.getInstance().getReference().child("Users").child(currentFirebaseUser.getUid()).child("Peoples").push();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(currentFirebaseUser.getUid()).child("Peoples");
        familyDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(currentFirebaseUser.getUid()).child("Peoples");
        mDatabase.keepSynced(true);
        String datatext = mDatabase.toString();
       // Toast.makeText(PeopleList.this, datatext, Toast.LENGTH_LONG).show();

        mTeacherList = (RecyclerView) findViewById(R.id.recyclerView2);
        mRelatives = (RecyclerView) findViewById(R.id.recycler_relatives);
        /*mTeacherList.setHasFixedSize(true);
        mTeacherList.setLayoutManager(new LinearLayoutManager(this));*/


        /*mSheredPre = getSharedPreferences("SortSettings", MODE_PRIVATE);
        String mSorting = mSheredPre.getString("Sort", "newest");
        if(mSorting.equals("newest")){
            mLayoutManager = new LinearLayoutManager(this);
            mLayoutManager.setReverseLayout(true);
            mLayoutManager.setStackFromEnd(true);
        }*/
        mTeacherList.setHasFixedSize(true);
        //mGalleryList.setLayoutManager(new LinearLayoutManager(this));
        mTeacherList.setLayoutManager(new LinearLayoutManager(this));
        mRelatives.setHasFixedSize(true);
        mRelatives.setLayoutManager(new LinearLayoutManager(this));

        popUp();
       // retrievePeopleData();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.add_people, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.add_peoples:
                popAddPeople.show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    protected void onStart() {
        super.onStart();


        Query familySearchQuery = familyDatabase.orderByChild("category").startAt("Family Members").endAt("Family Members");
        FirebaseRecyclerAdapter<AddPeople, PeopleViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<AddPeople, PeopleViewHolder>(
                AddPeople.class,
                R.layout.people_view,
                PeopleViewHolder.class,
                familySearchQuery
        ) {
            @Override
            protected void populateViewHolder(PeopleViewHolder viewHolder, AddPeople model, int position) {

                final String post_key = getRef(position).getKey();

                viewHolder.setName(model.getName());
                viewHolder.setRelation(model.getRelation());

                viewHolder.setImage(getApplicationContext(), model.getImage());

                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

//                        Toast.makeText(CseTeacherActivity.this, post_key, Toast.LENGTH_LONG).show();
                        Intent ditailsIntent = new Intent(PeopleList.this, PeopleDetails.class);
                        ditailsIntent.putExtra("post_id", post_key);

                        startActivity(ditailsIntent);
                    }
                });

            }
        };
        mTeacherList.setAdapter(firebaseRecyclerAdapter);


        Query relativesSearchQuery = familyDatabase.orderByChild("category").startAt("Relatives").endAt("Relatives");
        FirebaseRecyclerAdapter<AddPeople, PeopleViewHolder> relativesRecyclerAdapter = new FirebaseRecyclerAdapter<AddPeople, PeopleViewHolder>(
                AddPeople.class,
                R.layout.people_view,
                PeopleViewHolder.class,
                relativesSearchQuery
        ) {
            @Override
            protected void populateViewHolder(PeopleViewHolder viewHolder, AddPeople model, int position) {

                final String post_key = getRef(position).getKey();

                viewHolder.setName(model.getName());
                viewHolder.setRelation(model.getRelation());

                viewHolder.setImage(getApplicationContext(), model.getImage());

                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

//                        Toast.makeText(CseTeacherActivity.this, post_key, Toast.LENGTH_LONG).show();
                        Intent ditailsIntent = new Intent(PeopleList.this, PeopleDetails.class);
                        ditailsIntent.putExtra("post_id", post_key);

                        startActivity(ditailsIntent);
                    }
                });

            }
        };
        mRelatives.setAdapter(relativesRecyclerAdapter);
    }

    private static class PeopleViewHolder extends RecyclerView.ViewHolder {
        View mView;
        public PeopleViewHolder(View itemView) {
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

    private void popUp() {
        // Popup Manu inti..
        popAddPeople = new Dialog(this);
        popAddPeople.setContentView(R.layout.popup_add_people);
        popAddPeople.getWindow().setBackgroundDrawable(new ColorDrawable(Color.GRAY));
        popAddPeople.getWindow().setLayout(Toolbar.LayoutParams.MATCH_PARENT,Toolbar.LayoutParams.WRAP_CONTENT);
        popAddPeople.getWindow().getAttributes().gravity = Gravity.TOP;


        //Popup Widgets
        pName = popAddPeople.findViewById(R.id.name);
        pProfession = popAddPeople.findViewById(R.id.profession);
        pNumber = popAddPeople.findViewById(R.id.textNumber);
        pCategory = popAddPeople.findViewById(R.id.spinner_category);
        pRelation = popAddPeople.findViewById(R.id.spinner_relation);

        final String sGroup[] = {"Select Group","Family Members", "Relatives", "Friends", "Neighbors",
                "Co-Workers", "Important People", "Special People", "Following", "Followers"};
        final String sFamilyMember[] = {"Select Family Member","Father", "Mother", "Sister", "Brother", "Spouse", "Son", "Daughter"};
        final String sRelatives[] = {"Select Relatives","Grand Father", "Grand Mother", "Uncle", "Aunt", "Cousin", "Nephew", "Niece", "Father-in-law",
                "Mother-in-law", "Son-in-law", "Daughter-in-law", "Brother-in-law", "Sister-in-law"};
        final String sOthers[] = {"Select Relationship", "Friends", "Neighbors", "Co-Workers", "Important People", "Special People", "Following", "Followers"};

        final ArrayAdapter<String> group = new ArrayAdapter<String>(PeopleList.this, android.R.layout.simple_spinner_dropdown_item, sGroup);
        pCategory.setAdapter(group);

        pCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String pos = sGroup[position];
                if(position == 1){
                    ArrayAdapter<String> family = new ArrayAdapter<String>(PeopleList.this, android.R.layout.simple_spinner_dropdown_item, sFamilyMember);
                    pRelation.setAdapter(family);
                }
                else if(position == 2){
                    ArrayAdapter<String> family = new ArrayAdapter<String>(PeopleList.this, android.R.layout.simple_spinner_dropdown_item, sRelatives);
                    pRelation.setAdapter(family);
                }else{
                    ArrayAdapter<String> family = new ArrayAdapter<String>(PeopleList.this, android.R.layout.simple_spinner_dropdown_item, sOthers);
                    pRelation.setAdapter(family);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        pImage = popAddPeople.findViewById(R.id.imageButton);

        pAdd = popAddPeople.findViewById(R.id.add);

        // Select Image
        pImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(1, 1)
                        .start(PeopleList.this);
            }
        });

        // People add Button
        pAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPosting();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                imageUri = result.getUri();
                pImage.setImageURI(imageUri);
                //uploadImage();
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(this, ""+error, Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void uploadImage(){
        final ProgressDialog progressBar = new ProgressDialog(PeopleList.this, R.style.AppTheme_Dark_Dialog);
        progressBar.setIndeterminate(true);
        progressBar.setCanceledOnTouchOutside(false);
        progressBar.setMessage("Adding Image..");
        progressBar.show();

        //StorageReference filePath = storageDirectory.child(currentFirebaseUser.getUid());

        storageDirectory.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                String downloadUrl = taskSnapshot.getDownloadUrl().toString();
                databaseDirectory.setValue(downloadUrl);
                Toast.makeText(PeopleList.this, "Image Updated", Toast.LENGTH_LONG).show();
                progressBar.dismiss();
            }
        });
    }

    private void startPosting() {
        final ProgressDialog progressBar = new ProgressDialog(PeopleList.this, R.style.AppTheme_Dark_Dialog);
        progressBar.setIndeterminate(true);
        progressBar.setCanceledOnTouchOutside(false);
        progressBar.setMessage("Adding People...");
        progressBar.show();

        String nameValue = pName.getText().toString().trim();
        String professionValue = pProfession.getText().toString().trim();
        String phoneValue = pNumber.getText().toString().trim();
        String GroupValue = pCategory.getSelectedItem().toString();
        String relationValue = pRelation.getSelectedItem().toString();

        if(GroupValue.equals("Select Group") && relationValue.equals("Select Family Member") && relationValue.equals("Select Relatives") && relationValue.equals("Select Relationship")){
            Toast.makeText(PeopleList.this, "Select a Category and Relationship", Toast.LENGTH_LONG).show();
            progressBar.dismiss();
        }else{
            if(!TextUtils.isEmpty(nameValue) && !TextUtils.isEmpty(professionValue) && !TextUtils.isEmpty(phoneValue) && imageUri != null)
            {
                StorageReference filePath = storageDirectory.child(imageUri.getLastPathSegment());

                final String nameV = nameValue;
                final String professionV = professionValue;
                final String phoneV = phoneValue;
                final String categoryV = GroupValue;
                final String relationV = relationValue;

                filePath.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Uri downloadUrl = taskSnapshot.getDownloadUrl();
                        // DatabaseReference newPost = mDatabase.push();

                        databaseDirectory.child("name").setValue(nameV);
                        databaseDirectory.child("profession").setValue(professionV);
                        databaseDirectory.child("number").setValue(phoneV);
                        databaseDirectory.child("category").setValue(categoryV);
                        databaseDirectory.child("relation").setValue(relationV);
                        databaseDirectory.child("image").setValue(downloadUrl.toString());

                        progressBar.dismiss();

                        startActivity(new Intent(PeopleList.this, PeopleList.class));
                        finish();
                    }
                });

            }
            else{
                progressBar.dismiss();
                Toast.makeText(PeopleList.this, "Please fill up all the fields", Toast.LENGTH_SHORT).show();
            }
        }

    }


}
