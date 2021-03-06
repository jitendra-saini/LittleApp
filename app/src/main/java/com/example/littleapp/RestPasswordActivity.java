package com.example.littleapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.rengwuxian.materialedittext.MaterialEditText;

public class RestPasswordActivity extends AppCompatActivity {
    MaterialEditText send_email;
    Button send_btn;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rest_password);
        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Reset password");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



        send_email=findViewById(R.id.send_email);
        firebaseAuth=FirebaseAuth.getInstance();

        send_btn =findViewById(R.id.btn_reset);

        send_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email=send_email.getText().toString();
                if(email.equals("")){
                    Toast.makeText(RestPasswordActivity.this,"all fields are required",Toast.LENGTH_SHORT).show();



                }else {
                    firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){

                                Toast.makeText(RestPasswordActivity.this,"please check your email",Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(RestPasswordActivity.this,LogInActivity.class));
                            }else{

                                String error=task.getException().getMessage();
                                Toast.makeText(RestPasswordActivity.this,error,Toast.LENGTH_SHORT).show();

                            }
                        }
                    });
                }
            }
        });

    }
}
