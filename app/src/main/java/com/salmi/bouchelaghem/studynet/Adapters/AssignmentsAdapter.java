package com.salmi.bouchelaghem.studynet.Adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.salmi.bouchelaghem.studynet.Models.Assignment;
import com.salmi.bouchelaghem.studynet.databinding.LayoutAssignmentBinding;

import java.util.List;

public class AssignmentsAdapter extends RecyclerView.Adapter<AssignmentsAdapter.ViewHolder> {

    private List<Assignment> assignments;

    public AssignmentsAdapter() {
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutAssignmentBinding binding = LayoutAssignmentBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Assignment assignment = assignments.get(position);

        holder.binding.txtSection.setText(assignment.getSectionCode());
        holder.binding.txtModuleName.setText(assignment.getModuleCode());
        holder.binding.txtModuleTypes.setText(assignment.getModuleType());

        // Groups
        if (assignment.getConcernedGroups().size() > 1) { // There are more then 1 grp
            int nbGroups = assignment.getConcernedGroups().size();
            holder.binding.txtGroups.setText("");
            for (int grp = 0; grp < nbGroups - 1; grp++) {
                holder.binding.txtGroups.append((grp + 1) + ", ");
            }
            holder.binding.txtGroups.append(String.valueOf(nbGroups)); // The last group doesn't have a ',' after it

        } else { // There is only one grp
            holder.binding.txtGroups.setText(String.valueOf(assignment.getConcernedGroups().get(0)));
        }
    }

    @Override
    public int getItemCount() {
        return assignments.size();
    }

    public List<Assignment> getAssignments() {
        return assignments;
    }

    public void setAssignments(List<Assignment> assignments) {
        this.assignments = assignments;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        LayoutAssignmentBinding binding;

        public ViewHolder(LayoutAssignmentBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
