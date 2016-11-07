package xyz.zpayh.bus;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 文 件 名: ThreadMode
 * 创 建 人: 陈志鹏
 * 创建日期: 2016/10/27 23:16
 * 邮   箱: ch_zh_p@qq.com
 * 修改时间:
 * 修改备注:
 */
@Retention(RetentionPolicy.SOURCE)
@IntDef({ThreadMode.POSTING,ThreadMode.MAIN,ThreadMode.BACKGROUND,ThreadMode.ASYNC})
public @interface ThreadMode {
    int POSTING = 0;
    int MAIN = 1;
    int BACKGROUND = 2;
    int ASYNC = 3;
}
