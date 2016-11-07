package xyz.zpayh.agerabus;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initListener();
    }

    private void initListener() {
        findViewById(R.id.bt_base).setOnClickListener(this);
        findViewById(R.id.bt_sticky).setOnClickListener(this);
        findViewById(R.id.bt_priority).setOnClickListener(this);
        findViewById(R.id.bt_multithreading).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        final int id = view.getId();
        switch (id){
            case R.id.bt_base:{
                startActivity(new Intent(this,BaseActivity.class));
            }break;
            case R.id.bt_sticky:{
                startActivity(new Intent(this,StickyActivity.class));
            }break;
            case R.id.bt_priority:{
                startActivity(new Intent(this,PriorityActivity.class));
            }break;
            case R.id.bt_multithreading:{
                startActivity(new Intent(this,MultithreadingActivity.class));
            }break;
        }
    }
}
