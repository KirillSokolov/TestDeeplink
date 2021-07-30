package com.testtask.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;

public class BrowserData extends RealmObject {

    @PrimaryKey
    @SerializedName("id")
    @Expose
    private Long id = 1L;

    @SerializedName("enable")
    @Expose
    private Boolean enable = true;

    @SerializedName("editable")
    @Expose
    private Boolean editable = true;

    @SerializedName("showOboarding")
    @Expose
    private Boolean showOboarding = true;

    @SerializedName("url")
    @Expose
    private String url;

    @SerializedName("domain")
    @Expose
    private String domain;

    @SerializedName("land")
    @Expose
    private String land;

    public BrowserData() {

    }

    public BrowserData(String url) {
        this.url = url;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getEnable() {
        return enable;
    }

    public void setEnable(Boolean enable) {
        this.enable = enable;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }


    public Boolean getShowOboarding() {
        return showOboarding;
    }

    public void setShowOboarding(Boolean showOboarding) {
        this.showOboarding = showOboarding;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getLand() {
        return land;
    }

    public void setLand(String land) {
        this.land = land;
    }

    public Boolean getEditable() {
        return editable;
    }

    public void setEditable(Boolean editable) {
        this.editable = editable;
    }
}