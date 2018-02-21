package com.shakepoint.mobile.data.req;

/**
 * Created by jose.rubalcaba on 02/14/2018.
 */

public class ProfileRequest {
    private int age;
    private double height;
    private double weight;
    private String birthday;

    public ProfileRequest(int age, double height, double weight, String birthday) {
        this.age = age;
        this.height = height;
        this.weight = weight;
        this.birthday = birthday;
    }

    public int getAge() {
        return age;
    }
    public void setAge(int age) {
        this.age = age;
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
