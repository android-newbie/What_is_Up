package com.example.whatisup.ViewHolders;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.whatisup.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ReceiverViewHolder extends RecyclerView.ViewHolder {
   public TextView tvRecMsg,tvRecTime;
   public ImageView ivRecReact;


    public ReceiverViewHolder(@NonNull View itemView) {
        super(itemView);

        tvRecMsg=itemView.findViewById(R.id.tvRecMsg);
        tvRecTime=itemView.findViewById(R.id.tvRecTime);
        ivRecReact=itemView.findViewById(R.id.ivRecReact);



    }
}
