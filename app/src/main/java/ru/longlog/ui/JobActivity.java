package ru.longlog.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.longlog.AppApplication;
import ru.longlog.R;
import ru.longlog.chart.JobFullChart;
import ru.longlog.models.JobStatModel;

public class JobActivity extends BaseActivity {
    private int jobId;
    private ProgressBar jobStatsProgressBar;
    private List<JobStatModel> statModels;
    private LineChart mChart;
    private JobFullChart chartObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job);

        Intent intent = getIntent();
        jobId = intent.getIntExtra("id", 0);
        String name = intent.getStringExtra("name");
        // Critical duration in seconds
        int critDuration = intent.getIntExtra("critDuration", 0);

        setTitle(getString(R.string.job_stats_title, name));


        mChart = findViewById(R.id.jobFullChart);
        LinearLayout jobStatsStatusBarLayout = findViewById(R.id.jobStatsStatusBarLayout);
        TextView selectedStatDateValue = findViewById(R.id.jobStatsStatusBarDate);
        TextView selectedStatMinValue = findViewById(R.id.jobStatsStatusBarMin);
        TextView selectedStatAvgValue = findViewById(R.id.jobStatsStatusBarAvg);
        TextView selectedStatMaxValue = findViewById(R.id.jobStatsStatusBarMax);
        chartObject = new JobFullChart(mChart);
        chartObject.setSelectedValuesTextViews(jobStatsStatusBarLayout, selectedStatDateValue, selectedStatMinValue, selectedStatAvgValue, selectedStatMaxValue);
        if (critDuration > 0) {
            chartObject.setCriticalDuration(critDuration);
        }

        jobStatsProgressBar = findViewById(R.id.jobStatsProgressBar);
        // Show progress bar
        jobStatsProgressBar.setVisibility(View.VISIBLE);
        mChart.setVisibility(View.GONE);

        statModels = new ArrayList<>();

        AppApplication.getApi().stats(jobId).enqueue(new Callback<List<JobStatModel>>() {
            @Override
            public void onResponse(Call<List<JobStatModel>> call, Response<List<JobStatModel>> response) {
                // Hide progress bar
                jobStatsProgressBar.setVisibility(View.GONE);

                if (response.code() == 401) {
                    AppApplication.processFailedAuth(JobActivity.this);
                    return;
                }

                statModels.addAll(response.body());

                mChart.setVisibility(View.VISIBLE);
                chartObject.refreshChart(statModels);
            }

            @Override
            public void onFailure(Call<List<JobStatModel>> call, Throwable t) {
                // Hide progress bar
                jobStatsProgressBar.setVisibility(View.GONE);
                t.printStackTrace();
                Toast.makeText(JobActivity.this, "An error occurred during networking:\n" + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
