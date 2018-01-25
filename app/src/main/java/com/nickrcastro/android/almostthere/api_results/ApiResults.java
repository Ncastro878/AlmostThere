
package com.nickrcastro.android.almostthere.api_results;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ApiResults {

    @SerializedName("annotations")
    @Expose
    private Annotations annotations;
    @SerializedName("bounds")
    @Expose
    private Bounds bounds;
    @SerializedName("components")
    @Expose
    private Components components;
    @SerializedName("confidence")
    @Expose
    private Integer confidence;
    @SerializedName("formatted")
    @Expose
    private String formatted;
    @SerializedName("geometry")
    @Expose
    private Geometry geometry;

    public Annotations getAnnotations() {
        return annotations;
    }

    public void setAnnotations(Annotations annotations) {
        this.annotations = annotations;
    }

    public Bounds getBounds() {
        return bounds;
    }

    public void setBounds(Bounds bounds) {
        this.bounds = bounds;
    }

    public Components getComponents() {
        return components;
    }

    public void setComponents(Components components) {
        this.components = components;
    }

    public Integer getConfidence() {
        return confidence;
    }

    public void setConfidence(Integer confidence) {
        this.confidence = confidence;
    }

    public String getFormatted() {
        return formatted;
    }

    public void setFormatted(String formatted) {
        this.formatted = formatted;
    }

    public Geometry getGeometry() {
        return geometry;
    }

    public void setGeometry(Geometry geometry) {
        this.geometry = geometry;
    }

}
