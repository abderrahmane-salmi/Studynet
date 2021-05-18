package com.salmi.bouchelaghem.studynet.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.salmi.bouchelaghem.studynet.Models.Session;
import com.salmi.bouchelaghem.studynet.R;
import com.salmi.bouchelaghem.studynet.Utils.TestAPI;
import com.salmi.bouchelaghem.studynet.Utils.Utils;
import com.salmi.bouchelaghem.studynet.databinding.ActivityClassDetailsBinding;
import com.salmi.bouchelaghem.studynet.databinding.ActivityHomeworkDetailsBinding;

import java.util.Arrays;
import java.util.List;

public class ClassDetailsActivity extends AppCompatActivity {

    private ActivityClassDetailsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityClassDetailsBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        // Get the session's id
        Intent intent = getIntent();
        int id = intent.getIntExtra(Utils.ID, 0);

        fillFields(id);

        binding.btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);

        binding.btnCopyMeetingLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipData clipData = ClipData.newPlainText(Utils.MEETING_LINK, binding.classMeetingLink.getText());
                clipboard.setPrimaryClip(clipData);
                Toast.makeText(ClassDetailsActivity.this, getString(R.string.meeting_link_copied), Toast.LENGTH_SHORT).show();
            }
        });

        binding.btnCopyMeetingNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipData clipData = ClipData.newPlainText(Utils.MEETING_NUMBER, binding.classMeetingNumber.getText());
                clipboard.setPrimaryClip(clipData);
                Toast.makeText(ClassDetailsActivity.this, getString(R.string.meeting_number_copied), Toast.LENGTH_SHORT).show();
            }
        });

        binding.btnCopyMeetingPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipData clipData = ClipData.newPlainText(Utils.MEETING_PASSWORD, binding.classMeetingPassword.getText());
                clipboard.setPrimaryClip(clipData);
                Toast.makeText(ClassDetailsActivity.this, getString(R.string.meeting_password_copied), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fillFields(int id) {

        // Get the session's info from api
        Session session = new Session();
        for (Session s: TestAPI.getInstance().getSessions()){
            if (s.getId() == id){
                session = s;
            }
        }

        // Module
        binding.classSubject.setText(session.getModule());
        binding.classType.setText(session.getModuleType());

        // Day
        List<String> days = Arrays.asList(getResources().getStringArray(R.array.days));
        binding.classDay.setText(days.get(session.getDay()-1));

        // Time
        binding.classStartHour.setText(session.getStartTime().toString());
        binding.classEndHour.setText(session.getEndTime().toString());

        // Section
        binding.classSection.setText(session.getSection());

        // Session's groups
        if (session.getConcernedGroups().size() > 1){ // There are more then 1 grp
            int nbGroups = session.getConcernedGroups().size();
            binding.classGroup.setText("");
            for (int grp=0; grp<nbGroups-1; grp++){
                binding.classGroup.append(String.valueOf(grp+1) + ", ");
            }
            binding.classGroup.append(String.valueOf(nbGroups)); // The last group doesn't have a ',' after it

        } else { // There is only one grp
            binding.classGroup.setText(String.valueOf(session.getConcernedGroups().get(0)));
        }

        // Meeting info
        // Link
        binding.classMeetingLink.setText(session.getMeetingLink());

        // Other meeting info
        if (session.getMeetingNumber() != null && session.getMeetingPassword() != null){
            // Show other meeting info
            binding.otherMeetingInfoGroup.setVisibility(View.VISIBLE);

            binding.classMeetingNumber.setText(session.getMeetingNumber());
            binding.classMeetingPassword.setText(session.getMeetingPassword());
        }

    }
}