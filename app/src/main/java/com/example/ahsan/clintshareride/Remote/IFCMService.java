package com.example.ahsan.clintshareride.Remote;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

/**
 * Created by ahsan on 11/8/18.
 */

public interface IFCMService {

    @Headers({
            "Content-Type:application/json",
            "Authorization:key=AAAAV39nD5w:APA91bEIwjGzGBIUalDnsbma7YAvK-6v9eR8UmZegHvy9eZQ2cCEbDaNyUhwQo5dpTWTOGig8QTalns4ao5sg8EntSJa7qtKc7_plDn4IWBJviUpZVeXT-JjucHSF_ZDUGkHOu-WykXmg9UnYuhHuMg_xQHULMxmkg"
    })

    @POST("fcm/send")
    Call<String> sendMessage(@Body String body);
}
