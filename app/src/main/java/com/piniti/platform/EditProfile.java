package com.piniti.platform;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import id.zelory.compressor.Compressor;

public class EditProfile extends AppCompatActivity {

    private ImageButton mSelectImage;
    private Spinner mGender;

    // Declare edit text
    private EditText mName;
    private EditText mEmail;
    private EditText mPhone;
    private EditText mProfession;
    private EditText mAddress;

    private String URL;

    // Declare image location url
    private Uri imageUri = null;
    // Declare Compress image
    private Bitmap thumb_Bitmap = null;

    private Button mUpdate;

    // Declare Database for data fields
    private DatabaseReference databaseUser;

    // Declare Storage for images
    private StorageReference mStorage;

    //  Declare firebase user for get user id
    private FirebaseUser currentFirebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        // Initialize image button and spinner
        mSelectImage = findViewById(R.id.imageButton);
        mGender = findViewById(R.id.gender);

        // Initialize edit text
        mName = findViewById(R.id.name);
        mEmail = findViewById(R.id.email);
        mProfession = findViewById(R.id.profession);
        mPhone = findViewById(R.id.textNumber);
        mAddress = findViewById(R.id.address);

        // Initialize Button
        mUpdate = findViewById(R.id.update);

        // Here get user id in currentFirebaseUser
        currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        // Set storage and database location
        mStorage = FirebaseStorage.getInstance().getReference().child("UserPhoto");
        databaseUser = FirebaseDatabase.getInstance().getReference().child("Users").child(currentFirebaseUser.getUid());

        // Button action for update user information
        mUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateUserInformation();
            }
        });

        // Button action for update image information
        mSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(1, 1)
                        .start(EditProfile.this);
            }
        });

        //  View user information and image
        databaseUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mName.setText(dataSnapshot.child("name").getValue(String.class));
                mEmail.setText(dataSnapshot.child("email").getValue(String.class));
                mProfession.setText(dataSnapshot.child("profession").getValue(String.class));
                mPhone.setText(dataSnapshot.child("number").getValue(String.class));
                mAddress.setText(dataSnapshot.child("address").getValue(String.class));
                URL = (dataSnapshot.child("thumb_image").getValue(String.class));

                // mGender.setText(dataSnapshot.child("gender").getValue(String.class));
                Glide.with(getApplicationContext()).load(URL).into(mSelectImage);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    // Action when click Update user image Button
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                // Get Image Location
                imageUri = result.getUri();
                // Set image to Image view
                mSelectImage.setImageURI(imageUri);

                // Set compress image size and compress to Bitmap
                File thumb_filePath_Uri = new File(imageUri.getPath());
                try{
                    thumb_Bitmap = new Compressor(this)
                            .setMaxWidth(200)
                            .setMaxHeight(200)
                            .setQuality(45)
                            .compressToBitmap(thumb_filePath_Uri);
                } catch (IOException e){
                    e.printStackTrace();
                }

                uploadImage();  // Uploading process

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(this, ""+error, Toast.LENGTH_SHORT).show();
            }
        }
    }
    public void uploadImage(){
        // Show loading bar in the screen
        final ProgressDialog progressBar = new ProgressDialog(EditProfile.this, R.style.AppTheme_Dark_Dialog);
        progressBar.setIndeterminate(true);
        progressBar.setCanceledOnTouchOutside(false);
        progressBar.setMessage("Adding Image..");
        progressBar.show();

        //  Original and compress image storage location
        final StorageReference filePath = mStorage.child(currentFirebaseUser.getUid()).child("ProfileImage");
        final StorageReference thumbPath = mStorage.child(currentFirebaseUser.getUid()).child("ThumbImage");

        //  Compressing original image to low quality image
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        thumb_Bitmap.compress(Bitmap.CompressFormat.JPEG, 45, byteArrayOutputStream);
        final byte[] thumb_byte = byteArrayOutputStream.toByteArray();

        final UploadTask imageTask = filePath.putFile(imageUri);
        final UploadTask thumbTask = thumbPath.putFile(imageUri);
        UploadTask uploadTask = thumbPath.putBytes(thumb_byte);

        imageTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {

            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // this is where we will end up if our image uploads successfully.

                Task<Uri> downloadUrl = filePath.getDownloadUrl();
                downloadUrl.addOnSuccessListener(new OnSuccessListener<Uri>() {

                    @Override
                    public void onSuccess(Uri uri) {
                        String imageReference = uri.toString();
                        databaseUser.child("image").setValue(imageReference);
                    }
                });

                // Thumb Image Upload
                thumbTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {

                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // this is where we will end up if our image uploads successfully.
                        Task<Uri> downloadUrl = thumbPath.getDownloadUrl();
                        downloadUrl.addOnSuccessListener(new OnSuccessListener<Uri>() {

                            @Override
                            public void onSuccess(Uri uri) {
                                String imageReference = uri.toString();
                                databaseUser.child("thumb_image").setValue(imageReference);
                                progressBar.dismiss();
                            }
                        });
                    }
                });
            }
        });

        /*// Upload User original image and compress image
        filePath.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                final String downloadUrl = taskSnapshot.getDownloadUrl().toString();


                //  Perform action to upload compress image to database and storage
                UploadTask uploadTask = thumbPath.putBytes(thumb_byte);
                uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        String thumb_downloadUri = task.getResult().getDownloadUrl().toString();
                        if(task.isSuccessful()){
                            Map update_user_data = new HashMap();
                            update_user_data.put("image", downloadUrl);
                            update_user_data.put("thumb_image", thumb_downloadUri);

                            databaseUser.updateChildren(update_user_data).addOnCompleteListener(new OnCompleteListener() {
                                @Override
                                public void onComplete(@NonNull Task task) {

                                    // View image to Image button after upload image
                                    mSelectImage.setImageURI(imageUri);
                                    Toast.makeText(EditProfile.this, "Image Updated", Toast.LENGTH_LONG).show();
                                    progressBar.dismiss();
                                }
                            });
                        }
                        else{
                            Toast.makeText(getApplicationContext(), "Faild to Image upload", Toast.LENGTH_LONG).show();
                        }
                    }
                });

            }
        });*/

    }

    private void updateUserInformation() {
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
            databaseUser.child("name").setValue(nameV);
            databaseUser.child("profession").setValue(professionV);
            databaseUser.child("address").setValue(addressV);
            databaseUser.child("number").setValue(numberV);
            databaseUser.child("email").setValue(emailV);
            databaseUser.child("gender").setValue(genderV);

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
