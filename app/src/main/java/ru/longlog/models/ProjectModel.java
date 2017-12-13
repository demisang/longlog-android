package ru.longlog.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Project response
 */
public class ProjectModel {
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("token")
    @Expose
    private String token;
    @SerializedName("ownerId")
    @Expose
    private Integer ownerId;
    @SerializedName("isViewable")
    @Expose
    private Boolean isViewable;
    @SerializedName("isManageable")
    @Expose
    private Boolean isManageable;
    @SerializedName("createdAt")
    @Expose
    private String createdAt;
    @SerializedName("updatedAt")
    @Expose
    private String updatedAt;

    @SerializedName("currentProjectUser")
    @Expose
    private CurrentProjectUser currentProjectUser;
    @SerializedName("jobs")
    @Expose
    private List<JobModel> jobs;

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setIsManageable(Boolean isManageable) {
        this.isManageable = isManageable;
    }

    public Boolean getIsManageable() {
        return isManageable;
    }

    public void setCurrentProjectUser(CurrentProjectUser currentProjectUser) {
        this.currentProjectUser = currentProjectUser;
    }

    public CurrentProjectUser getCurrentProjectUser() {
        return currentProjectUser;
    }

    public void setJobs(List<JobModel> jobModels) {
        this.jobs = jobModels;
    }

    public List<JobModel> getJobs() {
        return jobs;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setOwnerId(Integer ownerId) {
        this.ownerId = ownerId;
    }

    public Integer getOwnerId() {
        return ownerId;
    }

    public void setIsViewable(Boolean isViewable) {
        this.isViewable = isViewable;
    }

    public Boolean getIsViewable() {
        return isViewable;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    /**
     * Current project user response
     */
    public class CurrentProjectUser {
        @SerializedName("role")
        @Expose
        private String role;
        @SerializedName("userId")
        @Expose
        private Integer userId;

        public void setRole(String role) {
            this.role = role;
        }

        public String getRole() {
            return role;
        }

        public void setUserId(Integer userId) {
            this.userId = userId;
        }

        public Integer getUserId() {
            return userId;
        }
    }
}
