package ru.longlog.chart;

import com.github.mikephil.charting.charts.LineChart;

/**
 * Job items shortly chart's
 */
public class JobItemViewChart extends BaseStatsChart {
    public JobItemViewChart(LineChart lineChart) {
        super(lineChart);
    }

    protected void onCreate() {
        setDateFormat(DATE_FORMAT_SHORT);
        setShowLegend(false);

        super.onCreate();
    }
}
