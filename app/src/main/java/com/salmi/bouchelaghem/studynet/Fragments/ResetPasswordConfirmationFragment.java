package com.salmi.bouchelaghem.studynet.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.salmi.bouchelaghem.studynet.R;
import com.salmi.bouchelaghem.studynet.databinding.FragmentResetPasswordConfirmationBinding;

public class ResetPasswordConfirmationFragment extends BottomSheetDialogFragment {

    private FragmentResetPasswordConfirmationBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentResetPasswordConfirmationBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        binding.btnSave.setOnClickListener(v -> {
            if (validateConfirmationCode() & validateNewPassword()){
                // TODO: Change password
                Toast.makeText(getContext(), "Changed", Toast.LENGTH_SHORT).show();
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
}