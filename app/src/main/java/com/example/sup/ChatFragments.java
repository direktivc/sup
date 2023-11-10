package com.example.sup;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;

public class ChatFragments extends Fragment {
    public static ChatFragments newInstance(){
        ChatFragments fragments = new ChatFragments();
        return fragments;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragments_chat ,container, false);
        return view;
    }
}
