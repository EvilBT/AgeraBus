package xyz.zpayh.rvbus;

import android.support.annotation.NonNull;

import com.google.android.agera.Result;
import com.google.android.agera.Supplier;
import com.google.android.agera.Updatable;


/**
 * 文 件 名: BusCompilerStates
 * 创 建 人: 陈志鹏
 * 创建日期: 2016/10/27 23:16
 * 邮   箱: ch_zh_p@qq.com
 * 修改时间:
 * 修改备注:
 */
public interface BusCompilerStates {

    interface RCreate<T>{

        @NonNull
        RRegister<T> get(Class<T> type);
    }

    interface RRegister<T>{

        @NonNull
        Supplier<Result<T>> addUpdatable(@NonNull Updatable updatable);
    }
}
