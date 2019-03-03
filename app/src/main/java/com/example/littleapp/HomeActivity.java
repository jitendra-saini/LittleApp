package com.example.littleapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.littleapp.Adapter.UserAdapter;

import com.example.littleapp.Model.Chatlist;
import com.example.littleapp.Model.FriendList;
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

public class HomeActivity extends AppCompatActivity {





    private FloatingActionButton floatingActionButton;
    private RecyclerView recyclerView;
    private UserAdapter userAdapter;

    private List<User> mUsers;
    FirebaseUser firebaseUser;
    DatabaseReference reference;

    private List<Chatlist> userlist;




    CircleImageView profile_image;
    TextView username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");



        profile_image = findViewById(R.id.profile_image);
        username = findViewById(R.id.username);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users")
                .child(firebaseUser.getUid());


        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                User user1 = dataSnapshot.getValue(User.class);
                username.setText(user1.getUsername());
                Log.e("profile image", user1.getImageURl());
                if (user1.getImageURl().equals("default")) {

                    profile_image.setImageResource(R.mipmap.ic_launcher_round);

                } else {
                    Glide.with(getApplicationContext()).load(user1.getImageURl())
                            .into(profile_image);

                }




            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        floatingActionButton=findViewById(R.id.find_button);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this,UsersActivity.class));

            }
        });
        recyclerView=findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();


        userlist=new ArrayList<>();

        reference=FirebaseDatabase.getInstance().getReference("Chatlist")
                .child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userlist.clear();
                for (DataSnapshot snapshot:dataSnapshot.getChildren()){

                    Chatlist chatlist=snapshot.getValue(Chatlist.class);
                    userlist.add(chatlist);
                }
                chatlist();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });





    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

         getMenuInflater().inflate(R.menu.menu,menu);


        return super.onCreateOptionsMenu(menu);


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
       switch (item.getItemId())
       {
           case R.id.logout:
               FirebaseAuth.getInstance().signOut();
               startActivity(new Intent(HomeActivity.this,MainActivity.class)
                       .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));

           case R.id.profile:
               startActivity(new Intent(HomeActivity.this,ProfileActivity.class));


              return true;
       }






        return false;
    }

    private void status(String status){


        Log.e("Status method called","ok");
        reference=FirebaseDatabase.getInstance().getReference("Users")
                .child(firebaseUser.getUid());
        HashMap<String ,Object> hashMap=new HashMap<>();
        hashMap.put("status",status);
        reference.updateChildren(hashMap);



    }

    private void chatlist() {
        //Log.e("ChatsFragment","I am in");

        mUsers=new ArrayList<>();
        reference=FirebaseDatabase.getInstance().getReference("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUsers.clear();
                //  Log.e("ChatsFragment","I a");

                for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                    //  Log.e("ChatsFragment","I am in");
                    User user=snapshot.getValue(User.class);
                    for(Chatlist chtlist : userlist){
                        //Log.e("ChatsFragment","I ");
                        if(user.getId().equals(chtlist.getId())){
                            // Log.e("ChatsFragment","I am ");
                            mUsers.add(user);
                        }
                    }
                } //Log.e("ChatsFragment","I ");
                userAdapter=new UserAdapter(HomeActivity.this,mUsers,true);
                recyclerView.setAdapter(userAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.e("OnResume","i am in");
          status("online");
    }

    @Override
    protected void onPause() {
        super.onPause();
         status("offline");
         Log.e("OnPause","i am in");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e("OnDestroy","i am in");
      status("offline");
    }
}
