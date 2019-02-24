package com.example.littleapp.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.littleapp.MessageActivity;
import com.example.littleapp.Model.Chat;
import com.example.littleapp.Model.User;
import com.example.littleapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MyViewHolder> {
    public static final int MSG_TYPE_LEFT=0;
    public static final int MSG_TYPE_RIGHT=1;
    private Context context;
    private List<Chat> mChat;
    private String imageurl;
    FirebaseUser firebaseUser;
    public MessageAdapter(Context context, List<Chat> mChat,String imageurl) {
        this.context = context;
        this.mChat = mChat;
        this.imageurl=imageurl;
    }

    @NonNull
    @Override
    public MessageAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {

      if(viewType==MSG_TYPE_LEFT){
          View view= LayoutInflater.from(context).inflate(R.layout.chat_item_left,viewGroup,false);
          return new MessageAdapter.MyViewHolder(view);

      }else {
          View view= LayoutInflater.from(context).inflate(R.layout.chat_item_right,viewGroup,false);
          return new MessageAdapter.MyViewHolder(view);

      }

         }

    @Override
    public void onBindViewHolder(@NonNull MessageAdapter.MyViewHolder myViewHolder, int i) {
           Chat chat=mChat.get(i);

           myViewHolder.show_message.setText(chat.getMessage());

           if(imageurl.equals("default")){

               myViewHolder.circleImageView.setImageResource(R.mipmap.ic_launcher_round);
           }else {

               Glide.with(context).load(imageurl).into(myViewHolder.circleImageView);
           }
    }

    @Override
    public int getItemCount() {
        return mChat.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        CircleImageView circleImageView;
        TextView show_message;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            circleImageView=itemView.findViewById(R.id.profile_image);
            show_message=itemView.findViewById(R.id.show_message);

        }
    }


    @Override
    public int getItemViewType(int position) {
      firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
      if(mChat.get(position).getSender().equals(firebaseUser.getUid())){


          return MSG_TYPE_RIGHT;
      }else {
          return MSG_TYPE_LEFT;
      }

    }
}

