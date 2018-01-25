
package com.nickrcastro.android.almostthere.api_results;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Rise {

    @SerializedName("apparent")
    @Expose
    private Integer apparent;
    @SerializedName("astronomical")
    @Expose
    private Integer astronomical;
    @SerializedName("civil")
    @Expose
    private Integer civil;
    @SerializedName("nautical")
    @Expose
    private Integer nautical;

    public Integer getApparent() {
        return apparent;
    }

    public void setApparent(Integer apparent) {
        this.apparent = apparent;
    }

    public Integer getAstronomical() {
        return astronomical;
    }

    public void setAstronomical(Integer astronomical) {
        this.astronomical = astronomical;
    }

    public Integer getCivil() {
        return civil;
    }

    public void setCivil(Integer civil) {
        this.civil = civil;
    }

    public Integer getNautical() {
        return nautical;
    }

    public void setNautical(Integer nautical) {
        this.nautical = nautical;
    }

}
