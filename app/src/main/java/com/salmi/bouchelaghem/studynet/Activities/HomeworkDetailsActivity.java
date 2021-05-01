package com.salmi.bouchelaghem.studynet.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.salmi.bouchelaghem.studynet.Models.Homework;
import com.salmi.bouchelaghem.studynet.Utils.Utils;
import com.salmi.bouchelaghem.studynet.databinding.ActivityHomeworkDetailsBinding;

import org.threeten.bp.format.DateTimeFormatter;

import java.text.SimpleDateFormat;

public class HomeworkDetailsActivity extends AppCompatActivity {

    private ActivityHomeworkDetailsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityHomeworkDetailsBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        Intent intent = getIntent();
        Homework homework = intent.getParcelableExtra(Utils.HOMEWORK);

        fillFields(homework);

        binding.btnClose.setOnClickListener(v -> finish());
    }

    private void fillFields(Homework homework) {
        binding.txtSubject.setText(homework.getAssignment().getModuleName());
        binding.txtSubjectCode.setText(homework.getAssignment().getModuleCode());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        binding.txtHomeworkDueDate.setText(formatter.format(homework.getDueDate()));
        binding.txtHomeworkDueHour.setText(homework.getDueTime().toString());

        binding.txtHomeworkTitle.setText(homework.getTitle());
        binding.txtHomeworkDescription.setText(homework.getComment());
    }
}