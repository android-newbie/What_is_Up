package com.example.whatisup;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.whatisup.Models.Users;
import com.example.whatisup.databinding.ActivityPhoneLoginDetailsBinding;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.pranavpandey.android.dynamic.toasts.DynamicToast;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class PhoneLoginDetails extends AppCompatActivity {

    ActivityPhoneLoginDetailsBinding binding;
    FirebaseAuth mAuth;
    FirebaseDatabase database;
    FirebaseStorage firebaseStorage;
    Users users;
    Uri profilePicUri;
    ProgressDialog dialog;
    ProgressDialog databaseDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityPhoneLoginDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        //
        getSupportActionBar().hide();
        //Firebase variables Initialization
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();

        //setting progress dialog box
        dialog = new ProgressDialog(PhoneLoginDetails.this);
        dialog.setTitle("Profile Setup");
        dialog.setMessage("Updating profile...");
        dialog.setCancelable(false);
        //setting progress dialog box for databaseDialog
        databaseDialog=new ProgressDialog(PhoneLoginDetails.this);
        databaseDialog.setTitle("Please wait");
        databaseDialog.setMessage("Getting details...");
        databaseDialog.setCancelable(false);


        databaseDialog.show();
        //To fetch profile pic and name from database; if user previously loged in
        database.getReference().child("Users").child(mAuth.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        users = snapshot.getValue(Users.class);
                        try {

                            Picasso.get().load(users.getProfilePic())

                                    .into(binding.ivProPicPhLoDet);

                            binding.etNamePhLoDet.setText(users.getName());


                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                      databaseDialog.dismiss();

                    }



                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }



                });






        //To save profile details
        binding.btnSavePhLoDet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                save();
            }
        });

        //To change profile photo
        binding.fabPicChaPhLoDet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isNetworkConnected()) {
                    String[] mimeTypes = {"image/png", "image/jpg", "image/jpeg"};
                    ImagePicker.with(PhoneLoginDetails.this)
                            .galleryMimeTypes(mimeTypes)
                            .compress(500)
                            .cropSquare()
                            .maxResultSize(200, 300)
                            .start(99);
                } else {
                    //Toast.makeText(Profile.this, "Check your Internet Connection", Toast.LENGTH_SHORT).show();
                    DynamicToast.makeWarning(PhoneLoginDetails.this, "Check your Internet Connection", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //To skip profile setup
        binding.btnSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(PhoneLoginDetails.this,MainActivity.class);
                startActivity(intent);
                finish();

            }
        });
    }

    public void save(){
        String name=binding.etNamePhLoDet.getText().toString().trim();
        if (name.isEmpty()) {
            binding.etNamePhLoDet.setError("Enter Your Name");
            DynamicToast.makeWarning(this, "Please Enter Your Name", Toast.LENGTH_SHORT).show();
            return;
        }

        if (profilePicUri!=null){
            dialog.show();

//            HashMap<String, Object> obj = new HashMap<>();
//            obj.put("name", name);
//
//            database.getReference().child("Users").child(mAuth.getUid())
//                    .updateChildren(obj);


            //To upload profile picture on firebase storage
            StorageReference storageReference = firebaseStorage.getReference().child("profile_pictures")
                    .child(mAuth.getUid());

            UploadTask task = storageReference.putFile(profilePicUri);

            task.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                    //To calculate the progress
                    int progress = (int) ((100.0 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount());



                    if (progress == 100) {
                        //do anything when progress is complete
                        // Toast.makeText(Profile.this, "Profile Picture Updated", Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    DynamicToast.makeSuccess(PhoneLoginDetails.this, "Profile Picture Updated", Toast.LENGTH_SHORT).show();

                    storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String imageUri=uri.toString();
                            String uid=mAuth.getUid();
                            String phoneNumber=mAuth.getCurrentUser().getPhoneNumber();
                            String name=binding.etNamePhLoDet.getText().toString();
                            Users users=new Users();
                            users.setId(uid);
                            users.setName(name);
                            users.setPhoneNumber(phoneNumber);
                            users.setProfilePic(imageUri);

                            //pass the picture link to the firebase realtime database
                            database.getReference().child("Users").child(uid).setValue(users).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    dialog.dismiss();
                                  Intent intent=new Intent(PhoneLoginDetails.this,MainActivity.class);
                                  startActivity(intent);
                                  finish();
                                }
                            });
                            //dismiss the progress dialog box

                        }
                    });

                }
            });

            startActivity(new Intent(PhoneLoginDetails.this, MainActivity.class)
                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK));

        }
        else {
            DynamicToast.makeWarning(PhoneLoginDetails.this,"Set Profile Image",Toast.LENGTH_SHORT).show();
        }
    }







    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
         profilePicUri = data.getData();

        if (requestCode == 99 & resultCode== Activity.RESULT_OK) {

            binding.ivProPicPhLoDet.setImageURI(profilePicUri); //set profile pic on imageview


            //To upload profile picture on firebase storage
//            StorageReference storageReference = firebaseStorage.getReference().child("profile_pictures")
//                    .child(mAuth.getUid());
//
//            UploadTask task = storageReference.putFile(uri);
//            ProgressDialog dialog = new ProgressDialog(PhoneLoginDetails.this);
//            dialog.setTitle("Profile Photo");
//            dialog.setMessage("Uploading");
//            task.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
//                @Override
//                public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
//                    //To calculate the progress
//                    int progress = (int) ((100.0 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount());
//                    dialog.show();
//
//
//                    if (progress == 100) {
//                        //do anything when progress is complete
//                        // Toast.makeText(Profile.this, "Profile Picture Updated", Toast.LENGTH_SHORT).show();
//                    }
//                }
//            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                @Override
//                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                    DynamicToast.makeSuccess(PhoneLoginDetails.this, "Profile Picture Updated", Toast.LENGTH_SHORT).show();
//
//                    storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//                        @Override
//                        public void onSuccess(Uri uri) {
//                            //pass the picture link to the firebase realtime database
//                            database.getReference().child("Users").child(mAuth.getUid())
//                                    .child("profilePic").setValue(uri.toString());
//                            //dismiss the progress dialog box
//                            dialog.dismiss();
//                        }
//                    });
//
//                }
//            });

        }else if (requestCode==ImagePicker.RESULT_ERROR){
            DynamicToast.makeError(this,ImagePicker.getError(data),Toast.LENGTH_SHORT).show();
        }else {
            DynamicToast.make(this,"Task cancelled",Toast.LENGTH_SHORT).show();
        }
    }

    //To check network connection
    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(getApplicationContext().CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

//    @Override
//    protected void onStart() {
//        super.onStart();
//        // Initialize firebase user
//        FirebaseUser currentUser=mAuth.getCurrentUser();
//        //
//        database.getReference().child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
//                    Users user = dataSnapshot.getValue(Users.class);
//                    try {
//                        if(user.getId().equals(mAuth.getUid())){
//                            Intent intent=new Intent(PhoneLoginDetails.this, MainActivity.class);
//                            startActivity(intent);
//                            finish();
//                        }
//                    }catch (Exception e){
//                        e.printStackTrace();
//
//                    }
//
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//        // Check condition
////        if (currentUser!=null | ){
////
////            // When user already sign in
////            // redirect to main activity
////            Intent intent=new Intent(VerifyPhoneNumber.this, MainActivity.class);
////            startActivity(intent);
////            finish();
////        }
//    }

}