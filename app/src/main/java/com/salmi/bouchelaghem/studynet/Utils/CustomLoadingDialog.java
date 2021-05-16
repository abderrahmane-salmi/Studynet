package com.salmi.bouchelaghem.studynet.Utils;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.NonNull;

import com.salmi.bouchelaghem.studynet.R;

public class CustomLoadingDialog extends Dialog {
    public CustomLoadingDialog(@NonNull Context context) {
        super(context);

        WindowManager.LayoutParams params = getWindow().getAttributes();

        params.gravity = Gravity.CENTER_HORIZONTAL;
        getWindow().setAttributes(params);
        setTitle(null);
        setCancelable(false);
        setOnCancelListener(null);
        View view = View.inflate(context, R.layout.loading_layout, null);
        setContentView(view);
    }
}
