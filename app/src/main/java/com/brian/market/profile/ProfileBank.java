package com.brian.market.profile;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.brian.market.R;

public class ProfileBank extends Fragment {


    public ProfileBank() {
        // Required empty public constructor
    }
    public static ProfileBank newInstance() {
        ProfileBank fragment = new ProfileBank();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile_bank, container, false);
    }
}
