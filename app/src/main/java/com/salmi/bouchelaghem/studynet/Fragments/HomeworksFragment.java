package com.salmi.bouchelaghem.studynet.Fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;

import com.google.android.material.button.MaterialButton;
import com.salmi.bouchelaghem.studynet.Activities.AddHomeworkActivity;
import com.salmi.bouchelaghem.studynet.Activities.HomeworkDetailsActivity;
import com.salmi.bouchelaghem.studynet.Activities.NavigationActivity;
import com.salmi.bouchelaghem.studynet.R;
import com.salmi.bouchelaghem.studynet.Utils.CurrentUser;
import com.salmi.bouchelaghem.studynet.Utils.Utils;
import com.salmi.bouchelaghem.studynet.databinding.ActivityAddClassBinding;
import com.salmi.bouchelaghem.studynet.databinding.FragmentHomeworksBinding;

import java.util.ArrayList;
import java.util.List;

public class HomeworksFragment extends Fragment {

    private FragmentHomeworksBinding binding;
    private CurrentUser currentUser = CurrentUser.getInstance();

    private boolean isChecked = false;

    private Dialog dialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentHomeworksBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        // Show the filter button
        NavigationActivity context = (NavigationActivity) getActivity();
        assert context != null;
        context.btnFilter.setVisibility(View.VISIBLE);

        // If the user is a teacher or an admin then show the filter button
        if (currentUser.getUserType().equals(Utils.TEACHER_ACCOUNT) || currentUser.getUserType().equals(Utils.ADMIN_ACCOUNT)){

            binding.noHomeworksMsg.setVisibility(View.GONE);
            binding.selectSectionMsg.setVisibility(View.VISIBLE);
            binding.btnAdd.setVisibility(View.VISIBLE);

            binding.btnAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(context, AddHomeworkActivity.class));
                }
            });

            context.btnFilter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    View view = View.inflate(context, R.layout.popup_teacher_homeworks_filter, null);
                    // Init Views
                    ImageView btnCloseFilter = view.findViewById(R.id.btnCloseFilter);
                    AutoCompleteTextView filterTimetableSection = view.findViewById(R.id.filterHomeworksSection);
                    AutoCompleteTextView filterHomeworksSubject = view.findViewById(R.id.filterHomeworksSubject);
                    MaterialButton btnApplyFilter = view.findViewById(R.id.btnApplyFilter);

                    // Init sections & subjects list
                    List<String> list = new ArrayList<>();
                    list.add("First");
                    list.add("Second");
                    list.add("Third");
                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(context, R.layout.dropdown_item, list);
                    filterTimetableSection.setAdapter(arrayAdapter);
                    filterHomeworksSubject.setAdapter(arrayAdapter);

                    // TODO: restoreFilterState(); // set the filter values to the last filter applied

                    // Init Buttons
                    btnApplyFilter.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            binding.selectSectionMsg.setVisibility(View.GONE);
                        }
                    });

                    btnCloseFilter.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });

                    builder.setView(view);
                    dialog = builder.create(); // creating our dialog
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    dialog.show();
                    // Show rounded corners
                    WindowManager.LayoutParams params =   dialog.getWindow().getAttributes();
                    dialog.getWindow().setAttributes(params);
                }
            });

        } else if (currentUser.getUserType().equals(Utils.STUDENT_ACCOUNT)){ // If its a student

            context.btnFilter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    View view = View.inflate(context, R.layout.popup_student_homworks_filter, null);
                    // Init Views
                    ImageView btnCloseFilter = view.findViewById(R.id.btnCloseFilter);
                    AutoCompleteTextView filterHomeworksDate = view.findViewById(R.id.filterHomeworksDate);
                    MaterialButton btnApplyFilter = view.findViewById(R.id.btnApplyFilter);

                    // Init sections & subjects list
                    List<String> list = new ArrayList<>();
                    list.add(Utils.YESTERDAY);
                    list.add(Utils.TODAY);
                    list.add(Utils.TOMORROW);
                    list.add(Utils.THIS_WEEK);
                    list.add(Utils.ALL);
                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(context, R.layout.dropdown_item, list);
                    filterHomeworksDate.setAdapter(arrayAdapter);

                    // TODO: restoreFilterState(); // set the filter values to the last filter applied

                    // Init Buttons
                    btnApplyFilter.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            binding.selectSectionMsg.setVisibility(View.GONE);
                        }
                    });

                    btnCloseFilter.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });

                    builder.setView(view);
                    dialog = builder.create(); // creating our dialog
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    dialog.show();
                    // Show rounded corners
                    WindowManager.LayoutParams params =   dialog.getWindow().getAttributes();
                    dialog.getWindow().setAttributes(params);
                }
            });

        }

        binding.included.btnCompleteHomework.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isChecked){
                    binding.included.btnCompleteHomework.setSpeed(-1);
                    binding.included.btnCompleteHomework.playAnimation();
                    isChecked = false;
//                    TODO: Add animation here
                    binding.included.homeworkTitle.setPaintFlags(binding.included.homeworkTitle.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                    binding.included.homeworkDate.setTextColor(getResources().getColor(R.color.secondary_text_color, null));
                    binding.included.homeworkDateIcon.setImageResource(R.drawable.ic_time);
                    DrawableCompat.setTint(DrawableCompat.wrap(binding.included.homeworkDateIcon.getDrawable()), ContextCompat.getColor(getContext(), R.color.secondary_text_color));
                } else {
                    binding.included.btnCompleteHomework.setSpeed(1);
                    binding.included.btnCompleteHomework.playAnimation();
                    isChecked = true;
                    binding.included.homeworkTitle.setPaintFlags(binding.included.homeworkTitle.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    binding.included.homeworkDate.setTextColor(getResources().getColor(R.color.primary_color, null));
                    binding.included.homeworkDateIcon.setImageResource(R.drawable.ic_checked);
                    DrawableCompat.setTint(DrawableCompat.wrap(binding.included.homeworkDateIcon.getDrawable()), ContextCompat.getColor(getContext(), R.color.primary_color));
                }
            }
        });

        binding.included.homeworkMainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), HomeworkDetailsActivity.class));
            }
        });

//        Check animation
//        binding.btnCompleteHomework.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (isChecked){
//                    binding.btnCompleteHomework.setSpeed(-1);
//                    binding.btnCompleteHomework.playAnimation();
//                    isChecked = false;
//                } else {
//                    binding.btnCompleteHomework.setSpeed(1);
//                    binding.btnCompleteHomework.playAnimation();
//                    isChecked = true;
//                }
//            }
//        });

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}