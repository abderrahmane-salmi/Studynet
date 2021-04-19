package com.salmi.bouchelaghem.studynet.Fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;

import com.google.android.material.button.MaterialButton;
import com.salmi.bouchelaghem.studynet.Activities.AddClassActivity;
import com.salmi.bouchelaghem.studynet.Activities.ClassDetailsActivity;
import com.salmi.bouchelaghem.studynet.Activities.NavigationActivity;
import com.salmi.bouchelaghem.studynet.R;
import com.salmi.bouchelaghem.studynet.Utils.CurrentUser;
import com.salmi.bouchelaghem.studynet.Utils.OnSwipeTouchListener;
import com.salmi.bouchelaghem.studynet.Utils.Utils;
import com.salmi.bouchelaghem.studynet.databinding.FragmentTimetableBinding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class TimetableFragment extends Fragment {

    private FragmentTimetableBinding binding;
    private final CurrentUser currentUser = CurrentUser.getInstance();

    private Dialog dialog;

    // Days
    private int currentDay = 1;
    List<String> days;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentTimetableBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        // If the user is a teacher or an admin then show the filter button
        if (currentUser.getUserType().equals(Utils.TEACHER_ACCOUNT) || currentUser.getUserType().equals(Utils.ADMIN_ACCOUNT)){

            binding.selectSectionMsg.setVisibility(View.VISIBLE);
            binding.btnAdd.setVisibility(View.VISIBLE);

            NavigationActivity context = (NavigationActivity) getActivity();
            assert context != null;

            binding.btnAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(context, AddClassActivity.class));
                }
            });

            context.btnFilter.setVisibility(View.VISIBLE);
            context.btnFilter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    View view = View.inflate(context, R.layout.popup_teacher_timetable_filter, null);
                    // Init Views
                    ImageView btnCloseFilter = view.findViewById(R.id.btnCloseFilter);
                    AutoCompleteTextView filterTimetableSection = view.findViewById(R.id.filterTimetableSection);
                    MaterialButton btnApplyFilter = view.findViewById(R.id.btnApplyFilter);

                    // Init sections list
                    List<String> list = new ArrayList<>();
                    list.add("First");
                    list.add("Second");
                    list.add("Third");
                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(context, R.layout.dropdown_item, list);
                    filterTimetableSection.setAdapter(arrayAdapter);

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

        // Init days
        days = Arrays.asList(getResources().getStringArray(R.array.days));

        binding.day1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToDay1();
            }
        });

        binding.day2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToDay2();
            }
        });

        binding.day3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToDay3();
            }
        });

        binding.day4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToDay4();
            }
        });

        binding.day5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToDay5();
            }
        });

        binding.day6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToDay6();
            }
        });


        // Used ViewTreeObserver to wait for the UI to be sized and then we can get the the view's width
        binding.day2.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                binding.day2.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                // Show today's classes by default
                Calendar calendar = Calendar.getInstance();
                switch (calendar.get(Calendar.DAY_OF_WEEK)){
                    case 0:
                        goToDay1();
                        break;
                    case 1:
                        goToDay2();
                        break;
                    case 2:
                        goToDay3();
                        break;
                    case 3:
                        goToDay4();
                        break;
                    case 4:
                        goToDay5();
                        break;
                    case 5:
                        goToDay6();
                        break;
                }
            }
        });

//        binding.included.classMainLayout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(getActivity(), ClassDetailsActivity.class));
//            }
//        });

//        binding.included.classMainLayout.setOnTouchListener(new OnSwipeTouchListener(getActivity()) {
//
//            @SuppressLint("ClickableViewAccessibility")
//            public void onSwipeRight() {
//                switch (currentDay){
//                    case 1:
//                        goToDay6();
//                        break;
//                    case 2:
//                        goToDay1();
//                        break;
//                    case 3:
//                        goToDay2();
//                        break;
//                    case 4:
//                        goToDay3();
//                        break;
//                    case 5:
//                        goToDay4();
//                        break;
//                    case 6:
//                        goToDay5();
//                        break;
//                }
//            }
//
//            public void onSwipeLeft() {
//                switch (currentDay){
//                    case 1:
//                        goToDay2();
//                        break;
//                    case 2:
//                        goToDay3();
//                        break;
//                    case 3:
//                        goToDay4();
//                        break;
//                    case 4:
//                        goToDay5();
//                        break;
//                    case 5:
//                        goToDay6();
//                        break;
//                    case 6:
//                        goToDay1();
//                        break;
//                }
//            }
//        });

        return view;
    }

    private void goToDay1(){
        currentDay = 1;
        // Adding an animation
        binding.selectedDay.animate().x(0).setDuration(100);
        // Changing text colors
        binding.day1.setTextColor(Color.WHITE);
        binding.day2.setTextColor(getResources().getColor(R.color.secondary_text_color, null));
        binding.day3.setTextColor(getResources().getColor(R.color.secondary_text_color, null));
        binding.day4.setTextColor(getResources().getColor(R.color.secondary_text_color, null));
        binding.day5.setTextColor(getResources().getColor(R.color.secondary_text_color, null));
        binding.day6.setTextColor(getResources().getColor(R.color.secondary_text_color, null));

        // Setting the day's name
        binding.txtSelectedDay.setText(days.get(0));
    }

    private void goToDay2(){
        currentDay = 2;
        int size = binding.day2.getWidth();
        binding.selectedDay.animate().x(size).setDuration(100);
        binding.day1.setTextColor(getResources().getColor(R.color.secondary_text_color, null));
        binding.day2.setTextColor(Color.WHITE);
        binding.day3.setTextColor(getResources().getColor(R.color.secondary_text_color, null));
        binding.day4.setTextColor(getResources().getColor(R.color.secondary_text_color, null));
        binding.day5.setTextColor(getResources().getColor(R.color.secondary_text_color, null));
        binding.day6.setTextColor(getResources().getColor(R.color.secondary_text_color, null));

        // Setting the day's name
        binding.txtSelectedDay.setText(days.get(1));
    }

    private void goToDay3(){
        currentDay = 3;
        int size = binding.day2.getWidth() * 2;
        binding.selectedDay.animate().x(size).setDuration(100);
        binding.day1.setTextColor(getResources().getColor(R.color.secondary_text_color, null));
        binding.day2.setTextColor(getResources().getColor(R.color.secondary_text_color, null));
        binding.day3.setTextColor(Color.WHITE);
        binding.day4.setTextColor(getResources().getColor(R.color.secondary_text_color, null));
        binding.day5.setTextColor(getResources().getColor(R.color.secondary_text_color, null));
        binding.day6.setTextColor(getResources().getColor(R.color.secondary_text_color, null));

        // Setting the day's name
        binding.txtSelectedDay.setText(days.get(2));
    }

    private void goToDay4(){
        currentDay = 4;
        int size = binding.day2.getWidth() * 3;
        binding.selectedDay.animate().x(size).setDuration(100);
        binding.day1.setTextColor(getResources().getColor(R.color.secondary_text_color, null));
        binding.day2.setTextColor(getResources().getColor(R.color.secondary_text_color, null));
        binding.day3.setTextColor(getResources().getColor(R.color.secondary_text_color, null));
        binding.day4.setTextColor(Color.WHITE);
        binding.day5.setTextColor(getResources().getColor(R.color.secondary_text_color, null));
        binding.day6.setTextColor(getResources().getColor(R.color.secondary_text_color, null));

        // Setting the day's name
        binding.txtSelectedDay.setText(days.get(3));
    }

    private void goToDay5(){
        currentDay = 5;
        int size = binding.day2.getWidth() * 4;
        binding.selectedDay.animate().x(size).setDuration(100);
        binding.day1.setTextColor(getResources().getColor(R.color.secondary_text_color, null));
        binding.day2.setTextColor(getResources().getColor(R.color.secondary_text_color, null));
        binding.day3.setTextColor(getResources().getColor(R.color.secondary_text_color, null));
        binding.day4.setTextColor(getResources().getColor(R.color.secondary_text_color, null));
        binding.day5.setTextColor(Color.WHITE);
        binding.day6.setTextColor(getResources().getColor(R.color.secondary_text_color, null));

        // Setting the day's name
        binding.txtSelectedDay.setText(days.get(4));
    }

    private void goToDay6(){
        currentDay = 6;
        int size = binding.day2.getWidth() * 5;
        binding.selectedDay.animate().x(size).setDuration(100);
        binding.day1.setTextColor(getResources().getColor(R.color.secondary_text_color, null));
        binding.day2.setTextColor(getResources().getColor(R.color.secondary_text_color, null));
        binding.day3.setTextColor(getResources().getColor(R.color.secondary_text_color, null));
        binding.day4.setTextColor(getResources().getColor(R.color.secondary_text_color, null));
        binding.day5.setTextColor(getResources().getColor(R.color.secondary_text_color, null));
        binding.day6.setTextColor(Color.WHITE);

        // Setting the day's name
        binding.txtSelectedDay.setText(days.get(5));
    }

}