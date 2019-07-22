package com.piniti.platform;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
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
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfile extends AppCompatActivity {

    //private ImageButton mSelectImage;
    private ImageButton mSelectImage;
    private Spinner mGender;

    private EditText mName;
    private EditText mEmail;
    private EditText mPhone;
    private EditText mProfession;
    private EditText mAddress;

    private String URL;


    private Uri imageUri = null;
    private Button mUpdate;
    private DatabaseReference mDatabase, databaseUser;
    private StorageReference mStorage;
    private FirebaseAuth mAuth;
    private FirebaseUser currentFirebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        mSelectImage = (ImageButton) findViewById(R.id.imageButton);
        mGender = (Spinner) findViewById(R.id.gender);

        mName = (EditText) findViewById(R.id.name);
        mEmail = (EditText) findViewById(R.id.email);
        mProfession = (EditText) findViewById(R.id.profession);
        mPhone = (EditText) findViewById(R.id.textNumber);
        mAddress = (EditText) findViewById(R.id.address);
        mUpdate = (Button) findViewById(R.id.update);

        currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        //Toast.makeText(this, "" + currentFirebaseUser.getUid(), Toast.LENGTH_SHORT).show();

        mStorage = FirebaseStorage.getInstance().getReference().child("UserPhoto");
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        databaseUser = FirebaseDatabase.getInstance().getReference().child("Users").child(currentFirebaseUser.getUid());

        mUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startPosting();
            }
        });

        mSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(1, 1)
                        .start(EditProfile.this);
            }
        });

        databaseUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mName.setText(dataSnapshot.child("name").getValue(String.class));
                mEmail.setText(dataSnapshot.child("email").getValue(String.class));
                mProfession.setText(dataSnapshot.child("profession").getValue(String.class));
                mPhone.setText(dataSnapshot.child("number").getValue(String.class));
                mAddress.setText(dataSnapshot.child("address").getValue(String.class));
                URL = (dataSnapshot.child("image").getValue(String.class));

                // mGender.setText(dataSnapshot.child("gender").getValue(String.class));
                Glide.with(EditProfile.this).load(URL).into(mSelectImage);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

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
                mSelectImage.setImageURI(imageUri);

                uploadImage();
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(this, ""+error, Toast.LENGTH_SHORT).show();
            }
        }
    }
    public void uploadImage(){
        final ProgressDialog progressBar = new ProgressDialog(EditProfile.this, R.style.AppTheme_Dark_Dialog);
        progressBar.setIndeterminate(true);
        progressBar.setCanceledOnTouchOutside(false);
        progressBar.setMessage("Adding Image..");
        progressBar.show();

        StorageReference filePath = mStorage.child(currentFirebaseUser.getUid());

        filePath.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                String downloadUrl = taskSnapshot.getDownloadUrl().toString();
                mDatabase.child(currentFirebaseUser.getUid()).child("image").setValue(downloadUrl);
                mSelectImage.setImageURI(imageUri);
                Toast.makeText(EditProfile.this, "Image Updated", Toast.LENGTH_LONG).show();
                progressBar.dismiss();
            }
        });
    }

    private void startPosting() {
        //final String user_id = mAuth.getCurrentUser().getUid();

        final String nameV =  mName.getText().toString().trim();
        final String emailV = mEmail.getText().toString().trim();
        final String professionV = mProfession.getText().toString().trim();
        final String addressV =  mAddress.getText().toString().trim();
        final String numberV = mPhone.getText().toString().trim();
        final String genderV = mGender.getSelectedItem().toString();

        if(!TextUtils.isEmpty(nameV) && !TextUtils.isEmpty(emailV) && !TextUtils.isEmpty(professionV) && !TextUtils.isEmpty(addressV) &&
                !TextUtils.isEmpty(numberV)&& !TextUtils.isEmpty(genderV))
        {
            mDatabase.child(currentFirebaseUser.getUid()).child("name").setValue(nameV);
            mDatabase.child(currentFirebaseUser.getUid()).child("profession").setValue(professionV);
            mDatabase.child(currentFirebaseUser.getUid()).child("address").setValue(addressV);
            mDatabase.child(currentFirebaseUser.getUid()).child("number").setValue(numberV);
            mDatabase.child(currentFirebaseUser.getUid()).child("email").setValue(emailV);
            mDatabase.child(currentFirebaseUser.getUid()).child("gender").setValue(genderV);

            Toast.makeText(this, "Updated Successfully", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(EditProfile.this, ShowProfile.class);
            startActivity(intent);

            /*final String uid = currentFirebaseUser.getUid();
            HashMap dataMap = new HashMap();
            dataMap.put("name", nameV);
            mDatabase.child(uid).updateChildren(dataMap).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()){
                        Toast.makeText(ProfileActivity.this, nameV, Toast.LENGTH_LONG);
                        progressBar.dismiss();
                    }
                    else{
                        String message = task.getException().getMessage();
                        Toast.makeText(ProfileActivity.this, message, Toast.LENGTH_LONG);
                        progressBar.dismiss();
                    }
                }
            });*/
        }
        else{
            Toast.makeText(EditProfile.this, "Please fill up all the fields", Toast.LENGTH_SHORT).show();
        }
    }
}
