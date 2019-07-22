package com.piniti.platform;


import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class UserFragment extends Fragment {

    private ImageButton mSelectImage;
    private Spinner mGender;

    private EditText mName;
    private EditText mEmail;
    private EditText mPhone;
    private EditText mProfession;
    private EditText mAddress;

    private Uri imageUri = null;
    private Button mUpdate;
    private DatabaseReference mDatabase;
    private StorageReference mStorage;

    public UserFragment() {
        // Required empty public constructor

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user, container, false);
        // Inflate the layout for this fragment
       // Button mUpdate = (Button)view.findViewById(R.id.update);

        return view;
    }

}
