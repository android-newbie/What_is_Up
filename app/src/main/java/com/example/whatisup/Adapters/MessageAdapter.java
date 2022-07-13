package com.example.whatisup.Adapters;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.Toast;

import com.example.whatisup.Models.MessageModel;
import com.example.whatisup.R;
import com.example.whatisup.ViewHolders.ReceiverViewHolder;
import com.example.whatisup.ViewHolders.SenderViewHolder;
import com.google.firebase.auth.FirebaseAuth;
import com.pranavpandey.android.dynamic.toasts.DynamicToast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import static java.security.AccessController.getContext;

public class MessageAdapter extends RecyclerView.Adapter implements Filterable {

    ArrayList<MessageModel> messageOriginalList;
    Context context;
  //  ArrayList<MessageModel> backupMessageList;

   private static final int SENDER_TYPE=1;
   private static final int RECEIVER_TYPE=2;

   public MessageAdapter(){}

    public MessageAdapter(ArrayList<MessageModel> messageData, Context context) {
        this.messageOriginalList =messageData;

   //   this. backupMessageList = new ArrayList<>(this.messageOriginalList);
        this.context = context;
    }

    //used to differentiate sender type and receiver type
    //FirebaseAuth.getInstance().getUid()    --used to get the user id, who is login in device
    @Override
    public int getItemViewType(int position) {
        if (messageOriginalList.get(position).getUId().equals(FirebaseAuth.getInstance().getUid())){
            return SENDER_TYPE;
        }else

            return RECEIVER_TYPE;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View view;
       if(viewType==SENDER_TYPE){//for sender layout
           view= LayoutInflater.from(parent.getContext()).inflate(R.layout.sample_sender,parent,false);
           return new SenderViewHolder(view);
       }else {//for receiver layout
           view=LayoutInflater.from(parent.getContext()).inflate(R.layout.sample_receiver,parent,false);
           return new ReceiverViewHolder(view);
       }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        MessageModel messageModel=messageOriginalList.get(position);
        if (holder.getClass()==SenderViewHolder.class){ //used to get all viewHolder classes at run time and compare
            ((SenderViewHolder)holder).tvSndMsg.setText(messageModel.getMessage());
            ((SenderViewHolder)holder).tvSndTime.setText(new SimpleDateFormat("HH:mm").format(new Date(messageModel.getTimeStamp())));
        }else{
            ((ReceiverViewHolder)holder).tvRecMsg.setText(messageModel.getMessage());
            ((ReceiverViewHolder)holder).tvRecTime.setText(new SimpleDateFormat("HH:mm").format(new Date(messageModel.getTimeStamp())));
        }


        // Long click to copy the text
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                String text;
                if (holder.getClass()==SenderViewHolder.class){ //used to get all viewHolder classes at run time and compare
                  text=  ((SenderViewHolder)holder).tvSndMsg.getText().toString();
                   // ((SenderViewHolder)holder).tvSndTime.setText(new SimpleDateFormat("HH:mm").format(new Date(messageModel.getTimeStamp())));
                }else{
                  text=  ((ReceiverViewHolder)holder).tvRecMsg.getText().toString();
                   // ((ReceiverViewHolder)holder).tvRecTime.setText(new SimpleDateFormat("HH:mm").format(new Date(messageModel.getTimeStamp())));
                }


                ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("label", text);
                clipboard.setPrimaryClip(clip);
                DynamicToast.makeSuccess(context, "Copied", Toast.LENGTH_SHORT).show();
                return false;
            }
        });

    }

    @Override
    public int getItemCount() {
        return messageOriginalList.size();
    }


    @Override
    public Filter getFilter() {
        //  Toast.makeText(context, .get(0).getName(), Toast.LENGTH_SHORT).show();
        return filter;
    }

    Filter filter=new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence keyword) {

            ArrayList<MessageModel>  filteredData=new ArrayList<>();
            //filteredData.clear();


            if (keyword==null||keyword.length()==0) {
                filteredData.addAll(messageOriginalList);

            }
            else {
                String filterPattern=keyword.toString().toLowerCase().trim();
                for (MessageModel messageModel : messageOriginalList) {
                    if ( messageModel.getMessage().toLowerCase().contains(filterPattern)) {
                        /// Log.i("up",user2.getName());
                        filteredData.add(messageModel);
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
           // backupMessageList.clear();
           // backupMessageList.addAll((ArrayList<MessageModel>)results.values);
            notifyDataSetChanged();


        }
    };
}
