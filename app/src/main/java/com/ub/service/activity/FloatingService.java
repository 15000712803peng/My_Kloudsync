package com.ub.service.activity;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kloudsync.techexcel.R;

import java.text.SimpleDateFormat;
import java.util.Timer;
import java.util.TimerTask;

import io.rong.calllib.RongCallClient;

/**
 * Created by wang on 2017/6/19.
 */

public class FloatingService extends Service implements View.OnClickListener {
    /**
     * 定义浮动窗口布局
     */
    LinearLayout mlayout1;
    /**
     * 悬浮窗控件
     */
    Button mfloatingIv;
    /**
     * 悬浮窗的布局
     */
    LayoutParams wmParams;
    LayoutInflater inflater;
    /**
     * 创建浮动窗口设置布局参数的对象
     */
    WindowManager mWindowManager;

    //触摸监听器
    GestureDetector mGestureDetector;
    TextView tv;

    private TextView waitconn; //等待接通按鈕
    private String customerRongCloudId;
    private int screenWidth;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private Timer timer;
    private TimerTask timerTask = null;
    private long currentTime = 0;

    private SimpleDateFormat format = new SimpleDateFormat("mm:ss");
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (waitconn != null) {
                waitconn.setText(formatTime(currentTime));
            }
        }
    };

    /**
     * 将毫秒数转化为分秒
     */
    private String formatTime(long ms) {

        int ss = 1000;
        int mi = ss * 60;
        int hh = mi * 60;
        int dd = hh * 24;

        long day = ms / dd;
        long hour = (ms - day * dd) / hh;
        long minute = (ms - day * dd - hour * hh) / mi;
        long second = (ms - day * dd - hour * hh - minute * mi) / ss;
        long milliSecond = ms - day * dd - hour * hh - minute * mi - second * ss;

        String strDay = day < 10 ? "0" + day : "" + day; //天
        String strHour = hour < 10 ? "0" + hour : "" + hour;//小时
        String strMinute = minute < 10 ? "0" + minute : "" + minute;//分钟
        String strSecond = second < 10 ? "0" + second : "" + second;//秒
        String strMilliSecond = milliSecond < 10 ? "0" + milliSecond : "" + milliSecond;//毫秒
        strMilliSecond = milliSecond < 100 ? "0" + strMilliSecond : "" + strMilliSecond;

        return strMinute + ":" + strSecond;
    }

    /**
     * 连接成功广播
     */
    private BroadcastReceiver floatconnect = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            startTime();
        }
    };

    /**
     * 开始自动计时
     */
    private void startTime() {
        if (timer == null) {
            timer = new Timer();
        }
        timerTask = new TimerTask() {
            @Override
            public void run() {
                currentTime = System.currentTimeMillis() - RongCallClient.getInstance().getCallSession().getActiveTime();
                //1000
                handler.sendEmptyMessage(0);
            }
        };
        timer.schedule(timerTask, 1000, 1000);
    }

    /**
     * 停止计时
     */
    private void stopTime() {
        if (timer != null) {
            timer.cancel();
        }
    }

    /**
     * 连接断开的广播
     */
    private BroadcastReceiver floatdisconnect = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            stopTime();
            stopSelf();
        }
    };


    @Override
    public void onCreate() {
        super.onCreate();
        initWindow();
        //注册广播
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.cn.floatconnect");
        registerReceiver(floatconnect, intentFilter);

        IntentFilter intentFilter2 = new IntentFilter();
        intentFilter2.addAction("com.cn.floatdisconnect");
        registerReceiver(floatdisconnect, intentFilter2);

    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        initFloating();//设置悬浮窗图标
        customerRongCloudId = intent.getStringExtra("customerRongCloudId");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mlayout1 != null) {
            mWindowManager.removeView(mlayout1);
        }
        if (floatconnect != null) {
            unregisterReceiver(floatconnect);
        }
        if (floatdisconnect != null) {
            unregisterReceiver(floatdisconnect);
        }
    }

    /**
     * 初始化windowManager
     */
    private void initWindow() {
        mWindowManager = (WindowManager) getApplication().getSystemService(Context.WINDOW_SERVICE);
        inflater = LayoutInflater.from(getApplication());
        mlayout1 = (LinearLayout) inflater.inflate(R.layout.floating_layout, null);
        wmParams = getParams(wmParams);//设置好悬浮窗的参数
        screenWidth = ((WindowManager) getApplication().getSystemService(getApplication().WINDOW_SERVICE)).getDefaultDisplay().getWidth();
        mWindowManager.addView(mlayout1, wmParams);
        Log.e("wmparams", "wmparams");
    }

    /**
     * 对windowManager进行设置
     *
     * @param wmParams
     * @return
     */
    public LayoutParams getParams(LayoutParams wmParams) {
        wmParams = new LayoutParams();
        wmParams.type = LayoutParams.TYPE_PHONE;
        wmParams.format = PixelFormat.RGBA_8888; // 背景透明
        wmParams.flags = LayoutParams.FLAG_NOT_TOUCH_MODAL
                | LayoutParams.FLAG_NOT_FOCUSABLE;
        wmParams.gravity = Gravity.CENTER;
        wmParams.width = LayoutParams.WRAP_CONTENT;
        wmParams.height = LayoutParams.WRAP_CONTENT;
        return wmParams;
    }

    /**
     * 找到悬浮窗的图标，并且设置事件
     * 设置悬浮窗的点击、滑动事件
     */
    private void initFloating() {
        mfloatingIv = (Button) mlayout1.findViewById(R.id.floating_imageView);
        tv = (TextView) mlayout1.findViewById(R.id.text);
        mGestureDetector = new GestureDetector(this, new MyOnGestureListener1());
        //设置监听器
        mlayout1.setOnTouchListener(new FloatingListener());
        mfloatingIv.setOnTouchListener(new FloatingListener());
        mfloatingIv.setOnClickListener(this);
        waitconn = (TextView) mlayout1.findViewById(R.id.waitconn);
        if (mlayout1 != null) {
            mlayout1.setVisibility(View.VISIBLE);
        }
    }


    //开始触控的坐标，移动时的坐标（相对于屏幕左上角的坐标）
    private int mTouchStartX, mTouchStartY, mTouchCurrentX, mTouchCurrentY;
    //开始时的坐标和结束时的坐标（相对于自身控件的坐标）
    private int mStartX, mStartY, mStopX, mStopY;
    private boolean isMove;//判断悬浮窗是否移动

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.floating_imageView:  // 放大悬浮窗
                mlayout1.setVisibility(View.INVISIBLE);
                Intent intent = new Intent(FloatingService.this, FloatingService2.class);
                intent.putExtra("customerRongCloudId", customerRongCloudId);
                startService(intent);
                break;
            default:
                break;
        }
    }

    /**
     * @tips :自己写的悬浮窗监听器
     */
    private class FloatingListener implements OnTouchListener {

        @Override
        public boolean onTouch(View arg0, MotionEvent event) {
            int action = event.getAction();
            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    isMove = false;
                    mTouchStartX = (int) event.getRawX();
                    mTouchStartY = (int) event.getRawY();
                    mStartX = (int) event.getX();
                    mStartY = (int) event.getY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    mTouchCurrentX = (int) event.getRawX();
                    mTouchCurrentY = (int) event.getRawY();
                    wmParams.x += mTouchCurrentX - mTouchStartX;

                    wmParams.y += mTouchCurrentY - mTouchStartY;

                    mWindowManager.updateViewLayout(mlayout1, wmParams);
                    mTouchStartX = mTouchCurrentX;
                    mTouchStartY = mTouchCurrentY;
                    break;
                case MotionEvent.ACTION_UP:
//                    mStopX = (int) event.getX();
//                    mStopY = (int) event.getY();
//                    if (Math.abs(mStartX - mStopX) >= 1 || Math.abs(mStartY - mStopY) >= 1) {
//                        isMove = true;
//                    }
                    mStopX = (int) event.getRawX();
                    mStopY = (int) event.getRawY();
                    float halfScreen = screenWidth / 2;
                    if (mStopX < halfScreen) {
                        mStopX = 0;
                    } else {
                        mStopX = screenWidth;
                    }
                    wmParams.x += mStopX - mTouchStartX;
                    wmParams.y += 0;
                    mWindowManager.updateViewLayout(mlayout1, wmParams);
                    mTouchStartX = mStopX;
                    mTouchStartY = mStopY;
                    break;
            }
            return mGestureDetector.onTouchEvent(event);  // 此处必须返回false，否则OnClickListener获取不到监听
        }

    }


    /**
     * @tips :自己定义的手势监听类
     */
    class MyOnGestureListener1 extends SimpleOnGestureListener {
        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            return super.onSingleTapConfirmed(e);
        }
    }

}
