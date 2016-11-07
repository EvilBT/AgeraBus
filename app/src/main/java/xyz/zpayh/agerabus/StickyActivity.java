package xyz.zpayh.agerabus;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.agera.Receiver;
import com.google.android.agera.Updatable;

import xyz.zpayh.agerabus.event.User;
import xyz.zpayh.bus.AgeraBus;

public class StickyActivity extends Activity {

    private TextView mStickyText;
    private TextInputEditText mStickyEditText;
    private Button mBtRegister;

    private Updatable mStickyUpdatable = new Updatable() {
        @Override
        public void update() {
            //获取事件源
            AgeraBus.getDefault()
                    .getSupplier(User.class)
                    .get()
                    .ifSucceededSendTo(new Receiver<User>() {
                        @Override
                        public void accept(@NonNull User value) {
                            mStickyText.setText(value.getName());
                        }
                    });
        }
    };

    private boolean mStickyRegistered = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sticky);

        init();
    }

    private void init() {
        mStickyText = (TextView) findViewById(R.id.tv_sticky);
        mStickyEditText = (TextInputEditText) findViewById(R.id.et_input);

        findViewById(R.id.bt_send)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final String name = mStickyEditText.getText().toString();
                        if (TextUtils.isEmpty(name)){
                            mStickyEditText.setError("请输入用户名");
                            return;
                        }
                        AgeraBus.getDefault().post(new User(name));
                        Toast.makeText(StickyActivity.this, "发送事件成功", Toast.LENGTH_SHORT).show();
                        mStickyEditText.setText("");
                    }
                });

        mBtRegister = (Button) findViewById(R.id.bt_register);

        mBtRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mStickyRegistered){
                    AgeraBus.getDefault()
                            .removeUpdatable(mStickyUpdatable,User.class);
                    mStickyRegistered = false;
                    mBtRegister.setText(R.string.register_sticky);
                }else{
                    AgeraBus.getDefault()
                            .compiler(User.class)//设置注册事件类型
                            .noPriority()//设置优先级
                            .sticky()//设置接收粘性事件
                            .compile(mStickyUpdatable);//注册监听
                    mStickyRegistered = true;
                    mBtRegister.setText(R.string.unregister_sticky);
                }
            }
        });
    }

}
