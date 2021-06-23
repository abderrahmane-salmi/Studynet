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
import com.salmi.bouchelaghem.studynet.Utils.CustomLoadingDialog;
import com.salmi.bouchelaghem.studynet.Utils.StudynetAPI;
import com.salmi.bouchelaghem.studynet.Utils.Utils;
import com.salmi.bouchelaghem.studynet.databinding.FragmentResetPasswordConfirmationBinding;

import org.json.JSONObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@SuppressWarnings("ConstantConditions")
public class ResetPasswordConfirmationFragment extends BottomSheetDialogFragment {

    private FragmentResetPasswordConfirmationBinding binding;
    // Studynet Api
    private StudynetAPI api;
    //Loading dialog
    private CustomLoadingDialog loadingDialog;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentResetPasswordConfirmationBinding.inflate(inflater, container, false);
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
            if (validateConfirmationCode() & validateNewPassword()) {
                String password = binding.txtNewPassword.getEditText().getText().toString();
                String code = binding.txtConfirmationCode.getEditText().getText().toString();
                //Create the json object to send to the api
                JsonObject codeAndNewPasswordJson = new JsonObject();
                codeAndNewPasswordJson.addProperty("password", password);
                codeAndNewPasswordJson.addProperty("token", code);
                //Make the call to the api
                Call<ResponseBody> changePasswordCall = api.confirm_change_password_email(codeAndNewPasswordJson);
                loadingDialog.show();
                changePasswordCall.enqueue(new PasswordResetCallback());
            }
        });

        return view;
    }

    public boolean validateConfirmationCode() {
        String code = binding.txtConfirmationCode.getEditText().getText().toString().trim();

        if (code.isEmpty()) {
            binding.txtConfirmationCode.setError(getString(R.string.empty_code_msg));
            return false;
        } else {
            binding.txtConfirmationCode.setError(null);
            return true;
        }
    }

    public boolean validateNewPassword() {
        String password = binding.txtNewPassword.getEditText().getText().toString().trim();

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
            switch (response.code()) {
                case Utils.HttpResponses.HTTP_200_OK:
                    Toast.makeText(getContext(), getString(R.string.password_changed_successfully), Toast.LENGTH_SHORT).show();
                    loadingDialog.dismiss();
                    dismiss();
                    break;
                case Utils.HttpResponses.HTTP_404_NOT_FOUND:
                    binding.txtConfirmationCode.setError(getString(R.string.invalid_confirmation_code));
                    break;
                default:
                    //Parse the error response and check if it is because the password is too common.
                    try {
                        assert response.errorBody() != null;
                        JSONObject errorBody = new JSONObject(response.errorBody().string());
                        if (errorBody.has("password")) {
                            Toast.makeText(getContext(), getString(R.string.password_too_common), Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        Toast.makeText(getContext(), getString(R.string.unknown_error), Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
            loadingDialog.dismiss();
        }

        @Override
        public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
            Toast.makeText(getContext(), getString(R.string.connection_failed), Toast.LENGTH_SHORT).show();
        }
    }
}