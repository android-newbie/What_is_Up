package com.example.whatisup.Fragments;

import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.whatisup.Adapters.UsersAdapter;
import com.example.whatisup.ChatDetailed;
import com.example.whatisup.Models.Users;
import com.example.whatisup.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Chat extends Fragment {

    //Declaration App Variables;
    RecyclerView mRecyclerView;

    //Declaration Firebase variables;
    FirebaseDatabase database;
    FirebaseAuth mAuth;

    //
    ArrayList<Users> mData = new ArrayList<>();

    public UsersAdapter usersAdapter;

    //Empty Constructor
    public Chat() {
        // Required empty public constructor

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Inflate the layout for this fragment
        View view;
        view = inflater.inflate(R.layout.fragment_chat, container, false);

        //help to implement menu in Host Activity ActionBar(the default one)
        setHasOptionsMenu(true);

        //Initialization App variables/Recycler View
        mRecyclerView = view.findViewById(R.id.mRecyclerView);

        //Initialization Firebase variables
        database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();

        //Setting layout Manager
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2, RecyclerView.VERTICAL, false);
        gridLayoutManager.scrollToPosition(0);
        mRecyclerView.setLayoutManager(gridLayoutManager);

        //Setting UserAdapter
        usersAdapter = new UsersAdapter(mData, getContext());
        mRecyclerView.setAdapter(usersAdapter);

        //getting all users data from Firebase database
        database.getReference().child("Users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //clear ArrayList for precaution
                mData.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                    Users user = dataSnapshot.getValue(Users.class);
                    user.setId(dataSnapshot.getKey());
                    // user.setId(dataSnapshot.getKey());
                    // user.setLastMessage(ChatDetailed.lastMessage);

                    if (user.getId().equals(mAuth.getUid())) {

                    } else {
                        mData.add(user);

                    }
                }
                usersAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        return view;
    }


    //Chat item Users data
//    public ArrayList<Users> mData(){
//        ArrayList<Users> mData=new ArrayList<>();
//
//        //Creating object
//        Users user[]=new Users[100];
//        for(int i=0;i<100;i++) {
//
//
//            user[i]= new Users();
//            user[i].setName("Clay Jenson");
//            user[i].setLastMessage("Hello, How are you?");
//            mData.add( user[i]);
//
//
//        }
//        return mData;
//    }


    //
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        //used to inflate search menu in Host Activity default Actionbar
        getActivity().getMenuInflater().inflate(R.menu.search, menu);
        MenuItem item = menu.findItem(R.id.search_menu);
        SearchView searchView = (SearchView) item.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                usersAdapter.getFilter().filter(newText);

                Toast.makeText(getContext(), "heeloo", Toast.LENGTH_SHORT).show();
                return true;
            }

        });

    }




}