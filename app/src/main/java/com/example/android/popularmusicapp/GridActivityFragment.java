package com.example.android.popularmusicapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A placeholder fragment containing a simple view.
 */
public class GridActivityFragment extends Fragment implements View.OnClickListener {

    public GridActivityFragment() {
    }

//    protected Context context = getActivity().getApplicationContext();

    protected GridView gridView;
    protected GridViewAdapter gridAdapter;
    protected View rootView;

    final String  POSTER_PATH = "poster_path";
    final String  TITLE = "original_title";
    final String  LANG = "original_language";
    final String  POPULARITY = "popularity";
    final String  VOTE_COUNT = "vote_count";
    final String  VOTE_AVE = "vote_average";
    final String  RELEASE_DATE = "release_date";
    final String  OVERVIEW = "overview";


    @Override
    public void onClick(View v) {

        if(v.getId()==R.id.searchText){
            EditText editText = (EditText) v.findViewById(R.id.searchText);
            editText.setText("");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_movie_grid, container, false);

        EditText editText = (EditText) rootView.findViewById(R.id.searchText);

        editText.setOnClickListener(this);

        editText.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {

                    //start AsyncTask
                    AsyncMetadata task = new AsyncMetadata();
                    task.execute(v.getText().toString());

                    InputMethodManager inputManager =
                            (InputMethodManager) getActivity().getApplicationContext().
                                    getSystemService(Context.INPUT_METHOD_SERVICE);
                    if(getActivity().getCurrentFocus()!=null) {
                        inputManager.hideSoftInputFromWindow(
                                getActivity().getCurrentFocus().getWindowToken(),
                                InputMethodManager.HIDE_NOT_ALWAYS);
                    }

                    return true;
                }
                return false;
            }
        });

        editText.setOnFocusChangeListener(new View.OnFocusChangeListener()
        {
            @Override
            public void onFocusChange(View v, boolean hasFocus)
            {
              TextView tv = (TextView) v;

                if (hasFocus==true)
                {
                    if (tv.getText().toString().compareTo(getString(R.string.gridView_editText))==0)
                    {
                        tv.setText("");
                    }
                }
            }
        });


        gridView = (GridView) rootView.findViewById(R.id.GridLayout_Movie);

        gridView.setAdapter(gridAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                ImageItem item = (ImageItem) parent.getItemAtPosition(position);

                //Create intent
                Intent detailsIntent = new Intent(getActivity().getApplicationContext(), MovieDetails.class);
              /*  detailsIntent.putExtra("TITLE", TITLE);
                detailsIntent.putExtra("POPULARITY", POPULARITY);
                detailsIntent.putExtra("VOTE_AVE", VOTE_AVE);
                detailsIntent.putExtra("IMAGE", item.getImage());*/

                //Start details activity
                startActivity(detailsIntent);
            }
        });

        return rootView;
    }


    private class AsyncMetadata extends AsyncTask<String, Void, List<Map<String,String>>> {

        private final String LOG_TAG = GridActivity.class.getSimpleName();

        public List<Map<String,String>> resultList;

        @Override
        protected void onPostExecute(List<Map<String,String>> list) {

            resultList = list;

            AsyncImage imageTask = new AsyncImage(){
                @Override
                protected void onPostExecute(GridViewAdapter adapter) {
                    gridView.setAdapter(gridAdapter);
                }
            };

            if(list!=null) {
                imageTask.execute(list);
            }
        }

        @Override
        protected List<Map<String,String>> doInBackground(String... params) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String response = null;
            List<Map<String,String>> jsonList = null; ;

            try {

                Uri.Builder uriBuilder = new Uri.Builder();

                uriBuilder.scheme("http")
                        .authority(getString(R.string.base_url))
                        .appendPath("3")
                        .appendPath("search")
                        .appendPath("movie")
                        .appendQueryParameter("query", params[0])
                        .appendQueryParameter("api_key", getString(R.string.api_key));


                String uri = uriBuilder.build().toString();
                Log.d("DEBUGINGATTENTION: ", uri);
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
                response = buffer.toString();

                jsonList = formatJsonResponse(response);

            } catch (IOException e) {
                Log.e(LOG_TAG, "Error by IO ", e);

                return null;
            } catch (JSONException e) {
                Log.e(LOG_TAG, "JSON Error", e);
            } finally{
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
                return jsonList;
            }
        }

        private List<Map<String,String>> formatJsonResponse(String jsonStr)
                throws JSONException {

            List resultList = new ArrayList();
            JSONObject o = new JSONObject(jsonStr);
            final Integer TOTAL_PAGES = (o.getInt("total_pages"));
            JSONArray arr = o.getJSONArray("results");

            if (arr.length()>0) {
                for (int i = 0; i < arr.length(); i++) {
                    Map<String, String> hashMap = new HashMap<>();
                    JSONObject obj = arr.getJSONObject(i);
                    hashMap.put(POSTER_PATH, obj.getString(POSTER_PATH));
                    hashMap.put(TITLE, obj.getString(TITLE));
                    hashMap.put(LANG, obj.getString(LANG));
                    hashMap.put(POPULARITY, obj.getString(POPULARITY));
                    hashMap.put(VOTE_COUNT, obj.getString(VOTE_COUNT));
                    hashMap.put(VOTE_AVE, obj.getString(VOTE_AVE));
                    hashMap.put(RELEASE_DATE, obj.getString(RELEASE_DATE));
                    hashMap.put(OVERVIEW, obj.getString(OVERVIEW));
                    resultList.add(hashMap);
                }
            }
            else {
                Log.d(LOG_TAG, "No movies.");
                return Collections.EMPTY_LIST;
            }
            return resultList;

        }
    }

    private class AsyncImage extends AsyncTask<List<Map<String,String>>, Void, GridViewAdapter> {

        private final String LOG_TAG = AsyncImage.class.getSimpleName();

        public Bitmap loadBitmap(String posterPath)
        {
            Uri.Builder uriBuilder = new Uri.Builder();

            uriBuilder.scheme("http")
                    .authority("image.tmdb.org")
                    .appendPath("t")
                    .appendPath("p")
                    .appendPath("w500")
                    .appendPath(posterPath)
                    .appendQueryParameter("api_key", getString(R.string.api_key));


            String uri = uriBuilder.build().toString();

            Bitmap bm = null;
            InputStream is = null;
            BufferedInputStream bis = null;
            try
            {
                URLConnection conn = new URL(uri).openConnection();
                conn.connect();
                is = conn.getInputStream();
                bis = new BufferedInputStream(is, 8192);
                bm = BitmapFactory.decodeStream(bis);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            finally {
                if (bis != null)
                {
                    try
                    {
                        bis.close();
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                }
                if (is != null)
                {
                    try
                    {
                        is.close();
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                }
            }
            return bm;
        }


        private Bitmap createThumbnailFromByteArray(byte[] imageData, String fileName) {
            try {

                final int THUMBNAIL_SIZE = 64;

                FileInputStream fis = new FileInputStream(fileName);
                Bitmap imageBitmap = BitmapFactory.decodeStream(fis);

                imageBitmap = Bitmap.createScaledBitmap(imageBitmap, THUMBNAIL_SIZE, THUMBNAIL_SIZE, false);

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
//                imageData = baos.toByteArray();
                return imageBitmap;

            } catch (Exception ex) {
                Log.e(LOG_TAG, ex.toString());
            }
            return null;
        }

        @Override
        protected GridViewAdapter doInBackground(List<Map<String,String>>... params) {

            ArrayList<ImageItem> bmps = new ArrayList<>();

            for (int i=0; i<params[0].size(); i++) {
                String posterPath = params[0].get(i).get(POSTER_PATH).toString().substring(1);
                Bitmap bm = loadBitmap(posterPath);
                bmps.add(new ImageItem(bm, params[0].get(i).get(TITLE)));
            }
            gridAdapter = new GridViewAdapter(getActivity().getApplicationContext(), R.layout.grid_item_layout, bmps);

            return gridAdapter;
        }

        @Override
        protected void onPostExecute(GridViewAdapter adapter) {
            super.onPostExecute(adapter);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
    }
}
