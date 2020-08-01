package com.habraham.abes_car_dealership.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.habraham.abes_car_dealership.R;
import com.habraham.abes_car_dealership.databinding.ActivityMainBinding;
import com.habraham.abes_car_dealership.fragments.ChatsFragment;
import com.habraham.abes_car_dealership.fragments.FavoritesFragment;
import com.habraham.abes_car_dealership.fragments.ListingsFragment;
import com.habraham.abes_car_dealership.fragments.MyListingsFragment;
import com.parse.ParseUser;

public class MainActivity extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        final FragmentManager fragmentManager = getSupportFragmentManager();
        bottomNavigationView = binding.bottomNavigation;

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
                        fragment = new MyListingsFragment();
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