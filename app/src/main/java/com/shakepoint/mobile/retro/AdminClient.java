package com.shakepoint.mobile.retro;

import com.shakepoint.mobile.data.req.CreatePartnerRequest;
import com.shakepoint.mobile.data.req.CreatePromoCodeRequest;
import com.shakepoint.mobile.data.req.admin.CreateFlavorRequest;
import com.shakepoint.mobile.data.req.admin.CreateTrainerRequest;
import com.shakepoint.mobile.data.req.admin.NewVendingRequest;
import com.shakepoint.mobile.data.res.ProductResponse;
import com.shakepoint.mobile.data.res.admin.CreatePromoCodeResponse;
import com.shakepoint.mobile.data.res.admin.GetVendingProducts;
import com.shakepoint.mobile.data.res.admin.Partner;
import com.shakepoint.mobile.data.res.admin.Product;
import com.shakepoint.mobile.data.res.admin.ProductFlavor;
import com.shakepoint.mobile.data.res.admin.Promotion;
import com.shakepoint.mobile.data.res.admin.Trainer;
import com.shakepoint.mobile.data.res.admin.Vending;
import com.shakepoint.mobile.data.res.admin.VendingProductDetails;
import com.shakepoint.mobile.data.res.admin.VendingProductResponse;

import java.util.List;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface AdminClient {

    @GET("admin/getVendings")
    public Call<List<Vending>> getVendings(@Header("Authorization") String token);

    @GET("admin/getProducts")
    public Call<List<ProductResponse>> getProducts(@Header("Authorization") String token);

    @GET("admin/getVendingProducts")
    public Call<GetVendingProducts> getVendingProducts(@Header("Authorization") String token, @Query("machineId") String id);

    @POST("admin/addVendingProduct")
    public Call<VendingProductResponse> addVendingProduct(@Header("Authorization") String token, @Query("machineId") String machineId,
                                                          @Query("productId") String productId, @Query("slotNumber") int slotNumber);

    @POST("admin/deleteVendingProduct")
    public Call<Void> removeVendingProduct(@Header("Authorization") String token, @Query("vendingProductId") String id);

    @Multipart
    @POST("admin/createProduct")
    public Call<Void> createProduct(@Header("Authorization") String token,
                                    @Part("name") RequestBody name, @Part("price") RequestBody price, @Part("description") RequestBody description,
                                    @Part("logoUrl") RequestBody logoUrl, @Part("engineUseTime") RequestBody engineUseTime, @Part("productType") RequestBody productType,
                                    @Part("file") RequestBody file);

    @POST("admin/createPartner")
    public Call<Void> createPartner(@Header("Authorization") String token, @Body CreatePartnerRequest request);

    @GET("admin/getActivePromos")
    public Call<List<Promotion>> getActivePromos(@Header("Authorization") String token);

    @GET("admin/getAllTrainers")
    public Call<List<Trainer>> getAllTrainers(@Header("Authorization") String token);

    @POST("admin/createPromoCode")
    public Call<CreatePromoCodeResponse> createPromoCode(@Header("Authorization") String token, @Body CreatePromoCodeRequest request);

    @POST("admin/createVending")
    public Call<Void> createVending(@Header("Authorization") String token, @Body NewVendingRequest request);

    @GET("admin/getPartners")
    public Call<List<Partner>> getPartners(@Header("Authorization") String token);

    @POST("admin/createTrainer")
    public Call<Void> createTrainer(@Header("Authorization") String token, @Body CreateTrainerRequest request);

    @POST("admin/deactivatePromoCode")
    public Call<Void> deactivatePromoCode(@Header("Authorization") String token, @Query("promoCodeId") String id);

    @GET("admin/getFlavors")
    public Call<List<ProductFlavor>> getFlavors(@Header("Authorization")String token);

    @POST("admin/createFlavor")
    public Call<Void> createFlavor(@Header("Authorization")String token, @Body CreateFlavorRequest request);
}
