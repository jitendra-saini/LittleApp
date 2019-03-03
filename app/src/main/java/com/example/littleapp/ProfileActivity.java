package com.example.littleapp;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.BoringLayout;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.littleapp.Model.User;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {
    CircleImageView profile_image;
    TextView username;

    DatabaseReference reference;
    FirebaseUser firebaseUser;
    StorageReference storageReference;
    private static final int IMAGE_REQUEST=66;
    private Uri imageUri;
    private StorageTask uploadTask;
    Button btnFlashLight, btnBlinkFlashLight;
    private static final int CAMERA_REQUEST = 123;
    boolean hasCameraFlash = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);


        btnFlashLight=findViewById(R.id.btnFlashlightToggle);
        btnBlinkFlashLight=findViewById(R.id.blinkFlashlight);


        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfileActivity.this,HomeActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });



           flashlight();

        profile_image =findViewById(R.id.profile_image);
        username=findViewById(R.id.username);
        Log.e("Profile","why is default 1");

        storageReference= FirebaseStorage.getInstance().getReference("uploads");

        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();

        reference= FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //Log.e("Profile","why is default 2");

                User user=dataSnapshot.getValue(User.class);
                User ir=dataSnapshot.getValue(User.class);
                //Log.e("Profile","why is default 2"+ir.getImageURl());
                username.setText(user.getUsername());
                if(user.getImageURl().equals("default")){
                    //      Log.e("Profile","why is default 3");
                    profile_image.setImageResource(R.mipmap.ic_launcher_round);

                }else{
                    // Log.e("Profile","why is default 4");
                    Glide.with(ProfileActivity.this).load(user.getImageURl()).into(profile_image);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImage();
            }
        });

    }

    private void flashlight() {
        hasCameraFlash = getPackageManager().
                hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);

        if(ContextCompat.checkSelfPermission(ProfileActivity.this, Manifest.permission.CAMERA)!=
                PackageManager.PERMISSION_GRANTED){

            ActivityCompat.requestPermissions(ProfileActivity.this,new String[] {Manifest.permission.CAMERA},CAMERA_REQUEST);


        }


        btnFlashLight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                  if(hasCameraFlash){
                      if(btnFlashLight.getText().toString().contains("ON")){
                          btnFlashLight.setText("OFF");
                          flashlightON();

                          btnBlinkFlashLight.setEnabled(false);
                      }else{
                          btnFlashLight.setText("ON");
                          btnBlinkFlashLight.setEnabled(true);
                          flashlightOFF();
                      }
                  }
                  else {
                      Toast.makeText(ProfileActivity.this,"NO FLASH AVAILABLE ON YOUR DEVICE",Toast.LENGTH_SHORT);

                  }
            }
        });

        btnBlinkFlashLight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(btnFlashLight.getText().toString().contains("ON")){
                    blinkFlash();
                }else {
                    Toast.makeText(ProfileActivity.this,"press above button first",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void blinkFlash() {

        CameraManager cameraManager= (CameraManager) getSystemService(Context.CAMERA_SERVICE);

        String myString ="0101010101";
       long blinkdelay=50;//delay in ms
        for(int i=0;i<myString.length();i++){

            if(myString.charAt(i) == '0'){
                try{
                    String cameraID=cameraManager.getCameraIdList()[0];
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        cameraManager.setTorchMode(cameraID,true);
                    }
                }catch (CameraAccessException e){

                }


            }else {
                try{
                    String cameraID=cameraManager.getCameraIdList()[0];
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        cameraManager.setTorchMode(cameraID,false);
                    }
                }catch (CameraAccessException e){
                    e.printStackTrace();
                }

            }
            try{
                Thread.sleep(blinkdelay);
            }catch (InterruptedException e){
                e.printStackTrace();

            }
        }

    }
    private void flashlightON() {

        CameraManager cameraManager= (CameraManager) getSystemService(Context.CAMERA_SERVICE);


        try{
            String cameraID=cameraManager.getCameraIdList()[0];
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                cameraManager.setTorchMode(cameraID,true);
            }
        }catch (CameraAccessException e){

        }
    }

    private void flashlightOFF() {
        CameraManager cameraManager= (CameraManager) getSystemService(Context.CAMERA_SERVICE);

        try{
            String cameraID=cameraManager.getCameraIdList()[0];
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                cameraManager.setTorchMode(cameraID,false);
            }
        }catch (CameraAccessException e){

        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            switch (requestCode){

                case CAMERA_REQUEST:
                    if (grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                        hasCameraFlash=getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);

                    }else {
                        btnFlashLight.setEnabled(false);
                        btnBlinkFlashLight.setEnabled(false);
                        Toast.makeText(ProfileActivity.this,"permission denied",Toast.LENGTH_LONG).show();
                    }
                    break;

            }
    }

    private void openImage() {

        Intent intent=new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,IMAGE_REQUEST);
    }

    private String getFileExtension(Uri uri){

        ContentResolver contentResolver=ProfileActivity.this.getContentResolver();
        MimeTypeMap mimeTypeMap=MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }
    private void uploadImage(){


        final ProgressDialog progressDialog=new ProgressDialog(ProfileActivity.this);
        progressDialog.setMessage("uploading");
        progressDialog.show();

        if(imageUri!=null){

            final StorageReference fileReference= storageReference.child(System.currentTimeMillis()+
                    "."+getFileExtension(imageUri));

            uploadTask=fileReference.putFile(imageUri);
            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot,Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if(!task.isSuccessful()){

                        throw task.getException();
                    }


                    return fileReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if(task.isSuccessful()){

                        Uri downloadUri=task.getResult();
                        String mUri=downloadUri.toString();


                        reference=FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
                        HashMap<String, Object> map=new HashMap<>();
                        map.put("imageURl",mUri);
                        reference.updateChildren(map);
                        progressDialog.dismiss();
                    }
                    else {
                        Toast.makeText(ProfileActivity.this,"faild",Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(ProfileActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            });

        }else {
            Toast.makeText(ProfileActivity.this, "no Image Selected", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==IMAGE_REQUEST && resultCode==RESULT_OK && data!=null && data.getData()!=null){

            imageUri=data.getData();

            if(uploadTask!=null && uploadTask.isInProgress()){


                Toast.makeText(ProfileActivity.this, "upload in progress", Toast.LENGTH_SHORT).show();
            }
            else {
                uploadImage();
            }
        }else{
         /*   Log.e("Profile","something wrong");
            Log.e("Profile","something wrong"+requestCode);
            Log.e("Profile","something wrong"+resultCode);
            Log.e("Profile","something wrong"+data.getData());
       */ }
    }

}
