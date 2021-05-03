package com.example.seniorprojectjan;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import gr.net.maroulis.library.EasySplashScreen;

public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        EasySplashScreen config = new EasySplashScreen(SplashScreenActivity.this)
                .withFullScreen()
                .withTargetActivity(MainActivity.class)
                .withSplashTimeOut(2000)
                .withBackgroundColor(Color.parseColor("#232b2b"))
                .withHeaderText("")
                .withFooterText("")
                .withBeforeLogoText("")
                .withAfterLogoText("")
                .withLogo(R.mipmap.bayti_icon_round);

        config.getHeaderTextView().setTextColor(Color.parseColor("#7df9ff"));
        config.getFooterTextView().setTextColor(Color.parseColor("#7df9ff"));
        config.getBeforeLogoTextView().setTextColor(Color.parseColor("#7df9ff"));
        config.getAfterLogoTextView().setTextColor(Color.parseColor("#7df9ff"));


        View easySplashScreen = config.create();
        setContentView(easySplashScreen);
    }
}