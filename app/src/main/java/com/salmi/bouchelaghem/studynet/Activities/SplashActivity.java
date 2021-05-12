package com.salmi.bouchelaghem.studynet.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;

import com.google.gson.Gson;
import com.salmi.bouchelaghem.studynet.Models.Admin;
import com.salmi.bouchelaghem.studynet.Models.Student;
import com.salmi.bouchelaghem.studynet.Models.Teacher;
import com.salmi.bouchelaghem.studynet.R;
import com.salmi.bouchelaghem.studynet.Utils.CurrentUser;
import com.salmi.bouchelaghem.studynet.Utils.StudynetAPI;
import com.salmi.bouchelaghem.studynet.Utils.Utils;
import com.salmi.bouchelaghem.studynet.databinding.ActivitySplashBinding;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SplashActivity extends AppCompatActivity {

    private ActivitySplashBinding binding;
    private SharedPreferences sharedPreferences;
    private boolean loggedIn;
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
                if (sharedPreferences.getBoolean(Utils.SHARED_PREFERENCES_LOGGED_IN,false) && loadUserData())
                {
                    startActivity(new Intent(SplashActivity.this, NavigationActivity.class));
                }
                else
                {
                    startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                }
                finish();
            }, 1000);

        }
    }

    /** Loads the saved data of the user from shared preferences.*/
    private boolean loadUserData()
    {
        CurrentUser currentUser = CurrentUser.getInstance();
        //The user is already logged in, we determine which type of user this is.
        String token = sharedPreferences.getString(Utils.SHARED_PREFERENCES_TOKEN, "");
        //Send the token to the API to receive the user data.
        return false;
//        switch(userType)
//        {
//            case Utils.STUDENT_ACCOUNT:
//                //Load the student data.
//                Student student = gson.fromJson(userData,Student.class);
//                currentUser.setCurrentStudent(student);
//                currentUser.setToken(token);
//                currentUser.setUserType(Utils.STUDENT_ACCOUNT);
//                return true;
//            case Utils.TEACHER_ACCOUNT:
//                //Load the teacher data.
//                Teacher teacher = gson.fromJson(userData,Teacher.class);
//                currentUser.setCurrentTeacher(teacher);
//                currentUser.setToken(token);
//                currentUser.setUserType(Utils.TEACHER_ACCOUNT);
//                return true;
//            case Utils.ADMIN_ACCOUNT:
//                //Load the admin data.
//                Admin admin = gson.fromJson(userData,Admin.class);
//                currentUser.setCurrentAdmin(admin);
//                currentUser.setToken(token);
//                currentUser.setUserType(Utils.ADMIN_ACCOUNT);
//                return true;
//            default:
//                return false;
//        }
//  }
    }
}