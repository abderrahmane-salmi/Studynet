package com.salmi.bouchelaghem.studynet.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.JsonObject;
import com.salmi.bouchelaghem.studynet.R;
import com.salmi.bouchelaghem.studynet.Utils.CurrentUser;
import com.salmi.bouchelaghem.studynet.Utils.CustomLoadingDialog;
import com.salmi.bouchelaghem.studynet.Utils.StudynetAPI;
import com.salmi.bouchelaghem.studynet.Utils.Utils;
import com.salmi.bouchelaghem.studynet.databinding.ActivityNavigationBinding;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NavigationActivity extends AppCompatActivity {

    private ActivityNavigationBinding binding;
    private final CurrentUser currentUser = CurrentUser.getInstance();
    private StudynetAPI api;
    public ImageView btnFilter;
    private SharedPreferences sharedPreferences;

    //Loading dialog
    private CustomLoadingDialog loadingDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityNavigationBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        //Get the shared preferences.
        sharedPreferences = getApplicationContext().getSharedPreferences(Utils.SHARED_PREFERENCES_USER_DATA,MODE_PRIVATE);
        // Init retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Utils.API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // Init our api, this will implement the code of all the methods in the interface
        api = retrofit.create(StudynetAPI.class);

        //Init loading dialog
        loadingDialog = new CustomLoadingDialog(this);
        btnFilter = binding.btnFilter;

        if (currentUser.getUserType().equals(Utils.TEACHER_ACCOUNT)){
            // If its a teacher then show the teacher's drawer menu
            binding.navigationView.getMenu().clear();
            binding.navigationView.inflateMenu(R.menu.drawer_teacher_menu);
        } else if (currentUser.getUserType().equals(Utils.ADMIN_ACCOUNT)){
            // If its a admin then show the admin's drawer menu
            binding.navigationView.getMenu().clear();
            binding.navigationView.inflateMenu(R.menu.drawer_admin_menu);
        }
        // If its a student then the default drawer menu will do

        binding.btnOpenDrawer.setOnClickListener(v -> binding.drawerLayout.openDrawer(GravityCompat.START));

        NavController navController = Navigation.findNavController(this, R.id.fragment);
        NavigationUI.setupWithNavController(binding.navigationView, navController);

        // Change toolbar title to the fragment's title
        navController.addOnDestinationChangedListener((controller, destination, arguments) -> binding.toolBarTitle.setText(destination.getLabel()));

        MenuItem btnLogout = binding.navigationView.getMenu().findItem(R.id.nav_logout);
        btnLogout.setOnMenuItemClickListener(item -> {

            loadingDialog.show();
            if(currentUser.getUserType().equals(Utils.TEACHER_ACCOUNT))
            {
                //This user is a teacher, unregister this device from the backend.
                //Get the FCM token for this device
                FirebaseMessaging.getInstance().getToken().addOnSuccessListener(FCMToken -> {
                    //Send the FCMToken to the backend to enable targeted notifications for this teacher.
                    JsonObject fcmTokenJson = new JsonObject();
                    fcmTokenJson.addProperty("FCM_token",FCMToken);
                    Call<ResponseBody> unregisterFcmCall = api.unregisterFCM(fcmTokenJson,"Token " + currentUser.getToken());
                    unregisterFcmCall.enqueue(new FCMTokenUnregisterCallback());
                });
            }
            else
            {
                Call<ResponseBody> logout = api.logout("Token " + currentUser.getToken());
                logout.enqueue(new logoutCallback());
            }
            return true;
        });
    }

    /** Callback logic for the logout process.*/
    private class logoutCallback implements Callback<ResponseBody>
    {

        @Override
        public void onResponse(@NonNull Call<ResponseBody> call, Response<ResponseBody> response) {
            switch(response.code())
            {

                case Utils.HttpResponses.HTTP_204_NO_CONTENT: //Logout successful.
                case Utils.HttpResponses.HTTP_401_UNAUTHORIZED: //Expired token, logout anyway since this token cannot be used.
                    if(currentUser.getUserType().equals(Utils.STUDENT_ACCOUNT))
                    {
                        //This user is a student, unsubscribe this device from his section's notifications.
                        String section = currentUser.getCurrentStudent().getSection().getCode();
                        FirebaseMessaging.getInstance().unsubscribeFromTopic(section.replace(' ', '_'));
                    }
                    //Log this user out.
                    currentUser.logout();
                    //Save that the user is no longer logged in locally.
                    SharedPreferences.Editor prefsEditor = sharedPreferences.edit();
                    prefsEditor.putBoolean(Utils.SHARED_PREFERENCES_LOGGED_IN,false);
                    prefsEditor.apply();

                    //Take the user to the login page.
                    Toast.makeText(NavigationActivity.this, getString(R.string.logout_msg), Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(NavigationActivity.this, LoginActivity.class));
                    loadingDialog.dismiss();
                    finish();
                    break;
                default:
                    Toast.makeText(NavigationActivity.this, getString(R.string.unknown_error), Toast.LENGTH_LONG).show();
                    loadingDialog.dismiss();
                    break;
            }
        }

        @Override
        public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
            Toast.makeText(NavigationActivity.this, getString(R.string.could_not_logout), Toast.LENGTH_LONG).show();
            loadingDialog.dismiss();

        }
    }
    public class FCMTokenUnregisterCallback<T> implements Callback<T>
    {

        @Override
        public void onResponse(@NonNull Call<T> call, @NonNull Response<T> response) {
            //Unregistered teacher's FCM token, we can disconnect him.
            Call<ResponseBody> logout = api.logout("Token " + currentUser.getToken());
            logout.enqueue(new logoutCallback());
        }

        @Override
        public void onFailure(@NonNull Call<T> call, @NonNull Throwable t) {
            Toast.makeText(NavigationActivity.this, getString(R.string.could_not_logout), Toast.LENGTH_LONG).show();
            loadingDialog.dismiss();
        }
    }
}