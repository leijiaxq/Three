package com.maymeng.three.api;


import com.maymeng.three.base.BaseApplication;
import com.maymeng.three.utils.CommonUtil;
import com.maymeng.three.utils.LogUtil;

import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * Created by hcc on 16/8/4 21:18
 * 100332338@qq.com
 * <p/>
 * Retrofit帮助类
 */
public class RetrofitHelper {

    public static final String TAG = RetrofitHelper.class.getSimpleName();
    private final static int    CACHE_SIZE = 10 * 1024 * 1024; // 10 MiB
    public static RetrofitHelper mRetrofitHelper;

    public static BaseService getBaseApi() {
        return createApi(BaseService.class, Constants.BASE_URL);
    }

    private RetrofitHelper() {
    }

/*    public static RetrofitHelper getInstance() {
        if (mRetrofitHelper == null) {
            synchronized (RetrofitHelper.class) {
                if (mRetrofitHelper == null) {
                    mRetrofitHelper = new RetrofitHelper();
                }
            }
        }
        return mRetrofitHelper;
    }*/

    /**
     * 根据传入的baseUrl，和api创建retrofit
     */
    private static  <T> T createApi(Class<T> clazz, String baseUrl) {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(client)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit.create(clazz);
    }

//    private File httpCacheDirectory = new File(BaseApplication.getInstance().getCacheDir(), "Ant");

    private static Cache cache = new Cache(new File(BaseApplication.getInstance().getCacheDir(), "Ant"), CACHE_SIZE);


    //缓存拦截
    private class CacheInterceptor implements Interceptor {

        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            if (CommonUtil.isNetworkAvailable(BaseApplication.getInstance())) {
                Response response = chain.proceed(request);
                // read from cache for 60 s
                int maxAge = 30;
                String cacheControl = request.cacheControl().toString();
                LogUtil.d(TAG, maxAge + "s load cahe:" + cacheControl);
                return response.newBuilder()
                        .removeHeader("Pragma")
                        .removeHeader("Cache-Control")
                        .header("Cache-Control", "public, max-age=" + maxAge)
                        .build();
            } else {
                LogUtil.d(TAG, " no network load cahe");
                request = request.newBuilder()
                        .cacheControl(CacheControl.FORCE_CACHE)
                        .build();
                Response response = chain.proceed(request);
                //set cahe times is 3 days
                int maxStale = 60 * 60 * 24 * 3;
                return response.newBuilder()
                        .removeHeader("Pragma")
                        .removeHeader("Cache-Control")
                        .header("Cache-Control", "public, only-if-cached, max-stale=" + maxStale)
                        .build();
            }
        }
    }


    //日志拦截
    private static class LogInterceptor implements Interceptor {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            LogUtil.d(TAG, "request:" + request.toString());

            long t1 = System.nanoTime();
            Response response = chain.proceed(chain.request());
            long t2 = System.nanoTime();
            LogUtil.d(TAG, String.format(Locale.getDefault(), "Received response for %s in %.1fms%n%s",
                    response.request().url(), (t2 - t1) / 1e6d, response.headers()));
            okhttp3.MediaType mediaType = response.body().contentType();
            String content = response.body().string();
            LogUtil.d(TAG, "response body:" + content);
            Response build = response.newBuilder()
                    .body(ResponseBody.create(mediaType, content))
                    .build();
            response.body().close();
            return build;
        }
    }

    //OkHttpClient 初始化
    private static OkHttpClient client = new OkHttpClient.Builder()
            //            .addInterceptor(new CacheInterceptor())
            //            .addNetworkInterceptor(new CacheInterceptor())
            .cache(cache)
            .addInterceptor(new LogInterceptor())
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build();


}
