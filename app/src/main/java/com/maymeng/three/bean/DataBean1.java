package com.maymeng.three.bean;

import java.util.List;

/**
 * Created by  leijiaxq
 * Date        2017/3/31 17:32
 * Describe
 */

public class DataBean1 extends BaseBean{


    /**
     * result : ok
     * applist : [{"account":"huo","acid":2,"appList":[{"aid":"cd85c2f2","cid":"3669190","maid":3,"name":"指南探戈視頻免費","pack":"com.video.socialtango"}],"name":"帮明"},{"account":"yy369963","acid":1,"appList":[{"aid":"c509245f","cid":"3667890","maid":1,"name":"我要搬家","pack":"com.com2us.puzzlefamilyvs.normal.freefull.google.cn.android.common"},{"aid":"dfb81849","cid":"3667658","maid":2,"name":"我爸刚弄死他","pack":"com.sunflower.game.gangnam"}],"name":"钟林"}]
     */

    public String result;
    public List<ChildrenBean1> applist;

}
