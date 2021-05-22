package com.salmi.bouchelaghem.studynet.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.salmi.bouchelaghem.studynet.Models.Homework;
import com.salmi.bouchelaghem.studynet.Utils.Utils;
import com.salmi.bouchelaghem.studynet.databinding.ActivityHomeworkDetailsBinding;

import org.threeten.bp.format.DateTimeFormatter;

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
        binding.txtSubject.setText(homework.getModule());
        binding.txtSubjectCode.setText(homework.getModuleType());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        binding.txtHomeworkDueDate.setText(formatter.format(homework.getDueDate()));
        binding.txtHomeworkDueHour.setText(homework.getDueTime());

        binding.txtHomeworkTitle.setText(homework.getTitle());
        binding.txtHomeworkDescription.setText(homework.getComment());
    }
}