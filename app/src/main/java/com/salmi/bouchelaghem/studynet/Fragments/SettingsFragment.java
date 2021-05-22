package com.salmi.bouchelaghem.studynet.Fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.salmi.bouchelaghem.studynet.Activities.LoginActivity;
import com.salmi.bouchelaghem.studynet.Activities.NavigationActivity;
import com.salmi.bouchelaghem.studynet.R;
import com.salmi.bouchelaghem.studynet.Utils.CurrentUser;
import com.salmi.bouchelaghem.studynet.Utils.CustomLoadingDialog;
import com.salmi.bouchelaghem.studynet.Utils.StudynetAPI;
import com.salmi.bouchelaghem.studynet.Utils.Utils;

import java.util.Locale;
import java.util.Objects;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SettingsFragment extends PreferenceFragmentCompat {

    // Studynet Api
    private StudynetAPI api;
    //Current user
    private final CurrentUser currentUser = CurrentUser.getInstance();
    //Shared preferences
    private SharedPreferences sharedPreferences;
    //Loading dialog
    private CustomLoadingDialog loadingDialog;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.prefrences, rootKey);

        // Init Views
        ListPreference languageList = findPreference(getString(R.string.key_language));
        Preference btnReportBugs = findPreference(getString(R.string.key_report_bugs));
        Preference btnChangePassword = findPreference(getString(R.string.key_change_password));
        Preference btnLogoutAll = findPreference(getString(R.string.key_logout_all));

        // Hide filter button
        NavigationActivity context = (NavigationActivity) getActivity();
        assert context != null;
        context.btnFilter.setVisibility(View.GONE);

        //Init loading dialog
        loadingDialog = new CustomLoadingDialog(requireContext());
        //Get the shared preferences.
        sharedPreferences = requireContext().getSharedPreferences(Utils.SHARED_PREFERENCES_USER_DATA, Context.MODE_PRIVATE);
        // Init retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Utils.API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // Init our api, this will implement the code of all the methods in the interface.
        api = retrofit.create(StudynetAPI.class);
        // TODO: Get the selected language from shared prefs (if the user already chosen a language) and set it in the languages list

        // Language List
        assert languageList != null;
        languageList.setOnPreferenceChangeListener((preference, newValue) -> {
            String selectedLanguage = (String) newValue;
            if (preference.getKey().equals(getString(R.string.key_language))){
                switch (selectedLanguage){
                    case "1":
                        // TODO: Remove this toast in production
                        Toast.makeText(requireContext(), "EN", Toast.LENGTH_SHORT).show();
                        setLocale(requireActivity(), Locale.ENGLISH);
                        break;
                    case "2":
                        Toast.makeText(requireContext(), "FR", Toast.LENGTH_SHORT).show();
                        // TODO: uncomment this when we add french language to the app
                        setLocale(requireActivity(), Locale.FRENCH);
                        break;
                }
                // TODO: Save the selected language to shared prefs and then read it from the splash screen every time
                /*
                Use the default prefs to save the language:
                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(requireContext());
                 */
            }

            return true;
        });

        // Buttons
        assert btnReportBugs != null;
        btnReportBugs.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @SuppressLint("IntentReset")
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent emailIntent = new Intent(Intent.ACTION_SEND);
                // The intent does not have a URI, so declare the "text/plain" MIME type
                emailIntent.setData(Uri.parse("mailto:"));
                emailIntent.setType("text/plain");
                emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{getString(R.string.developer_email1)}); // recipient
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name)+": Bug Report");
                try {
                    startActivity(emailIntent);
                } catch (ActivityNotFoundException exception){
                    // There is no third app to open our intent, so do nothing
                }
                return true;
            }
        });

        assert btnChangePassword != null;
        btnChangePassword.setOnPreferenceClickListener(preference -> {
            NavController navController = Navigation.findNavController(requireActivity(), R.id.fragment);
            if (Objects.requireNonNull(navController.getCurrentDestination()).getId() != R.id.nav_change_password){
                navController.navigate(R.id.action_nav_settings_to_changePasswordFragment);
            }
            return true;
        });

        assert btnLogoutAll != null;
        btnLogoutAll.setOnPreferenceClickListener(preference -> {
            // TODO: Logout from all sessions
            Call<ResponseBody> logoutAllCall = api.logoutAll(currentUser.getToken());
            loadingDialog.show();
            logoutAllCall.enqueue(new LogoutAllCallback());
            return true;
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

    private class LogoutAllCallback implements Callback<ResponseBody>
    {

        @Override
        public void onResponse(@NonNull Call<ResponseBody> call, Response<ResponseBody> response) {
            switch(response.code())
            {

                case Utils.HttpResponses.HTTP_204_NO_CONTENT: //Logout successful.
                case Utils.HttpResponses.HTTP_401_UNAUTHORIZED: //Expired token, logout anyway since this token cannot be used.
                    currentUser.logout();
                    //Save that the user is no longer logged in locally.
                    SharedPreferences.Editor prefsEditor = sharedPreferences.edit();
                    prefsEditor.putBoolean(Utils.SHARED_PREFERENCES_LOGGED_IN,false);
                    prefsEditor.apply();
                    //Take the user to the login page.
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                    Toast.makeText(getActivity(), getString(R.string.logout_all_sucess), Toast.LENGTH_SHORT).show();
                    loadingDialog.dismiss();
                    getActivity().finish();
                    break;
                default:
                    Toast.makeText(getActivity(), getString(R.string.unknown_error), Toast.LENGTH_LONG).show();
                    loadingDialog.dismiss();
                    break;
            }

        }

        @Override
        public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
            Toast.makeText(getActivity(), getString(R.string.connection_failed), Toast.LENGTH_LONG).show();
            loadingDialog.dismiss();
        }
    }
}
