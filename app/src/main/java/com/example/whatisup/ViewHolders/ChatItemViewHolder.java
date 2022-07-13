package com.example.whatisup.ViewHolders;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.whatisup.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ChatItemViewHolder extends RecyclerView.ViewHolder  {

    public ImageView ivProfilePic;
    public TextView tvName;
    public TextView tvLastMessage;
    public TextView tvLastMessageTime;
   public ImageView ivStatus;

  //  public Context context;


    public ChatItemViewHolder(@NonNull View itemView) {
        super(itemView);

        ivProfilePic=itemView.findViewById(R.id.ivProfilePic);
        tvName=itemView.findViewById(R.id.tvName);
        tvLastMessage=itemView.findViewById(R.id.tvLastMessage);
        tvLastMessageTime=itemView.findViewById(R.id.tvLastMessageTime);
         ivStatus=itemView.findViewById(R.id.ivStatusUser);




    }


}
