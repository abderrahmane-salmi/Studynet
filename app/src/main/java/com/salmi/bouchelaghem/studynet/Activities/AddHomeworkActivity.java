package com.salmi.bouchelaghem.studynet.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.salmi.bouchelaghem.studynet.Models.Section;
import com.salmi.bouchelaghem.studynet.Models.Teacher;
import com.salmi.bouchelaghem.studynet.R;
import com.salmi.bouchelaghem.studynet.Utils.CurrentUser;
import com.salmi.bouchelaghem.studynet.Utils.TestAPI;
import com.salmi.bouchelaghem.studynet.Utils.Utils;
import com.salmi.bouchelaghem.studynet.databinding.ActivityAddHomeworkBinding;
import com.salmi.bouchelaghem.studynet.databinding.ActivityHomeworkDetailsBinding;

import java.util.ArrayList;
import java.util.List;

public class AddHomeworkActivity extends AppCompatActivity {

    private ActivityAddHomeworkBinding binding;

    // Section
    private List<Section> sections;

    TestAPI testAPI = TestAPI.getInstance();
    private Teacher currentTeacher;
    private final CurrentUser currentUser = CurrentUser.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityAddHomeworkBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        // Get current teacher
        currentTeacher = currentUser.getCurrentTeacher();

        // Get the action type (Add/Update)
        Intent intent = getIntent();
        String action = intent.getStringExtra(Utils.ACTION);

        setupSectionsSpinner(currentTeacher.getId());

        switch (action){
            case Utils.ACTION_ADD:
                // When the user clicks on save we create a new homework
                binding.btnSave.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });

                // Spinners
                binding.classSection.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    }
                });
                break;
            case Utils.ACTION_UPDATE:
                break;
        }

        binding.btnClose.setOnClickListener(v -> finish());
    }

    private void setupSectionsSpinner(int id) {
        sections = testAPI.getSections();

        // Get only names
        List<String> sectionsNames = new ArrayList<>();
        for (Section section : sections) {
            sectionsNames.add(section.getCode());
        }
        if (!sectionsNames.isEmpty()) {
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(AddHomeworkActivity.this, R.layout.dropdown_item, sectionsNames);
            binding.classSection.setAdapter(arrayAdapter);
        }
    }
}