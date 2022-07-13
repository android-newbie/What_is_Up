package com.example.whatisup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;
import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.widget.Toast;

import com.example.whatisup.Adapters.UsersAdapter;
import com.example.whatisup.Adapters.ViewPager2Adapter;
import com.example.whatisup.Fragments.Call;
import com.example.whatisup.Fragments.Chat;
import com.example.whatisup.Fragments.Status;
import com.example.whatisup.Models.Users;
import com.faltenreich.skeletonlayout.Skeleton;
import com.faltenreich.skeletonlayout.SkeletonLayoutUtils;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pranavpandey.android.dynamic.toasts.DynamicToast;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    //Firebase variables
    private FirebaseAuth mAuth;
    GoogleSignInClient mGoogleSignInClient;

    //App Variables
    TabLayout tabLayout;
    ViewPager2 viewPager2;
    Skeleton skeleton;
    //IMPORT FROM CHAT FRAGMENTS
    //Declaration App Variables;
    RecyclerView mRecyclerView;

    //Declaration Firebase variables;
    FirebaseDatabase database;
     //  FirebaseAuth mAuth;

    //
    ArrayList<Users> mData = new ArrayList<>();

    public UsersAdapter usersAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //


        //To set ActionBar color to gradient
       // getSupportActionBar().setBackgroundDrawable(AppCompatResources.getDrawable(this,R.drawable.toolbar_bg));
        //Firebase variables initialization
        mAuth = FirebaseAuth.getInstance();

//        //App variables initialization
//        tabLayout=findViewById(R.id.tabLayout);
//        viewPager2=findViewById(R.id.viewPager2);
//
//        setViewPagerAdapter();
//        new TabLayoutMediator(tabLayout, viewPager2, this).attach();

//


//IMPORT FROM CHAT FRAGMENT
        //Initialization App variables/Recycler View
        mRecyclerView = findViewById(R.id.mRecyclerView);

        //Initialization Firebase variables
        database = FirebaseDatabase.getInstance();
        //mAuth = FirebaseAuth.getInstance();





        //Setting layout Manager
        GridLayoutManager gridLayoutManager = new GridLayoutManager(MainActivity.this, 2, RecyclerView.VERTICAL, false);
        gridLayoutManager.scrollToPosition(0);
        mRecyclerView.setLayoutManager(gridLayoutManager);
        //ios like overscroll bounce
        OverScrollDecoratorHelper.setUpOverScroll(mRecyclerView, OverScrollDecoratorHelper.ORIENTATION_VERTICAL);
        //Setting UserAdapter (set adapter here ,if you don't want to implement search view for RecyclerView)
//        usersAdapter = new UsersAdapter(mData, MainActivity.this);
//        mRecyclerView.setAdapter(usersAdapter);

        //applying skeleton view to the recyclerview item
        skeleton= SkeletonLayoutUtils.applySkeleton(mRecyclerView,R.layout.chat_item,8);
        skeleton.showSkeleton();



       // if ((isNetworkConnected())){

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
                    //end skeleton to show original layout
                    skeleton.showOriginal();
                    //setting adapter (set adapter here,if you want to implement search view for RecyclerView)
                    //more specific,
                    // above adapter.notifyDataSetChanged()
                    // below after adding the data to ArrayList (eg. mData.add(user))
                    //error, if you don't implement like this:you will get blank recyclerview everytime when activity/fragment start
                    usersAdapter = new UsersAdapter(mData, MainActivity.this);

                    mRecyclerView.setAdapter(usersAdapter);
                    usersAdapter.notifyDataSetChanged();



                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

//        }else {
//           getSettings();
//            usersAdapter = new UsersAdapter(mData, MainActivity.this);
//            mRecyclerView.setAdapter(usersAdapter);
//            usersAdapter.notifyDataSetChanged();
//        }
//


        if (isNetworkConnected()) {
          // DynamicToast.makeSuccess(MainActivity.this, "Ready to chat" , Toast.LENGTH_SHORT).show();
        }else {
            DynamicToast.makeWarning(MainActivity.this, "Check your Internet Connection", Toast.LENGTH_SHORT).show();
        }
 










        GoogleSignInOptions gso=new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient= GoogleSignIn.getClient(this,gso);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.mainactivitymenu,menu);
        //IMPORT FROM CHAT FRAGMENT
        MenuItem item = menu.findItem(R.id.search_menu);
        SearchView searchView = (SearchView) item.getActionView();
        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                usersAdapter.getFilter().filter(newText);
              //  usersAdapter.notifyDataSetChanged();
               // Toast.makeText(MainActivity.this, "heeloo", Toast.LENGTH_SHORT).show();
                return false;
            }

        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.logOut:
                if (isNetworkConnected()) {
                    mAuth.signOut();
                    mGoogleSignInClient.signOut();
                    DynamicToast.make(this, "Sign Out", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(MainActivity.this, SignInActivity.class);
                    startActivity(intent);
                    finish();
                }else {
                    DynamicToast.makeWarning(this, "Check your Internet connection!", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.settings:
               // Toast.makeText(this, "Settings", Toast.LENGTH_SHORT).show();
                Intent intent1=new Intent(MainActivity.this,Profile.class);
                startActivity(intent1);
                break;
            case R.id.sort:
                // To sort the mData ArrayList Alphabetically
                            Collections.sort(mData, new Comparator<Users>() {
                                @Override
                                public int compare(Users o1, Users o2) {
                                        return o1.getName().compareTo(o2.getName());
                                }
                            });
                usersAdapter = new UsersAdapter(mData, MainActivity.this);
                mRecyclerView.setAdapter(usersAdapter);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
//        Collections.sort(mData, new Comparator<Users>() {
//            @Override
//            public int compare(Users o1, Users o2) {
//                return o2.getTimestamp().compareTo(o1.getTimestamp());
//
//            }
//
//        });
      //  getSettings();

//            usersAdapter = new UsersAdapter(mData, MainActivity.this);
//            mRecyclerView.setAdapter(usersAdapter);
//            usersAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onPause() {

      //  saveSettings();
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
      //  saveSettings();
       finish();

    }



//    public   void setViewPagerAdapter() {
//             ViewPager2Adapter viewPager2Adapter;
//             viewPager2Adapter = new ViewPager2Adapter(this);
//             ArrayList<Fragment> fragmentList = new ArrayList<>();//creates an ArrayList of Fragments
//             fragmentList.add(new Chat());
////             fragmentList.add(new Status());
////             fragmentList.add(new Call());
//             viewPager2Adapter.setData(fragmentList);
//             viewPager2.setAdapter(viewPager2Adapter);
//
//         }


//    @Override
//    public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
//        //  tab.setIcon(icons.get(position));
//        switch (position) {
//
//            case 0:tab.setText("CHAT");
//                //tab.setIcon(R.drawable.chat_icon);
//
//                break;
//            case 1:tab.setText("STATUS");
//                // tab.setIcon(R.drawable.status_icon);
//                break;
//            case 2:tab.setText("CALL");
//                //tab.setIcon(R.drawable.call_icon);
//                break;
//
//        }
//    }




    public void saveSettings(){

        SharedPreferences sharedPreferences=getSharedPreferences("myPreferences",MODE_PRIVATE);
        SharedPreferences.Editor myEditor=sharedPreferences.edit();
       //creating a new variable for gson
        Gson gson=new Gson();

        //getting data from gson and storing it in a string
        String json=gson.toJson(mData);

        //below line is to save data in shared
        //prefs in the form of string.
        myEditor.putString("usersKey",json);

        //below line is to apply changes
        //and save data in shard prefs
        myEditor.apply();
        Toast.makeText(this, "Setting Saved", Toast.LENGTH_SHORT).show();
    }

    public void getSettings(){

        mData.clear();
        SharedPreferences sharedPreferences=getSharedPreferences("myPreferences",MODE_PRIVATE);

        //creating a vatriable for gson.
        Gson gson=new Gson();

        //below line is to get to string present from our
        //shared  prefs if not present setting it as null;
        String json=sharedPreferences.getString("usersKey",null);

        //below line is to get the type of our array list.
        Type type=new TypeToken<ArrayList<Users>>(){}.getType();

        //in below line we are getting data from gson
        //and saving it to our array list
        try {
            mData=gson.fromJson(json,type);
        }catch (Exception e){
            e.printStackTrace();
        }

       // usersAdapter.notifyDataSetChanged();

        //checking below if the array list is empty or not
//        if (mData==null){
//            //if the array list is empty
//            //creating a new array list
//            mData=new ArrayList<>();
//        }



    }



    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(getApplicationContext().CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

}