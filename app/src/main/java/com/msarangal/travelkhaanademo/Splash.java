package com.msarangal.travelkhaanademo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class Splash extends AppCompatActivity implements AnimationListener {

    private Boolean isBackPressed;
    private SharedPreferences sharedPreferences;

    // Splash screen timer
    private static int SPLASH_TIME_OUT = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);
        sharedPreferences = this.getSharedPreferences("tkhaana", MODE_PRIVATE);
        isBackPressed = false;
        new Handler().postDelayed(new Runnable() {

			/*
             * Showing splash screen with a timer. This will be useful when you
			 * want to show case your app logo / company
			 */

            public void run() {

                //user is not new, so just proceed with the app normally


                if (isBackPressed == true) {

                    finish();
                } else if (sharedPreferences.getBoolean("isConnected", false) == false) {
                    Intent i = new Intent(Splash.this, MainActivity.class);
                    startActivity(i);
                } else if (sharedPreferences.getBoolean("isConnected", false) == true) {
                    Intent i = new Intent(Splash.this, MapsActivity.class);
                    startActivity(i);
                }
                // close this activity
                finish();
            }
        }, SPLASH_TIME_OUT);
    }

    public void onAnimationStart(Animation animation) {
        // TODO Auto-generated method stub
    }

    public void onAnimationEnd(Animation animation) {
        // TODO Auto-generated method stub


    }

    public void onAnimationRepeat(Animation animation) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onBackPressed() {

        isBackPressed = true;

        finish();
    }
}
