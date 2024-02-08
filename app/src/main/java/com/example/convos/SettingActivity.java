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

import java.net.URI;

public class SettingActivity extends AppCompatActivity {

    ImageView setProfile;
    EditText setname,setstatus;
    Button donebut;
    FirebaseAuth auth;
    final FirebaseDatabase[] database = {null};
    final FirebaseStorage[] storage = {null};
    String email,password,cnfpassword;
    Uri setImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        auth=FirebaseAuth.getInstance();
        database[0]=FirebaseDatabase.getInstance();
        storage[0]=FirebaseStorage.getInstance();
        donebut=findViewById(R.id.donebut);
        setProfile=findViewById(R.id.settingprofile);
        setname=findViewById(R.id.settingname);
        setstatus=findViewById(R.id.settingstatus);

        DatabaseReference reference=database[0].getReference().child("user").child(auth.getUid());
        StorageReference storageReference=storage[0].getReference().child("Upload").child(auth.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                email=snapshot.child("mail").getValue().toString();
                password=snapshot.child("password").getValue().toString();
                cnfpassword=snapshot.child("cnfpassword").getValue().toString();
                String name=snapshot.child("userName").getValue().toString();
                String profile=snapshot.child("profilepic").getValue().toString();
                String status=snapshot.child("status").getValue().toString();
                setname.setText(name);
                setstatus.setText(status);
                Picasso.get().load(profile).into(setProfile);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        setProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"),10);

            }
        });
        donebut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name=setname.getText().toString();
                String status=setstatus.getText().toString();
                if(setImageUri!=null){
                    storageReference.putFile(setImageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String finalImageUri=uri.toString();
                                    Users users=new Users(auth.getUid(),name,email,password,cnfpassword,finalImageUri,status);
                                    reference.setValue(users).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                Toast.makeText(SettingActivity.this,"DataisSave",Toast.LENGTH_SHORT);
                                                Intent intent=new Intent(SettingActivity.this,MainActivity.class);
                                                startActivity(intent);
                                                finish();
                                            }
                                            else{
                                                Toast.makeText(SettingActivity.this,"Something went wrong....",Toast.LENGTH_SHORT);
                                            }
                                        }
                                    });
                                }
                            });
                        }
                    });
                }
                else{
                    storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String finalImageUri=uri.toString();
                            Users users=new Users(auth.getUid(),name,email,password,cnfpassword,finalImageUri,status);
                            reference.setValue(users).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Toast.makeText(SettingActivity.this,"DataisSave",Toast.LENGTH_SHORT);
                                        Intent intent=new Intent(SettingActivity.this,MainActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                    else{
                                        Toast.makeText(SettingActivity.this,"Something went wrong....",Toast.LENGTH_SHORT);
                                    }
                                }
                            });
                        }
                    });
                }
            }
        });

    }
    @Override
    protected void onActivityResult(int requestCode,int resultCode, @Nullable Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        if(requestCode==10){
            if(data!=null){
                setImageUri=data.getData();
                setProfile.setImageURI(setImageUri);
            }
        }
    }
}