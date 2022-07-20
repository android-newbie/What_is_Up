package com.example.whatisup;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

import com.example.whatisup.Adapters.MessageAdapter;
import com.example.whatisup.Models.MessageModel;
import com.example.whatisup.Models.Users;
import com.example.whatisup.databinding.ActivityChatDetailedBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class ChatDetailed extends AppCompatActivity {

    //
  public static String lastMessage;

    //Declaration Firebase Variables
    FirebaseAuth mAuth;
    FirebaseDatabase database;
    //view binding
   ActivityChatDetailedBinding binding;

    //
     MessageAdapter adapter;

     //
    private static final String PERMS =android.Manifest.permission.READ_EXTERNAL_STORAGE;
    private static final int RC_IMAGE_PERMS = 100;
    private static final int RC_CHOOSE_PHOTO = 200;

    //
    private Uri uriImageSelected;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityChatDetailedBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        //
        setSupportActionBar(binding.mToolbar);
        //

        //Initialization firebase variables
        mAuth=FirebaseAuth.getInstance();
        database=FirebaseDatabase.getInstance();
        //
       final String senderId=mAuth.getUid();

        //getting data from Chat Fragment of each user and Set to its respective field
        Intent intent=getIntent();
        //
       final String receiverId=intent.getStringExtra("id");
        String name=intent.getStringExtra("name");
        binding.tvName.setText(name);
        String profilePic=intent.getStringExtra("profilePic");
        Picasso.get().load(profilePic).placeholder(R.drawable.ic_defaultprofilepic).into(binding.ivProfilePic);
       database.getReference().child("Users").child(receiverId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Users users=snapshot.getValue(Users.class);
                        binding.tvLastSeen.setText(users.getAbout());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        //
        final ArrayList<MessageModel> messageModels=new ArrayList<>();

        final String senderRoom=senderId+receiverId;
        final String receiverRoom=receiverId+senderId;

        //setting adapter to the recycler view
        adapter=new MessageAdapter(messageModels,this,senderRoom,receiverRoom);
        binding.messagesRecyclerview.setAdapter(adapter);

        //setting layout manager for recycler view
        final LinearLayoutManager linearLayout=new LinearLayoutManager(this);
        //linearLayout.setReverseLayout(false);
        linearLayout.setStackFromEnd(true);     //used to set default scroll to end without changing stack direction
        binding.messagesRecyclerview.setLayoutManager(linearLayout);




        //getting messages from firebase database which we were upload earlier and update into recyclerview
        database.getReference()
                .child("chats")
                .child(senderRoom)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        messageModels.clear();      //avoid repeat of older messages which are already present in messageModels arrayLis
                        for (DataSnapshot dataSnapshot :snapshot.getChildren()) {
                            MessageModel model=dataSnapshot.getValue(MessageModel.class);
                            model.setMessageId(dataSnapshot.getKey());
                            messageModels.add(model);

                        }
                        adapter.notifyDataSetChanged(); //used to update recyclerView at runtime
                        binding.messagesRecyclerview.scrollToPosition(messageModels.size()-1);       //used to scroll last position of recyclerView


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });



        //sendButton listner responsible for sending messages and update to firebase database
        binding.sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Toast.makeText(ChatDetailed.this, "hello00", Toast.LENGTH_SHORT).show();
                String message = binding.typeMessage.getText().toString().trim();
                if (message.isEmpty()) {
                    Toast.makeText(ChatDetailed.this, "Type Something", Toast.LENGTH_SHORT).show();
                }else{
                MessageModel model = new MessageModel(senderId, message, new Date().getTime());
                // model.setTimeStamp(new Date().getTime());
                binding.typeMessage.setText("");

                //
                 String randomKey=database.getReference().push().getKey();

                //store message/model data to firebase database at sender side
                database.getReference().child("chats")
                        .child(senderRoom)
                        .child(randomKey)
                        .setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        binding.messagesRecyclerview.scrollToPosition(messageModels.size() - 1);    //used to scroll last position of recyclerView
                        //store message/model data to firebase database at receiver side

                        database.getReference().child("chats")
                                .child(receiverRoom)
                                .child(randomKey)
                                .setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {

                            }
                        });

                    }
                });
            }
        }
        });

        //onClickListener for ivBackArrow
        binding.ivBackArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                Intent intent1=new Intent(ChatDetailed.this, Chat.class);
//                intent1.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
//                startActivity(intent1);
//                UsersAdapter adapter1=new UsersAdapter();
//                adapter1.notifyAll();
                finish();

            }
        });
//
//        //      //used to set lastMessage in the chatActivity
//        FirebaseDatabase.getInstance().getReference().child("chats")
//                .child(senderRoom)
//                .orderByChild("timestamp")
//                .limitToLast(1)
//                .addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        if (snapshot.hasChildren()){
//                            for (DataSnapshot snapshot1 :snapshot.getChildren()) {
//                              lastMessage=snapshot1.child("message").getValue(String.class);
//
////                          holder.tvLastMessage.setText(snapshot1.child("message").getValue(String.class));
////                          holder.tvLastMessageTime.setText(snapshot1.child("timestamp").getValue(String.class));
//
//
//                            }
//                            //
//
//                        }
//
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//
//                    }
//
//
//                });


        binding.imageView12.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(ChatDetailed.this, "hillow hillow", Toast.LENGTH_SHORT).show();
                addFile();
            }
        });




    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
//        UsersAdapter adapter=new UsersAdapter();
//        adapter.notifyDataSetChanged();
    }


//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.detailedactivitymenu,menu);
//        //
//        MenuItem item = menu.findItem(R.id.search_menu_detail);
//        SearchView searchView = (SearchView) item.getActionView();
//        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);
//        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String newText) {
//                adapter.getFilter().filter(newText);
//                return false;
//            }
//        });
//        return true;
//    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        EasyPermissions.onRequestPermissionsResult(requestCode,permissions,grantResults,this);
    }

    @AfterPermissionGranted(RC_IMAGE_PERMS)
        public void addFile(){
            //DynamicToast.makeError(this,"you have permission to read storage!",Toast.LENGTH_SHORT).show();
            if (!EasyPermissions.hasPermissions(this,PERMS)){
               EasyPermissions.requestPermissions(this,getString(R.string.popup_title),RC_IMAGE_PERMS,PERMS);
                return;

            }

                Intent i=new Intent();
                i.setAction(Intent.ACTION_PICK);
                i.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i,RC_CHOOSE_PHOTO);

        }

        // Handle activity response(after user has chosen or not a picture)
        private void handleResponse(int requestCode, int resultCode, Intent data){
            if (requestCode==RC_CHOOSE_PHOTO){
                if (resultCode==RESULT_OK){ //SUCCESS
                    this.uriImageSelected=data.getData();
                    Picasso.get()
                            .load(this.uriImageSelected)
                            .into(binding.ivProfilePic);

                }else {
                    Toast.makeText(this, "nno image xhosedn", Toast.LENGTH_SHORT).show();
                }
            }
        }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        this.handleResponse(requestCode,resultCode,data);
    }
}