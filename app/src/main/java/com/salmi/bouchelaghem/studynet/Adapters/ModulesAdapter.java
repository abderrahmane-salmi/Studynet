package com.salmi.bouchelaghem.studynet.Adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.salmi.bouchelaghem.studynet.Fragments.SubjectsFragmentDirections;
import com.salmi.bouchelaghem.studynet.Models.Module;
import com.salmi.bouchelaghem.studynet.R;
import com.salmi.bouchelaghem.studynet.databinding.LayoutSubjectBinding;

import java.util.List;
import java.util.Objects;

public class ModulesAdapter extends RecyclerView.Adapter<ModulesAdapter.ViewHolder> {

    private List<Module> modules;
    private final Activity activity;

    public ModulesAdapter(Activity context) {
        this.activity = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutSubjectBinding binding = LayoutSubjectBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        ViewHolder holder = new ViewHolder(binding);

        holder.binding.subjectMainLayout.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(activity, R.id.fragment);
            // I added this condition to prevent the user from opening the same fragment twice (when clicking super
            if (Objects.requireNonNull(navController.getCurrentDestination()).getId() != R.id.subjectDetailsFragment) {
                Module module = modules.get(holder.getAdapterPosition());
                SubjectsFragmentDirections.ActionNavSubjectsToSubjectDetailsFragment action = SubjectsFragmentDirections.actionNavSubjectsToSubjectDetailsFragment(module);
                Navigation.createNavigateOnClickListener(action).onClick(holder.binding.getRoot());
            }
        });

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Module module = modules.get(position);

        holder.binding.txtSubjectCode.setText(module.getCode());
        holder.binding.txtSubjectName.setText(module.getName());
        holder.binding.txtSubjectTypes.setText("");
        for (int i = 0; i < module.getTypes().size() - 1; i++) {
            holder.binding.txtSubjectTypes.append(module.getTypes().get(i) + ", ");
        }
        holder.binding.txtSubjectTypes.append(module.getTypes().get(module.getTypes().size() - 1));
    }

    @Override
    public int getItemCount() {
        return modules.size();
    }

    public void setModules(List<Module> modules) {
        this.modules = modules;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        LayoutSubjectBinding binding;

        public ViewHolder(LayoutSubjectBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
