package com.habraham.abes_car_dealership.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.habraham.abes_car_dealership.R;
import com.habraham.abes_car_dealership.databinding.ActivityLoginBinding;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

public class LoginActivity extends AppCompatActivity {
    MaterialButton btnSignup;
    MaterialButton btnContinue;
    TextInputEditText usernameEditText;
    TextInputLayout passwordTextInput;
    TextInputEditText passwordEditText;
    private ActivityLoginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        btnSignup = binding.btnSignup;
        btnContinue = binding.btnContinue;

        usernameEditText = binding.usernameEditText;

        passwordTextInput = binding.passwordTextInput;
        passwordEditText = binding.passwordEditText;

        // Allow user to go to signup screen if they don't have an account
        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(intent);
            }
        });

        // Listens for when user tries to login
        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = usernameEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                attemptSignIn(username, password);
            }
        });
    }

    // Attempts to sign in user with given credentials, if successful direct user to main screen
    private void attemptSignIn(String username, String password) {
        ParseUser.logInInBackground(username, password, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if (e != null) {
                    passwordTextInput.setError("Username or password is incorrect.");
                } else {
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finishAffinity();
                }
            }
        });
    }
}