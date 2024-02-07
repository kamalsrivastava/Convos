package com.example.convos;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;


public class chatHeadActivity extends AppCompatActivity {

    String recieverImg, recieverUid, recieverName, senderUid;
    CircleImageView profile;
    TextView recieverNName;
    CardView btnsend;
    EditText etmsg;
    FirebaseAuth auth;
    final FirebaseDatabase[] database = {null};
    public static String senderImg;
    public static String reciverIImg;
    String senderRoom, reciverRoom;
    RecyclerView mmessangesAdapter;
    ArrayList<msgModelClass> messagessArrayList;
    messageAdapter messageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_head);
        database[0] = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();

        recieverName = getIntent().getStringExtra("namee");
        recieverImg = getIntent().getStringExtra("recieverImg");
        recieverUid = getIntent().getStringExtra("uid");

        messagessArrayList = new ArrayList<>();

        btnsend = findViewById(R.id.btnSend);
        etmsg = findViewById(R.id.etmsg);
        recieverNName = findViewById(R.id.txtrecievername);
        profile = findViewById(R.id.imggChat);


        mmessangesAdapter = findViewById(R.id.msgadapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        mmessangesAdapter.setLayoutManager(linearLayoutManager);

        messageAdapter = new messageAdapter(chatHeadActivity.this, messagessArrayList);
        mmessangesAdapter.setAdapter(messageAdapter);






        Picasso.get().load(recieverImg).into(profile);
        recieverNName.setText("" + recieverName);

        DatabaseReference reference = database[0].getReference().child("user").child(auth.getUid());
        DatabaseReference chatreference = database[0].getReference().child("user");

        chatreference.child(auth.getUid()).child(recieverUid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messagessArrayList.clear();
                if (snapshot.exists()) {
                    senderRoom = auth.getUid() + recieverUid;
                    reciverRoom = recieverUid + auth.getUid();

                    DataSnapshot messagesSnapshot = snapshot.child("messages");
                    if (messagesSnapshot.exists()) {
                        for (DataSnapshot dataSnapshot : messagesSnapshot.getChildren()) {
                            msgModelClass messages = dataSnapshot.getValue(msgModelClass.class);
                            if (messages != null) {
                                messagessArrayList.add(messages);
                            }
                        }
                        messageAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle onCancelled event
            }
        });

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                senderImg = snapshot.child("recieverImg").getValue(String.class);
                reciverIImg = recieverImg;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle onCancelled event
            }
        });

        senderUid = auth.getUid();

        btnsend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String msg = etmsg.getText().toString();
                if (msg.isEmpty()) {
                    Toast.makeText(chatHeadActivity.this, "Enter the message first", Toast.LENGTH_SHORT).show();
                } else {
                    etmsg.setText("");
                    Date date = new Date();
                    msgModelClass messages = new msgModelClass(msg, senderUid, date.getTime());
                    if (senderRoom != null && reciverRoom != null) {
                        DatabaseReference senderRoomRef = database[0].getReference().child("chats").child(senderRoom).child("messages");
                        DatabaseReference reciverRoomRef = database[0].getReference().child("chats").child(reciverRoom).child("messages");

                        senderRoomRef.push().setValue(messages)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (!task.isSuccessful()) {
                                            Toast.makeText(chatHeadActivity.this, "Failed to send message: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                            Log.e("sendMessage", "Failed to send message", task.getException());
                                        }
                                    }
                                });
                        reciverRoomRef.push().setValue(messages)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (!task.isSuccessful()) {
                                            Toast.makeText(chatHeadActivity.this, "Failed to send message: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                            Log.e("sendMessage", "Failed to send message", task.getException());
                                        }
                                    }
                                });
                    } else {
                        Toast.makeText(chatHeadActivity.this, "Failed to send message", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
}
