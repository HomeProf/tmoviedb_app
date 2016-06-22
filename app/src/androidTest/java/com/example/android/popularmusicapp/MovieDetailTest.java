package com.example.android.popularmusicapp;

import android.test.AndroidTestCase;

/**
 * Created by xander on 15.06.16.
 */
public class MovieDetailTest extends AndroidTestCase {

    private static final String LOG_TAG = MovieDetailTest.class.getSimpleName();

    @Override
    public void setUp() throws Exception {
        super.setUp();

    }

    public void testRenderRatingStars() throws Exception{

        assertTrue("Test mit 0.0 Fehler",0.0 == Utility.renderRatingStars(0.0));
        assertTrue("Test mit 2.1 Fehler",1.0 == Utility.renderRatingStars(2.1));
        assertTrue("Test mit 3.4 Fehler",1.5 == Utility.renderRatingStars(3.4));
        assertTrue("Test mit 3.5 Fehler",1.5 == Utility.renderRatingStars(3.5));
        assertTrue("Test mit 3.9 Fehler",2.5 == Utility.renderRatingStars(5.8));
        assertTrue("Test mit 10 Fehler",5.0 == Utility.renderRatingStars(10.0));
    }




    @Override
    public void tearDown() throws Exception {
        super.tearDown();
    }
}
