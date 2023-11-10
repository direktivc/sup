package com.example.sup;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

public class StoryFragment extends Fragment {
        public static com.example.sup.StoryFragment newInstance(){
            StoryFragment fragments = new StoryFragment();
            return fragments;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragments_story ,container, false);
            return view;
        }
}
