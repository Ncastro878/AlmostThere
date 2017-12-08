
package com.example.android.almostthere.api_results;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Timestamp {

    @SerializedName("created_http")
    @Expose
    private String createdHttp;
    @SerializedName("created_unix")
    @Expose
    private Integer createdUnix;

    public String getCreatedHttp() {
        return createdHttp;
    }

    public void setCreatedHttp(String createdHttp) {
        this.createdHttp = createdHttp;
    }

    public Integer getCreatedUnix() {
        return createdUnix;
    }

    public void setCreatedUnix(Integer createdUnix) {
        this.createdUnix = createdUnix;
    }

}
