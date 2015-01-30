package com.shengshi.base.widget;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.widget.TextView;

import com.shengshi.base.tools.Log;

import java.text.DecimalFormat;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * <p>Title:           类似余额宝数字滚动效果
 * <p>Description:
 * <p>1.设置跳跃的帧数，setFrames(int frames)，默认是25帧
 * <p>2.设置数字格式 ， setFormat(String pattern)，具体查DecimalFormat类的api
 * <p>3.需要动画效果用，playNumber(double number)方法代替setText()方法，小数如果超过3位小数会四舍五入保留2位小数
 * <p>@author:  Lance
 * <p>Copyright: Copyright (c) 2014
 * <p>Company: @小鱼网
 * <p>Create Time: 2014-11-11
 * <p>@author:
 * <p>Update Time:
 * <p>Updater:
 * <p>Update Comments:
 */
public class RunningTextView extends TextView {

    public double resultNumber;// 最后显示的数字
    private int frames = 25;// 总共跳跃的帧数,默认25跳
    private double nowNumber = 0.00;// 显示的时间
    private ExecutorService threadPool;
    private Handler mHandler;
    private DecimalFormat formater;// 格式化金钱，保留两位小数

    public RunningTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public RunningTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RunningTextView(Context context) {
        super(context);
        init();
    }

    public int getFrames() {
        return frames;
    }

    // 设置帧数
    public void setFrames(int frames) {
        this.frames = frames;
    }

    /**
     * 设置数字格式，具体查DecimalFormat类的api
     *
     * @param pattern
     */
    public void setFormat(String pattern) {
        formater = new DecimalFormat(pattern);
    }

    private void init() {
        threadPool = Executors.newFixedThreadPool(2);// 2个线程的线程池
        formater = new DecimalFormat("00.00");// 最多两位小数，而且不够两位整数用0占位。可以通过setFormat再次设置
        mHandler = new Handler() {

            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                setText(formater.format(nowNumber).toString());// 更新显示的数字
                nowNumber += Double.parseDouble(msg.obj.toString());// 跳跃arg1那么多的数字间隔
                if (nowNumber < resultNumber) {
                    Message msg2 = mHandler.obtainMessage();
                    msg2.obj = msg.obj;
                    mHandler.sendMessage(msg2);// 继续发送通知改变UI
                } else {
                    setText(formater.format(resultNumber).toString());// 最后显示的数字，动画停止
                }
            }
        };
    }

    public void playNumber(String moneyNumberStr) {
        double moneyNumber = 0.00;
        try {
            moneyNumber = Double.parseDouble(moneyNumberStr);
        } catch (NumberFormatException e) {
            Log.e(e.getMessage(), e);
        }
        playNumber(moneyNumber);
    }

    /**
     * 播放数字动画的方法
     *
     * @param moneyNumber
     */
    public void playNumber(double moneyNumber) {
        if (moneyNumber == 0) {
            setText("0.00");
            return;
        }
        resultNumber = moneyNumber;// 设置最后要显示的数字
        nowNumber = 0.00;// 默认都是从0开始动画
        threadPool.execute(new Runnable() {
            @Override
            public void run() {
                Message msg = mHandler.obtainMessage();
                double temp = resultNumber / frames;
                msg.obj = temp < 0.01 ? 0.01 : temp;// 如果每帧的间隔比1小，就设置为1
                mHandler.sendMessage(msg);// 发送通知改变UI
            }
        });
    }

}
