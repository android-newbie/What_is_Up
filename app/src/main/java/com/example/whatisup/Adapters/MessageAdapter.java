package com.example.whatisup.Adapters;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.Toast;

import com.example.whatisup.Models.MessageModel;
import com.example.whatisup.R;
import com.example.whatisup.ViewHolders.ReceiverViewHolder;
import com.example.whatisup.ViewHolders.SenderViewHolder;
import com.github.pgreze.reactions.ReactionPopup;
import com.github.pgreze.reactions.ReactionsConfig;
import com.github.pgreze.reactions.ReactionsConfigBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.pranavpandey.android.dynamic.toasts.DynamicToast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MessageAdapter extends RecyclerView.Adapter implements Filterable {

    ArrayList<MessageModel> messageOriginalList;
    Context context;
  //  ArrayList<MessageModel> backupMessageList;

   private static final int SENDER_TYPE=1;
   private static final int RECEIVER_TYPE=2;
   //

   //
    String senderRoom;
    String receiverRoom;

   public MessageAdapter(){}

    public MessageAdapter(ArrayList<MessageModel> messageData, Context context,String senderRoom,String receiverRoom) {
        this.messageOriginalList =messageData;

   //   this. backupMessageList = new ArrayList<>(this.messageOriginalList);
        this.context = context;
        this.senderRoom=senderRoom;
        this.receiverRoom=receiverRoom;
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

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        MessageModel messageModel=messageOriginalList.get(position);

        //Setting Reactions on messages
        int[] reactions =new int[]{
                R.drawable.ic_fb_like,
                R.drawable.ic_fb_love,
                R.drawable.ic_fb_laugh,
                R.drawable.ic_fb_wow,
                R.drawable.ic_fb_sad,
                R.drawable.ic_fb_angry
        };
        ReactionsConfig config = new ReactionsConfigBuilder(context)
                .withReactions(reactions)
                .build();

        ReactionPopup popup = new ReactionPopup(context, config, (pos) -> {
            if (holder.getClass()==SenderViewHolder.class){

                try {
                ((SenderViewHolder)holder).ivSndReact.setImageResource(reactions[pos]);
                ((SenderViewHolder)holder).ivSndReact.setVisibility(View.VISIBLE);
                }catch (Exception e){

                }

            }else{

                try {
                ((ReceiverViewHolder)holder).ivRecReact.setImageResource(reactions[pos]);
                ((ReceiverViewHolder)holder).ivRecReact.setVisibility(View.VISIBLE);
                }catch (Exception e){

                }

            }

            messageModel.setReaction(pos);

            FirebaseDatabase.getInstance().getReference()
                    .child("chats")
                    .child(senderRoom)
                    .child(messageModel.getMessageId()).setValue(messageModel);


            FirebaseDatabase.getInstance().getReference()
                    .child("chats")
                    .child(receiverRoom)
                    .child(messageModel.getMessageId()).setValue(messageModel);


            return true; // true is closing popup, false is requesting a new selection
        });




        if (holder.getClass()==SenderViewHolder.class){ //used to get all viewHolder classes at run time and compare
            ((SenderViewHolder)holder).tvSndMsg.setText(messageModel.getMessage());
            ((SenderViewHolder)holder).tvSndTime.setText(new SimpleDateFormat("HH:mm").format(new Date(messageModel.getTimeStamp())));


            if (messageModel.getReaction()>=0){
               // messageModel.setReaction(reactions[messageModel.getReaction()]);
                ((SenderViewHolder)holder).ivSndReact.setImageResource(reactions[messageModel.getReaction()]);
                ((SenderViewHolder)holder).ivSndReact.setVisibility(View.VISIBLE);
                //((SenderViewHolder)holder) .ivSndReact.setImageResource(reactions[messageModel.getReaction()]);
            }else{
                ((SenderViewHolder)holder).ivSndReact.setVisibility(View.GONE);
            }

          try {
            ((SenderViewHolder)holder).tvSndMsg.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        popup.onTouch(view,motionEvent);
                        return false;
                    }
                });
            }catch (Exception e){
                e.printStackTrace();
            }


        }else{
            ((ReceiverViewHolder)holder).tvRecMsg.setText(messageModel.getMessage());
            ((ReceiverViewHolder)holder).tvRecTime.setText(new SimpleDateFormat("HH:mm").format(new Date(messageModel.getTimeStamp())));



            if (messageModel.getReaction()>=0){
              //  messageModel.setReaction(reactions[messageModel.getReaction()]);
                ((ReceiverViewHolder)holder).ivRecReact.setImageResource(reactions[messageModel.getReaction()]);
                ((ReceiverViewHolder)holder).ivRecReact.setVisibility(View.VISIBLE);
               // ((ReceiverViewHolder)holder).ivRecReact.setImageResource(reactions[messageModel.getReaction()]);
            }else{
                ((ReceiverViewHolder)holder).ivRecReact.setVisibility(View.GONE);
            }

            try{
            ((ReceiverViewHolder)holder).tvRecMsg.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        popup.onTouch(view,motionEvent);
                        return false;
                    }
                });
            }catch (Exception e){
                e.printStackTrace();
            }


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
