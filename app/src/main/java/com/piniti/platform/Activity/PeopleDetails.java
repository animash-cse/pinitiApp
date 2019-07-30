package com.piniti.platform.Activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.piniti.platform.R;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

public class PeopleDetails extends AppCompatActivity {

    private String mPost_key = null;
    private DatabaseReference mDatabase;
    private StorageReference mStorage;

    private ImageView mImage;
    private TextView mName, mCategory, mRelation, mProfession, mNumber;

    private Button removeButton;

    private Button image, name, category, profession, relation, number;

    private Button editButton;
    private FirebaseAuth mAuth;
    private FirebaseUser currentFirebaseUser;

    private String postName, postCategory, postProfession, postRelation, postNumber, postImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_people_details);

        currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        mStorage = FirebaseStorage.getInstance().getReference();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(currentFirebaseUser.getUid()).child("Peoples");


        mPost_key = getIntent().getExtras().getString("post_id");

        mImage = findViewById(R.id.people_image);
        mName = findViewById(R.id.people_name);
        mCategory = findViewById(R.id.people_category);
        mProfession = findViewById(R.id.people_profession);
        mRelation = findViewById(R.id.people_relation);
        mNumber = findViewById(R.id.people_number);

        removeButton = findViewById(R.id.remove);

        image = findViewById(R.id.edit_image);
        name = findViewById(R.id.name_edit);
        category = findViewById(R.id.cate_edit);
        profession = findViewById(R.id.profe_edit);
        relation = findViewById(R.id.relation_edit);
        number = findViewById(R.id.number_edit);
        editButton = findViewById(R.id.edit);

        mAuth = FirebaseAuth.getInstance();

        mDatabase.child(mPost_key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                // postDesignation,postDepartment,postFaculty,postEmail, postNumber, postResearch, number
                postName = (String) dataSnapshot.child("name").getValue();
                postCategory = (String) dataSnapshot.child("category").getValue();
                postProfession = (String) dataSnapshot.child("profession").getValue();
                postRelation = (String) dataSnapshot.child("relation").getValue();
                postNumber = (String) dataSnapshot.child("number").getValue();
                postImage = (String) dataSnapshot.child("thumb_image").getValue();


                mName.setText(postName);
                mCategory.setText(postCategory);
                mProfession.setText(postProfession);
                mRelation.setText(postRelation);
                mNumber.setText(postNumber);

                Picasso.with(PeopleDetails.this).load(postImage).into(mImage);


                if(mAuth.getCurrentUser() != null){
                    removeButton.setVisibility(View.VISIBLE);
                    editButton.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDatabase.child(mPost_key).removeValue();
                Intent removeIntent = new Intent(PeopleDetails.this, PeopleList.class);
                startActivity(removeIntent);
                finish();
            }
        });
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                image.setVisibility(View.VISIBLE);
                name.setVisibility(View.VISIBLE);
                category.setVisibility(View.VISIBLE);
                profession.setVisibility(View.VISIBLE);
                relation.setVisibility(View.VISIBLE);
                number.setVisibility(View.VISIBLE);
            }
        });


        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(1, 1)
                        .start(PeopleDetails.this);
            }
        });
        name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(PeopleDetails.this);
                builder.setTitle("Edit People Name");

                final EditText nameEdit = new EditText(PeopleDetails.this);
                nameEdit.setText(postName);
                builder.setView(nameEdit);

                builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mDatabase.child(mPost_key).child("name").setValue(nameEdit.getText().toString());
                        Toast.makeText(PeopleDetails.this, "Name Updated", Toast.LENGTH_SHORT).show();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
                Dialog dialog = builder.create();
                dialog.show();
                dialog.getWindow().setBackgroundDrawableResource(android.R.color.holo_blue_bright);
            }
        });

        category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(PeopleDetails.this);
                builder.setTitle("Edit Category");

                final EditText desigEdit = new EditText(PeopleDetails.this);
                desigEdit.setText(postCategory);
                builder.setView(desigEdit);

                builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mDatabase.child(mPost_key).child("category").setValue(desigEdit.getText().toString());
                        Toast.makeText(PeopleDetails.this, "Category Updated", Toast.LENGTH_SHORT).show();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
                Dialog dialog = builder.create();
                dialog.show();
                dialog.getWindow().setBackgroundDrawableResource(android.R.color.holo_blue_bright);
            }
        });

        relation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(PeopleDetails.this);
                builder.setTitle("Edit Relation");

                final EditText departmentEdit = new EditText(PeopleDetails.this);
                departmentEdit.setText(postRelation);
                builder.setView(departmentEdit);

                builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mDatabase.child(mPost_key).child("relation").setValue(departmentEdit.getText().toString());
                        Toast.makeText(PeopleDetails.this, "Relation Updated", Toast.LENGTH_SHORT).show();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
                Dialog dialog = builder.create();
                dialog.show();
                dialog.getWindow().setBackgroundDrawableResource(android.R.color.holo_blue_bright);
            }
        });

        profession.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(PeopleDetails.this);
                builder.setTitle("Edit Profession");

                final EditText facultyEdit = new EditText(PeopleDetails.this);
                facultyEdit.setText(postProfession);
                builder.setView(facultyEdit);

                builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mDatabase.child(mPost_key).child("profession").setValue(facultyEdit.getText().toString());
                        Toast.makeText(PeopleDetails.this, "Profession Updated", Toast.LENGTH_SHORT).show();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
                Dialog dialog = builder.create();
                dialog.show();
                dialog.getWindow().setBackgroundDrawableResource(android.R.color.holo_blue_bright);
            }
        });

        number.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(PeopleDetails.this);
                builder.setTitle("Edit Number");

                final EditText numberEdit = new EditText(PeopleDetails.this);
                numberEdit.setText(postNumber);
                builder.setView(numberEdit);

                builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mDatabase.child(mPost_key).child("number").setValue(numberEdit.getText().toString());
                        Toast.makeText(PeopleDetails.this, "Number Updated", Toast.LENGTH_SHORT).show();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
                Dialog dialog = builder.create();
                dialog.show();
                dialog.getWindow().setBackgroundDrawableResource(android.R.color.holo_blue_bright);
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

                Uri imageUri = result.getUri();
                mImage.setImageURI(imageUri);

                final ProgressDialog progressBar = new ProgressDialog(PeopleDetails.this, R.style.AppTheme_Dark_Dialog);
                progressBar.setIndeterminate(true);
                progressBar.setMessage("Adding Image...");
                progressBar.show();

                StorageReference filePath = mStorage.child("Users").child(currentFirebaseUser.getUid()).child("Peoples").child(imageUri.getLastPathSegment());

                filePath.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Uri downloadUrl = taskSnapshot.getDownloadUrl();
                        DatabaseReference newPost = mDatabase.child(mPost_key);
                        newPost.child("image").setValue(downloadUrl.toString());
                        Toast.makeText(PeopleDetails.this, "Image Updated", Toast.LENGTH_SHORT).show();
                        progressBar.dismiss();

                    }
                });

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(this, ""+error, Toast.LENGTH_SHORT).show();
            }
        }
    }

}
