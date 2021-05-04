package com.salmi.bouchelaghem.studynet.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.salmi.bouchelaghem.studynet.R;
import com.salmi.bouchelaghem.studynet.Utils.CurrentUser;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityNavigationBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        // Init retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Utils.API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // Init our api, this will implement the code of all the methods in the interface
        api = retrofit.create(StudynetAPI.class);

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

            Call<ResponseBody> logout = api.logout("Token " + currentUser.getToken());
            logout.enqueue(new logoutCallback());
            return true;
        });
    }

    /** Callback logic for the logout process.*/
    private class logoutCallback implements Callback<ResponseBody>
    {

        @Override
        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
            switch(response.code())
            {

                case Utils.HttpResponses.HTTP_204_NO_CONTENT: //Logout successful.
                case Utils.HttpResponses.HTTP_401_UNAUTHORIZED: //Expired token, logout anyway since this token cannot be used.
                    currentUser.logout();
                    Toast.makeText(NavigationActivity.this, getString(R.string.logout_msg), Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(NavigationActivity.this, LoginActivity.class));
                    finish();
                    break;
                default:
                    Toast.makeText(NavigationActivity.this, getString(R.string.unknown_error), Toast.LENGTH_LONG).show();
                    break;
            }
        }

        @Override
        public void onFailure(Call<ResponseBody> call, Throwable t) {
            Toast.makeText(NavigationActivity.this, getString(R.string.could_not_logout), Toast.LENGTH_LONG).show();

        }
    }
}