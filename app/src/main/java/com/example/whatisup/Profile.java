package com.example.whatisup;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.whatisup.Models.Users;
import com.example.whatisup.databinding.ActivityProfileBinding;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
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

import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class Profile extends AppCompatActivity {
    //Firebase variables Declaration
    FirebaseAuth mAuth;
    FirebaseDatabase database;
    FirebaseStorage firebaseStorage;


    //
    ActivityProfileBinding binding;


    //
    Users users;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        //
        getSettings();

        //To hide ActionBar
        getSupportActionBar().hide();

        //Firebase variables Initialization
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();



        //get profile photo from firebase database and set to profile image view
        database.getReference().child("Users").child(mAuth.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                         users = snapshot.getValue(Users.class);
                        try {

                            Picasso.get().load(users.getProfilePic())
                                    .placeholder(R.drawable.ic_defaultprofilepic)
                                    .into(binding.ivProfilePhotoProfAct);

                            binding.tvEmailProfAct.setText(users.getEmail());
                            binding.tvProfActName.setText(users.getName());
                            binding.tvProActAbout.setText(users.getAbout());
                            binding.mainProfActName.setText(binding.tvProfActName.getText().toString());
                            binding.mainEmailProfAct.setText(binding.tvEmailProfAct.getText().toString());

                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                    }


                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }


                });

        // To open name bottomSheetDialog
        binding.editProfActName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Toast.makeText(Profile.this, "Clicked", Toast.LENGTH_SHORT).show();
                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(Profile.this, R.style.AppBottomSheetDialogTheme);
                //attach the layout file
                bottomSheetDialog.setContentView(R.layout.name_bottomsheet);
                bottomSheetDialog.findViewById(R.id.etPersonName).requestFocus();


                //add onClickListener to bottomSheetDialog elements
                bottomSheetDialog.findViewById(R.id.tvCancelNameBtmSht).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        bottomSheetDialog.dismiss();
                    }
                });

                bottomSheetDialog.findViewById(R.id.tvSaveNameBtmSht).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(Profile.this, "SAVE", Toast.LENGTH_SHORT).show();
                        EditText editText = bottomSheetDialog.findViewById(R.id.etPersonName);
                        String name = editText.getText().toString();
                        binding.tvProfActName.setText(name);
                        binding.mainProfActName.setText(binding.tvProfActName.getText().toString());

                        bottomSheetDialog.dismiss();
                        //To update data in Firebase database using key value pair
                        HashMap<String, Object> obj = new HashMap<>();
                        obj.put("name", name);

                        database.getReference().child("Users").child(mAuth.getUid())
                                .updateChildren(obj);


                    }
                });


                bottomSheetDialog.show();

            }
        });

        //To open about bottomSheetDialog
        binding.etProfActAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(Profile.this, "Clicked", Toast.LENGTH_SHORT).show();
                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(Profile.this, R.style.AppBottomSheetDialogTheme);
                //attach the layout file
                bottomSheetDialog.setContentView(R.layout.about_bottomsheet);
                bottomSheetDialog.show();

                //add onClickListener to bottomSheetDialog elements
                //Cancel button
                bottomSheetDialog.findViewById(R.id.tvCancelAboutBtmSht).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        bottomSheetDialog.dismiss();
                    }
                });

                //Save button
                bottomSheetDialog.findViewById(R.id.tvSaveAboutBtmSht).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(Profile.this, "SAVE", Toast.LENGTH_SHORT).show();
                        EditText editText = bottomSheetDialog.findViewById(R.id.etAddAboutProfAct);
                        String about = editText.getText().toString();
                        binding.tvProActAbout.setText(about);

                        bottomSheetDialog.dismiss();
                        //To update data in Firebase database using key value pair
                        HashMap<String, Object> obj = new HashMap<>();
                        obj.put("about", about);

                        database.getReference().child("Users").child(mAuth.getUid())
                                .updateChildren(obj);
                    }
                });
            }
        });

        //BackArrow Listener
        binding.ivBackArrowProfAct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //To change profile photo
        binding.changeProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isNetworkConnected()) {
                    String[] mimeTypes={"image/png","image/jpg","image/jpeg"};
                    ImagePicker.with(Profile.this)
                            .galleryMimeTypes(mimeTypes)
                            .compress(500)
                            .cropSquare()
                            .maxResultSize(200, 300)
                            .start(99);
                }else {
                    //Toast.makeText(Profile.this, "Check your Internet Connection", Toast.LENGTH_SHORT).show();
                    DynamicToast.makeWarning(Profile.this,"Check your Internet Connection",Toast.LENGTH_SHORT).show();
                }
            }
        });

        //


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Uri uri = data.getData();

        if (requestCode == 99 & resultCode== Activity.RESULT_OK) {

            binding.ivProfilePhotoProfAct.setImageURI(uri); //set profile pic on imageview


            //To upload profile picture on firebase storage
            StorageReference storageReference = firebaseStorage.getReference().child("profile_pictures")
                    .child(mAuth.getUid());

            UploadTask task = storageReference.putFile(uri);
            ProgressDialog dialog = new ProgressDialog(Profile.this);
            dialog.setTitle("Profile Photo");
            dialog.setMessage("Uploading");
            task.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                    //To calculate the progress
                    int progress = (int) ((100.0 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount());
                    dialog.show();


                    if (progress == 100) {
                        //do anything when progress is complete
                        // Toast.makeText(Profile.this, "Profile Picture Updated", Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    DynamicToast.makeSuccess(Profile.this, "Profile Picture Updated", Toast.LENGTH_SHORT).show();

                    storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            //pass the picture link to the firebase realtime database
                            database.getReference().child("Users").child(mAuth.getUid())
                                    .child("profilePic").setValue(uri.toString());
                            //dismiss the progress dialog box
                            dialog.dismiss();
                        }
                    });

                }
            });

        }else if (requestCode==ImagePicker.RESULT_ERROR){
            DynamicToast.makeError(this,ImagePicker.getError(data),Toast.LENGTH_SHORT).show();
        }else {
            DynamicToast.make(this,"Task cancelled",Toast.LENGTH_SHORT).show();
        }
    }

    //
    public void saveSettings(){

        SharedPreferences sharedPreferences=getSharedPreferences("profilePreferences",MODE_PRIVATE);
        SharedPreferences.Editor myEditor=sharedPreferences.edit();
        try {
            myEditor.putString("keyProfilePicture",users.getProfilePic());
            myEditor.apply();
        }catch (Exception e){
            e.printStackTrace();
        };
    }

    public void getSettings(){

        SharedPreferences sharedPreferences=getSharedPreferences("profilePreferences",MODE_PRIVATE);

        try {
           // Toast.makeText(this, "enter", Toast.LENGTH_SHORT).show();
            Picasso.get().load(sharedPreferences.getString("keyProfilePicture",null))
                    .placeholder(R.drawable.ic_defaultprofilepic)
                    .into(binding.ivProfilePhotoProfAct);

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    protected void onPause() {
        saveSettings();
        super.onPause();
    }

    //To check network connection
    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(getApplicationContext().CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }


}