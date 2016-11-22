package xyz.zpayh.agerabus;

import android.app.Application;

import xyz.zpayh.bus.AgeraBus;

/**
 * 文 件 名: MainApplication
 * 创 建 人: 陈志鹏
 * 创建日期: 2016/11/22 23:44
 * 邮   箱: ch_zh_p@qq.com
 * 修改时间:
 * 修改备注:
 */

public class MainApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        AgeraBus.init(this);
    }
}
