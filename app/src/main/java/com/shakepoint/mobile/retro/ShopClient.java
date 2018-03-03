package com.shakepoint.mobile.retro;

import com.shakepoint.mobile.data.req.ConfirmPurchaseRequest;
import com.shakepoint.mobile.data.req.ProfileRequest;
import com.shakepoint.mobile.data.res.AvailablePurchaseResponse;
import com.shakepoint.mobile.data.res.MachineSearchResponse;
import com.shakepoint.mobile.data.res.ProductResponse;
import com.shakepoint.mobile.data.res.ProductResponseWrapper;
import com.shakepoint.mobile.data.res.ProfileResponse;
import com.shakepoint.mobile.data.res.PurchaseConfirmationResponse;
import com.shakepoint.mobile.data.res.PurchaseResponse;
import com.shakepoint.mobile.data.res.QrCodeResponse;
import com.shakepoint.mobile.data.res.SimpleQRCodeResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by jose.rubalcaba on 02/13/2018.
 */

public interface ShopClient {

    @GET("shop/profile")
    public Call<ProfileResponse> getUserProfile(@Header(value = "Authorization") String token);

    @POST("shop/saveProfile")
    public Call<ProfileResponse> saveProfile(@Header(value = "Authorization") String token, @Body ProfileRequest request);

    @GET("shop/searchMachine")
    public Call<MachineSearchResponse> searchClosestMachine(@Header("Authorization") String token, @Query("latitude") double lat, @Query("longitude") double lon);

    @GET("shop/searchMachineByName")
    public Call<List<MachineSearchResponse>> searchMachinesByName(@Header("Authorization")String token, @Query("name")String name);

    @GET("shop/getProducts")
    public Call<ProductResponseWrapper> getProducts(@Header("Authorization")String token, @Query("machineId")String machineId);

    @GET("shop/productDetails")
    public Call<ProductResponse>getProductDetails(@Header("Authorization")String token, @Query("productId")String productId);

    @GET("shop/getPurchases")
    public Call<List<PurchaseResponse>> getUserPurchases(@Header("Authorization")String token);

    @GET("shop/getAuthorizedPurchases")
    public Call<List<QrCodeResponse>> getActiveCodesByMachine(@Header("Authorization")String token, @Query("machineId")String machineId);

    @POST("shop/confirmPurchase")
    public Call<PurchaseConfirmationResponse> confirmPurchase(@Header("Authorization")String token, @Body ConfirmPurchaseRequest request);

    @GET("shop/getAvailablePurchaseForMachine")
    public Call<AvailablePurchaseResponse> getAvailablePurchaseForMachine(@Header("Authorization")String token, @Query("productId") String productId, @Query("machineId")String machineId);

}
