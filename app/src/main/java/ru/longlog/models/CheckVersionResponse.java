package ru.longlog.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Check version response
 */
public class CheckVersionResponse {
    @SerializedName("isCompatible")
    @Expose
    private boolean isCompatible;

    @SerializedName("isLatest")
    @Expose
    private boolean isLatest;

    @SerializedName("latestVersion")
    @Expose
    private String latestVersion;

    @SerializedName("supportedVersions")
    @Expose
    private ArrayList<String> supportedVersions;

    public boolean isCompatible() {
        return isCompatible;
    }

    public void setCompatible(boolean compatible) {
        isCompatible = compatible;
    }

    public boolean isLatest() {
        return isLatest;
    }

    public void setLatest(boolean latest) {
        isLatest = latest;
    }

    public String getLatestVersion() {
        return latestVersion;
    }

    public void setLatestVersion(String latestVersion) {
        this.latestVersion = latestVersion;
    }

    public ArrayList<String> getSupportedVersions() {
        return supportedVersions;
    }

    public void setSupportedVersions(ArrayList<String> supportedVersions) {
        this.supportedVersions = supportedVersions;
    }
}
