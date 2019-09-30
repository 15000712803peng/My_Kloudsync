package com.mining.app.zxing;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.TextureView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.help.ApiTask;
import com.kloudsync.techexcel.help.ThreadManager;
import com.mining.app.zxing.camera.CameraManager;
import com.mining.app.zxing.decoding.CaptureActivityHandler;
import com.mining.app.zxing.decoding.InactivityTimer;
import com.mining.app.zxing.view.ViewfinderView;
import com.ub.techexcel.service.ConnectService;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Vector;

/**
 * Initial the camera
 *
 * @author Ryan.Tang
 */
public class MipcaActivityCapture extends Activity implements Callback, TextureView.SurfaceTextureListener {

    private CaptureActivityHandler handler;
    private ViewfinderView viewfinderView;
    private boolean hasSurface;
    private Vector<BarcodeFormat> decodeFormats;
    private String characterSet;
    private InactivityTimer inactivityTimer;
    private MediaPlayer mediaPlayer;
    private boolean playBeep;
    private static final float BEEP_VOLUME = 0.10f;
    private boolean vibrate;
    private boolean isHorization;
    private TextView titleText;
    private int type = 0;

    @SuppressLint("HandlerLeak")
    private Handler handler2 = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            playBeepSoundAndVibrate();
            switch (msg.what) {
                case AppConfig.SUCCESS:
                    String result = (String) msg.obj;
//					mJsonTV(result);
                    setResult(RESULT_OK);
                    finish();
                    break;
                case AppConfig.FAILED:
                    result = (String) msg.obj;
                    Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
                    RestartQRScan1();
                    RestartQRScan2();
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {

        isHorization = getIntent().getBooleanExtra("isHorization", false);
        type = getIntent().getIntExtra("type", 0);
        if (isHorization) {
            //设置横屏
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }else{
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture);
        titleText = (TextView) findViewById(R.id.tv_title);
        titleText.setText(R.string.qrcode);
        //ViewUtil.addTopView(getApplicationContext(), this, R.string.scan_card);
        CameraManager.init(getApplication());
        viewfinderView = (ViewfinderView) findViewById(R.id.viewfinder_view);

        RelativeLayout backLayout = (RelativeLayout) findViewById(R.id.layout_back);
        backLayout.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                MipcaActivityCapture.this.finish();
            }
        });
        hasSurface = false;
        inactivityTimer = new InactivityTimer(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        RestartQRScan2();
    }

    TextureView surfaceView;

    private void RestartQRScan2() {
        /*SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
        HorizationChange(surfaceView);
        SurfaceHolder surfaceHolder = surfaceView.getHolder();*/
        surfaceView = (TextureView) findViewById(R.id.preview_view);
        HorizationChange(surfaceView);
        surfaceView.setSurfaceTextureListener(this);
        if (hasSurface) {
//            initCamera(surfaceHolder);
            initCamera(surfaceView.getSurfaceTexture());
        } else {
//            surfaceHolder.addCallback(this);
//            surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }
        decodeFormats = null;
        characterSet = null;

        playBeep = true;
        AudioManager audioService = (AudioManager) getSystemService(AUDIO_SERVICE);
        if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
            playBeep = false;
        }
        initBeepSound();
        vibrate = true;
    }

    private void HorizationChange(TextureView surfaceView) {
        Log.e("duang",
                surfaceView.getRotation() + ":" + surfaceView.getPivotY() + ":" + surfaceView.getPivotX());
        if (isHorization) {
//            surfaceView.SetHorization(isHorization);
           /* SurfaceHolder surfaceHolder = surfaceView.getHolder();
            surfaceView.setZOrderOnTop(true);
            int width = getResources().getDisplayMetrics().widthPixels;
            int height = getResources().getDisplayMetrics().heightPixels;

            surfaceView.setPivotX(0);
            surfaceView.setPivotY(0);
            surfaceView.setRotation(90);*/
            int width = getResources().getDisplayMetrics().widthPixels;
            int height = getResources().getDisplayMetrics().heightPixels;

            ViewGroup.LayoutParams params = surfaceView.getLayoutParams();
            width = width > height ? height : width;
            surfaceView.setPivotX(width / 2);
            surfaceView.setPivotY(width / 2);
            params.height = width;
            params.width = width;
            surfaceView.setLayoutParams(params);
        }/*else{
            int width = getResources().getDisplayMetrics().widthPixels;
            int height = getResources().getDisplayMetrics().heightPixels;
            ViewGroup.LayoutParams params = surfaceView.getLayoutParams();
            surfaceView.setPivotX(width / 2);
            surfaceView.setPivotY(height / 2);
            params.height = height;
            params.width = width;
            surfaceView.setLayoutParams(params);
        }*/
        Log.e("duang",
                surfaceView.getRotation() + ":" + surfaceView.getPivotY() + ":" + surfaceView.getPivotX());
    }

    private void RestartQRScan1() {
        if (handler != null) {
            handler.quitSynchronously();
            handler = null;
        }
        CameraManager.get().closeDriver();
    }

    @Override
    protected void onPause() {
        super.onPause();
        RestartQRScan1();
    }

    @Override
    protected void onDestroy() {
        inactivityTimer.shutdown();
        super.onDestroy();
    }

    /**
     * 处理扫描结果
     *
     * @param result
     * @param barcode
     */
    public void handleDecode(Result result, Bitmap barcode) {
        inactivityTimer.onActivity();
        String resultString = result.getText();
        if (resultString.equals("")) {
            Toast.makeText(MipcaActivityCapture.this, getResources().getString(R.string.Scan_failed), Toast.LENGTH_SHORT).show();
        } else {
            Log.e("duang", resultString + ":" + barcode);
            NoticeTV(resultString);
        }
//		MipcaActivityCapture.this.finish();
    }

    private void NoticeTV(String resultString) {
        final JSONObject jsonobject = format(resultString);
        if (jsonobject == null) {
            return;
        }
        new ApiTask(new Runnable() {
            @Override
            public void run() {
                try {
                    Log.e("BindTV", jsonobject.toString() + "");
                    JSONObject responsedata = ConnectService.submitDataByJson(
                            AppConfig.URL_PUBLIC + "TV/BindTV",
                            jsonobject);
                    Log.e("BindTV", responsedata.toString() + "");
                    String retcode = responsedata.getString("RetCode");
                    Message msg = new Message();
                    if (retcode.equals(AppConfig.RIGHT_RETCODE)) {
                        msg.what = AppConfig.SUCCESS;
                        msg.obj = responsedata.toString();
                    } else {
                        msg.what = AppConfig.FAILED;
                        msg.obj = responsedata.getString("ErrorMessage");
                    }
                    handler2.sendMessage(msg);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start(ThreadManager.getManager());

    }


    private JSONObject format(String resultString) {
        JSONObject jsonObject = new JSONObject();
        try {
            String[] results = resultString.split("###Kloudsync_TV");
            if (results == null || results.length != 2) {
                Toast.makeText(this, "data format error ,please update", Toast.LENGTH_SHORT).show();
                return null;
            }
            jsonObject.put("TvID", results[0]);
            jsonObject.put("TvToken", results[1]);
            jsonObject.put("Type", 0);
            Log.e("eeee",resultString);
//            String[]  results = resultString.split("###Kloudsync_TV");
//            if(results == null || results.length != 2){
//                Toast.makeText(this,"data format error ,please update",Toast.LENGTH_SHORT).show();
//                return null;
//            }
//            jsonObject.put("TvID", results[0]);
//            jsonObject.put("TvToken",results[1]);
//            jsonObject.put("Type", type);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject;
    }

    //    private void initCamera(SurfaceHolder surfaceHolder) {
    private void initCamera(SurfaceTexture textureView) {
        try {
            CameraManager.get().openDriver(textureView, isHorization);
        } catch (IOException ioe) {
            return;
        } catch (RuntimeException e) {
            return;
        }
        if (handler == null) {
            handler = new CaptureActivityHandler(this, decodeFormats,
                    characterSet);
        }
    }

    @Override
    public void surfaceChanged(final SurfaceHolder holder, int format, final int width,
                               final int height) {
        if (isHorization) {

        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (!hasSurface) {
            hasSurface = true;
//            initCamera(holder);
        }

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        hasSurface = false;

    }

    public ViewfinderView getViewfinderView() {
        return viewfinderView;
    }

    public Handler getHandler() {
        return handler;
    }

    public void drawViewfinder() {
        viewfinderView.drawViewfinder();

    }

    private void initBeepSound() {
        if (playBeep && mediaPlayer == null) {
            // The volume on STREAM_SYSTEM is not adjustable, and users found it
            // too loud,
            // so we now play on the music stream.
            setVolumeControlStream(AudioManager.STREAM_MUSIC);
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setOnCompletionListener(beepListener);

            AssetFileDescriptor file = getResources().openRawResourceFd(
                    R.raw.beep);
            try {
                mediaPlayer.setDataSource(file.getFileDescriptor(),
                        file.getStartOffset(), file.getLength());
                file.close();
                mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
                mediaPlayer.prepare();
            } catch (IOException e) {
                mediaPlayer = null;
            }
        }
    }

    private static final long VIBRATE_DURATION = 200L;

    private void playBeepSoundAndVibrate() {
        if (playBeep && mediaPlayer != null) {
            mediaPlayer.start();
        }
        if (vibrate) {
            Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            vibrator.vibrate(VIBRATE_DURATION);
        }
    }

    /**
     * When the beep has finished playing, rewind to queue up another one.
     */
    private final OnCompletionListener beepListener = new OnCompletionListener() {
        public void onCompletion(MediaPlayer mediaPlayer) {
            mediaPlayer.seekTo(0);
        }
    };

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        if (!hasSurface) {
            hasSurface = true;
            if (isHorization) {
                surfaceView.setRotation(-90.0f);
            }
            initCamera(surface);
        }
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        hasSurface = false;
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }
}