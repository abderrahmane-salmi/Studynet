package com.salmi.bouchelaghem.studynet.Fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.salmi.bouchelaghem.studynet.R;

@SuppressLint("IntentReset")
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

        btnContactDeveloper1.setOnClickListener(v -> {
            Intent emailIntent = new Intent(Intent.ACTION_SEND);
            // The intent does not have a URI, so declare the "text/plain" MIME type
            emailIntent.setData(Uri.parse("mailto:"));
            emailIntent.setType("text/plain");
            emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{getString(R.string.developer_email1)}); // recipient
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name) + ": Bug Report or Feature Request");
            try {
                startActivity(emailIntent);
            } catch (ActivityNotFoundException exception) {
                // There is no third app to open our intent, so do nothing
            }
        });

        btnContactDeveloper2.setOnClickListener(v -> {
            Intent emailIntent = new Intent(Intent.ACTION_SEND);
            // The intent does not have a URI, so declare the "text/plain" MIME type
            emailIntent.setData(Uri.parse("mailto:"));
            emailIntent.setType("text/plain");
            emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{getString(R.string.developer_email2)}); // recipient
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name) + ": Bug Report or Feature Request");
            try {
                startActivity(emailIntent);
            } catch (ActivityNotFoundException exception) {
                // There is no third app to open our intent, so do nothing
            }
        });

        btnCloseAboutUs.setOnClickListener(v -> dismiss());

        builder.setView(view);
        Dialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        dialog.getWindow().setAttributes(params);

        return dialog;
    }
}