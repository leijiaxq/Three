package com.maymeng.three.base;

import android.os.Bundle;

import com.maymeng.three.api.Constants;
import com.maymeng.three.bean.BaseBean;
import com.maymeng.three.utils.ToastUtil;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by hcc on 16/8/7 21:18
 * 100332338@qq.com
 * <p/>
 * Activity基类
 */
public abstract class RxBaseActivity extends RxAppCompatActivity
  /*  implements CardPickerDialog.ClickListener */ {

    private Unbinder bind;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        //设置布局内容
        setContentView(getLayoutId());

        //初始化黄油刀控件绑定框架
        bind = ButterKnife.bind(this);
        //初始化控件
        initViews(savedInstanceState);
        //初始化ToolBar
        initToolBar();

        loadData();
    }


    public abstract int getLayoutId();

    public abstract void initViews(Bundle savedInstanceState);

    public abstract void initToolBar();

    public void loadData() {
    }


    public void initRecyclerView() {
    }

    public void initRefreshLayout() {
    }
    public void finishTask(BaseBean bean) {
    }
    public void showNetError() {
        ToastUtil.showShort(this, Constants.FAILURE);
    }
   @Override
    protected void onDestroy() {

        super.onDestroy();
        bind.unbind();
    }
}
