package com.salmi.bouchelaghem.studynet.Fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.salmi.bouchelaghem.studynet.R;

public class AboutUsFragment extends DialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View view = getLayoutInflater().inflate(R.layout.fragment_about_us, null);
        // Init Views
        TextView btnContactDeveloper1 = view.findViewById(R.id.btnContactDeveloper1);
        TextView btnContactDeveloper2 = view.findViewById(R.id.btnContactDeveloper2);
        TextView btnCloseAboutUs = view.findViewById(R.id.btnCloseAboutUs);

        btnContactDeveloper1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        btnContactDeveloper2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        btnCloseAboutUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        builder.setView(view);
        Dialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams params =   dialog.getWindow().getAttributes();
        dialog.getWindow().setAttributes(params);

        return dialog;
    }
}