package xyz.zpayh.bus;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.agera.Preconditions;
import com.google.android.agera.Reservoir;
import com.google.android.agera.Result;
import com.google.android.agera.Updatable;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import static xyz.zpayh.bus.ThreadMode.ASYNC;
import static xyz.zpayh.bus.ThreadMode.BACKGROUND;
import static xyz.zpayh.bus.ThreadMode.MAIN;
import static xyz.zpayh.bus.ThreadMode.POSTING;



/**
 * 文 件 名: BusReservoir
 * 创 建 人: 陈志鹏
 * 创建日期: 2016/10/27 23:16
 * 邮   箱: ch_zh_p@qq.com
 * 修改时间:
 * 修改备注:
 */

final class BusReservoir<T> implements Reservoir<T> {

    private final Executor executor;

    private final Handler handler;

    private final Object token = new Object();

    private T value;

    private final Queue<T> queue;

    private boolean interrupt;//中断事件

    private Thread sendUpdateThread;//发送事件线程

    private final PriorityQueueUpdate queueUpdate;

    BusReservoir() {
        this.executor = Executors.newSingleThreadExecutor();
        //Main线程
        this.handler = new Handler(Looper.getMainLooper());
        this.queueUpdate = PriorityQueueUpdate.getInstance();
        this.queue = new ArrayDeque<>();
    }

    void addUpdatable(@NonNull Updatable updatable,@ThreadMode int threadMode,boolean sticky,int priority){
        Preconditions.checkNotNull(updatable);
        synchronized (token){
            add(updatable,new PriorityQueueUpdate.Pair(priority,threadMode));
            if (sticky){
                //如果是接收粘性事件
                dispatch(updatable,threadMode);
            }
        }
    }

    @Deprecated
    @Override
    public void addUpdatable(@NonNull Updatable updatable) {
        addUpdatable(updatable,POSTING,false,0);
    }

    @Override
    public void removeUpdatable(@NonNull Updatable updatable) {
        Preconditions.checkNotNull(updatable);
        synchronized (token){
            remove(updatable);
        }
    }

    @Override
    public void accept(@NonNull T value) {
        //this.value = value;
        boolean shouldDispatchUpdate;
        synchronized (queue){
            boolean wasEmpty = queue.isEmpty();
            boolean added = queue.offer(value);
            shouldDispatchUpdate = wasEmpty && added;
        }
        if (shouldDispatchUpdate) {
            dispatchUpdate();
        }
    }

    @NonNull
    @Override
    public Result<T> get() {
        return Result.absentIfNull(value);
    }

    private void add(@NonNull final Updatable updatable,@NonNull final PriorityQueueUpdate.Pair pair) {
        queueUpdate.add(updatable, pair);
    }

    private void remove(@NonNull final Updatable updatable) {
        queueUpdate.remove(updatable);
    }

    private void dispatchUpdate() {
        boolean shouldDispatchUpdate;
        synchronized (queue){
            value = queue.poll();
            shouldDispatchUpdate = !queue.isEmpty();
        }
        synchronized (token){
            sendUpdate();
        }
        if (shouldDispatchUpdate){
            dispatchUpdate();
        }
    }

    private void sendUpdate() {
        sendUpdateThread = Thread.currentThread();
        queueUpdate.forEach(new PriorityQueueUpdate.Callback() {
            @Override
            public boolean callback(@Nullable Updatable updatable, PriorityQueueUpdate.Pair pair) {
                if (updatable != null){
                    Log.d("Bus","mode:"+pair.threadMode+",priority:"+pair.priority);
                    dispatch(updatable,pair.threadMode);
                }
                if (interrupt){
                    interrupt = false;
                    return true;
                }
                return false;
            }
        });
    }

    private void dispatch(final Updatable updatable, @ThreadMode int threadMode) {
        switch (threadMode){
            case POSTING:
                updatable.update();
                break;
            case MAIN:
                if (Looper.myLooper() == Looper.getMainLooper()){
                    updatable.update();
                }else {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            updatable.update();
                        }
                    });
                }
                break;

            case BACKGROUND:
                if (Looper.myLooper() != Looper.getMainLooper()){
                    //非主线程
                    updatable.update();
                }else{
                    executor.execute(new Runnable() {
                        @Override
                        public void run() {
                            updatable.update();
                        }
                    });
                }
                break;

            case ASYNC:
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        updatable.update();
                    }
                });
                break;

            default:
                break;
        }
    }

    void cancel(T value) {
        Thread cancel = Thread.currentThread();
        if (this.value == value){
            if (cancel == sendUpdateThread) {
                this.interrupt = true;
            }else{
                Log.d("AgeraBus","取消事件必须与发送事件处于同一个线程，否则无效");
                //取消订阅事件必须在POSTING线程
                //throw new IllegalStateException("Unsubscribing must be in the POSTING thread");
            }
        }
    }
}
