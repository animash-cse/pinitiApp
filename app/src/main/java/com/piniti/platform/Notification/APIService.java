package com.piniti.platform.Notification;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAAv6vloGA:APA91bFFqIeOdmvpD-1Y3PSXNL3Tc34GVpIUqJjMp8lebo363fFMv2LzpAMW8RnrlcPS1GEAAHazv64ZWJgR-pWtSWrBMVqf6m6cMfOu3YSCDxPpIDpe9ZpFYMzAhCpDIGkEg4UJnSdr"
            }
    )

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}
