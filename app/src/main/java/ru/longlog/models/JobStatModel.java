package ru.longlog.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * JobModel stat model
 */
public class JobStatModel {
    @SerializedName("label")
    @Expose
    private String label;
    @SerializedName("minOperationId")
    @Expose
    private Integer minOperationId;
    @SerializedName("maxOperationId")
    @Expose
    private Integer maxOperationId;
    @SerializedName("avgValue")
    @Expose
    private Float avgValue;
    @SerializedName("minValue")
    @Expose
    private Float minValue;
    @SerializedName("maxValue")
    @Expose
    private Float maxValue;

    public void setMaxOperationId(Integer maxOperationId) {
        this.maxOperationId = maxOperationId;
    }

    public Integer getMaxOperationId() {
        return maxOperationId;
    }

    public void setAvgValue(Float avgValue) {
        this.avgValue = avgValue;
    }

    public Float getAvgValue() {
        return avgValue;
    }

    public void setMinValue(Float minValue) {
        this.minValue = minValue;
    }

    public Float getMinValue() {
        return minValue;
    }

    public void setMinOperationId(Integer minOperationId) {
        this.minOperationId = minOperationId;
    }

    public Integer getMinOperationId() {
        return minOperationId;
    }

    public void setMaxValue(Float maxValue) {
        this.maxValue = maxValue;
    }

    public Float getMaxValue() {
        return maxValue;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
