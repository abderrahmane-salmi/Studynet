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
        Gson gson = new Gson();
        CurrentUser currentUser = CurrentUser.getInstance();
        //The user is already logged in, we determine which type of user this is.
        String userType = sharedPreferences.getString(Utils.SHARED_PREFERENCES_USER_TYPE,"");
        switch(userType)
        {
            case Utils.STUDENT_ACCOUNT:
                //Load the student data.
                String studentJson = sharedPreferences.getString(Utils.SHARED_PREFERENCES_CURRENT_USER, "");
                Student student = gson.fromJson(studentJson,Student.class);
                currentUser.setCurrentStudent(student);
                currentUser.setUserType(Utils.STUDENT_ACCOUNT);
                return true;
            case Utils.TEACHER_ACCOUNT:
                //Load the teacher data.
                String teacherJson = sharedPreferences.getString(Utils.SHARED_PREFERENCES_CURRENT_USER, "");
                Teacher teacher = gson.fromJson(teacherJson,Teacher.class);
                currentUser.setCurrentTeacher(teacher);
                currentUser.setUserType(Utils.TEACHER_ACCOUNT);
                return true;
            case Utils.ADMIN_ACCOUNT:
                //Load the admin data.
                String adminJson = sharedPreferences.getString(Utils.SHARED_PREFERENCES_CURRENT_USER, "");
                Admin admin = gson.fromJson(adminJson,Admin.class);
                currentUser.setCurrentAdmin(admin);
                currentUser.setUserType(Utils.ADMIN_ACCOUNT);
                return true;
            default:
                return false;
        }
    }
}