package ru.longlog.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.longlog.AppApplication;
import ru.longlog.R;
import ru.longlog.adapter.ProjectJobsAdapter;
import ru.longlog.models.JobModel;
import ru.longlog.models.ProjectModel;

public class ProjectActivity extends BaseActivity {
    private int projectId;
    private ProgressBar projectJobsProgressBar;
    private List<JobModel> jobModels;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project);

        Intent intent = getIntent();
        projectId = intent.getIntExtra("id", 0);
        String name = intent.getStringExtra("name");

        setTitle(getString(R.string.project_activity_title, name));

        projectJobsProgressBar = findViewById(R.id.projectJobsProgressBar);

        // Search jobModels
        // Show progress bar
        projectJobsProgressBar.setVisibility(View.VISIBLE);

        jobModels = new ArrayList<>();

        recyclerView = findViewById(R.id.projectJobsView);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        DividerItemDecoration mDividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                layoutManager.getOrientation());
        recyclerView.addItemDecoration(mDividerItemDecoration);

        // On Item Click Listener
        View.OnClickListener onItemClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = ((TextView) v.findViewById(R.id.jobItemId)).getText().toString();
                String name = ((TextView) v.findViewById(R.id.jobItemName)).getText().toString();
                String critDuration = ((TextView) v.findViewById(R.id.jobItemCritDuration)).getText().toString();

                Intent intent = new Intent(ProjectActivity.this, JobActivity.class);
                intent.putExtra("id", Integer.valueOf(id));
                intent.putExtra("name", name);
                if (!TextUtils.isEmpty(critDuration)) {
                    intent.putExtra("critDuration", Integer.valueOf(critDuration));
                }
                startActivity(intent);
            }
        };

        ProjectJobsAdapter adapter = new ProjectJobsAdapter(jobModels, onItemClickListener);
        recyclerView.setAdapter(adapter);

        AppApplication.getApi().project(projectId).enqueue(new Callback<ProjectModel>() {
            @Override
            public void onResponse(Call<ProjectModel> call, Response<ProjectModel> response) {
                // Hide progress bar
                projectJobsProgressBar.setVisibility(View.GONE);

                if (response.code() == 401) {
                    AppApplication.processFailedAuth(ProjectActivity.this);
                    return;
                }

                jobModels.addAll(response.body().getJobs());
                recyclerView.getAdapter().notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<ProjectModel> call, Throwable t) {
                // Hide progress bar
                projectJobsProgressBar.setVisibility(View.GONE);
                t.printStackTrace();
                Toast.makeText(ProjectActivity.this, "An error occurred during networking:\n" + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
