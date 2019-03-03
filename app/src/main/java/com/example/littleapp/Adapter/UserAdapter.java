package com.example.littleapp.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.BoringLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.littleapp.HomeActivity;
import com.example.littleapp.MessageActivity;
import com.example.littleapp.Model.Chat;
import com.example.littleapp.Model.User;
import com.example.littleapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.MyViewHolder> {

   private Context context;
   private List<User> mUser;
   private Boolean isChat;
   private String theLastMessage;
    FirebaseUser firebaseUser;
    DatabaseReference reference;
    public UserAdapter(Context context, List<User> mUser,Boolean isChat) {
        this.context = context;
        this.mUser = mUser;
        this.isChat=isChat;
    }

    @NonNull
    @Override
    public UserAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
              View view= LayoutInflater.from(context).inflate(R.layout.all_users,viewGroup,false);
              return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserAdapter.MyViewHolder myViewHolder, int i) {
             final User user=mUser.get(i);

             myViewHolder.textView.setText(user.getUsername());
        Log.e("profile iamge",user.getImageURl());
        if(user.getImageURl().equals("default")){

            myViewHolder.circleImageView.setImageResource(R.mipmap.ic_launcher_round);

        }else {
            Glide.with(context).load(user.getImageURl()).into(myViewHolder.circleImageView);

        }
        if(isChat){
            lastMessage(user.getId(),myViewHolder.last_msg);
        }else myViewHolder.last_msg.setVisibility(View.GONE);


        if(isChat){
            if(user.getStatus().equals("online")){
                myViewHolder.status_on.setVisibility(View.VISIBLE);
                myViewHolder.status_off.setVisibility(View.GONE);
            }else{
                myViewHolder.status_on.setVisibility(View.GONE);
                myViewHolder.status_off.setVisibility(View.VISIBLE);
            }
        }else {
            myViewHolder.status_on.setVisibility(View.GONE);
            myViewHolder.status_off.setVisibility(View.GONE);
        }
        myViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, MessageActivity.class);
                intent.putExtra("user_id",user.getId());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mUser.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        CircleImageView circleImageView,status_on,status_off;
        TextView textView,last_msg;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            circleImageView=itemView.findViewById(R.id.circleImageView);
            textView=itemView.findViewById(R.id.userlisttextView);
            status_on=itemView.findViewById(R.id.status_on);
            status_off=itemView.findViewById(R.id.status_off);
            last_msg=itemView.findViewById(R.id.last_message);
        }
    }
    private void lastMessage(final String userid, final TextView last_msg){


        theLastMessage="default";

        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
       reference= FirebaseDatabase.getInstance().getReference("chats");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot: dataSnapshot.getChildren()){

                    Chat chat =snapshot.getValue(Chat.class);

                    if(chat.getReceiver().equals(firebaseUser.getUid()) && chat.getSender().equals(userid) ||
                            chat.getReceiver().equals(userid) && chat.getSender().equals(firebaseUser.getUid())){

                           theLastMessage=chat.getMessage();

                    }
                }
                switch (theLastMessage){

                    case "default":
                    {
                        last_msg.setText("No Message");
                        break;
                    }
                    default:
                    {
                        last_msg.setText(theLastMessage);
                            break;
                    }
                         }theLastMessage="default";

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
