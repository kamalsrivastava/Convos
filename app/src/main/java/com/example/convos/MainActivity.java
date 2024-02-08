package com.example.convos;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private RecyclerView mainUser;
    private UserAdapter adapter;
    private FirebaseDatabase database;
    private ArrayList<Users> usersArrayList;
    private ImageView imgLogOut;
    private ImageView cambut, settingbut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeViews();
        setupListeners();
        setupFirebase();
        setupRecyclerView();
        checkUserLoggedIn();
    }

    private void initializeViews() {
        cambut = findViewById(R.id.camBut);
        settingbut = findViewById(R.id.settingBut);
        imgLogOut = findViewById(R.id.imgLogout);
        mainUser = findViewById(R.id.rviewmainUser);
    }

    private void setupListeners() {
        imgLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showLogoutDialog();
            }
        });

        settingbut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToSettingActivity();
            }
        });

        cambut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startImageCaptureIntent();
            }
        });
    }

    private void setupFirebase() {
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
    }

    private void setupRecyclerView() {
        mainUser.setLayoutManager(new LinearLayoutManager(this));
        usersArrayList = new ArrayList<>();
        adapter = new UserAdapter(MainActivity.this, usersArrayList);
        mainUser.setAdapter(adapter);
    }

    private void checkUserLoggedIn() {
        if (auth.getCurrentUser() == null) {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        } else {
            fetchUserListFromDatabase();
        }
    }

    private void fetchUserListFromDatabase() {
        DatabaseReference reference = database.getReference().child("user");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                usersArrayList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Users users = dataSnapshot.getValue(Users.class);
                    usersArrayList.add(users);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle database error
            }
        });
    }

    private void showLogoutDialog() {
        Dialog dialog = new Dialog(MainActivity.this, R.style.dialog);
        dialog.setContentView(R.layout.dialog_layout);
        Button no = dialog.findViewById(R.id.btnNo);
        Button yes = dialog.findViewById(R.id.btnYes);

        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void goToSettingActivity() {
        Intent intent = new Intent(MainActivity.this, SettingActivity.class);
        startActivity(intent);
    }

    private void startImageCaptureIntent() {
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, 10);
    }
}
