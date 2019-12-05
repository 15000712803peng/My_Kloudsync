package com.kloudsync.techexcel.help;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.tstudy.blepenlib.BlePenStreamManager;
import com.tstudy.blepenlib.callback.BleScanCallback;
import com.tstudy.blepenlib.data.BleDevice;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by tonyan on 2019/12/4.
 */

public class DigitalPenManager {

    private static DigitalPenManager mgr;
    private BluetoothAdapter mBluetoothAdapter;
    private Activity host;
    private static final int REQUEST_CODE_PERMISSION_LOCATION = 2;
    private static final int REQUEST_OPEN_BT_CODE = 3;

    public static DigitalPenManager getMgr(Activity host) {
        if (mgr == null) {
            synchronized (DigitalPenManager.class) {
                if (mgr == null) {
                    mgr = new DigitalPenManager(host);
                }
            }
        }
        return mgr;
    }

    private List<BleDevice> digitalPens = new ArrayList<>();

    private DigitalPenManager(Activity host) {
        this.host = host;
    }

    private boolean bluetoothEnable() {

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            Toast.makeText(host, "检查设备是否支持蓝牙BLE", Toast.LENGTH_LONG).show();
            return false;
        }

        if (!mBluetoothAdapter.isEnabled()) {
            Toast.makeText(host, "请先打开蓝牙", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            host.startActivityForResult(intent, REQUEST_OPEN_BT_CODE);
            return false;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //如果 API level 是大于等于 23(Android 6.0) 时
            //判断是否具有权限
            if (ContextCompat.checkSelfPermission(host, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                //判断是否需要向用户解释为什么需要申请该权限
                if (ActivityCompat.shouldShowRequestPermissionRationale(host, Manifest.permission.ACCESS_COARSE_LOCATION)) {
                    Toast.makeText(host, "自Android 6.0开始需要打开位置权限才可以搜索到Ble设备", Toast.LENGTH_LONG).show();
                }
                //请求权限
                ActivityCompat.requestPermissions(host,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        REQUEST_CODE_PERMISSION_LOCATION);
                return false;
            } else {
                return true;

            }
        } else {
            return true;
        }
    }

    public void setHost(Activity host){
        this.host = host;
    }

    private void scan() {
        BlePenStreamManager.getInstance().scan(scanCallback);
    }

    private Disposable disposable;

    public void getDigitalPens() {
          disposable = Observable.just(host).observeOn(Schedulers.io()).doOnNext(new Consumer<Activity>() {
            @Override
            public void accept(Activity activity) throws Exception {
                Log.e("getDigitalPens","one_do_on_next");
                if(!bluetoothEnable()){
                    if(disposable != null && !disposable.isDisposed()){
                        disposable.dispose();
                    }
                }
            }

        }).doOnNext(new Consumer<Activity>() {
              @Override
              public void accept(Activity activity) throws Exception {
                  Log.e("getDigitalPens","one_do_on_next");
                  scan();
              }
          }).subscribe();
    }

    private BleScanCallback scanCallback = new BleScanCallback() {
        //扫描回调

        @Override
        public void onScanFinished(List<BleDevice> list) {
            digitalPens.clear();
            digitalPens.addAll(list);
        }

        @Override
        public void onScanStarted(boolean b) {

        }

        @Override
        public void onScanning(BleDevice bleDevice) {

        }
    };

}
