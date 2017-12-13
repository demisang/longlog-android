package ru.longlog.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * JobModel response
 */
public class JobModel {
    @SerializedName("createdAt")
    @Expose
    private String createdAt;
    @SerializedName("critDuration")
    @Expose
    private Float critDuration;
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("projectId")
    @Expose
    private Integer projectId;
    @SerializedName("key")
    @Expose
    private String key;

    @SerializedName("stats")
    @Expose
    private List<JobStatModel> stats;

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCritDuration(Float critDuration) {
        this.critDuration = critDuration;
    }

    public Float getCritDuration() {
        return critDuration;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setProjectId(Integer projectId) {
        this.projectId = projectId;
    }

    public Integer getProjectId() {
        return projectId;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public List<JobStatModel> getStats() {
        return stats;
    }

    public void setStats(List<JobStatModel> stats) {
        this.stats = stats;
    }
}
