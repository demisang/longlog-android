package ru.longlog.api;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import ru.longlog.models.AuthResponse;
import ru.longlog.models.CheckVersionResponse;
import ru.longlog.models.JobStatModel;
import ru.longlog.models.ProjectModel;

public interface LongLogApi {
    @GET("/check-version")
    Call<CheckVersionResponse> checkVersion(@Query("version") String version);

    @FormUrlEncoded
    @POST("/auth")
    Call<AuthResponse> accessToken(@Field("login") String login, @Field("password") String password);

    @GET("/projects")
    Call<List<ProjectModel>> projects();

    @GET("/project/{id}")
    Call<ProjectModel> project(@Path("id") int id);

    @GET("/job/{id}/stats")
    Call<List<JobStatModel>> stats(@Path("id") long id);
}
