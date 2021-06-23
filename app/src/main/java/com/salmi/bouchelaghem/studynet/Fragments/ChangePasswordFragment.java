package com.salmi.bouchelaghem.studynet.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.gson.JsonObject;
import com.salmi.bouchelaghem.studynet.R;
import com.salmi.bouchelaghem.studynet.Utils.CurrentUser;
import com.salmi.bouchelaghem.studynet.Utils.CustomLoadingDialog;
import com.salmi.bouchelaghem.studynet.Utils.StudynetAPI;
import com.salmi.bouchelaghem.studynet.Utils.Utils;
import com.salmi.bouchelaghem.studynet.databinding.FragmentChangePasswordBinding;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@SuppressWarnings("ConstantConditions")
public class ChangePasswordFragment extends BottomSheetDialogFragment {

    private FragmentChangePasswordBinding binding;
    // Studynet Api
    private StudynetAPI api;
    private final CurrentUser currentUser = CurrentUser.getInstance();
    //Loading dialog
    private CustomLoadingDialog loadingDialog;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentChangePasswordBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        // Init retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Utils.API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // Init our api
        api = retrofit.create(StudynetAPI.class);

        //Init loading dialog
        loadingDialog = new CustomLoadingDialog(requireContext());

        binding.btnSave.setOnClickListener(v -> {
            if (validateOldPassword() & validateNewPassword()) {
                String oldPassword = binding.txtOldPassword.getEditText().getText().toString();
                String newPassword = binding.txtNewPassword.getEditText().getText().toString();
                if (!oldPassword.equals(newPassword)) {
                    //Create the json object to send to the api
                    JsonObject oldNewPasswordJson = new JsonObject();
                    oldNewPasswordJson.addProperty("old_password", oldPassword);
                    oldNewPasswordJson.addProperty("new_password", newPassword);

                    //Send the json data to the api
                    Call<ResponseBody> changePasswordCall = api.change_password(oldNewPasswordJson, "Token " + currentUser.getToken());
                    loadingDialog.show();
                    changePasswordCall.enqueue(new PasswordResetCallback());
                } else {
                    Toast.makeText(getActivity(), getString(R.string.old_new_password_same), Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }

    public boolean validateOldPassword() {
        String password = binding.txtOldPassword.getEditText().getText().toString();

        if (password.isEmpty()) {
            binding.txtOldPassword.setError(getString(R.string.empty_password_msg));
            return false;
        } else if (password.length() < 8) {
            binding.txtOldPassword.setError(getString(R.string.password_msg2));
            return false;
        } else {
            binding.txtOldPassword.setError(null);
            return true;
        }
    }

    public boolean validateNewPassword() {
        String password = binding.txtNewPassword.getEditText().getText().toString();

        if (password.isEmpty()) {
            binding.txtNewPassword.setError(getString(R.string.empty_password_msg));
            return false;
        } else if (password.length() < 8) {
            binding.txtNewPassword.setError(getString(R.string.password_msg2));
            return false;
        } else {
            binding.txtNewPassword.setError(null);
            return true;
        }
    }

    private class PasswordResetCallback implements Callback<ResponseBody> {
        @Override
        public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
            if (response.code() == Utils.HttpResponses.HTTP_200_OK) {
                Toast.makeText(getActivity(), getString(R.string.password_changed_successfully), Toast.LENGTH_SHORT).show();
                loadingDialog.dismiss();
                dismiss();
            } else {
                Toast.makeText(getActivity(), getString(R.string.invalid_old_password), Toast.LENGTH_SHORT).show();
                loadingDialog.dismiss();
            }

        }

        @Override
        public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
            Toast.makeText(getActivity(), getString(R.string.connection_failed), Toast.LENGTH_SHORT).show();
            loadingDialog.dismiss();
        }
    }
}