package com.salmi.bouchelaghem.studynet.Adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.salmi.bouchelaghem.studynet.Activities.HomeworkDetailsActivity;
import com.salmi.bouchelaghem.studynet.Models.Homework;
import com.salmi.bouchelaghem.studynet.R;
import com.salmi.bouchelaghem.studynet.Utils.CurrentUser;
import com.salmi.bouchelaghem.studynet.Utils.Utils;
import com.salmi.bouchelaghem.studynet.databinding.LayoutHomeworkBinding;

import org.threeten.bp.format.DateTimeFormatter;

import java.util.List;

public class HomeworkAdapter extends RecyclerView.Adapter<HomeworkAdapter.ViewHolder> {

    private List<Homework> homeworks;
    private final Context context;
    private final CurrentUser currentUser = CurrentUser.getInstance();

    public HomeworkAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutHomeworkBinding binding = LayoutHomeworkBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        ViewHolder holder = new ViewHolder(binding);

        holder.binding.homeworkMainLayout.setOnClickListener(v -> {
            Homework homework = homeworks.get(holder.getAdapterPosition());
            Intent intent = new Intent(context, HomeworkDetailsActivity.class);
            intent.putExtra(Utils.HOMEWORK, homework);
            context.startActivity(intent);
        });

        if (currentUser.getUserType().equals(Utils.STUDENT_ACCOUNT)) {
            // If its a student then show the check button
            holder.binding.checkMark.setVisibility(View.VISIBLE);
            // Init the check button
            holder.binding.btnCompleteHomework.setOnClickListener(v -> {
                // Read the current homework's check state
                Homework homework = homeworks.get(holder.getAdapterPosition());
                boolean isChecked = readCheckState(homework.getId());
                if (isChecked) {
                    // Mark the homework as unchecked
                    saveCheckState(false, homework.getId());
                    // Play the anim in reverse
                    holder.binding.btnCompleteHomework.setSpeed(-1);
                    holder.binding.btnCompleteHomework.playAnimation();
                    // Change the text color
                    holder.binding.homeworkTitle.setPaintFlags(holder.binding.homeworkTitle.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                    holder.binding.homeworkDate.setTextColor(context.getResources().getColor(R.color.secondary_text_color, null));
                    holder.binding.imageView2.setImageResource(R.drawable.ic_time);
                    DrawableCompat.setTint(DrawableCompat.wrap(holder.binding.imageView2.getDrawable()), ContextCompat.getColor(context, R.color.secondary_text_color));
                } else {
                    // Mark the homework as unchecked
                    saveCheckState(true, homework.getId());
                    // Play the animation
                    holder.binding.btnCompleteHomework.setSpeed(1);
                    holder.binding.btnCompleteHomework.playAnimation();
                    holder.binding.homeworkTitle.setPaintFlags(holder.binding.homeworkTitle.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    holder.binding.homeworkDate.setTextColor(context.getResources().getColor(R.color.primary_color, null));
                    holder.binding.imageView2.setImageResource(R.drawable.ic_checked);
                    DrawableCompat.setTint(DrawableCompat.wrap(holder.binding.imageView2.getDrawable()), ContextCompat.getColor(context, R.color.primary_color));
                }
            });
        }

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Homework homework = homeworks.get(position);

        holder.binding.homeworkTitle.setText(homework.getTitle());
        holder.binding.homeworkSubject.setText(homework.getModule());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        holder.binding.homeworkDate.setText(formatter.format(homework.getLocalDateDueDate()));

        if (currentUser.getUserType().equals(Utils.STUDENT_ACCOUNT)) {
            // Read the current homework's check state
            boolean isChecked = readCheckState(homework.getId());

            if (isChecked) {
                // Show that the homework is checked
                // Animation
                holder.binding.btnCompleteHomework.setProgress((float) 0.9);
                // Text
                holder.binding.homeworkTitle.setPaintFlags(holder.binding.homeworkTitle.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                holder.binding.homeworkDate.setTextColor(context.getResources().getColor(R.color.primary_color, null));
                holder.binding.imageView2.setImageResource(R.drawable.ic_checked);
                DrawableCompat.setTint(DrawableCompat.wrap(holder.binding.imageView2.getDrawable()), ContextCompat.getColor(context, R.color.primary_color));
            }
        } else if (currentUser.getUserType().equals(Utils.TEACHER_ACCOUNT)) {
            if (homework.getTeacherEmail().equals(currentUser.getCurrentTeacher().getEmail())) {
                holder.binding.imgBookmark.setVisibility(View.VISIBLE);
            } else {
                holder.binding.imgBookmark.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public int getItemCount() {
        return homeworks.size();
    }

    public void setHomeworks(List<Homework> homeworks) {
        this.homeworks = homeworks;
        notifyDataSetChanged();
    }

    public List<Homework> getHomeworks() {
        return homeworks;
    }

    // Checkmark methods
    private void saveCheckState(boolean isChecked, int homeworkId) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Utils.HOMEWORK, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (isChecked) {
            // Add the homework to the completed homeworks list
            editor.putString(String.valueOf(homeworkId), String.valueOf(homeworkId));
        } else {
            // Remove the homework from the completed homeworks list
            editor.remove(String.valueOf(homeworkId));
        }
        editor.apply();
    }

    private boolean readCheckState(int homeworkId) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Utils.HOMEWORK, Context.MODE_PRIVATE);
        return sharedPreferences.contains(String.valueOf(homeworkId));
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        LayoutHomeworkBinding binding;

        public ViewHolder(LayoutHomeworkBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

}
