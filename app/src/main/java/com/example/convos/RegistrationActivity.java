package com.example.convos;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import de.hdodenhof.circleimageview.CircleImageView;

public class RegistrationActivity extends AppCompatActivity {

    private EditText email, password, username, cnfpassword;
    private TextView login;
    private Button button;
    private CircleImageView rg_profileImg;

    private FirebaseAuth auth;
    private FirebaseDatabase database;
    private FirebaseStorage storage;
    private Uri imageURI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        initializeViews();
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegistrationActivity.this, LoginActivity.class));
                finish();
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerUser();
            }
        });

        rg_profileImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectProfileImage();
            }
        });
    }

    private void initializeViews() {
        email = findViewById(R.id.etEmail);
        password = findViewById(R.id.etPassword);
        username = findViewById(R.id.etUsername);
        cnfpassword = findViewById(R.id.etConfirm);
        login = findViewById(R.id.txtLogin);
        button = findViewById(R.id.btnRegister);
        rg_profileImg = findViewById(R.id.profilerg);
    }

    private void registerUser() {
        String name = username.getText().toString().trim();
        String userEmail = email.getText().toString().trim();
        String userPassword = password.getText().toString().trim();
        String confirmPassword = cnfpassword.getText().toString().trim();
        String status = "Hey I'm Using this Application";

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(userEmail) || TextUtils.isEmpty(userPassword) || TextUtils.isEmpty(confirmPassword)) {
            Toast.makeText(RegistrationActivity.this, "Please Enter Valid Information", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!userEmail.matches("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+")) {
            email.setError("Type A Valid EMAIL");
            return;
        }

        if (userPassword.length() < 8) {
            password.setError("Enter Password longer Than or Equal to 8");
            return;
        }

        if (!userPassword.equals(confirmPassword)) {
            cnfpassword.setError("The Password does not match");
            return;
        }

        auth.createUserWithEmailAndPassword(userEmail, userPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    uploadUserData(name, userEmail, userPassword, status);
                } else {
                    Toast.makeText(RegistrationActivity.this, "An Exception Occured", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void uploadUserData(String name, String email, String password, String status) {
        String userId = auth.getCurrentUser().getUid();
        DatabaseReference userRef = database.getReference().child("user").child(userId);
        StorageReference storageRef = storage.getReference().child("Upload").child(userId);

        if (imageURI != null) {
            storageRef.putFile(imageURI).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if (task.isSuccessful()) {
                        storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String imageUrl = uri.toString();
                                Users user = new Users(userId, name, email, password, password, imageUrl, status);
                                userRef.setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            startActivity(new Intent(RegistrationActivity.this, MainActivity.class));
                                            finish();
                                        } else {
                                            Toast.makeText(RegistrationActivity.this, "Error in creating user", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        });
                    }
                }
            });
        } else {
            String defaultImageUrl = "https://firebasestorage.googleapis.com/v0/b/convos-5eb54.appspot.com/o/man.png?alt=media&token=807acfb5-52d9-45aa-843c-a12fbb2a0671";
            Users user = new Users(userId, name, email, password, password, defaultImageUrl, status);
            userRef.setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        startActivity(new Intent(RegistrationActivity.this, MainActivity.class));
                        finish();
                    } else {
                        Toast.makeText(RegistrationActivity.this, "Error in creating user", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void selectProfileImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 10);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 10 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageURI = data.getData();
            rg_profileImg.setImageURI(imageURI);
        }
    }
}
