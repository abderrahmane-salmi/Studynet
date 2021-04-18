package com.salmi.bouchelaghem.studynet.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.salmi.bouchelaghem.studynet.databinding.ActivityAddHomeworkBinding;
import com.salmi.bouchelaghem.studynet.databinding.ActivityHomeworkDetailsBinding;

public class AddHomeworkActivity extends AppCompatActivity {

    private ActivityAddHomeworkBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityAddHomeworkBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        binding.btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}