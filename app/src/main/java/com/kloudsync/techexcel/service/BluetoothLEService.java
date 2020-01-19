package com.kloudsync.techexcel.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.tqltech.tqlpencomm.BLEException;
import com.tqltech.tqlpencomm.Dot;
import com.tqltech.tqlpencomm.ElementCode;
import com.tqltech.tqlpencomm.PenCommAgent;
import com.tqltech.tqlpencomm.PenStatus;
import com.tqltech.tqlpencomm.listener.TQLPenSignal;
import com.tqltech.tqlpencomm.util.BLELogUtil;


public class BluetoothLEService extends Service {
    private final static String TAG = "BluetoothLEService";
    private String mBluetoothDeviceAddress;
    public final static String ACTION_GATT_CONNECTED = "ACTION_GATT_CONNECTED";
    public final static String ACTION_GATT_DISCONNECTED = "ACTION_GATT_DISCONNECTED";
    public final static String ACTION_GATT_SERVICES_DISCOVERED = "ACTION_GATT_SERVICES_DISCOVERED";
    public final static String ACTION_DATA_AVAILABLE = "ACTION_DATA_AVAILABLE";
    public final static String ACTION_PEN_STATUS_CHANGE = "ACTION_PEN_STATUS_CHANGE";
    public final static String RECEVICE_DOT = "RECEVICE_DOT";

    public final static String DEVICE_DOES_NOT_SUPPORT_UART = "DEVICE_DOES_NOT_SUPPORT_UART";
    private PenCommAgent bleManager;
    private boolean isPenConnected = false;
    private Handler handlerThree = new Handler(Looper.getMainLooper());
    //----

    public static String mPenName = "SmartPen";
    public static String mFirmWare = "B736_OID1-V10000";
    public static String mMCUFirmWare = "MCUF_R01";
    public static String mCustomerID = "0000";
    public static String mBTMac = "00:00:00:00:00:2F";
    public static int mBattery = 100;
    public static boolean mCharging = false;
    public static int mUsedMem = 0;
    public static boolean mBeep = true;
    public static boolean mPowerOnMode = true;
    public static int mPowerOffTime = 20;
    public static long mTimer = 1262275200; // 2010-01-01 00:00:00
    public static int mPenSens = 0;
    public static int mTwentyPressure = 0;
    public static int mThreeHundredPressure = 0;
    public static long mElementCode = 0;

    public static String tmp_mPenName;
    public static boolean tmp_mBeep = true;
    public static boolean tmp_mPowerOnMode = true;
    public static boolean tmp_mEnableLED = false;
    public static int tmp_mPowerOffTime;
    public static int tmp_mPenSens;
    public static long tmp_mTimer;

    public boolean getPenStatus() {
        return isPenConnected;
    }

    private void broadcastUpdate(final String action) {
        final Intent intent = new Intent(action);
        sendBroadcast(intent);
    }

    public class LocalBinder extends Binder {
        public BluetoothLEService getService() {
            return BluetoothLEService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        // After using a given device, you should make sure that BluetoothGatt.close() is called
        // such that resources are cleaned up properly.  In this particular example, close() is
        // invoked when the UI is disconnected from the Service.

        BLELogUtil.i(TAG, "BluetoothLEService onUnbind");
        close();
        return super.onUnbind(intent);
    }

    private final IBinder mBinder = new LocalBinder();

    /**
     * Initializes a reference to the local Bluetooth adapter.
     *
     * @return Return true if the initialization is successful.
     */
    public boolean initialize(TQLPenSignal penSignal) {
        bleManager = PenCommAgent.GetInstance(getApplication());
        bleManager.setTQLPenSignalListener(penSignal);
        if (!bleManager.isSupportBluetooth()) {
            Log.e(TAG, "Unable to Support Bluetooth");
            return false;
        }

        if (!bleManager.isSupportBLE()) {
            Log.e(TAG, "Unable to Support BLE.");
            return false;
        }

        return true;
    }

    public boolean connect(final String address) {
        if (address == null) {
            Log.w(TAG, "BluetoothAdapter not initialized or unspecified address.");
            return false;
        }

        // Previously connected device.  Try to reconnect.
        if (mBluetoothDeviceAddress != null && address.equals(mBluetoothDeviceAddress)
                && bleManager.isConnect(address)) {
            Log.d(TAG, "Trying to use an existing pen for connection.===");
            return true;
        }

        Log.d(TAG, "Trying to create a new connection.");
        boolean flag = bleManager.connect(address);
        if (!flag) {
            Log.i(TAG, "bleManager.connect(address)-----false");
            return false;
        }

        Log.i(TAG, "bleManager.connect(address)-----true");
        return true;
    }

    public void disconnect() {
        BLELogUtil.i(TAG, "BluetoothLEService disconnect");
        bleManager.disconnect(mBluetoothDeviceAddress);
    }

    public void close() {
        if (bleManager == null) {
            return;
        }

        Log.w(TAG, "mBluetoothGatt closed");
        BLELogUtil.i(TAG, "BluetoothLEService close");
        bleManager.disconnect(mBluetoothDeviceAddress);
        mBluetoothDeviceAddress = null;
        bleManager = null;
    }

    /// ===========================================================
    private OnDataReceiveListener onDataReceiveListener = null;

    public interface OnDataReceiveListener {

        void onDataReceive(Dot dot);

        void onOfflineDataReceive(Dot dot);

        void onFinishedOfflineDown(boolean success);

        void onOfflineDataNum(int num);

        void onReceiveOfflineProgress(int i);

        void onReceivePenLED(int color);

        void onWriteCmdResult(int code);

        void onReceivePenType(String type);
    }

    public void setOnDataReceiveListener(OnDataReceiveListener dataReceiveListener) {
        onDataReceiveListener = dataReceiveListener;
    }


    private TQLPenSignal mPenSignalCallback = new TQLPenSignal() {

        /**********************************************************/
        /****************** part1: 蓝牙连接相关 *******************/
        /**********************************************************/
        @Override
        public void onConnected() {
            Log.d(TAG, "TQLPenSignal had Connected");
            String intentAction;

            intentAction = ACTION_GATT_CONNECTED;
            broadcastUpdate(intentAction);
            isPenConnected = true;
        }

        @Override
        public void onDisconnected() {
            String intentAction;
            Log.d(TAG, "TQLPenSignal had onDisconnected");
            intentAction = ACTION_GATT_DISCONNECTED;
            broadcastUpdate(intentAction);
            isPenConnected = false;
        }

        @Override
        public void onConnectFailed() {
            String intentAction;
            Log.d(TAG, "TQLPenSignal had onDisconnected");
            intentAction = ACTION_GATT_DISCONNECTED;
            broadcastUpdate(intentAction);
            isPenConnected = false;
            handlerThree.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), "连接失败", Toast.LENGTH_SHORT).show();
                }
            });
        }

        /**********************************************************/
        /****************** part2: 在线数据    *******************/
        /**********************************************************/
        @Override
        public void onReceiveDot(Dot dot) {
            Log.d(TAG, "bluetooth service recivice=====" + dot.toString());
            if (onDataReceiveListener != null) {
                onDataReceiveListener.onDataReceive(dot);
            }
        }

        /**********************************************************/
        /****************** part3: 离线数据    *******************/
        /**********************************************************/
        @Override
        public void onReceiveOfflineStrokes(Dot dot) {
            Log.d(TAG, dot.toString());
            if (onDataReceiveListener != null) {
                onDataReceiveListener.onOfflineDataReceive(dot);
            }
        }

        @Override
        public void onOfflineDataList(int offlineNotes) {
            if (onDataReceiveListener != null) {
                onDataReceiveListener.onOfflineDataNum(offlineNotes);
            }
        }

        @Override
        public void onStartOfflineDownload(final boolean isSuccess) {
            handlerThree.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), "StartOffline-->" + isSuccess, Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public void onStopOfflineDownload(boolean isSuccess) {

        }

        @Override
        public void onPenPauseOfflineDataTransferResponse(boolean isSuccess) {
            Log.i(TAG, "onPenPauseOfflineDataTransferResponse: " + isSuccess);
        }

        @Override
        public void onPenContinueOfflineDataTransferResponse(final boolean isSuccess) {
            Log.i(TAG, "onPenContinueOfflineDataTransferResponse: " + isSuccess);
            handlerThree.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), "ContinueOffline-->" + isSuccess, Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public void onFinishedOfflineDownload(boolean isSuccess) {
            Log.i(TAG, "-------offline download success-------");
            if (onDataReceiveListener != null) {
                onDataReceiveListener.onFinishedOfflineDown(isSuccess);
            }
        }

        @Override
        public void onReceiveOfflineProgress(int i) {
            Log.i(TAG, "onReceiveOfflineProgress----" + i);
            synchronized (this) {
                if (onDataReceiveListener != null) {
                    onDataReceiveListener.onReceiveOfflineProgress(i);
                }
            }
        }

        @Override
        public void onPenDeleteOfflineDataResponse(boolean isSuccess) {

        }

        /**********************************************************/
        /****************** part4: 请求的回复   *******************/
        /**********************************************************/
        @Override
        public void onReceivePenAllStatus(PenStatus status) {
            mBattery = status.mPenBattery;
            mUsedMem = status.mPenMemory;
            mTimer = status.mPenTime;
            Log.e(TAG, "mTimer is " + mTimer + ", status is " + status.toString());mPowerOnMode = status.mPenPowerOnMode;
            mPowerOffTime = status.mPenAutoOffTime;mBeep = status.mPenBeep;
            mPenSens = status.mPenSensitivity;
            tmp_mEnableLED = status.mPenEnableLed;

            mPenName = status.mPenName;mBTMac = status.mPenMac;
            mFirmWare = status.mBtFirmware;
            mMCUFirmWare = status.mPenMcuVersion;
            mCustomerID = status.mPenCustomer;

            mTwentyPressure = status.mPenTwentyPressure;
            mThreeHundredPressure = status.mPenThirdPressure;

            String intentAction = ACTION_PEN_STATUS_CHANGE;
            broadcastUpdate(intentAction);
        }

        @Override
        public void onReceivePenMac(String penMac) {
            Log.e(TAG, "receive pen Mac " + penMac);
            mBluetoothDeviceAddress = penMac;
        }

        @Override
        public void onReceivePenName(String penName) {

        }

        @Override
        public void onReceivePenBtFirmware(String penBtFirmware) {

        }

        @Override
        public void onReceivePenTime(long penTime) {

        }

        @Override
        public void onReceivePenBattery(int penBattery, boolean bIsCharging) {
            Log.e(TAG, "receive pen battery is " + penBattery);
        }

        @Override
        public void onReceivePenMemory(int penMemory) {

        }

        @Override
        public void onReceivePenAutoPowerOnModel(boolean bIsOn) {

        }

        @Override
        public void onReceivePenBeepModel(boolean bIsOn) {

        }

        @Override
        public void onReceivePenAutoOffTime(int autoOffTime) {

        }

        @Override
        public void onReceivePenMcuVersion(String penMcuVersion) {

        }

        @Override
        public void onReceivePenSensitivity(int penSensitivity) {

        }

        @Override
        public void onReceivePenType(String penType) {
            if (onDataReceiveListener != null) {
                onDataReceiveListener.onReceivePenType(penType);
            }
        }

        @Override
        public void onReceivePenDotType(int penDotType) {

        }

        @Override
        public void onReceivePenLedConfig(int penLedConfig) {
            Log.e(TAG, "receive hand write color is " + penLedConfig);
            if (onDataReceiveListener != null) {
                onDataReceiveListener.onReceivePenLED(penLedConfig);
            }
        }

        @Override
        public void onReceivePenEnableLed(Boolean bEnableFlag) {

        }

        @Override
        public void onReceivePresssureValue(int minPressure, int maxPressure) {

        }


        /**********************************************************/
        /****************** part5: 设置的回复   *******************/
        /**********************************************************/
        @Override
        public void onPenNameSetupResponse(boolean bIsSuccess) {
            if (bIsSuccess) {
                mPenName = tmp_mPenName;
            }
            String intentAction = ACTION_PEN_STATUS_CHANGE;
            Log.i(TAG, "Disconnected from GATT server.");
            broadcastUpdate(intentAction);
            handlerThree.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), "设置名字成功", Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public void onPenTimetickSetupResponse(boolean bIsSuccess) {
            if (bIsSuccess) {
                mTimer = tmp_mTimer;
            }
            String intentAction = ACTION_PEN_STATUS_CHANGE;
            Log.i(TAG, "Disconnected from GATT server.");
            broadcastUpdate(intentAction);
            handlerThree.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), "设置RTC时间成功", Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public void onPenAutoShutdownSetUpResponse(boolean bIsSuccess) {
            if (bIsSuccess) {
                mPowerOffTime = tmp_mPowerOffTime;
            }
            String intentAction = ACTION_PEN_STATUS_CHANGE;
            Log.i(TAG, "Disconnected from GATT server.");
            broadcastUpdate(intentAction);
            handlerThree.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), "设置自动关机时间成功", Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public void onPenFactoryResetSetUpResponse(boolean bIsSuccess) {

        }

        @Override
        public void onPenAutoPowerOnSetUpResponse(boolean bIsSuccess) {
            if (bIsSuccess) {
                mPowerOnMode = tmp_mPowerOnMode;
            }
            String intentAction = ACTION_PEN_STATUS_CHANGE;
            Log.i(TAG, "Disconnected from GATT server.");
            broadcastUpdate(intentAction);
        }

        @Override
        public void onPenBeepSetUpResponse(boolean bIsSuccess) {
            if (bIsSuccess) {
                mBeep = tmp_mBeep;
            }
            String intentAction = ACTION_PEN_STATUS_CHANGE;
            Log.i(TAG, "Disconnected from GATT server.");
            broadcastUpdate(intentAction);
        }

        @Override
        public void onPenSensitivitySetUpResponse(boolean bIsSuccess) {
            if (bIsSuccess) {
                mPenSens = tmp_mPenSens;
            }
            String intentAction = ACTION_PEN_STATUS_CHANGE;
            Log.i(TAG, "Disconnected from GATT server.");
            broadcastUpdate(intentAction);
            handlerThree.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), "设置灵敏度成功", Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public void onPenLedConfigResponse(boolean bIsSuccess) {

        }

        @Override
        public void onPenDotTypeResponse(boolean bIsSuccess) {

        }

        @Override
        public void onPenChangeLedColorResponse(boolean bIsSuccess) {

        }


        /**********************************************************/
        /****************** part6: 上报   *******************/
        /**********************************************************/
        @Override
        public void onReceiveElementCode(ElementCode elementCode) {
            Log.e(TAG, "onReceiveOIDFormat---> " + elementCode);
            mElementCode = elementCode.index;
            //if (onDataReceiveListener != null) {
            //    onDataReceiveListener.onReceiveOIDSize( penOIDSize);
            //}
            String intentAction;
            intentAction = ACTION_PEN_STATUS_CHANGE;
            broadcastUpdate(intentAction);
        }

        @Override
        public void onReceivePenHandwritingColor(int color) {
            Log.e(TAG, "receive hand write color is " + color);
            if (onDataReceiveListener != null) {
                onDataReceiveListener.onReceivePenLED(color);
            }
        }


        /**********************************************************/
        /****************** part7: 其它   *******************/
        /**********************************************************/
        @Override
        public void onWriteCmdResult(final int code) {
            Log.i(TAG, "onWriteCmdResult: " + code);
            handlerThree.post(new Runnable() {
                @Override
                public void run() {
                    if (code != 0) {
                        Toast.makeText(getApplicationContext(), "WriteCmdResult :" + code, Toast.LENGTH_SHORT).show();
                    }
                }
            });

            if (onDataReceiveListener != null) {
                onDataReceiveListener.onWriteCmdResult(code);
            }
        }

        @Override
        public void onException(final BLEException exception) {
            handlerThree.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), "onException :" + exception.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        }
    };

    public PenCommAgent getBleManager() {
        return bleManager;
    }

}

