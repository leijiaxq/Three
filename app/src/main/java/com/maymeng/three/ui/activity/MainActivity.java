package com.maymeng.three.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.baidu.mobads.AdView;
import com.baidu.mobads.AdViewListener;
import com.maymeng.three.R;
import com.maymeng.three.api.Constants;
import com.maymeng.three.api.RetrofitHelper;
import com.maymeng.three.api.RxBus;
import com.maymeng.three.base.RxBaseActivity;
import com.maymeng.three.bean.BaseBean;
import com.maymeng.three.bean.ChildrenBean1;
import com.maymeng.three.bean.ChildrenBean2;
import com.maymeng.three.bean.DataBean1;
import com.maymeng.three.bean.DataBean2;
import com.maymeng.three.bean.DataBean3;
import com.maymeng.three.utils.SPUtil;
import com.maymeng.three.utils.ThreadPoolFactory;
import com.maymeng.three.utils.ToastUtil;
import com.tencent.smtt.sdk.CacheManager;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class MainActivity extends RxBaseActivity {
    @BindView(R.id.et1)
    EditText mEt1;
    @BindView(R.id.et2)
    EditText mEt2;
    @BindView(R.id.et3)
    EditText mEt3;

    @BindView(R.id.spinner1)
    Spinner mSpinner1;
    @BindView(R.id.spinner2)
    Spinner mSpinner2;
    @BindView(R.id.banner_layout1)
    LinearLayout mBannerLayout1;
    @BindView(R.id.banner_layout2)
    LinearLayout mBannerLayout2;
    @BindView(R.id.banner_layout3)
    LinearLayout mBannerLayout3;
    @BindView(R.id.banner_layout4)
    LinearLayout mBannerLayout4;
    @BindView(R.id.banner_layout5)
    LinearLayout mBannerLayout5;
    @BindView(R.id.bottom_layout)
    LinearLayout mBottomLayout;
    @BindView(R.id.activity_main)
    LinearLayout mActivityMain;

    int mPosition1 = 0;
    int mPosition2 = 0;
    private List<ChildrenBean1> mDatas;

    private List<String> mList1 = new ArrayList<>();
    private List<String> mList2 = new ArrayList<>();
    private ArrayAdapter<String> mAdapter1;
    private ArrayAdapter<String> mAdapter2;

    List<LinearLayout> mLayouts = new ArrayList<>();

    private List<AdView> mAdViewsList = new ArrayList<>();
    private List<AdView> mAdViewsList2 = new ArrayList<>();
    private ChildrenBean2 mBean2;

    Handler mHandler;


    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 1) {
                    ToastUtil.showShort(MainActivity.this, "缓存清除成功");
                } else if (msg.what == -1) {
                    ToastUtil.showShort(MainActivity.this, "缓存清除失败");
                }
            }
        };

        mLayouts.add(mBannerLayout1);
        mLayouts.add(mBannerLayout2);
        mLayouts.add(mBannerLayout3);
        mLayouts.add(mBannerLayout4);
        mLayouts.add(mBannerLayout5);

        mAdapter1 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, mList1);
        mAdapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//绑定 Adapter到控件
        mSpinner1.setAdapter(mAdapter1);

        mSpinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int pos, long id) {
                mPosition1 = pos;
                mPosition2 = 0;

                ChildrenBean1 bean = mDatas.get(pos);
                mList2.clear();
                for (int i = 0; i < bean.appList.size(); i++) {
                    ChildrenBean2 bean2 = bean.appList.get(i);
                    mList2.add(bean2.name);
                }
                mAdapter2.notifyDataSetChanged();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Another interface callback
            }
        });

        mAdapter2 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, mList2);
        mAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//绑定 Adapter到控件
        mSpinner2.setAdapter(mAdapter2);

        mSpinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int pos, long id) {
                mPosition2 = pos;
//                ToastUtil.showShort(MainActivity.this, mList2.get(pos));


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Another interface callback
            }
        });


        //用于--从商品详情页返回市场时 ,从新获取购物车列表数据
        RxBus.getDefault().toObservable(DataBean2.class)
                .compose(this.<DataBean2>bindToLifecycle())
                .subscribe(new Action1<DataBean2>() {
                    @Override
                    public void call(DataBean2 bean2) {
                        refreshBanner();
                    }
                });


    }


    @Override
    public void initToolBar() {

    }


    @Override
    public void loadData() {
        super.loadData();
        getNetData();

    }


    private void getNetData() {
        RetrofitHelper.getBaseApi()
                .getData1Net()
                .compose(this.<DataBean1>bindToLifecycle())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<DataBean1>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        showNetError();
                    }

                    @Override
                    public void onNext(DataBean1 bean) {
                        if (Constants.OK.equals(bean.result)) {
                            finishTask(bean);
                        } else {
                            ToastUtil.showShort(MainActivity.this, TextUtils.isEmpty(bean.result) ? "Error" : bean.result);
                        }
                    }
                });
    }

    private void getWebviewUrlNet(String key1, String key2, String key3, int maid) {
        RetrofitHelper.getBaseApi()
                .getWebViewUrlNet(key1, key2, key3, maid)
                .compose(this.<DataBean2>bindToLifecycle())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<DataBean2>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        showNetError();
                    }

                    @Override
                    public void onNext(DataBean2 bean) {
                        if (Constants.OK.equals(bean.result)) {
                            finishTask(bean);
                        } else {
                            ToastUtil.showShort(MainActivity.this, TextUtils.isEmpty(bean.result) ? "Error" : bean.result);
                        }
                    }
                });
    }

    //获取随机关键词
    private void getKeyWorld() {
        RetrofitHelper.getBaseApi()
                .getKeyWorldNet()
                .compose(this.<DataBean3>bindToLifecycle())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<DataBean3>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        showNetError();
                    }

                    @Override
                    public void onNext(DataBean3 bean) {
                        if (Constants.OK.equals(bean.result)) {
                            finishTask(bean);
                        } else {
                            ToastUtil.showShort(MainActivity.this, TextUtils.isEmpty(bean.result) ? "Error" : bean.result);
                        }
                    }
                });
    }

    @Override
    public void finishTask(BaseBean bean) {
        super.finishTask(bean);
        if (bean instanceof DataBean1) {
            setDataBean1((DataBean1) bean);
        } else if (bean instanceof DataBean2) {
            setDataBean2((DataBean2) bean);
        } else if (bean instanceof DataBean3) {
            setDataBean3((DataBean3) bean);
        }
    }

    boolean mFirstFlag = true;

    private void setDataBean1(DataBean1 bean) {
        mDatas = bean.applist;
        mList1.clear();
        mPosition1 = 0;
        mPosition2 = 0;
        for (int i = 0; i < mDatas.size(); i++) {
            ChildrenBean1 bean1 = mDatas.get(i);
            String str = bean1.name + ":" + bean1.account;
            mList1.add(str);
        }
        if (!mFirstFlag) {
            mAdapter1.notifyDataSetChanged();

            ChildrenBean1 bean1 = mDatas.get(0);
            mList2.clear();
            for (int i = 0; i < bean1.appList.size(); i++) {
                ChildrenBean2 bean2 = bean1.appList.get(i);
                mList2.add(bean2.name);
            }
            mAdapter2.notifyDataSetChanged();


        } else {
            mFirstFlag = false;
            int acid = (int) SPUtil.get(this, Constants.ACID, 0);
            int maid = (int) SPUtil.get(this, Constants.MAID, 0);
            if (acid != 0 || maid != 0) {
                for (int i = 0; i < mDatas.size(); i++) {
                    ChildrenBean1 bean11 = mDatas.get(i);
                    if (bean11.acid == acid) {
                        mPosition1 = i;
                        break;
                    }
                }
                if (mDatas.size() > mPosition1) {
                    mList2.clear();
                    ChildrenBean1 childrenBean1 = mDatas.get(mPosition1);

                    for (int i = 0; i < childrenBean1.appList.size(); i++) {
                        ChildrenBean2 bean2 = childrenBean1.appList.get(i);
                        mList2.add(bean2.name);
                    }


                    ChildrenBean1 bean12 = mDatas.get(mPosition1);
                    for (int i = 0; i < bean12.appList.size(); i++) {
                        ChildrenBean2 bean22 = bean12.appList.get(i);
                        if (bean22.maid == maid) {
                            mPosition2 = i;
                            break;
                        }
                    }
                }
            }
            mSpinner1.setSelection(mPosition1);
            mAdapter1.notifyDataSetChanged();
            mSpinner2.setSelection(mPosition2);
            mAdapter2.notifyDataSetChanged();

            ChildrenBean1 bean33 = mDatas.get(mPosition1);
            mBean2 = bean33.appList.get(mPosition2);
            refreshBanner();
        }
    }

    String mUrl = "";

    private void setDataBean2(DataBean2 bean) {
        mUrl = bean.url;
    }

    //设置关键词数据
    private void setDataBean3(DataBean3 bean) {

        mEt1.setText(TextUtils.isEmpty(bean.k1) ? "" : bean.k1);
        mEt2.setText(TextUtils.isEmpty(bean.k2) ? "" : bean.k2);
        mEt3.setText(TextUtils.isEmpty(bean.k3) ? "" : bean.k3);

    }

    int mIndex = 0;

    @OnClick(R.id.confirm_btn)
    public void clickConfirm(View view) {
        if (mDatas.size() <= mPosition1) {
            return;
        }
        ChildrenBean1 bean1 = mDatas.get(mPosition1);

        if (bean1.appList.size() <= mPosition2) {
            return;
        }
        mBean2 = bean1.appList.get(mPosition2);
        refreshBanner();

        saveRecord(bean1.acid, mBean2.maid);

    }

    //保存记录
    private void saveRecord(int acid, int maid) {

        SPUtil.put(this, Constants.ACID, acid);
        SPUtil.put(this, Constants.MAID, maid);

    }

    @OnClick(R.id.refresh_btn2)
    public void clickRefresh2(View v) {
        refreshBanner();
    }

    private void refreshBanner() {
        mIndex = 0;
        mAdViewsList.clear();
        ThreadPoolFactory.getNormalThreadPool().remove(mRunnable);
        ThreadPoolFactory.getNormalThreadPool().excute(mRunnable);
    }

    Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            if (mBean2 == null) {
                return;
            }
            AdView.setAppSid(MainActivity.this, mBean2.aid);
            for (int i = 0; i < 5; i++) {
                final int postion = i;

                AdView adView = new AdView(MainActivity.this, mBean2.cid);

                adView.setListener(new AdViewListener() {
                    public void onAdSwitch() {
                        Log.w("AdViewListener", "onAdSwitch");
                    }

                    public void onAdShow(JSONObject info) {
                        // 广告已经渲染出来
                        Log.w("AdViewListener", "onAdShow " + info.toString());
                      /*  mIndex++;
                        if (mIndex == 4) {
                            //所有广告已经加载完成了，进行下一轮加载

                            //缓存一轮广告
//                            mFlag = false;

                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    initAdView();
                                }
                            });
                        }*/
                    }

                    public void onAdReady(AdView adView) {
                        // 资源已经缓存完毕，还没有渲染出来
                        Log.w("AdViewListener", "onAdReady " + adView);
                    }

                    public void onAdFailed(String reason) {
                        Log.w("AdViewListener", "onAdFailed " + reason);
                    }

                    public void onAdClick(JSONObject info) {
                        Log.w("AdViewListener", "onAdClick " + info.toString());

                    }

                    @Override
                    public void onAdClose(JSONObject arg0) {
                        Log.w("AdViewListener", "onAdClose");
                    }
                });

                mAdViewsList.add(adView);

                mIndex++;
                if (mIndex == 5) {
                    //所有广告已经加载完成了，进行下一轮加载

                    //缓存一轮广告
//                            mFlag = false;

                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            initAdView();
                        }
                    });
                }

            }
        }
    };


    private void initAdView() {

        for (int i = 0; i < 5; i++) {
            AdView adView = mAdViewsList.get(i);
            LinearLayout layout = mLayouts.get(i);
            // 将adView添加到父控件中（注：该父控件不一定为您的根控件，只要该控件能通过addView添加广告视图即可）
            layout.removeAllViews();
            layout.addView(adView);
        }
    }


    @OnClick(R.id.refresh_btn1)
    public void clickRefresh1(View view) {
        getNetData();
    }

    @OnClick(R.id.bottom_btn1)
    public void clickBottom1(View view) {
        mUrl = null;
        getKeyWorld();
    }

    @OnClick(R.id.bottom_btn2)
    public void clickBottom2(View view) {
        String key1 = mEt1.getText().toString().trim();
        String key2 = mEt2.getText().toString().trim();
        String key3 = mEt3.getText().toString().trim();

        if (mDatas.size() <= mPosition1) {
            return;
        }
        ChildrenBean1 bean1 = mDatas.get(mPosition1);

        if (bean1.appList.size() <= mPosition2) {
            return;
        }
        ChildrenBean2 bean2 = bean1.appList.get(mPosition2);

        getWebviewUrlNet(key1, key2, key3, bean2.maid);
    }

    @OnClick(R.id.bottom_btn3)
    public void clickBottom3(View view) {
        if (!TextUtils.isEmpty(mUrl)) {
            Intent intent = new Intent(MainActivity.this, SecondWebActivity.class);
            intent.putExtra("url", mUrl);
            startActivity(intent);
        } else {
            ToastUtil.showShort(MainActivity.this, "地址链接不能为空");
        }
    }

    @OnClick(R.id.clear_btn)
    public void clickClear(View view) {
        clearCookies();
    }


    //清除cookie和缓存
    private void clearCookies() {
        ThreadPoolFactory.getNormalThreadPool().excute(new Runnable() {
            @Override
            public void run() {
                Message message = Message.obtain();

                try {
                    clearAppCache();
                    message.what = 1;


                } catch (Exception e) {
                    e.printStackTrace();
                    message.what = -1;
                }
                mHandler.sendMessage(message);
            }
        });
    }


//    在项目中经常会使用到WebView 控件,当加载html 页面时,会在/data/data/package_name目录下生成database与cache 两个文件夹。请求的url 记录是保存在WebViewCache.db,而url 的内容是保存在WebViewCache 文件夹下

    /**
     * 清除app缓存
     */
    public void clearAppCache() {
        //清除webview缓存
        @SuppressWarnings("deprecation")
        File file = CacheManager.getCacheFileBaseDir();

        //先删除WebViewCache目录下的文件

        if (file != null && file.exists() && file.isDirectory()) {
            for (File item : file.listFiles()) {
                item.delete();
            }
            file.delete();
        }
        deleteDatabase("webview.db");
        deleteDatabase("webview.db-shm");
        deleteDatabase("webview.db-wal");
        deleteDatabase("webviewCache.db");
        deleteDatabase("webviewCache.db-shm");
        deleteDatabase("webviewCache.db-wal");
        //清除数据缓存
        clearCacheFolder(getFilesDir(), System.currentTimeMillis());
        clearCacheFolder(getCacheDir(), System.currentTimeMillis());
        //2.2版本才有将应用缓存转移到sd卡的功能
//        if(isMethodsCompat(android.os.Build.VERSION_CODES.FROYO)){
//            clearCacheFolder(getExternalCacheDir(this),System.currentTimeMillis());
//        }

    }

    /**
     * 清除缓存目录
     *
     * @param dir     目录
     * @param curTime 当前系统时间
     * @return
     */
    private int clearCacheFolder(File dir, long curTime) {
        int deletedFiles = 0;
        if (dir != null && dir.isDirectory()) {
            try {
                for (File child : dir.listFiles()) {
                    if (child.isDirectory()) {
                        deletedFiles += clearCacheFolder(child, curTime);
                    }
                    if (child.lastModified() < curTime) {
                        if (child.delete()) {
                            deletedFiles++;
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return deletedFiles;
    }
}
