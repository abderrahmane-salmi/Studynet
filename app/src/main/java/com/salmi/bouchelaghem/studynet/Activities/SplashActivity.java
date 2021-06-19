package com.salmi.bouchelaghem.studynet.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;

import com.google.gson.JsonObject;
import com.salmi.bouchelaghem.studynet.R;
import com.salmi.bouchelaghem.studynet.Utils.CurrentUser;
import com.salmi.bouchelaghem.studynet.Utils.Serializers;
import com.salmi.bouchelaghem.studynet.Utils.StudynetAPI;
import com.salmi.bouchelaghem.studynet.Utils.Utils;
import com.salmi.bouchelaghem.studynet.databinding.ActivitySplashBinding;

import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SplashActivity extends AppCompatActivity {

    private ActivitySplashBinding binding;
    private SharedPreferences sharedPreferences;
    private CurrentUser currentUser;
    private StudynetAPI api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySplashBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        // Init retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Utils.API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // Init our api, this will implement the code of all the methods in the interface.
        api = retrofit.create(StudynetAPI.class);
        currentUser = CurrentUser.getInstance();
        //Get the shared preferences.
        sharedPreferences = getApplicationContext().getSharedPreferences(Utils.SHARED_PREFERENCES_USER_DATA, MODE_PRIVATE);
        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        //Load the language specified in settings if specified.
        String language = defaultSharedPreferences.getString(getString(R.string.key_language), "");
        if (!language.isEmpty()) {
            switch (language) {
                case "1":
                    setLocale(this, Locale.ENGLISH);
                    break;
                case "2":
                    setLocale(this, Locale.FRENCH);
                    break;
            }
        }
        // Set light theme as the default theme
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        verifyInternet();

        binding.btnTryAgain.setOnClickListener(v -> verifyInternet());
    }

    private void verifyInternet() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo network = connectivityManager.getActiveNetworkInfo();
        if (network == null) { // No internet
            binding.mainLayout.setBackgroundColor(getColor(R.color.white));
            binding.noInternetMsg.setVisibility(View.VISIBLE);
        } else {
            //Check if the user is already logged in using shared preferences.
            if (sharedPreferences.getBoolean(Utils.SHARED_PREFERENCES_LOGGED_IN, false)) {
                loadUserData();
            } else {
                new Handler().postDelayed(() -> {
                    startActivity(new Intent(SplashActivity.this, OnboardingActivity.class));
                    finish();
                }, 1000);
            }
        }
    }

    /**
     * Loads the saved data of the user from shared preferences.
     */
    private void loadUserData() {
        CurrentUser currentUser = CurrentUser.getInstance();
        //The user is already logged in, we determine which type of user this is.
        //Get the token from shared preferences
        String token = sharedPreferences.getString(Utils.SHARED_PREFERENCES_TOKEN, "");
        currentUser.setToken(token);
        //Send the token to the API to receive the user data.
        Call<JsonObject> call = api.getUserData("Token " + token);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                switch (response.code()) {
                    case Utils.HttpResponses.HTTP_200_OK:
                        JsonObject responseData = response.body();
                        if (responseData != null) {
                            if (responseData.has(Utils.STUDENT_ACCOUNT)) {
                                //It's a student
                                loadStudent(responseData.getAsJsonObject(Utils.STUDENT_ACCOUNT));
                            } else {
                                if (responseData.has(Utils.TEACHER_ACCOUNT)) {
                                    //It's a teacher
                                    loadTeacher(responseData.getAsJsonObject(Utils.TEACHER_ACCOUNT));
                                } else {
                                    if (responseData.has(Utils.ADMIN_ACCOUNT)) {
                                        //It's an administrator
                                        loadAdmin(responseData.getAsJsonObject(Utils.ADMIN_ACCOUNT));
                                    } else {
                                        //Unexpected response from the server.
                                        Toast.makeText(SplashActivity.this, getString(R.string.unknown_error), Toast.LENGTH_SHORT).show();
                                        break;
                                    }
                                }
                            }
                            startActivity(new Intent(SplashActivity.this, NavigationActivity.class));
                            finish();
                        } else {
                            Toast.makeText(SplashActivity.this, getString(R.string.unknown_error), Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case Utils.HttpResponses.HTTP_401_UNAUTHORIZED:
                        //Token is invalid, disconnect the user.
                        SharedPreferences.Editor prefsEditor = sharedPreferences.edit();
                        prefsEditor.putBoolean(Utils.SHARED_PREFERENCES_LOGGED_IN, false);
                        prefsEditor.apply();
                        Toast.makeText(SplashActivity.this, getString(R.string.session_expired), Toast.LENGTH_LONG).show();
                        startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                        finish();
                        break;
                    default:
                        Toast.makeText(SplashActivity.this, getString(R.string.unknown_error), Toast.LENGTH_LONG).show();
                        binding.mainLayout.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
                        binding.noInternetMsg.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                Toast.makeText(SplashActivity.this, getString(R.string.connection_failed), Toast.LENGTH_LONG).show();
                binding.mainLayout.setBackgroundColor(getColor(R.color.white));
                binding.noInternetMsg.setVisibility(View.VISIBLE);

            }
        });
    }

    // Change the device's language
    public static void setLocale(Activity activity, Locale locale) {
        Locale.setDefault(locale);
        Resources resources = activity.getResources();
        Configuration config = resources.getConfiguration();
        config.setLocale(locale);
        resources.updateConfiguration(config, resources.getDisplayMetrics());
    }

    private void loadStudent(JsonObject student) {
        currentUser.setUserType(Utils.STUDENT_ACCOUNT);
        currentUser.setCurrentStudent(Serializers.StudentDeserializer(student));
    }

    private void loadTeacher(JsonObject teacher) {
        currentUser.setUserType(Utils.TEACHER_ACCOUNT);
        currentUser.setCurrentTeacher(Serializers.TeacherDeserializer(teacher));
    }

    private void loadAdmin(JsonObject admin) {
        currentUser.setUserType(Utils.ADMIN_ACCOUNT);
        currentUser.setCurrentAdmin(Serializers.AdminDeserializer(admin));
    }
}