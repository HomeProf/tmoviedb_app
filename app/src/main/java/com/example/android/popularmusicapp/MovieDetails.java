package com.example.android.popularmusicapp;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.util.List;

public class MovieDetails extends AppCompatActivity {



    private static final String LOG_TAG = MovieDetails.class.getSimpleName();

    protected Intent curIntent = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_movie_details, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        List<ApplicationInfo> packages;
        PackageManager pm;
        pm = getPackageManager();

        //get a list of installed apps.
        packages = pm.getInstalledApplications(0);

        ActivityManager mActivityManager = (ActivityManager) getApplicationContext()
                .getSystemService(Context.ACTIVITY_SERVICE);

        switch (item.getItemId()) {

            case R.id.action_booking_hotelDE:
                for (ApplicationInfo packageInfo : packages) {

                    if(packageInfo.packageName.equals("de.hotel.android")) {
                        final String APP_PACKAGE_NAME = packageInfo.packageName;
                        startAppActivity(getApplicationContext(), APP_PACKAGE_NAME);
                        break;
                    }
                }
                break;

            case R.id.action_booking_hrs:
                for (ApplicationInfo packageInfo : packages) {
                    if(packageInfo.packageName.equals("com.hrs.android")) {
                        Log.e("AAAAAAAA",packageInfo.packageName);
                        final String APP_PACKAGE_NAME = packageInfo.packageName;
                        startAppActivity(getApplicationContext(), APP_PACKAGE_NAME);
                        break;
                    }
                }
                break;

            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    public void startAppActivity(Context context, String packageName) {
        Intent intent = context.getPackageManager().getLaunchIntentForPackage(packageName);
        if (intent == null) {
            // Bring user to the market or let them choose an app?
            intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("market://details?id=" + packageName));
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }


}
