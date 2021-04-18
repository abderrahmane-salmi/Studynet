package com.salmi.bouchelaghem.studynet.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.salmi.bouchelaghem.studynet.R;
import com.salmi.bouchelaghem.studynet.Utils.CurrentUser;
import com.salmi.bouchelaghem.studynet.Utils.Utils;
import com.salmi.bouchelaghem.studynet.databinding.ActivityAddClassBinding;
import com.salmi.bouchelaghem.studynet.databinding.ActivityNavigationBinding;

public class NavigationActivity extends AppCompatActivity {

    private ActivityNavigationBinding binding;
    private CurrentUser currentUser = CurrentUser.getInstance();

    public ImageView btnFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityNavigationBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        btnFilter = binding.btnFilter;

        if (currentUser.getUserType().equals(Utils.TEACHER_ACCOUNT)){
            binding.navigationView.getMenu().clear();
            binding.navigationView.inflateMenu(R.menu.drawer_teacher_menu);
        } else if (currentUser.getUserType().equals(Utils.ADMIN_ACCOUNT)){
            binding.navigationView.getMenu().clear();
            binding.navigationView.inflateMenu(R.menu.drawer_teacher_menu);
        }

        binding.btnOpenDrawer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        NavController navController = Navigation.findNavController(this, R.id.fragment);
        NavigationUI.setupWithNavController(binding.navigationView, navController);

        navController.addOnDestinationChangedListener(new NavController.OnDestinationChangedListener() {
            @Override
            public void onDestinationChanged(@NonNull NavController controller, @NonNull NavDestination destination, @Nullable Bundle arguments) {
                binding.toolBarTitle.setText(destination.getLabel());
            }
        });

        MenuItem btnLogout = binding.navigationView.getMenu().findItem(R.id.nav_logout);
        btnLogout.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                Toast.makeText(NavigationActivity.this, getString(R.string.logout_msg), Toast.LENGTH_SHORT).show();
                startActivity(new Intent(NavigationActivity.this, LoginActivity.class));
                finish();

                return true;
            }
        });
    }
}