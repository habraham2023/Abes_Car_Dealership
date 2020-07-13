package com.habraham.abes_car_dealership;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.habraham.abes_car_dealership.fragments.ChatsFragment;
import com.habraham.abes_car_dealership.fragments.FavoritesFragment;
import com.habraham.abes_car_dealership.fragments.ListingsFragment;
import com.habraham.abes_car_dealership.fragments.ProfileFragment;
import com.parse.ParseUser;

public class MainActivity extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final FragmentManager fragmentManager = getSupportFragmentManager();
        bottomNavigationView = findViewById(R.id.bottomNavigation);

        // Direct user to Onboarding screen if they don't already logged in
        if (ParseUser.getCurrentUser() == null) {
            Intent intent = new Intent(this, OnboardingActivity.class);
            startActivity(intent);
        }

        // Navigate to fragment that the user picks
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment;
                switch (item.getItemId()) {
                    case R.id.miListings:
                        fragment = new ListingsFragment();
                        break;
                    case R.id.miFavorites:
                        fragment = new FavoritesFragment();
                        break;
                    case R.id.miChat:
                        fragment = new ChatsFragment();
                        break;
                    case R.id.miProfile:
                    default:
                        fragment = new ProfileFragment();
                        break;
                }
                // Replace contents of rlContainer with fragment
                fragmentManager.beginTransaction().replace(R.id.rlContainer, fragment).commit();
                return true;
            }
        });
        // Set default selection
        bottomNavigationView.setSelectedItemId(R.id.miListings);
    }
}