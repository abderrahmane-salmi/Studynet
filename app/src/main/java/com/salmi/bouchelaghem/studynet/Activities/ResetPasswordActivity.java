package com.salmi.bouchelaghem.studynet.Activities;

import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.JsonObject;
import com.salmi.bouchelaghem.studynet.Fragments.ResetPasswordConfirmationFragment;
import com.salmi.bouchelaghem.studynet.R;
import com.salmi.bouchelaghem.studynet.Utils.CustomLoadingDialog;
import com.salmi.bouchelaghem.studynet.Utils.StudynetAPI;
import com.salmi.bouchelaghem.studynet.Utils.Utils;
import com.salmi.bouchelaghem.studynet.databinding.ActivityResetPasswordBinding;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@SuppressWarnings("ConstantConditions")
public class ResetPasswordActivity extends AppCompatActivity {

    private ActivityResetPasswordBinding binding;
    // Studynet Api
    private StudynetAPI api;
    //Loading dialog
    private CustomLoadingDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityResetPasswordBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        // Init retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Utils.API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // Init our api
        api = retrofit.create(StudynetAPI.class);

        //Init loading dialog
        loadingDialog = new CustomLoadingDialog(ResetPasswordActivity.this);

        binding.btnGoBackFromReset.setOnClickListener(v -> finish());

        binding.btnResetPassword.setOnClickListener(v -> {
            if (validateEmail()) {
                String email = binding.txtEmail.getEditText().getText().toString().trim();
                //Create the json object to send to the api
                JsonObject emailJson = new JsonObject();
                emailJson.addProperty("email", email);
                Call<ResponseBody> changePasswordEmailCall = api.change_password_email(emailJson);
                loadingDialog.show();
                changePasswordEmailCall.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                        if (response.code() == Utils.HttpResponses.HTTP_200_OK) {
                            Toast.makeText(ResetPasswordActivity.this, getString(R.string.password_reset_email_sent), Toast.LENGTH_LONG).show();
                            showConfirmationFragment();
                        } else {
                            binding.txtEmail.setError(getString(R.string.email_does_not_exist));
                        }
                        loadingDialog.dismiss();
                    }

                    @Override
                    public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                        Toast.makeText(ResetPasswordActivity.this, getString(R.string.connection_failed), Toast.LENGTH_SHORT).show();
                        loadingDialog.dismiss();
                    }
                });

            }
        });

        binding.btnConfirmReset.setOnClickListener(v -> showConfirmationFragment());
    }

    private void showConfirmationFragment() {
        ResetPasswordConfirmationFragment fragment = new ResetPasswordConfirmationFragment();
        fragment.show(getSupportFragmentManager(), "ResetPasswordConfirmationFragment");
    }

    public boolean validateEmail() {
        String email = binding.txtEmail.getEditText().getText().toString().trim();

        if (email.isEmpty()) {
            binding.txtEmail.setError(getString(R.string.email_msg1));
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.txtEmail.setError(getString(R.string.email_msg2));
            return false;
        } else {
            binding.txtEmail.setError(null);
            return true;
        }
    }
}