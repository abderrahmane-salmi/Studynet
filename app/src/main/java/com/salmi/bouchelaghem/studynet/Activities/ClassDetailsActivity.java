package com.salmi.bouchelaghem.studynet.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.salmi.bouchelaghem.studynet.R;
import com.salmi.bouchelaghem.studynet.databinding.ActivityClassDetailsBinding;
import com.salmi.bouchelaghem.studynet.databinding.ActivityHomeworkDetailsBinding;

public class ClassDetailsActivity extends AppCompatActivity {

    private ActivityClassDetailsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityClassDetailsBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        binding.btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        binding.btnCopyMeetingLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ClassDetailsActivity.this, getString(R.string.meeting_link_copied), Toast.LENGTH_SHORT).show();
            }
        });

        binding.btnCopyMeetingNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ClassDetailsActivity.this, getString(R.string.meeting_number_copied), Toast.LENGTH_SHORT).show();
            }
        });

        binding.btnCopyMeetingPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ClassDetailsActivity.this, getString(R.string.meeting_password_copied), Toast.LENGTH_SHORT).show();
            }
        });
    }
}