package com.salmi.bouchelaghem.studynet.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.salmi.bouchelaghem.studynet.Activities.HomeworkDetailsActivity;
import com.salmi.bouchelaghem.studynet.Models.Homework;
import com.salmi.bouchelaghem.studynet.R;
import com.salmi.bouchelaghem.studynet.Utils.Utils;
import com.salmi.bouchelaghem.studynet.databinding.LayoutHomeworkBinding;

import org.threeten.bp.format.DateTimeFormatter;

import java.util.List;

public class HomeworkAdapter extends RecyclerView.Adapter<HomeworkAdapter.ViewHolder> {

    private List<Homework> homeworks;
    private final Context context;

    public HomeworkAdapter(Context context) {
        this.context = context;
    }

    public HomeworkAdapter(List<Homework> homeworks, Context context) {
        this.homeworks = homeworks;
        this.context = context;
        notifyDataSetChanged();
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

        holder.binding.btnCompleteHomework.setOnClickListener(v -> {
            // TODO: read isChecked value from shared prefs
            boolean isChecked = false;
            if (isChecked){
                // Mark the homework as unchecked
                isChecked = false;
                // TODO: save this value to shared prefs
                // Play the anim in reverse
                holder.binding.btnCompleteHomework.setSpeed(-1);
                holder.binding.btnCompleteHomework.playAnimation();
//                    TODO: Add strike through text animation here (slowly)
                // Change the text color
                holder.binding.homeworkTitle.setPaintFlags(holder.binding.homeworkTitle.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                holder.binding.homeworkDate.setTextColor(context.getResources().getColor(R.color.secondary_text_color, null));
                holder.binding.imageView2.setImageResource(R.drawable.ic_time);
                DrawableCompat.setTint(DrawableCompat.wrap(holder.binding.imageView2.getDrawable()), ContextCompat.getColor(context, R.color.secondary_text_color));
            } else {
                // Mark the homework as unchecked
                isChecked = true;
                // TODO: save this value to shared prefs
                // Play the animation
                holder.binding.btnCompleteHomework.setSpeed(1);
                holder.binding.btnCompleteHomework.playAnimation();
                holder.binding.homeworkTitle.setPaintFlags(holder.binding.homeworkTitle.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                holder.binding.homeworkDate.setTextColor(context.getResources().getColor(R.color.primary_color, null));
                holder.binding.imageView2.setImageResource(R.drawable.ic_checked);
                DrawableCompat.setTint(DrawableCompat.wrap(holder.binding.imageView2.getDrawable()), ContextCompat.getColor(context, R.color.primary_color));
            }
        });

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Homework homework = homeworks.get(position);

        holder.binding.homeworkTitle.setText(homework.getTitle());
        holder.binding.homeworkSubject.setText(homework.getAssignment().getModuleName());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        holder.binding.homeworkDate.setText(formatter.format(homework.getDueDate()));
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

    static class ViewHolder extends RecyclerView.ViewHolder{

        LayoutHomeworkBinding binding;

        public ViewHolder(LayoutHomeworkBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

}
