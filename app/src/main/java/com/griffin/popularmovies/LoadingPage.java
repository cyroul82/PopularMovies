package com.griffin.popularmovies;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class LoadingPage extends Activity {

    // Splash screen timer
    private final static int SPLASH_TIME_OUT = 3500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading_page);

        new Handler().postDelayed(new Runnable() {

            /*
             * Showing splash screen with a timer.
             */

            @Override
            public void run() {
                // This method will be executed once the timer is over
                // Start the app main activity
                Intent i = new Intent(LoadingPage.this, MainActivity.class);
                startActivity(i);

                // close this activity
                finish();
            }
        }, SPLASH_TIME_OUT);
    }
}
