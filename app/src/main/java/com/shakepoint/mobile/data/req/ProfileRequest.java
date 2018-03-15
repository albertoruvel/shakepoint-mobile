package com.shakepoint.mobile.data.req;

/**
 * Created by jose.rubalcaba on 02/14/2018.
 */

public class ProfileRequest {
    private double height;
    private double weight;
    private String birthday;

    public ProfileRequest(double height, double weight, String birthday) {
        this.height = height;
        this.weight = weight;
        this.birthday = birthday;
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
    public String getBirthday() {
        return birthday;
    }
    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }
}
