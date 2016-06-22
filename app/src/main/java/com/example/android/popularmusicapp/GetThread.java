package com.example.android.popularmusicapp;

import android.net.Uri;
import android.util.Log;

import java.net.HttpURLConnection;

/**
 * Created by xander on 13.06.16.
 */
public class GetThread extends Thread {

    private HttpURLConnection httpClient;
    private Uri uri;
    private int id;

    private static final String LOG_TAG = GetThread.class.getSimpleName();

    public GetThread(HttpURLConnection httpClient, Uri uri, int id) {
        this.httpClient = httpClient;
        this.uri = uri;
        this.id = id;
    }


    public void run() {

        try {


        } catch (Exception e) {
            System.out.println(id + " - error: " + e);
        } finally {
            // always release the connection after we're d
            Log.v(LOG_TAG, id + " - connection released");
        }
    }
}