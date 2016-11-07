package xyz.zpayh.bus;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.android.agera.Preconditions;
import com.google.android.agera.Updatable;

import java.util.Arrays;

/**
 * 文 件 名: PriorityQueueUpdate
 * 创 建 人: 陈志鹏
 * 创建日期: 2016/10/27 23:16
 * 邮   箱: ch_zh_p@qq.com
 * 修改时间:
 * 修改备注:
 */

final class PriorityQueueUpdate {

    private int noPrioritySize;
    private int prioritySize;
    private Object[] updatablesAndHandlers;

    private Object[] noPriorityUpdatablesAndHandlers;

    static PriorityQueueUpdate getInstance(){
        return new PriorityQueueUpdate();
    }

    private PriorityQueueUpdate() {
        this.updatablesAndHandlers = new Object[0];
        this.noPriorityUpdatablesAndHandlers = new Object[0];
        this.noPrioritySize = 0;
        this.prioritySize = 0;
    }

    void add(@NonNull final Updatable updatable,@NonNull final Pair pair){
        Preconditions.checkNotNull(updatable);
        Preconditions.checkNotNull(pair);
        if (pair.priority == 0){
            // 不参与调度优先级
            addNoPriority(updatable,pair);
        }else{
            addPriority(updatable,pair);
        }
    }

    void remove(@NonNull final Updatable updatable){

        for (int index = 0; index < noPriorityUpdatablesAndHandlers.length; index += 2) {
            if (noPriorityUpdatablesAndHandlers[index] == updatable){
                noPriorityUpdatablesAndHandlers[index] = null;
                noPriorityUpdatablesAndHandlers[index+1] = null;
                noPrioritySize--;
                return;
            }
        }

        int indexToRemove = -1;

        for (int index = 0; index < updatablesAndHandlers.length; index+=2) {
            if (updatablesAndHandlers[index] == updatable){
                indexToRemove = index;
                break;
            }
        }

        if (indexToRemove == -1){
            throw new IllegalStateException("Updatable not added, cannot remove.");
        }

        for (int index = indexToRemove; index < updatablesAndHandlers.length-2; index+=2) {
            updatablesAndHandlers[index] = updatablesAndHandlers[index+2];
            updatablesAndHandlers[index+1] = updatablesAndHandlers[index+3];
            if (updatablesAndHandlers[index+2] == null){
                prioritySize--;
                return;
            }
        }

        if (updatablesAndHandlers.length>=2) {
            updatablesAndHandlers[updatablesAndHandlers.length - 2] = null;
            updatablesAndHandlers[updatablesAndHandlers.length - 1] = null;
        }
    }

    private void addNoPriority(@NonNull final Updatable updatable,@NonNull final  Pair pair) {
        int indexToAdd = -1;

        for (int index = 0; index < updatablesAndHandlers.length; index+=2) {
            if (updatablesAndHandlers[index] == updatable){
                throw new IllegalStateException("Updatable already added, cannot add.");
            }
        }

        for (int index = 0; index < noPriorityUpdatablesAndHandlers.length; index += 2) {
            if (noPriorityUpdatablesAndHandlers[index] == updatable){
                throw new IllegalStateException("Updatable already added, cannot add.");
            }
            if (noPriorityUpdatablesAndHandlers[index] == null){
                indexToAdd = index;
            }
        }
        if (indexToAdd == -1){
            indexToAdd = noPriorityUpdatablesAndHandlers.length;
            noPriorityUpdatablesAndHandlers = Arrays.copyOf(noPriorityUpdatablesAndHandlers,
                    indexToAdd < 2 ? 2 : indexToAdd * 2);
        }
        noPriorityUpdatablesAndHandlers[indexToAdd] = updatable;
        noPriorityUpdatablesAndHandlers[indexToAdd+1] = pair;
        noPrioritySize++;
    }

    private void addPriority(@NonNull final Updatable updatable,@NonNull final  Pair pair) {
        // 因为要寻找是否已经加了 updatable ，故直接选择插入排序算法
        for (int index = 0; index < noPriorityUpdatablesAndHandlers.length; index+=2) {
            if (noPriorityUpdatablesAndHandlers[index] == updatable){
                throw new IllegalStateException("Updatable already added, cannot add.");
            }
        }

        int indexToAdd = -1;
        boolean hasNull = false;
        for (int index = 0; index < updatablesAndHandlers.length; index += 2) {
            if (updatablesAndHandlers[index] == updatable){
                throw new IllegalStateException("Updatable already added, cannot add.");
            }
            if (updatablesAndHandlers[index] == null){
                //如果已经找到空的，就代表它找不到比它还小的
                if (indexToAdd == -1) {
                    indexToAdd = index;
                }
                hasNull = true;
                break;
            }

            if (indexToAdd == -1) {
                final Pair arrayPair = (Pair) updatablesAndHandlers[index + 1];
                if (pair.priority > arrayPair.priority) {
                    indexToAdd = index;
                }
            }
        }

        if (indexToAdd == -1){
            //表示没找到位置，且是最小的，直接放在最后
            indexToAdd = updatablesAndHandlers.length;
            updatablesAndHandlers = Arrays.copyOf(updatablesAndHandlers,
                    indexToAdd < 2 ? 2 : indexToAdd * 2);
            updatablesAndHandlers[indexToAdd] = updatable;
            updatablesAndHandlers[indexToAdd+1] = pair;
            prioritySize++;
            return;
        }

        if (!hasNull){
            //如果没有空余的位置了，先扩展
            updatablesAndHandlers = Arrays.copyOf(updatablesAndHandlers,
                    updatablesAndHandlers.length * 2);
        }

        Object tempUpdatable = null;
        Object tempPair = null;
        for (int index = indexToAdd; index < updatablesAndHandlers.length - 2; index+=2) {
            tempUpdatable = updatablesAndHandlers[index+2];
            tempPair = updatablesAndHandlers[index+3];
            updatablesAndHandlers[index+2] = updatablesAndHandlers[indexToAdd];
            updatablesAndHandlers[index+3] = updatablesAndHandlers[indexToAdd+1];

            updatablesAndHandlers[indexToAdd] = tempUpdatable;
            updatablesAndHandlers[indexToAdd+1] = tempPair;
            if (tempUpdatable == null){
                break;
            }
        }

        updatablesAndHandlers[indexToAdd] = updatable;
        updatablesAndHandlers[indexToAdd+1] = pair;
        prioritySize++;
    }

    void forEach(@NonNull Callback callback){
        Preconditions.checkNotNull(callback);
        for (int index = 0; index < updatablesAndHandlers.length; index+=2) {
            final Updatable updatable = (Updatable) updatablesAndHandlers[index];
            final Pair pair = (Pair) updatablesAndHandlers[index+1];
            if(callback.callback(updatable,pair)) return;
        }
        for (int index = 0; index < noPriorityUpdatablesAndHandlers.length; index+=2) {
            final Updatable updatable = (Updatable) noPriorityUpdatablesAndHandlers[index];
            final Pair pair = (Pair) noPriorityUpdatablesAndHandlers[index+1];
            if(callback.callback(updatable,pair)) return;
        }
    }

    interface Callback{
        /**
         *  返回true则代表不在执行后面的更新事件，用于拦截事件的更新
         */
        boolean callback(@Nullable Updatable updatable, Pair pair);
    }

    final static class Pair {
        final int priority;
        final @ThreadMode int threadMode;

        Pair(int priority, int threadMode) {
            this.priority = priority;
            this.threadMode = threadMode;
        }
    }
}
