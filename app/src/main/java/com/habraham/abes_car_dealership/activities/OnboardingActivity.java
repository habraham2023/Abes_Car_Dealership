package com.habraham.abes_car_dealership.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.habraham.abes_car_dealership.R;
import com.habraham.abes_car_dealership.databinding.ActivityOnboardingBinding;

public class OnboardingActivity extends AppCompatActivity {
    private static final String TAG = "OnboardingActivity";

    MaterialButton btnLogin;
    MaterialButton btnSignup;
    private ActivityOnboardingBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOnboardingBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        btnLogin = binding.btnLogin;
        btnSignup = binding.btnSignup;

        // Direct user to Login screen when login button is pressed.
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "onClick: Login");
                Intent intent = new Intent(OnboardingActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        // Direct user to Signup screen when signup button is pressed.
        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "onClick: Signup");
                Intent intent = new Intent(OnboardingActivity.this, SignupActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed() {
        Log.i(TAG, "onBackPressed");
        finishAffinity();
    }
}