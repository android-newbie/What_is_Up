package com.example.whatisup.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.whatisup.MainActivity;
import com.example.whatisup.R;

public class Status extends Fragment {


    public Status() {
        // Required empty public constructor
    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_status, container, false);
    }

//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        Intent intent=new Intent(getContext(),Chat.class);
//        startActivity(intent);
//    }
}