package com.application.breakmoment;

import java.util.Calendar;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

/**
 * 类MainActivity.java的实现描述：TODO 类实现描述
 * 
 * @author hankunfang 2013年12月8日 下午2:55:36
 */
public class MainActivity extends Activity {

    private LinearLayout        ll_settime;
    private TextView mTextView;
    private Calendar calendar;
    private String INTENT_NAME = "com.application.breakmoment.intent.action";
    private static AlarmManager alarmManager;
    private Boolean             canExit     = false;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        mTextView = (TextView)this.findViewById(R.id.mText);
        ll_settime = (LinearLayout) this.findViewById(R.id.ll_alarmset_bt);

        calendar = Calendar.getInstance();
        
        final SharedPreferences sharedPreferences = getSharedPreferences(getPackageName(), 0);
        String time = sharedPreferences.getString("time", null);

        if (time != null) {
            mTextView.setText(getResources().getString(R.string.timeTips) + time);
        }
        
        ll_settime.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                calendar.setTimeInMillis(System.currentTimeMillis());
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);
                new SelfTimePickerDialog(MainActivity.this, new SelfTimePickerDialog.OnTimeSetListener() {
                    
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        calendar.setTimeInMillis(System.currentTimeMillis());
                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        calendar.set(Calendar.MINUTE, minute);
                        calendar.set(Calendar.SECOND, 0);
                        calendar.set(Calendar.MILLISECOND, 0);
                        
                        Intent intent = new Intent(INTENT_NAME);
                        PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                        
                        // 获取系统进程
                        alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
                        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                        // 设置周期
                        //alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()+(10*1000), (24*60*60*1000), pendingIntent);
                        String tmps = format(hourOfDay) + ":" + format(minute);
                        mTextView.setText(getResources().getString(R.string.timeTips) + tmps);
                        sharedPreferences.edit().putString("time", tmps).commit();
                    }
                },hour,minute,true).show();
                canExit = false;
            }


        });

    }
    
    private String format(int x) {
        String s = ""+x;
        if(s.length() == 1)
            s = "0"+s;
        return s;
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        canExit = false;
    }
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

    @Override
    public void onBackPressed() {
        if (canExit) {
            finish();
        } else {
            Toast.makeText(this, R.string.hint_back_to_exit, Toast.LENGTH_SHORT).show();
            canExit = true;
        }
    }

    public class SelfTimePickerDialog extends TimePickerDialog {

        public SelfTimePickerDialog(Context context, OnTimeSetListener callBack, int hourOfDay, int minute,
                                    boolean is24HourView) {
            super(context, callBack, hourOfDay, minute, is24HourView);
        }

        public SelfTimePickerDialog(Context context, OnTimeSetListener callBack, Calendar c) {
            super(context, callBack, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), true);
        }

        @Override
        protected void onStop() {
            // super.onStop();
        }

        @Override
        public void show() {
            // TODO Auto-generated method stub
            this.setButton(BUTTON_NEGATIVE, getResources().getString(R.string.timepicker_dialog_canel),
                           new OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // TODO Auto-generated method stub
                }
            });
            super.show();
        }

    }
}
