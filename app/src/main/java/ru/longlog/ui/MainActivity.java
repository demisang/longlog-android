package ru.longlog.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
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
import ru.longlog.adapter.SearchProjectsAdapter;
import ru.longlog.models.ProjectModel;

public class MainActivity extends BaseActivity {

    private ProgressBar searchProjectsProgressBar;
    private List<ProjectModel> projects;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle(getString(R.string.main_activity_title));

        if (TextUtils.isEmpty(AppApplication.getBaseUrl())) {
            // Show login activity
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        }

        searchProjectsProgressBar = findViewById(R.id.searchProjectsProgressBar);

        // Search projects
        // Show progress bar
        searchProjectsProgressBar.setVisibility(View.VISIBLE);

        projects = new ArrayList<>();

        recyclerView = findViewById(R.id.searchProjectsView);
        recyclerView.setVisibility(View.GONE);
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
                String id = ((TextView) v.findViewById(R.id.searchProjectsItemId)).getText().toString();
                String name = ((TextView) v.findViewById(R.id.searchProjectsItemName)).getText().toString();

                Intent intent = new Intent(MainActivity.this, ProjectActivity.class);
                intent.putExtra("id", Integer.valueOf(id));
                intent.putExtra("name", name);
                startActivity(intent);
            }
        };

        SearchProjectsAdapter adapter = new SearchProjectsAdapter(projects, onItemClickListener);
        recyclerView.setAdapter(adapter);

        AppApplication.getApi().projects().enqueue(new Callback<List<ProjectModel>>() {
            @Override
            public void onResponse(Call<List<ProjectModel>> call, Response<List<ProjectModel>> response) {
                // Hide progress bar
                searchProjectsProgressBar.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);

                if (response.code() == 401) {
                    AppApplication.processFailedAuth(MainActivity.this);
                    return;
                }

                projects.addAll(response.body());
                recyclerView.getAdapter().notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<List<ProjectModel>> call, Throwable t) {
                // Hide progress bar
                searchProjectsProgressBar.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                t.printStackTrace();
                Toast.makeText(MainActivity.this, "An error occurred during networking:\n" + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
