package com.example.littleapp.Fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.littleapp.Adapter.UserAdapter;
import com.example.littleapp.Model.User;
import com.example.littleapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class Users extends Fragment {

    FirebaseUser firebaseUser;
    DatabaseReference references;
    private RecyclerView recyclerView;
    private UserAdapter userAdapter;
    private List<User> mUser;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_users, container, false);

      recyclerView=view.findViewById(R.id.recycler_view);
      recyclerView.setHasFixedSize(true);
      recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));


      mUser= new ArrayList<>();

      readUsers();

        return view;
    }

   private void readUsers(){


        firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
        try {
            references = FirebaseDatabase.getInstance().getReference("Users");
        }catch (Exception e){
            e.printStackTrace();
        }
       references.addValueEventListener(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               mUser.clear();
               for(DataSnapshot snapshot : dataSnapshot.getChildren()){

                  User user=snapshot.getValue(User.class);
                  if(!user.getId().equals(firebaseUser.getUid())){


                       mUser.add(user);
                   }

               }
               userAdapter= new UserAdapter(getContext(),mUser);
               recyclerView.setAdapter(userAdapter);
           }

           @Override
           public void onCancelled(@NonNull DatabaseError databaseError) {

           }
       });




   }
}