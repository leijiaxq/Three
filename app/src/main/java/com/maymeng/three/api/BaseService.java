package com.maymeng.three.api;


import com.maymeng.three.bean.DataBean1;
import com.maymeng.three.bean.DataBean2;
import com.maymeng.three.bean.DataBean3;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Create by  leijiaxq
 * Date       2017/3/2 16:57
 * Describe
 */

public interface BaseService {

    //
    @GET("Mssp/getMsspAppList.jsp")
    Observable<DataBean1> getData1Net();

    //webView url
    @GET("Mssp/goSeeAd.jsp")
    Observable<DataBean2> getWebViewUrlNet(@Query("k1") String key1, @Query("k2") String key2, @Query("k3") String key3, @Query("maid") int maid);

    //webView url
    @GET("Mssp/ranKeys.jsp")
    Observable<DataBean3> getKeyWorldNet();

}
