package com.habraham.abes_car_dealership;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.parse.ParseUser;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Direct user to Onboarding screen if they don't already logged in
        if (ParseUser.getCurrentUser() == null) {
            Intent intent = new Intent(this, OnboardingActivity.class);
            startActivity(intent);
        }
    }
}