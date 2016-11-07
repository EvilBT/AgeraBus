package xyz.zpayh.agerabus;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.google.android.agera.Receiver;
import com.google.android.agera.Result;
import com.google.android.agera.Supplier;
import com.google.android.agera.Updatable;

import xyz.zpayh.agerabus.event.User;
import xyz.zpayh.bus.AgeraBus;

public class MultithreadingActivity extends Activity {

    private TextInputEditText mInput;
    private TextView mPosting;
    private TextView mMain;
    private TextView mBackground;
    private TextView mAsync;

    private Supplier<Result<User>> mResultSupplier;

    private Updatable mPostingUpdatable = new Updatable() {
        @Override
        public void update() {
            mResultSupplier.get()
                    .ifSucceededSendTo(new Receiver<User>() {
                        @Override
                        public void accept(@NonNull User value) {
                            final String threadName = Thread.currentThread().getName();
                            final String hint = getString(R.string.posting);
                            final String text = hint+",线程名:"+threadName+",user name:"+value.getName();
                            mPosting.post(new Runnable() {
                                @Override
                                public void run() {
                                    mPosting.setText(text);
                                }
                            });
                        }
                    });
        }
    };

    private Updatable mMainUpdatable = new Updatable() {
        @Override
        public void update() {
            mResultSupplier.get()
                    .ifSucceededSendTo(new Receiver<User>() {
                        @Override
                        public void accept(@NonNull User value) {
                            final String threadName = Thread.currentThread().getName();
                            final String hint = getString(R.string.main);
                            final String text = hint+",线程名:"+threadName+",user name:"+value.getName();
                            mMain.setText(text);
                        }
                    });
        }
    };

    private Updatable mBackgroundUpdatable = new Updatable() {
        @Override
        public void update() {
            mResultSupplier.get()
                    .ifSucceededSendTo(new Receiver<User>() {
                        @Override
                        public void accept(@NonNull User value) {
                            final String threadName = Thread.currentThread().getName();
                            final String hint = getString(R.string.background);
                            final String text = hint+",线程名:"+threadName+",user name:"+value.getName();
                            mBackground.post(new Runnable() {
                                @Override
                                public void run() {
                                    mBackground.setText(text);
                                }
                            });
                        }
                    });
        }
    };

    private Updatable mAsyncUpdatable = new Updatable() {
        @Override
        public void update() {
            mResultSupplier.get()
                    .ifSucceededSendTo(new Receiver<User>() {
                        @Override
                        public void accept(@NonNull User value) {
                            final String threadName = Thread.currentThread().getName();
                            final String hint = getString(R.string.async);
                            final String text = hint+",线程名:"+threadName+",user name:"+value.getName();
                            mAsync.post(new Runnable() {
                                @Override
                                public void run() {
                                    mAsync.setText(text);
                                }
                            });
                        }
                    });
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multithreading);

        initView();
    }

    @Override
    protected void onStart() {
        super.onStart();

        AgeraBus.getDefault()
                .compiler(User.class)
                .noPriority()
                .noSticky()
                .posting()//或者不需要亦可，默认就是posting属性
                .compile(mPostingUpdatable);

        AgeraBus.getDefault()
                .compiler(User.class)
                .noPriority()
                .noSticky()
                .main()
                .compile(mMainUpdatable);

        AgeraBus.getDefault()
                .compiler(User.class)
                .noPriority()
                .noSticky()
                .background()
                .compile(mBackgroundUpdatable);

        AgeraBus.getDefault()
                .compiler(User.class)
                .noPriority()
                .noSticky()
                .async()
                .compile(mAsyncUpdatable);
    }

    @Override
    protected void onStop() {
        super.onStop();

        AgeraBus.getDefault().removeUpdatable(mPostingUpdatable,User.class);
        AgeraBus.getDefault().removeUpdatable(mMainUpdatable,User.class);
        AgeraBus.getDefault().removeUpdatable(mBackgroundUpdatable,User.class);
        AgeraBus.getDefault().removeUpdatable(mAsyncUpdatable,User.class);
    }

    private void initView() {
        mInput = (TextInputEditText) findViewById(R.id.et_input);
        mPosting = (TextView) findViewById(R.id.tv_posting);
        mMain = (TextView) findViewById(R.id.tv_main);
        mBackground = (TextView) findViewById(R.id.tv_background);
        mAsync = (TextView) findViewById(R.id.tv_async);

        mResultSupplier = AgeraBus.getDefault().getSupplier(User.class);

        findViewById(R.id.bt_send)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final String name = mInput.getText().toString();
                        if (TextUtils.isEmpty(name)){
                            mInput.setError("请输入用户名");
                            mInput.requestFocus();
                            return;
                        }

                        Thread thread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                AgeraBus.getDefault()
                                        .post(new User(name));
                            }
                        },"postingThread");
                        thread.start();


                        mInput.setText("");
                        mInput.clearFocus();
                    }
                });
    }

}
