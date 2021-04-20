package com.salmi.bouchelaghem.studynet.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.salmi.bouchelaghem.studynet.Activities.ClassDetailsActivity;
import com.salmi.bouchelaghem.studynet.Models.Session;
import com.salmi.bouchelaghem.studynet.Utils.Utils;
import com.salmi.bouchelaghem.studynet.databinding.LayoutClassBinding;

import java.util.List;

public class SessionsAdapter extends RecyclerView.Adapter<SessionsAdapter.ViewHolder> {

    private List<Session> sessions;
    private String userType;
    private final Context context;

    public SessionsAdapter(Context context, String userType) {
        this.context = context;
        this.userType = userType;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutClassBinding binding = LayoutClassBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        ViewHolder holder = new ViewHolder(binding);

        holder.binding.classMainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the session's id
                int id = sessions.get(holder.getAdapterPosition()).getId();

                Intent intent = new Intent(context, ClassDetailsActivity.class);
                intent.putExtra(Utils.ID, id);
                context.startActivity(intent);

                /* TODO: We can send the whole session object to optimize the nb of reads from the db*/
            }
        });

        if (userType.equals(Utils.TEACHER_ACCOUNT)){ // Show teacher control buttons
            binding.teacherControlButtons.setVisibility(View.VISIBLE);

            binding.btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

            binding.btnModify.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

        } else if (userType.equals(Utils.ADMIN_ACCOUNT)){ // Show admin control buttons
            binding.adminControlButtons.setVisibility(View.VISIBLE);

            binding.btnReport.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Session session = sessions.get(position);

        // Session's groups
        if (session.getConcernedGroups().size() > 1){ // There are more then 1 grp
            int nbGroups = session.getConcernedGroups().size();
            holder.binding.txtClassGroup.setText("");
            for (int grp=0; grp<nbGroups-1; grp++){
                holder.binding.txtClassGroup.append(String.valueOf(grp+1) + ", ");
            }
            holder.binding.txtClassGroup.append(String.valueOf(nbGroups)); // The last group doesn't have a ',' after it

        } else { // There is only one grp
            holder.binding.txtClassGroup.setText(String.valueOf(session.getConcernedGroups().get(0)));
        }

        // Time
        holder.binding.txtClassStartHour.setText(session.getStartTime());
        holder.binding.txtClassEndHour.setText(session.getEndTime());

        // Module
        holder.binding.txtClassSubject.setText(session.getAssignment().getModuleName());
        holder.binding.txtClassType.setText(session.getAssignment().getModuleType());

    }

    @Override
    public int getItemCount() {
        return sessions.size();
    }

    public void setSessions(List<Session> sessions) {
        this.sessions = sessions;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{

        LayoutClassBinding binding;

        public ViewHolder(LayoutClassBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

}
