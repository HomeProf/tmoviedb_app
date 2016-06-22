package com.example.android.popularmusicapp;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by xander on 15.06.16.
 */
public class AboutFragment extends Fragment {

    public AboutFragment() {
        super();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = (View) inflater.inflate(R.layout.activity_about, container, false);

        return rootView;
    }

}
