package com.application.breakmoment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.Vibrator;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.application.breskmoment.listener.SenorShakeListener;
import com.application.breskmoment.listener.SenorShakeListener.OnShakeListener;

/**
 * 类AlarmRingActivity.java的实现描述：TODO 类实现描述
 * 
 * @author hankunfang 2013年12月8日 下午8:03:14
 */
@SuppressLint("HandlerLeak")
public class AlarmRingActivity extends Activity implements OnShakeListener {

    private SenorShakeListener mShakeListener;
    private Vibrator         vibrator;
    private static final int   SENSOR_SHAKE = 10;
    private static final int   EXIT_BUTTON  = 11;
    private SoundPool        mSoundPool;
    private WakeLock           mWakelock;
    private LinearLayout       lv_exit_bt;
    private String             alarmService = "com.application.breakmoment.alarmservice";

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 锁屏情况显示设置
        final Window win = getWindow();
        win.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                     | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        win.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        // contentView填充
        setContentView(R.layout.ac_alarmring);
        
        lv_exit_bt = (LinearLayout) findViewById(R.id.ll_alarmexit_bt);
        lv_exit_bt.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Message msg = mHandler.obtainMessage();
                msg.what = EXIT_BUTTON;
                mHandler.sendMessage(msg);
            }
        });
        
        // 图片动画设置
        ImageView iv_alarm = (ImageView) findViewById(R.id.iv_alarm);
        Animation shakeAnimation = AnimationUtils.loadAnimation(this, R.anim.shake);
        iv_alarm.startAnimation(shakeAnimation);

        // 启动铃声service调用
        Intent serviceIntent = new Intent(alarmService);
        startService(serviceIntent);
        
        // 铃声设置
        mSoundPool = new SoundPool(10, AudioManager.STREAM_SYSTEM, 5);
        mSoundPool.load(this, R.raw.shakesound, 1);
        mSoundPool.load(this, R.raw.shakeend, 1);
        
        // 摇动监听listener 初始化
        mShakeListener = new SenorShakeListener(getApplicationContext());
        mShakeListener.setOnShakeListener(this);
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onResume() {
        super.onResume();
        startShakeListener();

        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        mWakelock = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_DIM_WAKE_LOCK,
                                   this.getComponentName().getShortClassName());
        if (mWakelock != null) mWakelock.acquire();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mWakelock != null) mWakelock.release();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSoundPool.release();
        stopShakeListener();
    }

    private Handler mHandler = new Handler() {
                                 public void handleMessage(android.os.Message msg) {
                                     switch (msg.what) {
                                         case SENSOR_SHAKE:
                                             mSoundPool.play(1, 1, 1, 0, 0, 1);
                                             vibrator.vibrate(200);
                                             Intent serviceIntent = new Intent(alarmService);
                                             stopService(serviceIntent);
                                             finish();
                                             break;
                                         case EXIT_BUTTON:
                                             Intent i = new Intent(alarmService);
                                             stopService(i);
                                             finish();
                                             break;
                                         default:
                                             break;
                                     }
                                 }
                             };

    private void startShakeListener() {
        mShakeListener.start();
    }

    private void stopShakeListener() {
        mShakeListener.stop();
    }

    @Override
    public void onShake() {
        Message msg = mHandler.obtainMessage();
        msg.what = SENSOR_SHAKE;
        mHandler.sendMessage(msg);
    }

}
