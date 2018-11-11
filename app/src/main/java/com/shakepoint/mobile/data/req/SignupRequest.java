package com.shakepoint.mobile.data.req;

/**
 * Created by jose.rubalcaba on 02/12/2018.
 */

public class SignupRequest {
    private String name;
    private String email;
    private String password;
    private String birthdate;
    private String facebookId;

    public SignupRequest(String name, String email, String password, String birthDate) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.birthdate = birthDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(String birthDate) {
        this.birthdate = birthDate;
    }

    public String getFacebookId() {
        return facebookId;
    }

    public void setFacebookId(String facebookId) {
        this.facebookId = facebookId;
    }
}
