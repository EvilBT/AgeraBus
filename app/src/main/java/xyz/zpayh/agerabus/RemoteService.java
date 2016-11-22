package xyz.zpayh.agerabus;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.Process;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.google.android.agera.Receiver;
import com.google.android.agera.Updatable;

import xyz.zpayh.agerabus.event.RemoteUser;
import xyz.zpayh.bus.AgeraBus;

public class RemoteService extends Service implements Updatable{
    public RemoteService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("RemoteService", "注册" + Process.myPid());
        AgeraBus.getDefault()
                .addUpdatable(this, RemoteUser.class);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("RemoteService", "注销" + Process.myPid());
        AgeraBus.getDefault()
                .removeUpdatable(this, RemoteUser.class);
    }

    @Override
    public void update() {
        Log.d("RemoteService", "有调用到" + Process.myPid());
        AgeraBus.getDefault()
                .getSupplier(RemoteUser.class)
                .get().ifSucceededSendTo(new Receiver<RemoteUser>() {
            @Override
            public void accept(@NonNull RemoteUser value) {
                Toast.makeText(RemoteService.this, value.mName, Toast.LENGTH_SHORT).show();
                Log.d("RemoteService", "value.mDate.getTime():" + value.mDate.getTime());
            }
        });
    }
}
