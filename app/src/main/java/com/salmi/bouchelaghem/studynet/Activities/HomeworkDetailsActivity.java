package com.salmi.bouchelaghem.studynet.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.salmi.bouchelaghem.studynet.databinding.ActivityHomeworkDetailsBinding;
import com.salmi.bouchelaghem.studynet.databinding.ActivitySignUpBinding;

public class HomeworkDetailsActivity extends AppCompatActivity {

    private ActivityHomeworkDetailsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityHomeworkDetailsBinding.inflate(getLayoutInflater());
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