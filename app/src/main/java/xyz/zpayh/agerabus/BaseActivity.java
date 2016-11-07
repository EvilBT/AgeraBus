package xyz.zpayh.agerabus;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.agera.Receiver;
import com.google.android.agera.Result;
import com.google.android.agera.Supplier;
import com.google.android.agera.Updatable;

import xyz.zpayh.bus.AgeraBus;

public class BaseActivity extends Activity implements Updatable{

    private TextView mTvEvent;

    private Button mBtSend;

    private Supplier<Result<String>> mStringSupplier;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        init();
    }

    @Override
    protected void onStart() {
        super.onStart();
        //register
        AgeraBus.getDefault()
                .addUpdatable(this,String.class);
    }

    @Override
    protected void onStop() {
        super.onStop();
        // unregister
        AgeraBus.getDefault()
                .removeUpdatable(this,String.class);
    }

    private void init() {
        mTvEvent = (TextView) findViewById(R.id.tv_event);
        mBtSend = (Button) findViewById(R.id.bt_send);

        mStringSupplier = AgeraBus.getDefault().getSupplier(String.class);

        mBtSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // post
                AgeraBus.getDefault()
                        .post("AgeraBus type is string class");
            }
        });
    }

    @Override
    public void update() {
        // accept
        mStringSupplier.get().ifSucceededSendTo(new Receiver<String>() {
            @Override
            public void accept(@NonNull String value) {
                mTvEvent.setText(value);
            }
        });
    }
}
