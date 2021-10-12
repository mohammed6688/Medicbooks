package com.mocomp.developer.medicbooks.notification;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAARLTqXps:APA91bGzutKoeTbYZ-XXd24tdypC-Btb02oiGV2-1xYrSbiNFJfTmr4yp_PGRV3Iu5TwTAZZrPEUaCAR2RFNEepl5H18hLfBjXAnUimcJs2eGFKnVzL6Vo_2PoSFs8-bsootMdPtgDIX"
            }
    )

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}
