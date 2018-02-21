package com.shakepoint.mobile.data.res;

/**
 * Created by jose.rubalcaba on 02/13/2018.
 */

public class ProfileResponse {
    private String userName;
    private String userId;
    private String userSince;
    private boolean availableProfile;
    private int age;
    private String birthday;
    private double weight;
    private double height;
    private double purchasesTotal;
    private String email;
    private double cmi;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserSince() {
        return userSince;
    }

    public void setUserSince(String userSince) {
        this.userSince = userSince;
    }

    public boolean isAvailableProfile() {
        return availableProfile;
    }

    public void setAvailableProfile(boolean availableProfile) {
        this.availableProfile = availableProfile;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public double getPurchasesTotal() {
        return purchasesTotal;
    }

    public void setPurchasesTotal(double purchasesTotal) {
        this.purchasesTotal = purchasesTotal;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public double getCmi() {
        return cmi;
    }

    public void setCmi(double cmi) {
        this.cmi = cmi;
    }
}
