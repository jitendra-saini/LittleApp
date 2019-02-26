package com.example.littleapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rengwuxian.materialedittext.MaterialEditText;


import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {
    MaterialEditText email,password,username;
    Button btn;
    FirebaseAuth firebaseAuth;
    DatabaseReference reference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Register");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        firebaseAuth=FirebaseAuth.getInstance();


        email=findViewById(R.id.email);
        username=findViewById(R.id.username);
        password=findViewById(R.id.password);
        btn=findViewById(R.id.btn_register);


        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String txt_username=username.getText().toString();
                String txt_email=email.getText().toString();
                String txt_password=password.getText().toString();

                if(TextUtils.isEmpty(txt_email) ||
                        TextUtils.isEmpty(txt_password) || TextUtils.isEmpty(txt_username)){
                    Toast.makeText(RegisterActivity.this,"All fileds are required",Toast.LENGTH_SHORT).show();


                }else if(txt_password.length()<6){

                    Toast.makeText(RegisterActivity.this,"minimum 6 characters required",Toast.LENGTH_SHORT).show();



                }else {


                    register(txt_email,txt_username,txt_password);
                }



            }
        });


    }


    private void register(String email, final String username, String password){

       firebaseAuth.createUserWithEmailAndPassword(email,password)
               .addOnCompleteListener( new OnCompleteListener<AuthResult>() {
                   @Override
                   public void onComplete(@NonNull Task<AuthResult> task) {
                   if(task.isSuccessful()){
                       FirebaseUser user=firebaseAuth.getCurrentUser();
                       assert user != null;
                       String userId=user.getUid();

                       reference= FirebaseDatabase.getInstance().getReference("Users").child(userId);

                       HashMap<String, String> hashMap=new HashMap<>();

                       hashMap.put("id",userId);
                       hashMap.put("username",username.toLowerCase());
                       hashMap.put("imageURl","default");
                       hashMap.put("status","offline");

                       reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                           @Override
                           public void onComplete(@NonNull Task<Void> task) {
                               if(task.isSuccessful()){

                                   Intent intent =new Intent(RegisterActivity.this,HomeActivity.class);
                                   intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                   startActivity(intent);
                                   finish();
                               }
                           }
                       });



                   }else {

                       Toast.makeText(RegisterActivity.this,"you can't register with this email or password",Toast.LENGTH_SHORT).show();

                   }
                   }
               });

    }

}
