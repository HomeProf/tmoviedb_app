package com.example.android.popularmusicapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by xander on 13.06.16.
 */
public class Utility {

    public Utility() {
    }

    private static final String LOG_TAG = Utility.class.getSimpleName();

    public String getUriForKeywords(Context context, String param) {
        Uri.Builder uriBuilder = new Uri.Builder();


        uriBuilder.scheme("http")
                .authority(context.getString(R.string.base_url))
                .appendPath("3")
                .appendPath("search")
                .appendPath("keyword")
                .appendQueryParameter("query", param)
                .appendQueryParameter("api_key", context.getString(R.string.api_key));

        try {
            return uriBuilder.build().toString();
        } catch (UnsupportedOperationException e) {
            Log.e(LOG_TAG, e.getMessage());
            return "";
        }
    }


    public String getUriForParam(Context context, String[] keywords) {

        Uri.Builder uriBuilder = new Uri.Builder();

        String kwConcat = "";
        final String concatAND = "|";

        if(keywords.length>0) {
            kwConcat = keywords[0];
        }
        for (int i = 1; i<keywords.length;i++) {
            kwConcat += concatAND + keywords[i];
        }

        SharedPreferences sharedpreferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        String sortOrderPref = sharedpreferences.getString(context.getString(R.string.pref_sort_order),context.getString(R.string.pref_most_popular));

        uriBuilder.scheme("http")
                .authority(context.getString(R.string.base_url))
                .appendPath("3")
                .appendPath("discover")
                .appendPath("movie")
                .encodedQuery("with_keywords=" + kwConcat)
                .appendQueryParameter("primary_release_date.gte", "0")
                .appendQueryParameter("api_key", context.getString(R.string.api_key)
                );


        if(sortOrderPref.equals(context.getString(R.string.pref_most_popular_default))) {

            uriBuilder.appendQueryParameter("sort_by", "popularity.desc");
        }
        else if(sortOrderPref.equals(context.getString(R.string.pref_vote_ave_default))) {

            uriBuilder.appendQueryParameter("sort_by", "vote_average.desc");
        }
        try {
            return uriBuilder.build().toString();
        } catch (UnsupportedOperationException e) {
            Log.e(LOG_TAG, e.getMessage());
            return "";
        }
    }

    public String getUriStringForImages(Context context, String posterPath) {
        Uri.Builder uriBuilder = new Uri.Builder();

        uriBuilder.scheme("http")
                .authority("image.tmdb.org")
                .appendPath("t")
                .appendPath("p")
                .appendPath("w185")
                .appendPath(posterPath)
                .appendQueryParameter("api_key", context.getString(R.string.api_key));


        try {
            return uriBuilder.build().toString();
        } catch (UnsupportedOperationException e) {
            Log.e(LOG_TAG, e.getMessage());
            return "";
        }
    }

    public String[] getKeywordsFromJson(String jsonStr) throws JSONException {

            List resultList = new ArrayList();
            JSONObject o = new JSONObject(jsonStr);
            final Integer TOTAL_PAGES = (o.getInt("total_pages"));
            JSONArray arr = o.getJSONArray("results");

            String[] result = new String[arr.length()];

        for(int i=0; i<arr.length(); i++) {
            result[i] =  arr.getJSONObject(i).getString("id");
        }

        return result;
    }

    public List<Map<String,String>> formatJsonResponse(String jsonStr)
            throws JSONException {

        List resultList = new ArrayList();
        JSONObject o = new JSONObject(jsonStr);
        final Integer TOTAL_PAGES = (o.getInt("total_pages"));
        JSONArray arr = o.getJSONArray("results");

        if (arr.length()>0) {
            for (int i = 0; i < arr.length(); i++) {
                Map<String, String> hashMap = new HashMap<>();
                JSONObject obj = arr.getJSONObject(i);
                hashMap.put(Constants.POSTER_PATH, obj.getString(Constants.POSTER_PATH));
                hashMap.put(Constants.TITLE, obj.getString(Constants.TITLE));
                hashMap.put(Constants.LANG, obj.getString(Constants.LANG));
                hashMap.put(Constants.POPULARITY, obj.getString(Constants.POPULARITY));
                hashMap.put(Constants.VOTE_COUNT, obj.getString(Constants.VOTE_COUNT));
                hashMap.put(Constants.VOTE_AVE, obj.getString(Constants.VOTE_AVE));
                hashMap.put(Constants.RELEASE_DATE, obj.getString(Constants.RELEASE_DATE));
                hashMap.put(Constants.OVERVIEW, obj.getString(Constants.OVERVIEW));
                resultList.add(hashMap);
            }
        }
        else {
            Log.d(LOG_TAG, "No movies.");
            return Collections.EMPTY_LIST;
        }
        return resultList;

    }


    public static double renderRatingStars(double rating) {
        double halfNumb = rating / 2.0;
        double integer = Math.floor(halfNumb);
        double diff =  halfNumb - integer;
        final double step = 0.5d;
        if(diff>=step) {
            integer += step;
        }
        return integer;
    }
}

