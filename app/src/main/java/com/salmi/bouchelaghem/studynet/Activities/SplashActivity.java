package com.salmi.bouchelaghem.studynet.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;

import com.google.gson.Gson;
import com.salmi.bouchelaghem.studynet.R;
import com.salmi.bouchelaghem.studynet.Utils.CurrentUser;
import com.salmi.bouchelaghem.studynet.Utils.Utils;
import com.salmi.bouchelaghem.studynet.databinding.ActivitySplashBinding;

public class SplashActivity extends AppCompatActivity {

    private ActivitySplashBinding binding;
    private SharedPreferences sharedPreferences;
    private boolean loggedIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySplashBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        //Get the shared preferences.
        sharedPreferences = getApplicationContext().getSharedPreferences(Utils.SHARED_PREFERENCES_USER_DATA,MODE_PRIVATE);
        // Set light theme as the default theme
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        verifyInternet();

        binding.btnTryAgain.setOnClickListener(v -> verifyInternet());
    }

    private void verifyInternet (){
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo network = connectivityManager.getActiveNetworkInfo();
        if (network == null) { // No internet

            binding.mainLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
            binding.noInternetMsg.setVisibility(View.VISIBLE);

        } else {

            new Handler().postDelayed(() -> {
                //Check if the user is already logged in using shared preferences.
                if (sharedPreferences.getBoolean(Utils.SHARED_PREFERENCES_LOGGED_IN,false))
                {
                    //The user is already logged in.
                    Gson gson = new Gson();
                    String json = sharedPreferences.getString(Utils.SHARED_PREFERENCES_CURRENT_USER, "");
                    CurrentUser currentUserData = gson.fromJson(json, CurrentUser.class);
                    CurrentUser.setInstance(currentUserData);
                    startActivity(new Intent(SplashActivity.this, NavigationActivity.class));
                } else {
                    startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                }
                finish();
            }, 1000);

        }
    }
}