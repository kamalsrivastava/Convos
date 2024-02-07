package com.example.convos;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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
    EditText email,password,username,cnfpassword;
    TextView login;
    Button button;
    CircleImageView rg_profileImg;
    FirebaseAuth auth;
    Uri imageURI;
    String imageuri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        login=findViewById(R.id.txtLogin);
        username=findViewById(R.id.etUsername);
        password=findViewById(R.id.etPassword);
        email=findViewById(R.id.etEmail);
        cnfpassword=findViewById(R.id.etConfirm);
        button=findViewById(R.id.btnRegister);
        rg_profileImg=findViewById(R.id.profilerg);
        String epattern="[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        final FirebaseDatabase[] database = {null};
        final FirebaseStorage[] storage = {null};

        auth = FirebaseAuth.getInstance();


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(RegistrationActivity.this,LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name=username.getText().toString();
                String emaill=email.getText().toString();
                String pass=password.getText().toString();
                String cnfpass=cnfpassword.getText().toString();
                String status="Hey I'm Using this Application";
                database[0] =FirebaseDatabase.getInstance();
                storage[0] =FirebaseStorage.getInstance();


                if(TextUtils.isEmpty(name) || TextUtils.isEmpty(emaill) || TextUtils.isEmpty(pass) || TextUtils.isEmpty(cnfpass)){
                    Toast.makeText(RegistrationActivity.this,"Please Enter Valid Information",Toast.LENGTH_SHORT).show();
                }
                else if(!emaill.matches(epattern)){
                    email.setError("Type A Valid EMAIL");
                } else if (pass.length()<8) {
                    password.setError("Enter Password longer Than or Equal to 8");
                }
                else if(!pass.equals(cnfpass)){
                    cnfpassword.setError("The Password does not match");
                }
                else{
                    auth.createUserWithEmailAndPassword(emaill, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                String id=task.getResult().getUser().getUid();
                                DatabaseReference reference= database[0].getReference().child("user").child(id);
                                StorageReference storageReference= storage[0].getReference().child("Upload").child(id);

                                if(imageURI!=null){
                                    storageReference.putFile(imageURI).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                            if(task.isSuccessful()){
                                                storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                    @Override
                                                    public void onSuccess(Uri uri) {
                                                        imageuri = uri.toString();
                                                        Users users=new Users(id,name,emaill,pass,cnfpass,imageuri,status);
                                                        reference.setValue(users).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if(task.isSuccessful()){
                                                                    Intent intent=new Intent(RegistrationActivity.this,MainActivity.class);
                                                                    startActivity(intent);
                                                                    finish();
                                                                }else{
                                                                    Toast.makeText(RegistrationActivity.this,"Error in creating user",Toast.LENGTH_SHORT).show();
                                                                }
                                                            }
                                                        });
                                                    }
                                                });
                                            }
                                        }
                                    });
                                }
                                else{
                                    String status="Hey I'm Using this Application";
                                    imageuri="https://firebasestorage.googleapis.com/v0/b/convos-5eb54.appspot.com/o/man.png?alt=media&token=807acfb5-52d9-45aa-843c-a12fbb2a0671";
                                    Users users=new Users(id,name,emaill,pass,imageuri,status, status);
                                    reference.setValue(users).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                Intent intent=new Intent(RegistrationActivity.this,MainActivity.class);
                                                startActivity(intent);
                                                finish();
                                            }else{
                                                Toast.makeText(RegistrationActivity.this,"Error in creating user",Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }
                            }
                            else{
                                Toast.makeText(RegistrationActivity.this,"An Exception Occured",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });


        rg_profileImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Select Picture"),10);

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==10){
            if(data!=null){
                imageURI=data.getData();
                rg_profileImg.setImageURI(imageURI);
            }
        }
    }
}