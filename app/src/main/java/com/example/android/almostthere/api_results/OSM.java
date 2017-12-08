
package com.example.android.almostthere.api_results;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class OSM {

    @SerializedName("edit_url")
    @Expose
    private String editUrl;
    @SerializedName("url")
    @Expose
    private String url;

    public String getEditUrl() {
        return editUrl;
    }

    public void setEditUrl(String editUrl) {
        this.editUrl = editUrl;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

}
