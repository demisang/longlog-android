package ru.longlog.adapter;

import android.annotation.SuppressLint;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;

import java.util.List;

import ru.longlog.R;
import ru.longlog.chart.JobItemViewChart;
import ru.longlog.models.JobModel;

public class ProjectJobsAdapter extends RecyclerView.Adapter<ProjectJobsAdapter.ViewHolder> {

    private List<JobModel> jobs;
    private View.OnClickListener onItemClickListener;

    public ProjectJobsAdapter(List<JobModel> jobs, View.OnClickListener onItemClickListener) {
        this.jobs = jobs;
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.job_item, parent, false);
        v.setOnClickListener(onItemClickListener);

        // init chart

        return new ViewHolder(v);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        JobModel job = jobs.get(position);

        // Text
        holder.id.setText(job.getId().toString());
        if (job.getCritDuration() != null && job.getCritDuration() > 0) {
            Integer duration = Math.round(job.getCritDuration());
            holder.critDuration.setText(duration.toString());
        }
        holder.name.setText(TextUtils.isEmpty(job.getTitle()) ? job.getKey() : job.getTitle());

        // Set chart data
        if (job.getStats() != null) {
            JobItemViewChart chartObject = new JobItemViewChart(holder.chart);
            if (job.getCritDuration() != null && job.getCritDuration() > 0) {
                chartObject.setCriticalDuration(Math.round(job.getCritDuration()));
            }
            chartObject.refreshChart(job.getStats());
        }
    }

    @Override
    public int getItemCount() {
        if (jobs == null) {
            return 0;
        }

        return jobs.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView id;
        TextView name;
        TextView critDuration;
        LineChart chart;

        ViewHolder(View itemView) {
            super(itemView);

            id = itemView.findViewById(R.id.jobItemId);
            name = itemView.findViewById(R.id.jobItemName);
            critDuration = itemView.findViewById(R.id.jobItemCritDuration);
            chart = itemView.findViewById(R.id.jobItemChart);
        }
    }
}
