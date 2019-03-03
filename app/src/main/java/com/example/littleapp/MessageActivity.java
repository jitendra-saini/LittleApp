package com.example.littleapp;

import android.content.Intent;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.littleapp.Adapter.MessageAdapter;
import com.example.littleapp.Model.Chat;
import com.example.littleapp.Model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageActivity extends AppCompatActivity {

    CircleImageView circleImageView;
    ImageButton send_Image;
    TextView textView;
    FirebaseUser firebaseUser;
    DatabaseReference reference;
    EditText textToBeSend;
    Intent intent;

    MessageAdapter messageAdapter;
    RecyclerView recyclerView;
    List<Chat> mChat;
    ValueEventListener seenListener;
    String userid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);


        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MessageActivity.this,HomeActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });

        circleImageView=findViewById(R.id.profile_image);
        textView=findViewById(R.id.username);
        send_Image=findViewById(R.id.btn_send);
        textToBeSend=findViewById(R.id.text_send);



        recyclerView=findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);


        firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
        reference= FirebaseDatabase.getInstance().getReference("Users").child(userid);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                User user1=dataSnapshot.getValue(User.class);
                textView.setText(user1.getUsername());
                Log.e("profile iamge",user1.getImageURl());
                if(user1.getImageURl().equals("default")){

                    circleImageView.setImageResource(R.mipmap.ic_launcher_round);

                }else {
                    Glide.with(getApplicationContext()).load(user1.getImageURl()).into(circleImageView);

                }
                readMessage(firebaseUser.getUid(),userid,user1.getImageURl());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        intent=getIntent();

        userid=intent.getStringExtra("user_id");
        send_Image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg=textToBeSend.getText().toString();
                if(!msg.equals("")){
                    sendMessage(firebaseUser.getUid(),userid,msg);
                }else{

                    Toast.makeText(MessageActivity.this,"you can't sent empty message",Toast.LENGTH_SHORT).show();
                }
                textToBeSend.setText("");
                textToBeSend.setHint("type here");
            }
        });



           seenMessage(userid);
    }

    private void seenMessage(final String userid){


        reference=FirebaseDatabase.getInstance().getReference("chats");

        seenListener=reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                   Chat chat=snapshot.getValue(Chat.class);
                   if(chat.getReceiver().equals(firebaseUser.getUid()) && chat.getSender().equals(userid)){

                       HashMap<String,Object> hashMap=new HashMap<>();
                       hashMap.put("isSeen",true);
                       snapshot.getRef().updateChildren(hashMap);

                   }


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void sendMessage(String sender,String receiver,String message){


        DatabaseReference reference=FirebaseDatabase.getInstance().getReference();

        HashMap<String, Object> hashMap=new HashMap<>();

        hashMap.put("sender",sender);
        hashMap.put("receiver",receiver);
        hashMap.put("message",message);
        hashMap.put("isSeen",false);


        reference.child("chats").push().setValue(hashMap);


        final DatabaseReference chatRef=FirebaseDatabase.getInstance().getReference("Users")
                .child(firebaseUser.getUid())
                .child("chatList");




        chatRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()){

                    chatRef.child("id").setValue(userid);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void readMessage(final String myId, final String userId, final String imageurl){


        mChat =new ArrayList<>();

        reference=FirebaseDatabase.getInstance().getReference("chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mChat.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                  Chat chat=snapshot.getValue(Chat.class);
                  if(chat.getReceiver().equals(myId) && chat.getSender().equals(userId)
                          || chat.getReceiver().equals(userId) && chat.getSender().equals(myId)){
                       mChat.add(chat);
                  }

                  messageAdapter=new MessageAdapter(MessageActivity.this,mChat,imageurl);
                  recyclerView.setAdapter(messageAdapter);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void status(String status){

        reference=FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        HashMap<String ,Object> hashMap=new HashMap<>();
        hashMap.put("status",status);
        reference.updateChildren(hashMap);



    }

    @Override
    protected void onResume() {
        super.onResume();

        status("online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        reference.removeEventListener(seenListener);
        status("offline");
    }
}
