package ru.longlog.adapter;

import android.annotation.SuppressLint;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import ru.longlog.R;
import ru.longlog.models.ProjectModel;

public class SearchProjectsAdapter extends RecyclerView.Adapter<SearchProjectsAdapter.ViewHolder> {

    private List<ProjectModel> projects;
    private View.OnClickListener onItemClickListener;

    public SearchProjectsAdapter(List<ProjectModel> projects, View.OnClickListener onItemClickListener) {
        this.projects = projects;
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_projects_item, parent, false);
        v.setOnClickListener(onItemClickListener);

        return new ViewHolder(v);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ProjectModel project = projects.get(position);

        // Text
        holder.id.setText(project.getId().toString());
        holder.name.setText(project.getName());
        // @todo Show role label instead role key
        holder.role.setText(project.getCurrentProjectUser().getRole());
    }

    @Override
    public int getItemCount() {
        if (projects == null) {
            return 0;
        }

        return projects.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView id;
        TextView name;
        TextView role;

        ViewHolder(View itemView) {
            super(itemView);

            id = itemView.findViewById(R.id.searchProjectsItemId);
            name = itemView.findViewById(R.id.searchProjectsItemName);
            role = itemView.findViewById(R.id.searchProjectsItemMyRole);
        }
    }
}
