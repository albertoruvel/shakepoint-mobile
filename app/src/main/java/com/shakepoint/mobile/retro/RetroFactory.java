package com.shakepoint.mobile.retro;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by jose.rubalcaba on 02/12/2018.
 */

public class RetroFactory {
    private static Retrofit retrofit;
    private static final String BASE_URL = "http://shakepoint.com.mx/rest/";


    public static Retrofit retrofit(){
        if (retrofit == null){
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}