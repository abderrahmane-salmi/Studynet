package com.salmi.bouchelaghem.studynet.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;

import com.salmi.bouchelaghem.studynet.R;
import com.salmi.bouchelaghem.studynet.databinding.ActivityAddClassBinding;

import java.util.ArrayList;
import java.util.List;

public class AddClassActivity extends AppCompatActivity {

    private boolean otherMeetingFields = false;
    private ActivityAddClassBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityAddClassBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        binding.btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        binding.btnShowOtherMeetingFields.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!otherMeetingFields){
                    otherMeetingFields = true;
                    binding.meetingNumberLayout.setVisibility(View.VISIBLE);
                    binding.meetingPasswordLayout.setVisibility(View.VISIBLE);
                    binding.btnShowOtherMeetingFields.setImageResource(R.drawable.ic_arrow_up);
                } else {
                    otherMeetingFields = false;
                    binding.meetingNumberLayout.setVisibility(View.GONE);
                    binding.meetingPasswordLayout.setVisibility(View.GONE);
                    binding.btnShowOtherMeetingFields.setImageResource(R.drawable.ic_arrow_down);
                }
            }
        });

        List<String> list = new ArrayList<>();
        list.add("First");
        list.add("Second");
        list.add("Third");
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, R.layout.dropdown_item, list);

        binding.newClassSection.setAdapter(arrayAdapter);
        binding.newClassGroup.setAdapter(arrayAdapter);
        binding.newClassType.setAdapter(arrayAdapter);
        binding.newClassDay.setAdapter(arrayAdapter);
    }
}