package com.example.whatisup.ViewHolders;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.whatisup.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class SenderViewHolder extends RecyclerView.ViewHolder {

     public TextView tvSndMsg,tvSndTime;

    public SenderViewHolder(@NonNull View itemView) {
        super(itemView);
        tvSndMsg=itemView.findViewById(R.id.tvSndMsg);
        tvSndTime=itemView.findViewById(R.id.tvSndTime);



    }
}
