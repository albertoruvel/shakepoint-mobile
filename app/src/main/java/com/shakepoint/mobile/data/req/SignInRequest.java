package com.shakepoint.mobile.data.req;

public class SignInRequest {
    private String email;
    private String password;
    private String facebookId;

    public SignInRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public SignInRequest(String facebookId) {
        this.facebookId = facebookId;
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
}
