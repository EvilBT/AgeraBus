package xyz.zpayh.agerabus;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Process;
import android.util.Log;
import android.view.View;

import java.util.Date;

import xyz.zpayh.agerabus.event.RemoteTeacher;
import xyz.zpayh.agerabus.event.RemoteUser;
import xyz.zpayh.bus.AgeraBus;

public class RemoteEventActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_remote_event);

        Intent intent = new Intent(this,RemoteService.class);
        startService(intent);

        findViewById(R.id.bt_send)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Date date = new Date(System.currentTimeMillis());
                        Log.d("RemoteEventActivity", "date.getTime():" + date.getTime()+",Pid:"+ Process.myPid());
                        AgeraBus.getDefault()
                                .post(new RemoteUser("Sherlock",date));
                    }
                });

        findViewById(R.id.bt_send_teacher)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Date date = new Date(System.currentTimeMillis());
                        Log.d("RemoteEventActivity", "date.getTime():" + date.getTime()+",Pid:"+ Process.myPid());
                        AgeraBus.getDefault()
                                .post(new RemoteTeacher(date,"一个老师"));
                    }
                });
    }
}
