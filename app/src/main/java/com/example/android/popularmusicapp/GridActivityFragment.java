package com.example.android.popularmusicapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import org.json.JSONException;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * A placeholder fragment containing a simple view.
 */
public class GridActivityFragment extends Fragment {


    public GridActivityFragment() {

    }

    private static final String LOG_TAG = GridActivityFragment.class.getSimpleName();

    private final Utility mUtility = new Utility();

    protected GridView gridView;
    protected GridViewAdapter gridAdapter;
    protected View rootView;
    protected SearchView searchView = null;

    public static String CUR_PREF_ORDER;
    protected SharedPreferences sharedpreferences;

    protected static String CUR_SORT_ORDER = null;

    protected static String CUR_TITLE = null;
    protected static String CUR_RELEASE_DATE = null;
    protected static String CUR_POPULARITY = null;
    protected static String CUR_VOTE_AVE = null;
    protected static String CUR_SYNOPSIS = null;
    protected static Bitmap CUR_IMAGE = null;

    protected ProgressDialog mProgressDialog;


    public static String getCurPopularity() {
        return CUR_POPULARITY;
    }

    public static void setCurPopularity(String curPopularity) {
        GridActivityFragment.CUR_POPULARITY = curPopularity;
    }

    public static String getCurVoteAve() {
        return CUR_VOTE_AVE;
    }

    public static void setCurVoteAve(String curVoteAve) {
        GridActivityFragment.CUR_VOTE_AVE = curVoteAve;
    }

    public static String getCurSynopsis() {
        return CUR_SYNOPSIS;
    }

    public static void setCurSynopsis(String curSynopsis) {
        GridActivityFragment.CUR_SYNOPSIS = curSynopsis;
    }

    public static String getCurTitle() {
        return CUR_TITLE;
    }

    public static void setCurTitle(String curTitle) {
        GridActivityFragment.CUR_TITLE = curTitle;
    }

    public static Bitmap getCurImage() {
        return CUR_IMAGE;
    }

    public static void setCurImage(Bitmap curImage) {
        GridActivityFragment.CUR_IMAGE = curImage;
    }

    public static String getCurReleaseDate() {
        return CUR_RELEASE_DATE;
    }

    public static void setCurReleaseDate(String curReleaseDate) {
        GridActivityFragment.CUR_RELEASE_DATE = curReleaseDate;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        setRetainInstance(true);
    }

    @Override
    public void onResume() {
        super.onResume();

        String tmp = sharedpreferences.getString(getString(R.string.pref_sort_order), getString(R.string.pref_most_popular));

        if(searchView != null) {
            if(!tmp.equals(CUR_PREF_ORDER)) {
            String query = searchView.getQuery().toString();
            if(query != "" || query != null) {

                if (query != null && query != "") {
                    processRequestWithDependencies(query);
                } else {
                    Log.d(LOG_TAG, "query String is null or empty.");
                }
            }
            }
        }
    }

    protected AsyncMetadata task;

    private void processSearchText(String searchText) {

        task = new AsyncMetadata();
        task.execute(searchText);

        mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                task.cancel(true);
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        sharedpreferences = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());


        rootView = inflater.inflate(R.layout.fragment_movie_grid, container, false);

        gridView = (GridView) rootView.findViewById(R.id.GridLayout_Movie);
        gridView.setAdapter(gridAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

                ImageItem item = (ImageItem) parent.getItemAtPosition(position);

                setCurTitle(item.getTitle());
                setCurPopularity(item.getPopularity());
                setCurSynopsis(item.getSynopsis());
                setCurVoteAve(item.getVoteAve());
                setCurImage(item.getImage());
                setCurReleaseDate(item.getReleaseDate());


                //Create intent
                Intent detailsIntent = new Intent(getActivity().getApplicationContext(), MovieDetails.class);

                startActivity(detailsIntent);
            }
        });

        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setMessage(getString(R.string.on_progress_caption));
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setCancelable(true);

        if(!isWiFiInternetAvailable()) {
            getActivity().runOnUiThread(new Runnable() {
                public void run() {

                    Toast toast = Toast.makeText(getContext(), "WiFi is disabled!", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.TOP | Gravity.CENTER, 0, 100);
                    toast.show();
                }
            });
        }

        return rootView;
    }

    private boolean isWiFiInternetAvailable() {
        WifiManager wifiManager = (WifiManager) getActivity().getSystemService(getActivity().WIFI_SERVICE);
        boolean wifiEnabled = wifiManager.isWifiEnabled();

        ConnectivityManager connectivityManager =
                (ConnectivityManager) getActivity().getSystemService(getActivity().CONNECTIVITY_SERVICE);

        if(wifiEnabled) {
            NetworkInfo mobileInfo =
                    connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

            if (mobileInfo.getState().equals(NetworkInfo.State.CONNECTED)) {
                Log.d(LOG_TAG, "connection via WiFi internet available.");
                return true;
            }
        }
        return false;
    }

    private boolean isMobileInternetAvailable() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getActivity().getSystemService(getActivity().CONNECTIVITY_SERVICE);

        NetworkInfo mobileInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        boolean mobileConnected = mobileInfo.getState().equals(NetworkInfo.State.CONNECTED);
        if(mobileConnected) {
            Log.d(LOG_TAG, "connection via mobil internet available.");
        }

        return mobileConnected;
    }

    private void processRequestWithDependencies(String query) {
        searchView.clearFocus();
        CUR_PREF_ORDER = sharedpreferences.getString(getString(R.string.pref_sort_order), getString(R.string.pref_most_popular));

        if(isWiFiInternetAvailable()) {
            processSearchText(query);
        }
        else if(isMobileInternetAvailable()) {
            processSearchText(query);
        }
        else {
            getActivity().runOnUiThread(new Runnable() {
                public void run() {

                    Toast toast = Toast.makeText(getContext(), "No Internet Connection.", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.TOP | Gravity.CENTER, 0, 100);
                    toast.show();
                }
            });
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        MenuItem searchItem = menu.findItem(R.id.action_search);

        if (searchItem != null) {
            searchView = (SearchView) searchItem.getActionView();
        }

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            public boolean onQueryTextChange(String newText) {
                // this is your adapter that will be filtered
                return true;
            }

            public boolean onQueryTextSubmit(String query) {

                if (query != null && query != "") {
                    processRequestWithDependencies(query);
                } else {
                    Log.d(LOG_TAG, "query String is null or empty.");
                }

                return true;
            }
        });
    }

    protected PowerManager.WakeLock mWakeLock;


    private class AsyncMetadata extends AsyncTask<String, Integer, List<Map<String, String>>> {

        private final String LOG_TAG = GridActivity.class.getSimpleName();

        public List<Map<String, String>> resultList;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // take CPU lock to prevent CPU from going off if the user
            // presses the power button during download
            PowerManager pm = (PowerManager) getContext().getSystemService(Context.POWER_SERVICE);
            mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                    getClass().getName());
            mWakeLock.acquire();
            mProgressDialog.show();
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);
            // if we get here, length is known, now set indeterminate to false
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.setMax(20);
            mProgressDialog.setProgress(progress[0]);

        }


        @Override
        protected void onPostExecute(List<Map<String, String>> list) {

            if(list!=null && list.size()>0) {

                final AsyncImage imageTask = new AsyncImage() {
                    @Override
                    protected void onPostExecute(ArrayList<ImageItem> imageItems) {
                        if (imageItems.size() > 0) {

                            gridAdapter = new GridViewAdapter(getActivity().getApplicationContext(), R.layout.grid_item_layout, imageItems);
                            gridView.setAdapter(gridAdapter);

//                            searchView.clearFocus();
                        } else {
                            gridAdapter = new GridViewAdapter(getActivity().getApplicationContext(), R.layout.grid_item_layout, new ArrayList());
                            gridView.setAdapter(gridAdapter);
//                            Toast.makeText(getActivity(), "No results found for that", Toast.LENGTH_SHORT).show();
                        }

                    }
                };
                imageTask.execute(list);
            }
            else {
                mWakeLock.release();
                mProgressDialog.dismiss();

                    getActivity().runOnUiThread(new Runnable() {
                        public void run() {

                            Toast toast = Toast.makeText(getContext(), "No movies for that.", Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.TOP|Gravity.CENTER, 0, 100);
                            toast.show();
                        }
                    });
                    Log.d(LOG_TAG, "No Movies for that.");

            }

        }

        private String getResponseForUri(String uri) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(uri);

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();

                if (inputStream == null) {

                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {

                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {

                    return null;
                }
                return buffer.toString();
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error by IO ", e);
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }
            return "";
        }

        @Override
        protected List<Map<String, String>> doInBackground(String... params) {

            List<Map<String, String>> jsonList = new ArrayList<>();

            String inputParam = params[0];

            try {

                if (inputParam.length() > 0) {

                    String kw_uri = mUtility.getUriForKeywords(getActivity(), inputParam);
                    String kw_response = getResponseForUri(kw_uri);
                    String[] keywords = mUtility.getKeywordsFromJson(kw_response);

                    if(keywords.length>0) {
                        String uri = mUtility.getUriForParam(getActivity(), keywords);
                        String response = getResponseForUri(uri);

                        Log.d("DEBUGINGATTENTION: ", uri);

                        jsonList = mUtility.formatJsonResponse(response);
                    }
                    else {
                       return null;
                    }
                }
                return jsonList;
            } catch (JSONException e) {
                Log.e(LOG_TAG, "JSON Error", e);
            }
            return jsonList;
        }
    }

        private class AsyncImage extends AsyncTask<List<Map<String, String>>, Integer, ArrayList<ImageItem>> {

            private final String LOG_TAG = AsyncImage.class.getSimpleName();

            public Bitmap loadBitmap(String posterPath) {

                String uri = mUtility.getUriStringForImages(getActivity(), posterPath);

                Bitmap bm = null;
                InputStream is = null;
                BufferedInputStream bis = null;

                try {
                    URLConnection conn = new URL(uri).openConnection();
                    conn.connect();
                    is = conn.getInputStream();
                    bis = new BufferedInputStream(is, 8192);
                    bm = BitmapFactory.decodeStream(bis);

                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    bm.compress(Bitmap.CompressFormat.JPEG, 20, out);
                    Bitmap decoded = BitmapFactory.decodeStream(new ByteArrayInputStream(out.toByteArray()));
                    bm = decoded;

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (bis != null) {
                        try {
                            bis.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (is != null) {
                        try {
                            is.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                return bm;
            }



            @Override
            protected ArrayList<ImageItem> doInBackground(List<Map<String, String>>... params) {


                List<Map<String, String>> list = params[0];

                try {

                    return publishImageBulk(list);

                } catch (Exception e) {
                    Log.e(LOG_TAG, e.getMessage());
                }
                return null;
            }

            private ArrayList<ImageItem> publishImageBulk(List<Map<String, String>> helperList) {
                ArrayList<ImageItem> bmps = new ArrayList<ImageItem>();

                Bitmap bm = null;

                for (int i = 0; i < helperList.size(); i++) {
                    Map cur = helperList.get(i);
                    String posterPath = cur.get(Constants.POSTER_PATH).toString().substring(1);
                    if (posterPath != null) {
                        bm = loadBitmap(posterPath);
                    }
                    if (bm == null) {
                        bm = BitmapFactory.decodeResource(getResources(), R.drawable.not_available);
                    }

                    bmps.add(new ImageItem(bm, cur.get(Constants.OVERVIEW).toString(),
                            cur.get(Constants.VOTE_AVE).toString(),
                            cur.get(Constants.POPULARITY).toString(),
                            cur.get(Constants.TITLE).toString(),
                            cur.get(Constants.RELEASE_DATE).toString()));

                }
                mWakeLock.release();
                mProgressDialog.dismiss();
                return bmps;
            }

            @Override
            protected void onProgressUpdate(Integer... values) {
                task.onProgressUpdate(values);
            }
        }


    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
    }
}