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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.config.AppConfig;

import java.util.Timer;
import java.util.TimerTask;

import io.rong.calllib.RongCallClient;

import static io.rong.imkit.utilities.RongUtils.screenWidth;

/**
 * Created by wang on 2017/6/19.
 */

public class FloatingService2 extends Service implements View.OnClickListener {
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

    private ImageView guaduan;
    private ImageView mute;
    private ImageView spearer;
    private TextView waitconn;
    /**
     * 创建浮动窗口设置布局参数的对象
     */
    WindowManager mWindowManager;

    GestureDetector mGestureDetector;
    private String customerRongCloudId;


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private Timer timer;
    private TimerTask timerTask = null;
    private long currentTime = 0;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (waitconn != null) {
                waitconn.setText("当前通话时间" + formatTime((currentTime)));
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

        return strMinute + " 分钟 " + strSecond + " 秒";
    }

    /**
     * 连接成功广播
     */
    private BroadcastReceiver floatconnect = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateicon(AppConfig.isConnect);
            //计时器
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
            currentTime = 0;
            timer.cancel();
        }
    }

    /**
     * 连接断开的广播
     */
    private BroadcastReceiver floatdisconnect = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            stopSelf(); //关闭服务
            stopTime();
        }
    };


    @Override
    public void onCreate() {
        super.onCreate();
        initWindow();  //设置窗口的参数

        //注册广播
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.cn.float2connect");
        registerReceiver(floatconnect, intentFilter);

        IntentFilter intentFilter2 = new IntentFilter();
        intentFilter2.addAction("com.cn.float2disconnect");
        registerReceiver(floatdisconnect, intentFilter2);

    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        initFloating();
        //接受上个服务传过来的时间  不走广播
        if (AppConfig.isConnect) {  // 只执行一次
            startTime();
        }
        customerRongCloudId = intent.getStringExtra("customerRongCloudId");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mlayout1 != null) {
            // 移除悬浮窗口
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
        mlayout1 = (LinearLayout) inflater.inflate(R.layout.floating_layout2, null);
        wmParams = getParams(wmParams);//设置好悬浮窗的参数
        mWindowManager.addView(mlayout1, wmParams);

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
        wmParams.format = PixelFormat.RGBA_8888;
        wmParams.flags = LayoutParams.FLAG_NOT_TOUCH_MODAL
                | LayoutParams.FLAG_NOT_FOCUSABLE;
        wmParams.gravity = Gravity.CENTER;
        wmParams.width = screenWidth * 3 / 4;
        wmParams.height = LayoutParams.WRAP_CONTENT;

        return wmParams;
    }

    /**
     * 找到悬浮窗的图标，并且设置事件
     * 设置悬浮窗的点击、滑动事件
     */
    private void initFloating() {
        mfloatingIv = (Button) mlayout1.findViewById(R.id.floating_imageView2);
        mGestureDetector = new GestureDetector(this, new MyOnGestureListener1());
        //设置监听器
        mlayout1.setOnTouchListener(new FloatingListener());
        mfloatingIv.setOnTouchListener(new FloatingListener());
        guaduan = (ImageView) mlayout1.findViewById(R.id.guaduan);
        guaduan.setOnClickListener(this);
        mfloatingIv.setOnClickListener(this);
        waitconn = (TextView) mlayout1.findViewById(R.id.waitconn);
        spearer = (ImageView) mlayout1.findViewById(R.id.speaker);
        mute = (ImageView) mlayout1.findViewById(R.id.mute);
        spearer.setOnClickListener(this);
        mute.setOnClickListener(this);

        if (mlayout1 != null) {
            mlayout1.setVisibility(View.VISIBLE);
        }
        updateicon(AppConfig.isConnect);

    }


    private int mTouchStartX, mTouchStartY, mTouchCurrentX, mTouchCurrentY;
    private int mStartX, mStartY, mStopX, mStopY;
    private boolean isMove;//判断悬浮窗是否移动

    private boolean muted = false; //是否靜音
    private boolean handFree = false;//是否免提

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.guaduan:
                stopTime();
                stopSelf();
                RongCallClient.getInstance().hangUpCall(customerRongCloudId);
                break;
            case R.id.floating_imageView2: //缩小悬浮窗
                Intent intent = new Intent(FloatingService2.this, FloatingService.class);
                intent.putExtra("customerRongCloudId", customerRongCloudId);
                startService(intent);
                mlayout1.setVisibility(View.INVISIBLE);
                break;
            case R.id.speaker: //免提
                RongCallClient.getInstance().setEnableSpeakerphone(!view.isSelected());
                view.setSelected(!view.isSelected());
                handFree = view.isSelected();
                if (view.isSelected()) {
                    spearer.setImageResource(R.drawable.speaker2);
                } else {
                    spearer.setImageResource(R.drawable.speaker);
                }
                break;
            case R.id.mute://静音
                RongCallClient.getInstance().setEnableLocalAudio(view.isSelected());
                view.setSelected(!view.isSelected());
                muted = view.isSelected();
                if (view.isSelected()) {
                    mute.setImageResource(R.drawable.mute2);
                } else {
                    mute.setImageResource(R.drawable.mute);
                }
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
                    mStopX = (int) event.getX();
                    mStopY = (int) event.getY();
                    //System.out.println("|X| = "+ Math.abs(mStartX - mStopX));
                    //System.out.println("|Y| = "+ Math.abs(mStartY - mStopY));
                    if (Math.abs(mStartX - mStopX) >= 1 || Math.abs(mStartY - mStopY) >= 1) {
                        isMove = true;
                    }
                    break;
            }
            return mGestureDetector.onTouchEvent(event);  // 此处必须返回false，否则OnClickListener获取不到监听
        }
    }


    /**
     * 自己定义的手势监听类
     */
    class MyOnGestureListener1 extends SimpleOnGestureListener {
        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            return super.onSingleTapConfirmed(e);
        }
    }

    /**
     * 通话成功后显示免提和静音
     */
    private void updateicon(boolean isconnect) {
        if (isconnect) {
            spearer.setVisibility(View.VISIBLE);
            mute.setVisibility(View.VISIBLE);
        } else {
            spearer.setVisibility(View.GONE);
            mute.setVisibility(View.GONE);
        }
    }

}
