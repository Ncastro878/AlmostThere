
package com.nickrcastro.android.almostthere.api_results;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Sun {

    @SerializedName("rise")
    @Expose
    private Rise rise;
    @SerializedName("set")
    @Expose
    private Set set;

    public Rise getRise() {
        return rise;
    }

    public void setRise(Rise rise) {
        this.rise = rise;
    }

    public Set getSet() {
        return set;
    }

    public void setSet(Set set) {
        this.set = set;
    }

}
