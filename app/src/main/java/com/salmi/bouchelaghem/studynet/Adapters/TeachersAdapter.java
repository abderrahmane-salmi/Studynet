package com.salmi.bouchelaghem.studynet.Adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.salmi.bouchelaghem.studynet.Fragments.TeachersFragmentDirections;
import com.salmi.bouchelaghem.studynet.Models.Teacher;
import com.salmi.bouchelaghem.studynet.databinding.LayoutTeacherBinding;

import java.util.List;

public class TeachersAdapter extends RecyclerView.Adapter<TeachersAdapter.ViewHolder> {

    private List<Teacher> teachers;

    public TeachersAdapter() {
    }

    public TeachersAdapter(List<Teacher> teachers) {
        this.teachers = teachers;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutTeacherBinding binding = LayoutTeacherBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        ViewHolder holder = new ViewHolder(binding);

        holder.binding.teacherMainLayout.setOnClickListener(v -> {
            Teacher teacher = teachers.get(holder.getAdapterPosition());
            NavDirections action = TeachersFragmentDirections.actionNavTeachersToTeacherDetailsFragment(teacher);
            Navigation.createNavigateOnClickListener(action).onClick(holder.binding.getRoot());
        });

        return holder;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Teacher teacher = teachers.get(position);

        holder.binding.txtTeacherName.setText(teacher.getLastName()+" "+teacher.getFirstName());
        holder.binding.txtTeacherInfo.setText(teacher.getEmail());
    }

    @Override
    public int getItemCount() {
        return teachers.size();
    }

    public void setTeachers(List<Teacher> teachers) {
        this.teachers = teachers;
        notifyDataSetChanged();
    }

    public List<Teacher> getTeachers() {
        return teachers;
    }

    static class ViewHolder extends RecyclerView.ViewHolder{

        LayoutTeacherBinding binding;

        public ViewHolder(LayoutTeacherBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
