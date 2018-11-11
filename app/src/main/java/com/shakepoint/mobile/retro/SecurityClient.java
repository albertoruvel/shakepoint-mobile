package com.shakepoint.mobile.retro;

import com.shakepoint.mobile.data.req.ResetPasswordRequest;
import com.shakepoint.mobile.data.req.SignInRequest;
import com.shakepoint.mobile.data.req.SignupRequest;
import com.shakepoint.mobile.data.req.ValidateForgotPasswordTokenResponse;
import com.shakepoint.mobile.data.res.AuthenticationResponse;
import com.shakepoint.mobile.data.res.ForgotPasswordResponse;
import com.shakepoint.mobile.data.res.ResetPasswordResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface SecurityClient {

    @POST(value = "security/signUp")
    public Call<AuthenticationResponse> signup(@Body SignupRequest request);

    @POST(value = "security/signIn")
    public Call<AuthenticationResponse> signin(@Body SignInRequest request);

    @POST(value = "security/forgotPassword")
    public Call<ForgotPasswordResponse> forgotPasswordRequest(@Query("email")String email);

    @POST(value = "security/validateForgotPasswordToken")
    public Call<ValidateForgotPasswordTokenResponse> validateForgotPasswordToken(@Query("token")String token);

    @POST(value = "security/resetPassword")
    Call<ResetPasswordResponse> resetPassword(@Body  ResetPasswordRequest request);
}
