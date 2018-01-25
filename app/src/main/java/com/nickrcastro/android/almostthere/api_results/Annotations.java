
package com.nickrcastro.android.almostthere.api_results;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Annotations {

    @SerializedName("DMS")
    @Expose
    private DMS dMS;
    @SerializedName("MGRS")
    @Expose
    private String mGRS;
    @SerializedName("Maidenhead")
    @Expose
    private String maidenhead;
    @SerializedName("Mercator")
    @Expose
    private Mercator mercator;
    @SerializedName("OSM")
    @Expose
    private OSM oSM;
    @SerializedName("callingcode")
    @Expose
    private Integer callingcode;
    @SerializedName("geohash")
    @Expose
    private String geohash;
    @SerializedName("sun")
    @Expose
    private Sun sun;
    @SerializedName("timezone")
    @Expose
    private Timezone timezone;
    @SerializedName("what3words")
    @Expose
    private What3words what3words;

    public DMS getDMS() {
        return dMS;
    }

    public void setDMS(DMS dMS) {
        this.dMS = dMS;
    }

    public String getMGRS() {
        return mGRS;
    }

    public void setMGRS(String mGRS) {
        this.mGRS = mGRS;
    }

    public String getMaidenhead() {
        return maidenhead;
    }

    public void setMaidenhead(String maidenhead) {
        this.maidenhead = maidenhead;
    }

    public Mercator getMercator() {
        return mercator;
    }

    public void setMercator(Mercator mercator) {
        this.mercator = mercator;
    }

    public OSM getOSM() {
        return oSM;
    }

    public void setOSM(OSM oSM) {
        this.oSM = oSM;
    }

    public Integer getCallingcode() {
        return callingcode;
    }

    public void setCallingcode(Integer callingcode) {
        this.callingcode = callingcode;
    }

    public String getGeohash() {
        return geohash;
    }

    public void setGeohash(String geohash) {
        this.geohash = geohash;
    }

    public Sun getSun() {
        return sun;
    }

    public void setSun(Sun sun) {
        this.sun = sun;
    }

    public Timezone getTimezone() {
        return timezone;
    }

    public void setTimezone(Timezone timezone) {
        this.timezone = timezone;
    }

    public What3words getWhat3words() {
        return what3words;
    }

    public void setWhat3words(What3words what3words) {
        this.what3words = what3words;
    }

}
