package com.application.breakmoment.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.application.breakmoment.AlarmRingActivity;

/**
 * 类AlarmReceiver.java的实现描述：TODO 类实现描述
 * 
 * @author hankunfang 2013年12月8日 下午2:55:43
 */
public class AlarmReceiver extends BroadcastReceiver {

    // private MediaPlayer mMediaPlayer;
    Context context;

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
        this.context = context;

        Intent it = new Intent(context, AlarmRingActivity.class);
        it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(it);
    }
}
