package xyz.zpayh.bus;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.Process;
import android.os.RemoteException;
import android.support.annotation.NonNull;

import java.io.Serializable;
import java.lang.ref.WeakReference;

import xyz.zpayh.bus.ipc.AgeraBusService;
import xyz.zpayh.bus.ipc.MessageState;

/**
 * 文 件 名: AgeraBusClient
 * 创 建 人: 陈志鹏
 * 创建日期: 2016/11/22 22:45
 * 邮   箱: ch_zh_p@qq.com
 * 修改时间:
 * 修改备注:
 */

public class AgeraBusClient implements ServiceConnection{

    private static volatile AgeraBusClient sProcessClient;

    public static AgeraBusClient getProcessClient() {
        if (!sInit){
            return null;
        }
        if (sProcessClient == null){
            synchronized (AgeraBusClient.class){
                if (sProcessClient == null){
                    sProcessClient = new AgeraBusClient();
                }
            }
        }
        return sProcessClient;
    }

    private static boolean sInit = false;

    static synchronized void init(@NonNull Context context){
        if (sInit){
            return;
        }
        sInit = true;
        getProcessClient();
        sProcessClient.bindService(context);
    }

    private final AgeraBus mBus;

    private Messenger mClientMessenger;
    private Messenger mServiceMessenger;

    public AgeraBusClient() {
        mClientMessenger = new Messenger(new ClientHandler(this));
        mBus = AgeraBus.getDefault();
    }

    public void bindService(@NonNull Context context){
        Intent intent = new Intent(context,AgeraBusService.class);

        context.bindService(intent,this,Context.BIND_AUTO_CREATE);
    }

    private void dispatch(@NonNull Message event){
        final Bundle bundle = event.getData();
        Object obj = bundle.getSerializable(AgeraBusService.MSG_EVENT);
        if (obj != null) {
            mBus.postRemote(obj);
        }
    }

    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        mServiceMessenger = new Messenger(iBinder);
        sendClient();
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {

    }

    void postEvent(@NonNull Serializable event){
        Message msg = Message.obtain(null,MessageState.MSG_FROM_EVENT);
        Bundle bundle = new Bundle(1);
        bundle.putSerializable(AgeraBusService.MSG_EVENT,event);
        msg.setData(bundle);
        try {
            mServiceMessenger.send(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void sendClient() {
        Message msg = Message.obtain(null, MessageState.MSG_FROM_CLIENT);
        msg.arg1 = Process.myPid();
        msg.replyTo = mClientMessenger;
        try {
            mServiceMessenger.send(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private final static class ClientHandler extends Handler{

        private final WeakReference<AgeraBusClient> mWeakReference;

        public ClientHandler(AgeraBusClient client){
            mWeakReference = new WeakReference<AgeraBusClient>(client);
        }

        @Override
        public void handleMessage(Message msg) {
            final AgeraBusClient client = mWeakReference.get();
            if (client == null) return;
            final int state = msg.what;
            switch (state){
                case MessageState.MSG_FROM_EVENT:
                    client.dispatch(msg);
                    break;
            }
        }
    }
}
