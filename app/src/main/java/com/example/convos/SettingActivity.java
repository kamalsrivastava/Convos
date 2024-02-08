package com.example.convos;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class SettingActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 10;

    private EditText setname, setstatus;
    private ImageView setProfile;
    private Button donebut;

    private FirebaseAuth auth;
    private FirebaseDatabase database;
    private FirebaseStorage storage;
    private DatabaseReference userReference;
    private StorageReference uploadReference;
    private Uri setImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();
        userReference = database.getReference().child("user").child(auth.getUid());
        uploadReference = storage.getReference().child("Upload").child(auth.getUid());

        initializeViews();
        loadUserData();

        setProfile.setOnClickListener(view -> chooseImage());
        donebut.setOnClickListener(view -> saveUserData());
    }

    private void initializeViews() {
        setname = findViewById(R.id.settingname);
        setstatus = findViewById(R.id.settingstatus);
        setProfile = findViewById(R.id.settingprofile);
        donebut = findViewById(R.id.donebut);
    }

    private void loadUserData() {
        userReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                setname.setText(snapshot.child("userName").getValue(String.class));
                setstatus.setText(snapshot.child("status").getValue(String.class));
                Picasso.get().load(snapshot.child("profilepic").getValue(String.class)).into(setProfile);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(SettingActivity.this, "Failed to load user data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    private void saveUserData() {
        String name = setname.getText().toString();
        String status = setstatus.getText().toString();

        if (setImageUri != null) {
            uploadReference.putFile(setImageUri).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    uploadReference.getDownloadUrl().addOnSuccessListener(uri -> {
                        String imageUrl = uri.toString();
                        updateUserProfile(name, status, imageUrl);
                    });
                } else {
                    Toast.makeText(SettingActivity.this, "Failed to upload image", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            updateUserProfile(name, status, null);
        }
    }

    private void updateUserProfile(String name, String status, String imageUrl) {
        Users user = new Users(auth.getUid(), name, auth.getCurrentUser().getEmail(), "", "", imageUrl, status);
        userReference.setValue(user).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(SettingActivity.this, "Data saved successfully", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(SettingActivity.this, MainActivity.class));
                finish();
            } else {
                Toast.makeText(SettingActivity.this, "Failed to save data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            setImageUri = data.getData();
            setProfile.setImageURI(setImageUri);
        }
    }
}
