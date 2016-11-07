package xyz.zpayh.agerabus;

import android.os.Bundle;
import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.google.android.agera.Receiver;
import com.google.android.agera.Result;
import com.google.android.agera.Supplier;
import com.google.android.agera.Updatable;

import java.util.Locale;

import xyz.zpayh.agerabus.event.User;
import xyz.zpayh.bus.AgeraBus;

public class PriorityActivity extends Activity{

    private TextInputEditText mInput;
    private TextView mPriority10;
    private TextView mPriority15;
    private TextView mPriority20;
    private TextView mPriority5;

    private Supplier<Result<User>> mResultSupplier;

    private int order;

    private Updatable mUpdatable10 = new Updatable() {
        @Override
        public void update() {
            mResultSupplier.get()
                    .ifSucceededSendTo(new Receiver<User>() {
                        @Override
                        public void accept(@NonNull User value) {
                            order++;
                            final String text =
                                    String.format(Locale.CHINA,
                                            "%s:我是第%d个被调用的,用户名为:%s,我中断了事件的传递，优先级比我低的将接收不到此事件",
                                            getString(R.string.priority_10),order,value.getName());
                            mPriority10.setText(text);
                            AgeraBus.getDefault()
                                    .cancel(value);
                        }
                    });
        }
    };

    private Updatable mUpdatable15 = new Updatable() {
        @Override
        public void update() {
            mResultSupplier.get()
                    .ifSucceededSendTo(new Receiver<User>() {
                        @Override
                        public void accept(@NonNull User value) {
                            order++;
                            final String text =
                                    String.format(Locale.CHINA,"%s:我是第%d个被调用的,用户名为:%s",getString(R.string.priority_15),order,value.getName());
                            mPriority15.setText(text);
                        }
                    });
        }
    };

    private Updatable mUpdatable20 = new Updatable() {
        @Override
        public void update() {
            mResultSupplier.get()
                    .ifSucceededSendTo(new Receiver<User>() {
                        @Override
                        public void accept(@NonNull User value) {
                            order++;
                            final String text =
                                    String.format(Locale.CHINA,"%s:我是第%d个被调用的,用户名为:%s",getString(R.string.priority_20),order,value.getName());
                            mPriority20.setText(text);
                        }
                    });
        }
    };

    private Updatable mUpdatable5 = new Updatable() {
        @Override
        public void update() {
            mResultSupplier.get()
                    .ifSucceededSendTo(new Receiver<User>() {
                        @Override
                        public void accept(@NonNull User value) {
                            order++;
                            final String text =
                                    String.format(Locale.CHINA,"%s:我是第%d个被调用的,用户名为:%s",getString(R.string.priority_5),order,value.getName());
                            mPriority5.setText(text);
                        }
                    });
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_priority);

        initView();
    }

    @Override
    protected void onStart() {
        super.onStart();

        AgeraBus.getDefault()
                .compiler(User.class)
                .priority(10)
                .compile(mUpdatable10);

        AgeraBus.getDefault()
                .compiler(User.class)
                .priority(20)
                .compile(mUpdatable20);

        AgeraBus.getDefault()
                .compiler(User.class)
                .priority(15)
                .compile(mUpdatable15);

        AgeraBus.getDefault()
                .compiler(User.class)
                .priority(5)
                .compile(mUpdatable5);
    }

    @Override
    protected void onStop() {
        super.onStop();

        AgeraBus.getDefault().removeUpdatable(mUpdatable10,User.class);
        AgeraBus.getDefault().removeUpdatable(mUpdatable20,User.class);
        AgeraBus.getDefault().removeUpdatable(mUpdatable15,User.class);
        AgeraBus.getDefault().removeUpdatable(mUpdatable5,User.class);
    }

    private void initView() {
        mInput = (TextInputEditText) findViewById(R.id.et_input);
        mPriority10 = (TextView) findViewById(R.id.tv_priority_10);
        mPriority15 = (TextView) findViewById(R.id.tv_priority_15);
        mPriority20 = (TextView) findViewById(R.id.tv_priority_20);
        mPriority5 = (TextView) findViewById(R.id.tv_priority_5);

        mResultSupplier = AgeraBus.getDefault().getSupplier(User.class);

        findViewById(R.id.bt_send).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String name = mInput.getText().toString();
                if (TextUtils.isEmpty(name)){
                    mInput.setError("请输入用户名");
                    mInput.requestFocus();
                    return;
                }
                AgeraBus.getDefault()
                        .post(new User(name));
                mInput.setText("");
                mInput.clearFocus();
                order = 0;
            }
        });
    }
}
