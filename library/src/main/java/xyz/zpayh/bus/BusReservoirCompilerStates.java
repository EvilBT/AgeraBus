package xyz.zpayh.bus;

import android.support.annotation.NonNull;

import com.google.android.agera.Updatable;


/**
 * 文 件 名: BusReservoirCompilerStates
 * 创 建 人: 陈志鹏
 * 创建日期: 2016/10/27 23:16
 * 邮   箱: ch_zh_p@qq.com
 * 修改时间:
 * 修改备注:
 */

public interface BusReservoirCompilerStates {

    interface REventSource<T> extends RCompile{

        /**
         * 设置优先级
         * @param priority 优先级，一般大于0
         */
        @NonNull
        RSticky<T> priority(int priority);

        /*
         * 0 优先级
         */
        @NonNull
        RSticky<T> noPriority();
   }

    interface RSticky<T> extends RCompile{

        @NonNull
        RWhenUpdate<T> sticky();

        @NonNull
        RWhenUpdate<T> noSticky();
    }

    interface RWhenUpdate<T> extends RCompile{

        @NonNull
        RCompile posting();

        @NonNull
        RCompile main();

        @NonNull
        RCompile background();

        @NonNull
        RCompile async();
    }

    interface RCompile{

        void compile(Updatable updatable);
    }
}
