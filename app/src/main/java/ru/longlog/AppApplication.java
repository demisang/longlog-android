package ru.longlog;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.TextUtils;

import java.io.File;
import java.io.IOException;

import okhttp3.Request;
import ru.longlog.api.LongLogApi;
import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import ru.longlog.ui.LoginActivity;
import ru.longlog.ui.MainActivity;

public class AppApplication extends Application {

    private static LongLogApi longLogApi;
    private Retrofit retrofit;
    private static Context context;
    public static final String APP_PREFERENCES = "clientSettings";
    /**
     * Current application API version.
     * If this value is incompatible with server API version - show error alert
     */
    private static final String APP_API_VERSION = "1";
    private static final String DEFAULT_HOST = "http://api.longlog.ru/";
    private static SharedPreferences settings;
    private static String accessToken;
    private static String baseUrl;
    private static Cache httpCache;

    @Override
    public void onCreate() {
        super.onCreate();
        AppApplication.context = getApplicationContext();

        AppApplication.settings = context.getSharedPreferences(AppApplication.APP_PREFERENCES, Context.MODE_PRIVATE);

        OkHttpClient.Builder client = new OkHttpClient.Builder();

        // Add Bearer auth token and API version to request headers
        client.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                String token = getAccessToken();
                if (TextUtils.isEmpty(token)) {
                    return chain.proceed(chain.request());
                }

                Request newRequest = chain.request().newBuilder()
                        .addHeader("Authorization", "Bearer " + token)
                        .addHeader("ApiVersion", getAppApiVersion())
                        .build();
                return chain.proceed(newRequest);
            }
        });
        // Add Cache-Control headers interceptor
        client.networkInterceptors().add(REWRITE_CACHE_CONTROL_INTERCEPTOR);

        // Setup cache
        File httpCacheDirectory = new File(context.getCacheDir(), "responses");
        int cacheSize = 10 * 1024 * 1024; // 10 MiB
        httpCache = new Cache(httpCacheDirectory, cacheSize);

        // Add cache to the client
        client.cache(httpCache);

        retrofit = new Retrofit.Builder()
                .baseUrl(getBaseUrl()) // Base URL
                .client(client.build())
                .addConverterFactory(GsonConverterFactory.create()) // Json converter
                .build();
        longLogApi = retrofit.create(LongLogApi.class); // Requests object
    }

    public static Context getAppContext() {
        return AppApplication.context;
    }

    public static LongLogApi getApi() {
        return longLogApi;
    }

    public static SharedPreferences getSettings() {
        return settings;
    }

    public static String getAccessToken() {
        if (!TextUtils.isEmpty(accessToken)) {
            return accessToken;
        }

        return accessToken = getSettings().getString("accessToken", "");
    }

    public static void setAccessToken(String accessToken) {
        if (!accessToken.contentEquals(AppApplication.accessToken)) {
            // Invalidate OkHttpClient cache
            try {
                httpCache.evictAll();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        AppApplication.accessToken = accessToken;

        getSettings().edit().putString("accessToken", accessToken).apply();
    }

    public static String getBaseUrl() {
        if (!TextUtils.isEmpty(baseUrl)) {
            return baseUrl;
        }

        return baseUrl = getSettings().getString("baseUrl", DEFAULT_HOST);
    }

    public static void setBaseUrl(String baseUrl) {
        AppApplication.baseUrl = baseUrl;

        getSettings().edit().putString("baseUrl", baseUrl).apply();
    }

    public static String getAppApiVersion() {
        return APP_API_VERSION;
    }

    /**
     * Show login screen if auth failed
     *
     * @param context Activity
     */
    public static void processFailedAuth(Activity context) {
        // Show login activity
        Intent intent = new Intent(context, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
        context.finish();
    }

    private static final Interceptor REWRITE_CACHE_CONTROL_INTERCEPTOR = new Interceptor() {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Response originalResponse = chain.proceed(chain.request());
            if (Utils.isNetworkAvailable(getAppContext())) {
                int maxAge = 60 * 10; // read from cache for 10 minutes
                return originalResponse.newBuilder()
                        .header("Cache-Control", "public, max-age=" + maxAge)
                        .build();
            } else {
                int maxStale = 60 * 60 * 24 * 28; // tolerate 4-weeks stale
                return originalResponse.newBuilder()
                        .header("Cache-Control", "public, only-if-cached, max-stale=" + maxStale)
                        .build();
            }
        }
    };
}
