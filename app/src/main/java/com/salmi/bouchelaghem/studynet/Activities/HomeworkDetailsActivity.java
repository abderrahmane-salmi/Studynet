package com.salmi.bouchelaghem.studynet.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.salmi.bouchelaghem.studynet.Models.Homework;
import com.salmi.bouchelaghem.studynet.R;
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
        binding.txtSubjectType.setText(homework.getModuleType());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        binding.txtHomeworkDueDate.setText(formatter.format(homework.getLocalDateDueDate()));
        binding.txtHomeworkDueTime.setText(homework.getLocalTimeDueTime().toString());

        binding.txtHomeworkSection.setText(homework.getSection());
        // Homework's groups
        if (homework.getConcernedGroups().size() > 1) { // There are more then 1 grp
            int nbGroups = homework.getConcernedGroups().size();
            binding.txtHomeworksGroups.setText("");
            for (int i = 0; i < nbGroups - 1; i++) {
                binding.txtHomeworksGroups.append(homework.getConcernedGroups().get(i) + ", ");
            }
            binding.txtHomeworksGroups.append(String.valueOf(homework.getConcernedGroups().get(nbGroups - 1))); // The last group doesn't have a ',' after it

            binding.textView3.setText(R.string.groups);
        } else { // There is only one grp
            binding.txtHomeworksGroups.setText(String.valueOf(homework.getConcernedGroups().get(0)));
        }

        binding.txtHomeworkTeacher.setText(homework.getTeacherName());
        binding.txtHomeworkTeacherEmail.setText(homework.getTeacherEmail());

        binding.txtHomeworkTitle.setText(homework.getTitle());
        binding.txtHomeworkDescription.setText(homework.getComment());
    }
}