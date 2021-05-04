package com.salmi.bouchelaghem.studynet.Fragments;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.salmi.bouchelaghem.studynet.Activities.AddTeacherActivity;
import com.salmi.bouchelaghem.studynet.Activities.NavigationActivity;
import com.salmi.bouchelaghem.studynet.Adapters.TeachersAdapter;
import com.salmi.bouchelaghem.studynet.Models.Student;
import com.salmi.bouchelaghem.studynet.Models.Teacher;
import com.salmi.bouchelaghem.studynet.R;
import com.salmi.bouchelaghem.studynet.Utils.CurrentUser;
import com.salmi.bouchelaghem.studynet.Utils.TestAPI;
import com.salmi.bouchelaghem.studynet.Utils.Utils;
import com.salmi.bouchelaghem.studynet.databinding.FragmentTeachersBinding;

import java.util.ArrayList;
import java.util.List;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class TeachersFragment extends Fragment {

    private FragmentTeachersBinding binding;

    // Recycler view
    private List<Teacher> teachers;
    private TeachersAdapter adapter;

    private final CurrentUser currentUser = CurrentUser.getInstance();
    private String userType;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentTeachersBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        userType = currentUser.getUserType();
        initRecView();

        // Hide filter button
        NavigationActivity context = (NavigationActivity) getActivity();
        assert context != null;
        context.btnFilter.setVisibility(View.GONE);

        if (userType.equals(Utils.ADMIN_ACCOUNT)){
            // Show add button
            binding.btnAdd.setVisibility(View.VISIBLE);
            binding.btnAdd.setOnClickListener(v -> {
                Intent intent = new Intent(getContext(), AddTeacherActivity.class);
                intent.putExtra(Utils.ACTION, Utils.ACTION_ADD);
                startActivity(intent);
            });
        }

        return view;
    }

    private void initRecView() {
        teachers = new ArrayList<>();
        binding.teachersRecView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new TeachersAdapter(getContext(), userType);

        // Swipe to action in rec view
        if (userType.equals(Utils.ADMIN_ACCOUNT)){
            // If its a teacher then show delete + edit buttons
            ItemTouchHelper itemTouchHelper = new ItemTouchHelper(adminCallBack);
            itemTouchHelper.attachToRecyclerView(binding.teachersRecView);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (teachers != null && teachers.isEmpty()) {
            // if its the first time we launch the activity so go ahead and get the data from the database
            switch (userType){
                case Utils.STUDENT_ACCOUNT:
                    // Get the current student
                    Student currentStudent = CurrentUser.getInstance().getCurrentStudent();
                    getMyTeachers(currentStudent.getSection().getCode());
                    break;
                case Utils.ADMIN_ACCOUNT:
                    getAllTeachers();
                    break;
            }
        } else { // if we already retrieved the data from the database, just keep using it
            adapter.setTeachers(teachers);
            binding.teachersRecView.setAdapter(adapter);
            binding.teachersRecView.setVisibility(View.VISIBLE);
            binding.emptyMsg.setVisibility(View.GONE);
        }
    }

    // Get the current section's teachers
    private void getMyTeachers(String section){
        teachers = TestAPI.getInstance().getTeachers();
        if (!teachers.isEmpty()) {
            adapter.setTeachers(teachers);
            binding.teachersRecView.setAdapter(adapter);
            binding.teachersRecView.setVisibility(View.VISIBLE);
            binding.emptyMsg.setVisibility(View.GONE);
        } else {
            binding.teachersRecView.setVisibility(View.GONE);
            binding.emptyMsg.setVisibility(View.VISIBLE);
        }
    }

    private void getAllTeachers() {
        teachers = TestAPI.getInstance().getTeachers();
        if (!teachers.isEmpty()) {
            adapter.setTeachers(teachers);
            binding.teachersRecView.setAdapter(adapter);
            binding.teachersRecView.setVisibility(View.VISIBLE);
            binding.emptyMsg.setVisibility(View.GONE);
        } else {
            binding.teachersRecView.setVisibility(View.GONE);
            binding.emptyMsg.setVisibility(View.VISIBLE);
        }
    }

    // Swipe to delete and edit in the recycler view
    ItemTouchHelper.SimpleCallback adminCallBack = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

            int position = viewHolder.getAdapterPosition();
            Teacher currentTeacher = adapter.getTeachers().get(position);

            switch (direction){
                case ItemTouchHelper.LEFT: // Swipe left to right <- : Delete item

                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setMessage(R.string.are_you_sure);
                    builder.setPositiveButton(R.string.yes, (dialog, which) -> {
                        teachers.remove(currentTeacher);
                        adapter.getTeachers().remove(currentTeacher);
                        adapter.notifyItemRemoved(position);
                        Toast.makeText(getContext(), getString(R.string.teacher_deleted_msg), Toast.LENGTH_SHORT).show();
                    });
                    builder.setNegativeButton(R.string.no, (dialog, which) -> {
                        // Do Nothing
                        adapter.notifyItemChanged(position); // To reset the item on the screen
                    });
                    builder.create().show();
                    break;
                case ItemTouchHelper.RIGHT: // Swipe right to left -> : Edit item
                    Intent intent = new Intent(getContext(), AddTeacherActivity.class);
                    intent.putExtra(Utils.ACTION, Utils.ACTION_UPDATE);
                    intent.putExtra(Utils.TEACHER, currentTeacher);
                    startActivity(intent);
                    adapter.notifyItemChanged(position); // To reset the item on the screen
                    break;
            }
        }

        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

            new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                    .addSwipeLeftBackgroundColor(ContextCompat.getColor(requireContext(), R.color.red))
                    .addSwipeLeftActionIcon(R.drawable.ic_delete)
                    .addSwipeRightBackgroundColor(ContextCompat.getColor(requireContext(), R.color.green))
                    .addSwipeRightActionIcon(R.drawable.ic_modify)
                    .create()
                    .decorate();

            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    };

}