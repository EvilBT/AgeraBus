package xyz.zpayh.bus.ipc;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.Parcelable;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.v4.util.ArrayMap;

import java.lang.ref.WeakReference;

public class AgeraBusService extends Service {

    public final static String MSG_SERIALIZABLE_EVENT = "Serializable event";
    public final static String MSG_PARCELABLE_EVENT = "Parcelable event";

    private final Messenger mServerMessenger = new Messenger(new AgeraHandler(this));

    public AgeraBusService() {
    }

    private final ArrayMap<Integer,Messenger> mProcessMessengers = new ArrayMap<>();

    @Override
    public void onCreate() {
        super.onCreate();

    }

    @Override
    public IBinder onBind(Intent intent) {
        return mServerMessenger.getBinder();
    }

    private void putMessenger(int pid, @NonNull Messenger messenger){
        mProcessMessengers.put(pid,messenger);
    }

    private void dispatch(@NonNull Message event){
        for (Messenger messenger : mProcessMessengers.values()) {
            try {
                Message msg = Message.obtain(event);
                messenger.send(msg);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    static class AgeraHandler extends Handler{
        final WeakReference<AgeraBusService> mWeakReference;

        public AgeraHandler(@NonNull AgeraBusService service) {
            mWeakReference = new WeakReference<AgeraBusService>(service);
        }

        @Override
        public void handleMessage(Message msg) {
            final AgeraBusService service = mWeakReference.get();
            if (service == null) return;

            final int state = msg.what;
            switch (state){
                case MessageState.MSG_FROM_CLIENT:
                    service.putMessenger(msg.arg1,msg.replyTo);
                    break;
                case MessageState.MSG_FROM_SERIALIZABLE_EVENT:
                    final Bundle bundle = msg.getData();
                    final Object serializableEvent = bundle.getSerializable(MSG_SERIALIZABLE_EVENT);
                    if (serializableEvent != null) {
                        service.dispatch(msg);
                    }
                    break;
                case MessageState.MSG_FROM_PARCELABLE_EVENT:
                    final Bundle bundle1 = msg.getData();
                    bundle1.setClassLoader(getClass().getClassLoader());
                    final Parcelable parcelableEvent = bundle1.getParcelable(MSG_PARCELABLE_EVENT);
                    if (parcelableEvent != null){
                        service.dispatch(msg);
                    }
                    break;
            }
        }
    }
}
