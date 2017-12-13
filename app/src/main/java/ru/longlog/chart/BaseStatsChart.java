package ru.longlog.chart;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import ru.longlog.R;
import ru.longlog.models.JobStatModel;

/**
 * Base class for all statistic chart's
 */
abstract public class BaseStatsChart implements OnChartValueSelectedListener {
    private final int MIN_AXIS = 1;
    private final int MAX_AXIS = 2;
    private final int AVG_AXIS = 3;
    public final int DATE_FORMAT_LONG = 1;
    public final int DATE_FORMAT_SHORT = 2;

    private LineChart lineChart;
    private LinearLayout selectedLayout;
    private TextView selectedDateTextView;
    private TextView selectedMinValueTextView;
    private TextView selectedAvgValueTextView;
    private TextView selectedMaxValueTextView;
    private List<JobStatModel> statModels = new ArrayList<>();
    private int dateFormat = DATE_FORMAT_LONG;
    private boolean showLegend = false;

    BaseStatsChart(LineChart lineChart) {
        this.lineChart = lineChart;

        onCreate();
    }

    public void setSelectedValuesTextViews(LinearLayout layout, TextView date, TextView min, TextView avg, TextView max) {
        this.selectedLayout = layout;
        this.selectedDateTextView = date;
        this.selectedMinValueTextView = min;
        this.selectedAvgValueTextView = avg;
        this.selectedMaxValueTextView = max;
    }

    protected void onCreate() {
        int textColor = Color.DKGRAY;

        lineChart.setOnChartValueSelectedListener(this);

        // no description text
        lineChart.getDescription().setEnabled(false);

        // enable touch gestures
        lineChart.setTouchEnabled(true);

        lineChart.setDragDecelerationFrictionCoef(0.9f);

        // enable scaling and dragging
        lineChart.setDragEnabled(true);
        lineChart.setScaleEnabled(true);
        lineChart.setDrawGridBackground(false);
        lineChart.setHighlightPerDragEnabled(true);

        // if disabled, scaling can be done on x- and y-axis separately
        lineChart.setPinchZoom(true);

        // set an alternative background color
        lineChart.setBackgroundColor(Color.WHITE);

//        lineChart.animateX(2500);

        // get the legend (only possible after setting data)
        Legend legend = lineChart.getLegend();
        legend.setEnabled(showLegend);

        // modify the legend ...
        legend.setForm(Legend.LegendForm.LINE);
//        legend.setTypeface(mTfLight);
        legend.setTextSize(11f);
        legend.setTextColor(textColor);
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        legend.setDrawInside(false);
//        legend.setYOffset(11f);

        XAxis xAxis = lineChart.getXAxis();
//        xAxis.setTypeface(mTfLight);
        xAxis.setTextSize(11f);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextColor(textColor);
        xAxis.setDrawGridLines(true);

        // X-AXIS date custom formatter:
        final Calendar calendar = new GregorianCalendar();
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                String statDateString = statModels.get((int) value).getLabel();
                try {
                    calendar.setTime(sdf.parse(statDateString));
                } catch (ParseException e) {
                    // If date parse error - return original date label
                    return statDateString;
                }

                int dateFlags = DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_NUMERIC_DATE;
                if (dateFormat == DATE_FORMAT_LONG) {
                    dateFlags |= DateUtils.FORMAT_SHOW_YEAR;
                }

                return DateUtils.formatDateTime(lineChart.getContext(), calendar.getTimeInMillis(), dateFlags);
            }
        });

        YAxis leftAxis = lineChart.getAxisLeft();
        leftAxis.setTextColor(textColor);
        leftAxis.setAxisMinimum(0f);
        leftAxis.setDrawGridLines(true);
        leftAxis.setGranularityEnabled(true);

        YAxis rightAxis = lineChart.getAxisRight();
        rightAxis.setTextColor(textColor);
        rightAxis.setAxisMinimum(0f);
    }

    /**
     * Set critical duration limit line
     *
     * @param duration Critical duration in seconds
     */
    public void setCriticalDuration(int duration) {
        // Critical duration limit line
        LimitLine limitLine = new LimitLine(duration / 60, lineChart.getContext().getString(R.string.chart_critical_duration_label));
        limitLine.setLineColor(Color.RED);
        limitLine.setLineWidth(4f);
        limitLine.setTextColor(Color.DKGRAY);
        limitLine.setLabelPosition(LimitLine.LimitLabelPosition.LEFT_TOP);
        limitLine.setTextSize(10f);

        lineChart.getAxisLeft().addLimitLine(limitLine);
    }

    public void setDateFormat(int dateFormat) {
        this.dateFormat = dateFormat;
    }

    public void setShowLegend(boolean showLegend) {
        this.showLegend = showLegend;
    }

    public void refreshChart(List<JobStatModel> statModels) {
        this.statModels = statModels;

        ArrayList<Entry> yMinVals = new ArrayList<>();
        ArrayList<Entry> yMaxVals = new ArrayList<>();
        ArrayList<Entry> yAvgVals = new ArrayList<>();

        int counter = 0;
        for (JobStatModel statModel : statModels) {
            yMinVals.add(new Entry(counter, statModel.getMinValue()));
            yMaxVals.add(new Entry(counter, statModel.getMaxValue()));
            yAvgVals.add(new Entry(counter, statModel.getAvgValue()));
            counter++;
        }

        LineDataSet minSet, maxSet, avgSet;

        if (lineChart.getData() != null && lineChart.getData().getDataSetCount() > 0) {
            minSet = (LineDataSet) lineChart.getData().getDataSetByIndex(0);
            maxSet = (LineDataSet) lineChart.getData().getDataSetByIndex(1);
            avgSet = (LineDataSet) lineChart.getData().getDataSetByIndex(2);
            minSet.setValues(yMinVals);
            maxSet.setValues(yMaxVals);
            avgSet.setValues(yAvgVals);
            lineChart.getData().notifyDataChanged();
            lineChart.notifyDataSetChanged();
        } else {
            // create a dataset and give it a type
            minSet = new LineDataSet(yMinVals, "Min time");
            customizeAxis(MIN_AXIS, minSet);

            // create a dataset and give it a type
            maxSet = new LineDataSet(yMaxVals, "Max time");
            customizeAxis(MAX_AXIS, maxSet);

            avgSet = new LineDataSet(yAvgVals, "Average time");
            customizeAxis(AVG_AXIS, avgSet);

            // create a data object with the datasets
            LineData data = new LineData(maxSet, avgSet, minSet);
            data.setValueTextColor(Color.WHITE);
            data.setValueTextSize(9f);

            // set data
            lineChart.setData(data);
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onValueSelected(Entry e, Highlight h) {
        Log.i("Entry selected", e.toString());

//        lineChart.centerViewToAnimated(e.getX(), e.getY(), lineChart.getData().getDataSetByIndex(h.getDataSetIndex())
//                .getAxisDependency(), 500);
        //lineChart.zoomAndCenterAnimated(2.5f, 2.5f, e.getX(), e.getY(), lineChart.getData().getDataSetByIndex(dataSetIndex)
        // .getAxisDependency(), 1000);
        //lineChart.zoomAndCenterAnimated(1.8f, 1.8f, e.getX(), e.getY(), lineChart.getData().getDataSetByIndex(dataSetIndex)
        // .getAxisDependency(), 1000);

        Highlight highlight[] = new Highlight[lineChart.getData().getDataSets().size()];
        for (int j = 0; j < lineChart.getData().getDataSets().size(); j++) {

            IDataSet iDataSet = lineChart.getData().getDataSets().get(j);

            for (int i = 0; i < ((LineDataSet) iDataSet).getValues().size(); i++) {
                if (((LineDataSet) iDataSet).getValues().get(i).getX() == e.getX()) {
                    highlight[j] = new Highlight(e.getX(), e.getY(), j);
                }
            }

        }
        lineChart.highlightValues(highlight);

        JobStatModel model = statModels.get((int) e.getX());

        if (selectedLayout != null && selectedLayout.getVisibility() != View.VISIBLE) {
            selectedLayout.setVisibility(View.VISIBLE);
        }
        if (selectedDateTextView != null) {
            selectedDateTextView.setText(model.getLabel());
        }
        if (selectedMinValueTextView != null) {
            selectedMinValueTextView.setText(model.getMinValue().toString());
        }
        if (selectedAvgValueTextView != null) {
            selectedAvgValueTextView.setText(model.getAvgValue().toString());
        }
        if (selectedMaxValueTextView != null) {
            selectedMaxValueTextView.setText(model.getMaxValue().toString());
        }
    }

    @Override
    public void onNothingSelected() {
        if (selectedLayout != null) {
            selectedLayout.setVisibility(View.INVISIBLE);
        }
    }

    private void customizeAxis(int axisType, LineDataSet lineDataSet) {
        Context context = lineChart.getContext();
        int circleColor = Color.WHITE;
        float lineWidth = 2f;
        float circleRadius = 3f;
        int fillAlpha = 100;
        int fillColor = Color.GREEN;
        int highLightColor = Color.rgb(244, 117, 117);

        int baseColor;
        switch (axisType) {
            case MIN_AXIS:
                // 54, 241, 57, 0.2
                // ColorTemplate.colorWithAlpha(Color.GREEN, 200)
                baseColor = context.getResources().getColor(R.color.colorChartMin);
//                baseColor = ColorTemplate.colorWithAlpha(baseColor, 200);
                break;
            case MAX_AXIS:
                // 241, 57, 54, 0.2
                baseColor = context.getResources().getColor(R.color.colorChartMax);
//                baseColor = ColorTemplate.colorWithAlpha(baseColor, 200);
                break;
            case AVG_AXIS:
                // 237, 241, 54, 0.6
                baseColor = context.getResources().getColor(R.color.colorChartAvg);
//                baseColor = ColorTemplate.colorWithAlpha(baseColor, 600);
                break;
            default:
                return;
        }
        fillColor = circleColor = highLightColor = baseColor;

        lineDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        lineDataSet.setMode(LineDataSet.Mode.HORIZONTAL_BEZIER);
        lineDataSet.setColor(baseColor);
        lineDataSet.setCircleColor(circleColor);
        lineDataSet.setLineWidth(lineWidth);
        lineDataSet.setCircleRadius(circleRadius);
        lineDataSet.setFillAlpha(fillAlpha);
        lineDataSet.setFillColor(fillColor);
        lineDataSet.setHighLightColor(highLightColor);
        lineDataSet.setDrawCircleHole(true);
        lineDataSet.setDrawFilled(true);
        lineDataSet.setDrawValues(false);
    }
}
