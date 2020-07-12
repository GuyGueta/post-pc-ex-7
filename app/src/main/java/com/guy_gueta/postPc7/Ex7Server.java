package com.guy_gueta.postPc7;


import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;


public interface Ex7Server {

    class TokenResponse {
        String data;
    }

    class UserResponse {
        User data;
    }

    class User {
        String image_url;
        String pretty_name;
        String username;
    }

    class SetUserPrettyNameRequest {
        String pretty_name;
    }

    @GET("users/{username}/token/")
    Call<TokenResponse>  getToken(@Path("username") String userName);

    @GET("user/")
    Call<UserResponse> getUserInfo(@Header("Authorization") String auth);

    @Headers({"Content-Type: application/json"})
    @POST("/user/edit/")
    Call<UserResponse> setUserPrettyName(@Header("Authorization") String auth,
                                         @Body SetUserPrettyNameRequest userPrettyNameRequest);

}
