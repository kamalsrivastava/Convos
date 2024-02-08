package com.example.convos;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;


public class ChatActivity extends AppCompatActivity {

    String recieverImg, recieverUid, recieverName, senderUid;
    CircleImageView profile;
    TextView recieverNName;
    CardView btnsend;
    EditText etmsg;
    FirebaseAuth auth;
    FirebaseDatabase database = null;
    public static String senderImg;
    public static String reciverIImg;
    String senderRoom, reciverRoom;
    RecyclerView mmessangesAdapter;
    ArrayList<MessageModelClass> messagessArrayList;
    MessageAdapter messageAdapter;
    private DatabaseReference senderChatreference,receiverChatReference;
    private String TAG = ChatActivity.class.getName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_head);
        database = FirebaseDatabase.getInstance();
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
        messagessArrayList = new ArrayList<>();
        messageAdapter = new MessageAdapter(ChatActivity.this, messagessArrayList);
        mmessangesAdapter.setAdapter(messageAdapter);

        Picasso.get().load(recieverImg).into(profile);
        recieverNName.setText("" + recieverName);

        DatabaseReference reference = database.getReference().child("chats").child(auth.getUid());
        senderRoom = auth.getUid()+recieverUid;
        reciverRoom = recieverUid+auth.getUid();;
        receiverChatReference = database.getReference().child("chats").child(reciverRoom).child("messages");
        senderChatreference = database.getReference().child("chats").child(senderRoom).child("messages");
        bindChildEventListener();


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

        btnsend.setOnClickListener(view -> {
            String msg = etmsg.getText().toString();
            if (msg.isEmpty()) {
                Toast.makeText(ChatActivity.this, "Enter the message first", Toast.LENGTH_SHORT).show();
            } else {
                etmsg.setText("");
                Date date = new Date();
                MessageModelClass messages = new MessageModelClass(msg, senderUid, date.getTime());
                if (senderRoom != null && reciverRoom != null) {
                    DatabaseReference senderRoomRef = database.getReference().child("chats").child(senderRoom).child("messages");
                    DatabaseReference reciverRoomRef = database.getReference().child("chats").child(reciverRoom).child("messages");

                    senderRoomRef.push().setValue(messages)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (!task.isSuccessful()) {
                                        Toast.makeText(ChatActivity.this, "Failed to send message: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                        Log.e("sendMessage", "Failed to send message", task.getException());
                                    }else{
                                        Toast.makeText(ChatActivity.this, "Sent message: " , Toast.LENGTH_SHORT).show();

                                    }
                                }
                            });
                    reciverRoomRef.push().setValue(messages)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (!task.isSuccessful()) {
                                        Toast.makeText(ChatActivity.this, "Failed to send message: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                        Log.e("sendMessage", "Failed to send message", task.getException());
                                    }
                                }
                            });
                } else {
                    Toast.makeText(ChatActivity.this, "Failed to send message", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void bindChildEventListener(){
        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "onChildAdded:" + dataSnapshot.getKey());

                // A new comment has been added, add it to the displayed list
//                Comment comment = dataSnapshot.getValue(Comment.class);

                // ...
                MessageModelClass msg=dataSnapshot.getValue(MessageModelClass.class);
                messagessArrayList.add(msg);
                messageAdapter.notifyDataSetChanged();

                Log.d(TAG, "onChildAddedSize:" + messagessArrayList.size());

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "onChildChanged:" + dataSnapshot.getKey());
                String commentKey = dataSnapshot.getKey();
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Log.d(TAG, "onChildRemoved:" + dataSnapshot.getKey());

                // A comment has changed, use the key to determine if we are displaying this
                // comment and if so remove it.
                String commentKey = dataSnapshot.getKey();

                // ...
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "onChildMoved:" + dataSnapshot.getKey());

                // A comment has changed position, use the key to determine if we are
                // displaying this comment and if so move it.
//                Comment movedComment = dataSnapshot.getValue(Comment.class);
                String commentKey = dataSnapshot.getKey();

                // ...
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "postComments:onCancelled", databaseError.toException());

            }
        };
        senderChatreference.addChildEventListener(childEventListener);
    }
}