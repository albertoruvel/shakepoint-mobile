package com.shakepoint.mobile.retro;

import com.shakepoint.mobile.data.req.SignupRequest;
import com.shakepoint.mobile.data.res.AuthenticationResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by jose.rubalcaba on 02/12/2018.
 */

public interface SecurityClient {

    @POST(value = "security/signup")
    public Call<AuthenticationResponse> signup(@Body SignupRequest request);

    @FormUrlEncoded
    @POST(value = "shop/shakepoint_rest_security_check")
    public Call<AuthenticationResponse> signin(@Field(value = "shakepoint_email", encoded = true)String email,  @Field(value = "shakepoint_password", encoded = true)String password);
}
