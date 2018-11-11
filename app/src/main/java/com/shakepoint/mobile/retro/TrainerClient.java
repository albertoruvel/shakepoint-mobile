package com.shakepoint.mobile.retro;

import com.shakepoint.mobile.data.res.admin.Promotion;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;

public interface TrainerClient {

    @GET("trainer/getAssignedPromos")
    public Call<List<Promotion>> getTrainerPromoCodes(@Header("Authorization") String token);
}
