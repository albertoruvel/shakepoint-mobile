package com.shakepoint.mobile.data.req;

/**
 * Created by jose.rubalcaba on 02/14/2018.
 */

public class ProfileRequest {
    private double height;
    private double weight;

    public ProfileRequest(double height, double weight) {
        this.height = height;
        this.weight = weight;
    }
    public double getHeight() {
        return height;
    }
    public void setHeight(double height) {
        this.height = height;
    }
    public double getWeight() {
        return weight;
    }
    public void setWeight(double weight) {
        this.weight = weight;
    }
}
