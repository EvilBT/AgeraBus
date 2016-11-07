package xyz.zpayh.bus;

import android.support.annotation.NonNull;

import com.google.android.agera.Preconditions;
import com.google.android.agera.Updatable;

import static xyz.zpayh.bus.ThreadMode.ASYNC;
import static xyz.zpayh.bus.ThreadMode.BACKGROUND;
import static xyz.zpayh.bus.ThreadMode.MAIN;
import static xyz.zpayh.bus.ThreadMode.POSTING;


/**
 * 文 件 名: BusReservoirCompiler
 * 创 建 人: 陈志鹏
 * 创建日期: 2016/10/27 23:16
 * 邮   箱: ch_zh_p@qq.com
 * 修改时间:
 * 修改备注:
 */
@SuppressWarnings({"unchecked, rawtypes"})
final class BusReservoirCompiler<T> implements
        BusReservoirCompilerStates.REventSource<T>,
        BusReservoirCompilerStates.RSticky<T>,
        BusReservoirCompilerStates.RWhenUpdate<T>,
        BusReservoirCompilerStates.RCompile{

    private static final ThreadLocal<BusReservoirCompiler> compilers = new ThreadLocal<>();

    @NonNull
    static <TVal> BusReservoirCompilerStates.REventSource<TVal> reservoir(
            @NonNull final AgeraBus bus,
            @NonNull final Class<TVal> type) {
        BusReservoirCompiler compiler = compilers.get();
        if (compiler == null) {
            compiler = new BusReservoirCompiler(bus,type);
        } else {
            compilers.set(null);
            compiler.bus = Preconditions.checkNotNull(bus);
            compiler.type = Preconditions.checkNotNull(type);

        }
        return compiler;
    }

    private static void recycle(@NonNull final BusReservoirCompiler compiler) {
        compilers.set(compiler);
    }


    private AgeraBus bus;

    private Class<T> type;

    private int priority;
    private boolean sticky;

    @ThreadMode
    private int threadMode;

    private BusReservoirCompiler(@NonNull AgeraBus bus, @NonNull final Class<T> type) {
        this.type = Preconditions.checkNotNull(type);
        this.bus = bus;
        this.priority = 0;
        this.sticky = false;
        this.threadMode = POSTING;
    }

    @NonNull
    @Override
    public BusReservoirCompiler priority(int priority) {
        this.priority = Math.max(0,priority);
        return this;
    }

    @NonNull
    @Override
    public BusReservoirCompiler noPriority() {
        return priority(0);
    }

    @NonNull
    @Override
    public BusReservoirCompiler sticky() {
        this.sticky = true;
        return this;
    }

    @NonNull
    @Override
    public BusReservoirCompiler noSticky() {
        this.sticky = false;
        return this;
    }

    @NonNull
    @Override
    public BusReservoirCompiler main() {
        this.threadMode = MAIN;
        return this;
    }

    @NonNull
    @Override
    public BusReservoirCompiler background() {
        this.threadMode = BACKGROUND;
        return this;
    }

    @NonNull
    @Override
    public BusReservoirCompiler async() {
        this.threadMode = ASYNC;
        return this;
    }

    @Override
    public void compile(@NonNull final Updatable updatable) {
        BusReservoir<T> reservoir = bus.getBusReservoir(type);
        reservoir.addUpdatable(updatable,threadMode,sticky,priority);
        reset();
        recycle(this);
    }

    private void reset() {
        this.priority = 0;
        this.sticky = false;
        this.threadMode = POSTING;
    }
}
