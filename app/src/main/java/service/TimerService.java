package service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import listener.TimerCallBack;

/**
 * Created by Administrator on 2017/11/9.
 */

public class TimerService extends Service {

    private boolean pushthread = false;
    public static TimerCallBack timerCallBack;

    public TimerService() {
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent.getStringExtra("flag").equals("1")) {
            int currentApiVersion = Build.VERSION.SDK_INT;
            if (currentApiVersion > 20) {
                getPushThread();
            } else {
                getHttp();
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        pushthread = false;
    }

    private void getPushThread() {
        pushthread = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(10000);
                    getHttp();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void getHttp() {
        timerCallBack.timerCall();
    }

    public static void getConnet(Context mContext, TimerCallBack callBack) {
        timerCallBack = callBack;
        try {
            Intent intent = new Intent(mContext, TimerService.class);
            intent.putExtra("flag", "1");
            int currentApiVersion = Build.VERSION.SDK_INT;
            if (currentApiVersion > 20) {
                mContext.startService(intent);
            } else {
                PendingIntent pIntent = PendingIntent.getService(mContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 10000, pIntent);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void stop(Context mContext) {
        Intent intent = new Intent(mContext, TimerService.class);
        PendingIntent pIntent = PendingIntent.getService(mContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pIntent);
    }
}
