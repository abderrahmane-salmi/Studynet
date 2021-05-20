package com.salmi.bouchelaghem.studynet.Fragments;

import android.os.Bundle;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.salmi.bouchelaghem.studynet.R;
import com.salmi.bouchelaghem.studynet.databinding.FragmentChangePasswordBinding;

public class ChangePasswordFragment extends BottomSheetDialogFragment {

    private FragmentChangePasswordBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentChangePasswordBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        binding.btnSave.setOnClickListener(v -> {
            if (validateOldPassword() & validateNewPassword()){
                // TODO: Change password
                Toast.makeText(getContext(), "Changed", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    public boolean validateOldPassword() {
        String password = binding.txtOldPassword.getEditText().getText().toString().trim();

        if (password.isEmpty()) {
            binding.txtOldPassword.setError(getString(R.string.empty_password_msg));
            return false;
        } else if (password.length() < 6) {
            binding.txtOldPassword.setError(getString(R.string.password_msg2));
            return false;
        } else {
            binding.txtOldPassword.setError(null);
            return true;
        }
    }

    public boolean validateNewPassword() {
        String password = binding.txtNewPassword.getEditText().getText().toString().trim();

        if (password.isEmpty()) {
            binding.txtNewPassword.setError(getString(R.string.empty_password_msg));
            return false;
        } else if (password.length() < 6) {
            binding.txtNewPassword.setError(getString(R.string.password_msg2));
            return false;
        } else {
            binding.txtNewPassword.setError(null);
            return true;
        }
    }
}