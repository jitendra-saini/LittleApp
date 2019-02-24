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
import com.example.littleapp.HomeActivity;
import com.example.littleapp.MessageActivity;
import com.example.littleapp.Model.User;
import com.example.littleapp.R;

import org.w3c.dom.Text;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.MyViewHolder> {

   private Context context;
   private List<User> mUser;

    public UserAdapter(Context context, List<User> mUser) {
        this.context = context;
        this.mUser = mUser;
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

        CircleImageView circleImageView;
        TextView textView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            circleImageView=itemView.findViewById(R.id.circleImageView);
            textView=itemView.findViewById(R.id.userlisttextView);

        }
    }
}
