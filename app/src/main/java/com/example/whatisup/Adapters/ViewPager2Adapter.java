package com.example.whatisup.Adapters;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class ViewPager2Adapter extends FragmentStateAdapter {
   ArrayList<Fragment> fragments=new ArrayList<>();//variable holds the fragments the ViewPager2 allows us to swipe to.

   public void setData(ArrayList<Fragment> fragments){
      this.fragments=fragments;
   }

   //Constructor
   public ViewPager2Adapter(@NonNull FragmentActivity fragmentActivity) {
      super(fragmentActivity);

   }

   @NonNull
   @Override
   public Fragment createFragment(int position) {
      return fragments.get(position);
   }

   @Override
   public int getItemCount() {
      return fragments.size();
   }
}
