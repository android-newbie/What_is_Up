package com.example.whatisup.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import com.example.whatisup.ChatDetailed;
import com.example.whatisup.Models.Users;
import com.example.whatisup.R;
import com.example.whatisup.ViewHolders.ChatItemViewHolder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

//import com.example.whatisup.Filter.FilterCustomSearch;

public class UsersAdapter extends RecyclerView.Adapter<ChatItemViewHolder> implements Filterable {
   ArrayList<Users>  originalList;
   Context context;
    ArrayList<Users> backupList;
  // ArrayList<Users> mData;

   public UsersAdapter(){
     // backup=new ArrayList<>(mData);
   }

   public UsersAdapter(ArrayList<Users> mData, Context context) {
      this.originalList =mData ;
     // this.mData=mData;
      this.backupList = new ArrayList<>(this.originalList);
      this.context = context;




   }

   @NonNull
   @Override
   public ChatItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
      LayoutInflater inflater=LayoutInflater.from(parent.getContext());
      View view=inflater.inflate(R.layout.chat_item,parent,false);


      return new ChatItemViewHolder(view);


   }

   @Override
   public void onBindViewHolder(@NonNull ChatItemViewHolder holder, int position) {

      //extracting one user data from ArrayList mData;
      Users user=backupList.get(position);


      Picasso.get().load(user.getProfilePic()).placeholder(R.drawable.ic_defaultprofilepic).into(holder.ivProfilePic);
      holder.tvName.setText(user.getName());
//      holder.tvLastMessage.setText(user.getLastMessage());
//      try {
//          holder.tvLastMessageTime.setText(new SimpleDateFormat("HH:mm").format(new Date(user.getTimestamp())));
////      Toast.makeText(context, user.getLastMessage(), Toast.LENGTH_SHORT).show();
//      }catch (Exception e){
//          e.printStackTrace();
//      }

//      //used to set lastMessage in the chatActivity
      FirebaseDatabase.getInstance().getReference().child("chats")
              .child(FirebaseAuth.getInstance().getUid()+user.getId())
              .orderByChild("timeStamp")
              .limitToLast(1)
              .addValueEventListener(new ValueEventListener() {
                 @Override
                 public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.hasChildren()){
                      
                       for (DataSnapshot snapshot1 :snapshot.getChildren()) {



                           holder.tvLastMessage.setText(snapshot1.child("message").getValue(String.class));
                          // user .setTimestamp(snapshot1.child("timeStamp").getValue(Long.class));
                           //  FirebaseDatabase.getInstance().getReference().child("Users").child((FirebaseAuth.getInstance().getUid()+user.getId()).setValue(user);

                         //  holder.tvLastMessage.setTextColor(Color.BLUE);

                         try{
                           holder.tvLastMessageTime.setText(new SimpleDateFormat("HH:mm").format(new Date(snapshot1.child("timeStamp").getValue(Long.class))));
                       }catch (Exception e){
                             e.printStackTrace();
                         }


                       }


                        // FirebaseDatabase.getInstance().getReference().child("Users").child(user.getId()).setValue(user);

                    }
                  //  try {
                     // To sort the mData ArrayList Alphabetically


//                    }catch (Exception e){
//                        e.printStackTrace();
//                    }




                 }

                 @Override
                 public void onCancelled(@NonNull DatabaseError error) {

                 }


              });



      //Click listener for the entire row
      holder.itemView.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            // holder.tvLastMessage.setTextColor(Color.parseColor("#B3B9B5"));
          //  Toast.makeText(context, user.getName(), Toast.LENGTH_SHORT).show();
            Intent intent=new Intent(context, ChatDetailed.class);
            intent.putExtra("name",user.getName());
            intent.putExtra("profilePic",user.getProfilePic());
            intent.putExtra("id",user.getId());
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);

         }
      });


   }

   @Override
   public int getItemCount() {
      return backupList.size();
   }


   @Override
   public Filter getFilter() {
    //  Toast.makeText(context, .get(0).getName(), Toast.LENGTH_SHORT).show();
      return filter;
   }

     Filter filter=new Filter() {
      @Override
      protected FilterResults performFiltering(CharSequence keyword) {

         ArrayList<Users>  filteredData=new ArrayList<>();
            //filteredData.clear();


         if (keyword==null||keyword.length()==0) {
            filteredData.addAll(originalList);

         }
         else {
            String filterPattern=keyword.toString().toLowerCase().trim();
            for (Users user : originalList) {
             if ( user.getName().toLowerCase().contains(filterPattern)) {
               /// Log.i("up",user2.getName());
                filteredData.add(user);
             }
            }
         }

         //Log.i("up",filteredData.get(0).getName());
         FilterResults filterResults=new FilterResults();
         filterResults.count=filteredData.size()   ;
         filterResults.values=filteredData;
         return filterResults ;
      }

      @Override
      protected void publishResults(CharSequence constraint, FilterResults results) {
         backupList.clear();
         backupList.addAll((ArrayList<Users>)results.values);
         notifyDataSetChanged();


      }
   };
}
