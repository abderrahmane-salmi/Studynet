package com.salmi.bouchelaghem.studynet.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
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
    private final Context context;

    public SessionsAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutClassBinding binding = LayoutClassBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        ViewHolder holder = new ViewHolder(binding);

        holder.binding.classMainLayout.setOnClickListener(v -> {
            //Prepare the intent and pass in the session.
            Intent intent = new Intent(context, ClassDetailsActivity.class);
            intent.putExtra(Utils.SESSION, sessions.get(holder.getAdapterPosition()));
            context.startActivity(intent);
        });

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
                holder.binding.txtClassGroup.append((grp + 1) + ", ");
            }
            holder.binding.txtClassGroup.append(String.valueOf(nbGroups)); // The last group doesn't have a ',' after it

        } else { // There is only one grp
            holder.binding.txtClassGroup.setText(String.valueOf(session.getConcernedGroups().get(0)));
        }

        // Time
        holder.binding.txtClassStartHour.setText(session.getLocalTimeStartTime().toString());
        holder.binding.txtClassEndHour.setText(session.getLocalTimeEndTime().toString());

        // Module
        holder.binding.txtClassSubject.setText(session.getModule());
        holder.binding.txtClassType.setText(session.getModuleType());

    }

    @Override
    public int getItemCount() {
        return sessions.size();
    }

    public void setSessions(List<Session> sessions) {
        this.sessions = sessions;
        notifyDataSetChanged();
    }

    public List<Session> getSessions() {
        return sessions;
    }

    static class ViewHolder extends RecyclerView.ViewHolder{

        LayoutClassBinding binding;

        public ViewHolder(LayoutClassBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

}
