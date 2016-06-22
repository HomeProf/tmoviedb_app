package com.example.android.popularmusicapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * A placeholder fragment containing a simple view.
 */
public class MovieDetailsFragment extends Fragment {

    public MovieDetailsFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = (View) inflater.inflate(R.layout.fragment_movie_details, container, false);
        Intent intent = getActivity().getIntent();

            TextView titleTextView = (TextView) rootView.findViewById(R.id.movie_details_title);
            if(GridActivityFragment.getCurTitle() != null) {
                titleTextView.setText(GridActivityFragment.CUR_TITLE);
            }
            else {
                titleTextView.setText(getString(R.string.empty_title_en));
            }

            TextView dateTextView = (TextView) rootView.findViewById(R.id.movie_details_date);
            if(GridActivityFragment.getCurTitle() != null) {
                dateTextView.setText(GridActivityFragment.CUR_RELEASE_DATE);
            }
            else {
                dateTextView.setText(getString(R.string.empty_date_en));
            }

            TextView popularityTextView = (TextView) rootView.findViewById(R.id.movie_details_popularity);
            if(GridActivityFragment.getCurSynopsis() != null) {
                popularityTextView .setText(GridActivityFragment.CUR_POPULARITY);
            }
            else {
                popularityTextView.setText(getString(R.string.empty_popularity_en));
            }

            TextView rateCountTextView = (TextView) rootView.findViewById(R.id.movie_details_vote_ave);
            if(GridActivityFragment.getCurSynopsis() != null) {
                rateCountTextView.setText(GridActivityFragment.CUR_VOTE_AVE);
            }
            else {
                rateCountTextView.setText(getString(R.string.empty_vote_ave_en));
            }

            TextView synopsisTextView = (TextView) rootView.findViewById(R.id.movie_details_synopsis);
            if(GridActivityFragment.getCurSynopsis() != null) {
                synopsisTextView.setText(GridActivityFragment.CUR_SYNOPSIS);
            }
            else {
                synopsisTextView.setText(getString(R.string.empty_synopsis_en));
            }

            ImageView imageView = (ImageView) rootView.findViewById(R.id.movie_details_image);
            imageView.setImageBitmap(GridActivityFragment.CUR_IMAGE);


        return rootView;
    }
}
