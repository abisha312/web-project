package com.smart.alerts.services;
import com.smart.alerts.models.APIResult;
import com.smart.alerts.models.CustomerDetailRequest;
import com.smart.alerts.models.CustomerDetailResponse;
import com.smart.alerts.models.DealListResult;
import com.smart.alerts.models.DealModel;
import com.smart.alerts.models.ExistingDealModel;
import com.smart.alerts.models.ForgotPwRequest;
import com.smart.alerts.models.ForgotPwResponse;
import com.smart.alerts.models.LoginRequest;
import com.smart.alerts.models.ShopDetailRequest;
import com.smart.alerts.models.ShopDetailResponse;
import com.smart.alerts.models.SigninResponse;
import com.smart.alerts.models.SignupRequest;
import com.smart.alerts.models.SignupResponse;
import com.smart.alerts.models.UserModel;
import com.smart.alerts.models.GeoFenceRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.POST;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface APIService {
    @POST("Account/Authenticate")
    Call<APIResult<SigninResponse>> AuthenticateUser(@Body LoginRequest loginRequest);
    @POST("Account/Register")
    Call<APIResult<SignupResponse>> RegisterUser(@Body SignupRequest signupRequest);
    @POST("Account/ForgotPassword")
    Call<APIResult<ForgotPwResponse>> ForgotPassword(@Body ForgotPwRequest forgotPasswordRequest);
    @POST("Detail/addshopdetail")
    Call<APIResult<ShopDetailResponse>> AddShopDetail(@Body ShopDetailRequest shopDetailRequest);
    @POST("Detail/addcustomerdetail")
    Call<APIResult<CustomerDetailResponse>> AddCustomerDetail(@Body CustomerDetailRequest customerDetailRequest);
    @POST("Sale/AddDeal")
    Call<APIResult<ExistingDealModel>> AddDeal(@Body DealModel deal);
    @POST("Sale/UpdateDeal")
    Call<APIResult<ExistingDealModel>> UpdateDeal(@Body ExistingDealModel existingDeal);
    @GET("UserAPI/GetProfile")
    Call<APIResult<UserModel>> GetProfile(@Query("userId") long userId);
    @POST("Sale/ViewDeals")
    Call<APIResult<DealListResult>> ViewDeals(@Query("shopId") long shopId);
    @POST("Sale/DeleteDeal")
    Call<APIResult<String>> deleteDeal(@Query("shopDealId") long shopDealId);
    @POST("GeoFence/CheckProximity")
    Call<APIResult<String>> CheckProximity(@Body GeoFenceRequest request);

}
