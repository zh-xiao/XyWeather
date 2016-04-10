package com.xy.weather.utils;

import java.util.Timer;
import java.util.TimerTask;

import android.R.integer;
import android.content.Context;
import android.net.TrafficStats;
import android.os.Handler;
import android.os.Message;

/**
 * 监听手机总的网速
 * 
 * 监视网速调用getNetSpeedByPeriod方法
 * 
 * 取消监视调用cancle方法
 * 
 * @author zhangxiao
 * 
 */
public class NetSpeedWatcher {
    private long lastTime = 0;
    private long currentTime = 0;
    private long lastUidRxBytes = 0;
    private long currentUidRxBytes = 0;
    private Timer timer = new Timer();

    private long getTotalRxBytes(Context context) {
        return TrafficStats.getUidRxBytes(context.getApplicationInfo().uid) == TrafficStats.UNSUPPORTED ? 0
                : (TrafficStats.getTotalRxBytes()) / 1024;
    }

    private void getNetSpeed(Context context, Handler handler) {
        currentTime = System.currentTimeMillis();
        currentUidRxBytes = getTotalRxBytes(context);
        Message msg = new Message();
        msg.what = 111;
        if (lastUidRxBytes != 0 && lastTime != 0) {
            msg.obj = (currentUidRxBytes - lastUidRxBytes) * 1000 / (currentTime - lastTime) + "kb/s";
            handler.sendMessage(msg);
        }
        lastTime = currentTime;
        lastUidRxBytes = currentUidRxBytes;
    }

    /**
     * 按周期更新当前网速
     * 
     * @param context
     * @param handler
     * @param delay
     *            延时(毫秒)
     * @param period
     *            周期(毫秒)
     */
    public void getNetSpeedByPeriod(final Context context, final Handler handler, int delay, int period) {
        timer.schedule(new TimerTask() {

            @Override
            public void run() {
                getNetSpeed(context, handler);
            }
        }, delay, period);
    }

    /**
     * 取消网速监视
     */
    public void cancle() {
        timer.cancel();
    }
}
