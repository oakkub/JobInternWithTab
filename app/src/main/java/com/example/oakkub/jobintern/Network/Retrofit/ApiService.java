package com.example.oakkub.jobintern.Network.Retrofit;

import com.example.oakkub.jobintern.Objects.CheckNewJobAdvance;
import com.example.oakkub.jobintern.Objects.JobAdvance;
import com.example.oakkub.jobintern.Objects.JobUpdateManager;
import com.example.oakkub.jobintern.Objects.ProgressCallback;

import java.util.List;

import retrofit.Callback;
import retrofit.client.Response;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Query;

/**
 * Created by OaKKuB on 7/29/2015.
 */
public interface ApiService {

    @GET("/fetch_job_advance.php")
    void getJobAdvance(@Query("condition") String condition,
                       @Query("startFetchPosition") int startFetchPosition,
                       @Query("fetchAmount") int fetchAmount,
                       Callback<List<JobAdvance>> jobAdvancesCallback);

    @GET("/fetch_job_advance.php")
    void getSearchJobAdvance(@Query("condition") String condition,
                             @Query("searchQuery") String searchQuery,
                             @Query("startFetchPosition") int startFetchPosition,
                             @Query("fetchAmount") int fetchAmount,
                             Callback<List<JobAdvance>> searchResultJobAdvancesCallback);

    @GET("/check_job_advance.php")
    void checkNewJobAdvance(Callback<CheckNewJobAdvance> checkNewJobAdvanceCallback);

    @GET("/set_notified_job_advance.php")
    Response setNotifiedJobAdvance();

    @FormUrlEncoded
    @POST("/login.php")
    void hasUser(@Field("username") String username,
                 @Field("password") String password,
                 Callback<ProgressCallback> checkUserCallback);

    @FormUrlEncoded
    @POST("/approve_job_advance.php")
    void approveJobAdvance(@Field("jobAdvanceId") String jobAdvanceId,
                           @Field("approveBy") String userApprove,
                           Callback<JobUpdateManager> checkApproveCallback);

    @FormUrlEncoded
    @POST("/postpone_job_advance.php")
    void postponeJobAdvance(@Field("jobAdvanceId") String jobAdvanceId,
                            Callback<JobUpdateManager> checkPostponeCallback);

    @FormUrlEncoded
    @POST(("/cancel_job_advance.php"))
    void cancelJobAdvance(@Field("jobAdvanceId") String jobAdvanceId,
                          @Field("cancelBy") String cancelBy,
                          Callback<JobUpdateManager> checkCancelCallback);
}
