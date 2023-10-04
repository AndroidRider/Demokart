package com.androidrider.demokartadmin.Notification

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface NotificationService {
    @Headers("Authorization: key=AAAAV56fk9E:APA91bGBUxm3Mi_XZkodIpJFALJbibD8DWLshqccSGNanbV21QcpBgaFiaf99AmkoTPUtBkazaZfTrZq2piVt5GIyf7LHaqgrI6tjaD3uBtbRorGqyhHOrW9ych_e0il826z6r0PzHC2",
        "Content-Type: application/json")

//    @POST("your_notification_endpoint")
    @POST("/fcm/send")
    fun sendNotification(@Body payload: NotificationPayload): Call<Void>
}







