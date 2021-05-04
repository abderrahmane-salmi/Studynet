package com.salmi.bouchelaghem.studynet.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.salmi.bouchelaghem.studynet.Fragments.TeachersFragmentDirections;
import com.salmi.bouchelaghem.studynet.Models.Teacher;
import com.salmi.bouchelaghem.studynet.Utils.Utils;
import com.salmi.bouchelaghem.studynet.databinding.LayoutTeacherBinding;

import java.util.List;

public class TeachersAdapter extends RecyclerView.Adapter<TeachersAdapter.ViewHolder> {

    private List<Teacher> teachers;
    private String userType;
    private Context context;

    public TeachersAdapter(Context context, String userType) {
        this.context = context;
        this.userType = userType;
    }

    public TeachersAdapter(List<Teacher> teachers, Context context, String userType) {
        this.teachers = teachers;
        this.context = context;
        this.userType = userType;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutTeacherBinding binding = LayoutTeacherBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        ViewHolder holder = new ViewHolder(binding);

        if (userType.equals(Utils.STUDENT_ACCOUNT)){
            holder.binding.teacherMainLayout.setOnClickListener(v -> {
                Teacher teacher = teachers.get(holder.getAdapterPosition());
                // TODO: Send the teacher object with safe args
                NavDirections action = TeachersFragmentDirections.actionNavTeachersToTeacherDetailsFragment(teacher);
                Navigation.createNavigateOnClickListener(action).onClick(holder.binding.getRoot());
            });
        }

        return holder;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Teacher teacher = teachers.get(position);

        holder.binding.txtTeacherName.setText(teacher.getFirstName()+" "+teacher.getLastName());
        if (userType.equals(Utils.STUDENT_ACCOUNT)){
            // If its a student then show the teacher's module
            // TODO: Show module name (The module that this teacher teaches in this section)
        } else if (userType.equals(Utils.ADMIN_ACCOUNT)){
            // If its an admin then show the teacher's email
            holder.binding.txtTeacherInfo.setText(teacher.getEmail());
        }
    }

    @Override
    public int getItemCount() {
        return teachers.size();
    }

    public void setTeachers(List<Teacher> teachers) {
        this.teachers = teachers;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{

        LayoutTeacherBinding binding;

        public ViewHolder(LayoutTeacherBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
