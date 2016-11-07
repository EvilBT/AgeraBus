package xyz.zpayh.bus;


import android.support.annotation.NonNull;
import android.support.v4.util.ArrayMap;

import com.google.android.agera.Preconditions;
import com.google.android.agera.Reservoir;
import com.google.android.agera.Result;
import com.google.android.agera.Supplier;
import com.google.android.agera.Updatable;


/**
 * 文 件 名: AgeraBus
 * 创 建 人: 陈志鹏
 * 创建日期: 2016/10/27 23:16
 * 邮   箱: ch_zh_p@qq.com
 * 修改时间:
 * 修改备注:
 */
@SuppressWarnings({"unchecked, rawtypes"})
public final class AgeraBus {

    private static volatile AgeraBus sBus;

    public static AgeraBus getDefault() {
        if (sBus == null){
            synchronized (AgeraBus.class){
                if (sBus == null){
                    sBus = new AgeraBus();
                }
            }
        }
        return sBus;
    }

    private final ArrayMap<String,BusReservoir> reservoirMap;

    public AgeraBus() {
        reservoirMap = new ArrayMap<>();
    }

    public <T> BusReservoirCompilerStates.REventSource<T> compiler(@NonNull final Class<T> type){
        return BusReservoirCompiler.reservoir(this,type);
    }

    public <T> void addUpdatable(@NonNull final Updatable updatable,@NonNull final Class<T> type){
        BusReservoirCompiler.reservoir(this,type)
                .noPriority()
                .noSticky()
                .compile(updatable);
    }

    public <T> void removeUpdatable(@NonNull final Updatable updatable,@NonNull final Class<T> type){
        Preconditions.checkNotNull(updatable);
        final String key = type.getName();
        Reservoir<T> reservoir = getBusReservoir(key);
        reservoir.removeUpdatable(updatable);
    }

    public <T> void post(@NonNull final T value){
        final String key = value.getClass().getName();
        Reservoir<T> reservoir = getBusReservoir(key);
        reservoir.accept(value);
    }

    @NonNull
    private <T> Reservoir<T> getBusReservoir(@NonNull String key) {
        BusReservoir<T> reservoir;

        if (reservoirMap.containsKey(key)){
            reservoir = (BusReservoir<T>) reservoirMap.get(key);
        }else{
            reservoir = new BusReservoir<>();
            reservoirMap.put(key,reservoir);
        }
        return reservoir;
    }

    public <T> Supplier<Result<T>> getSupplier(@NonNull final Class<T> type){
        return getBusReservoir(type);
    }

    <T> BusReservoir<T> getBusReservoir(@NonNull final Class<T> type){
        final String key = type.getName();
        BusReservoir<T> reservoir;

        if (reservoirMap.containsKey(key)){
            reservoir = (BusReservoir<T>) reservoirMap.get(key);
        }else{
            reservoir = new BusReservoir<>();
            reservoirMap.put(key,reservoir);
        }
        return reservoir;
    }

    public <T> void cancel(@NonNull final T value){
        Preconditions.checkNotNull(value);
        final String key = value.getClass().getName();
        BusReservoir<T> reservoir = reservoirMap.get(key);
        if (reservoir == null){
            throw new IllegalStateException("No find "+value.getClass().getName()+"'s eventBus");
        }

        reservoir.cancel(value);
    }
}
