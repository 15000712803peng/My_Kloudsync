package com.kloudsync.techexcel.help;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import com.kloudsync.techexcel.bean.EverPen;
import com.kloudsync.techexcel.service.BluetoothLEService;
import com.tqltech.tqlpencomm.BLEException;
import com.tqltech.tqlpencomm.BLEScanner;
import com.tqltech.tqlpencomm.Dot;
import com.tqltech.tqlpencomm.ElementCode;
import com.tqltech.tqlpencomm.PenCommAgent;
import com.tqltech.tqlpencomm.PenStatus;
import com.tqltech.tqlpencomm.listener.TQLPenSignal;
import com.tstudy.blepenlib.BlePenManager;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.BIND_AUTO_CREATE;
import static org.chromium.base.ThreadUtils.runOnUiThread;

/**
 * Created by tonyan on 2020/1/15.
 */

public class EverPenManger implements BluetoothLEService.OnDataReceiveListener, BLEScanner.OnBLEScanListener,TQLPenSignal {
    private static EverPenManger manger;
    private BluetoothLEService mService = null;
    PenCommAgent agent;
    Activity host;
    boolean serviceConnected;
    List<EverPen> everPens = new ArrayList<>();
    EverPen currentConnectedPen;

    private EverPenManger(Activity host){
        this.host = host;
    }

    public static EverPenManger getInstance(Activity host) {
        if (manger == null) {
            synchronized (EverPenManger.class) {
                if (manger == null) {
                    manger = new EverPenManger(host);
                }
            }
        }
        return manger;
    }

    public void init() {
        Intent gattServiceIntent = new Intent(host, BluetoothLEService.class);
        host.bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
    }

    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        public void onServiceConnected(final ComponentName className, IBinder rawBinder) {
            mService = ((BluetoothLEService.LocalBinder) rawBinder).getService();
            if (mService.initialize(getInstance(host))) {
                mService.setOnDataReceiveListener(getInstance(host));
                agent = mService.getBleManager();
                serviceConnected = true;
                Log.e("EverPenManger","onServiceConnected:" + agent);
                scanLeDevice();

            }

        }

        public void onServiceDisconnected(ComponentName classname) {
            mService = null;
        }
    };


    @Override
    public void onDataReceive(Dot dot) {

    }

    @Override
    public void onOfflineDataReceive(Dot dot) {

    }

    @Override
    public void onFinishedOfflineDown(boolean success) {

    }

    @Override
    public void onOfflineDataNum(int num) {

    }

    @Override
    public void onConnected() {
        if(currentConnectedPen != null){
            currentConnectedPen.setConnected(true);
        }
        Log.e("EverPenManager", "onConnected");

    }

    @Override
    public void onDisconnected() {
        Log.e("EverPenManager", "onDisconnected");
        if(currentConnectedPen != null){
            currentConnectedPen.setConnected(false);
        }
    }

    @Override
    public void onConnectFailed() {
        Log.e("EverPenManager", "onConnectFailed");
        if(currentConnectedPen != null){
            currentConnectedPen.setConnected(false);
        }
    }

    @Override
    public void onReceiveDot(Dot dot) {
        Log.e("EverPenManager", "onReceiveDot:" + dot);

    }

    @Override
    public void onReceiveOfflineStrokes(Dot dot) {

    }

    @Override
    public void onOfflineDataList(int i) {

    }

    @Override
    public void onStartOfflineDownload(boolean b) {

    }

    @Override
    public void onStopOfflineDownload(boolean b) {

    }

    @Override
    public void onPenPauseOfflineDataTransferResponse(boolean b) {

    }

    @Override
    public void onPenContinueOfflineDataTransferResponse(boolean b) {

    }

    @Override
    public void onFinishedOfflineDownload(boolean b) {

    }

    @Override
    public void onReceiveOfflineProgress(int i) {

    }

    @Override
    public void onPenDeleteOfflineDataResponse(boolean b) {

    }

    @Override
    public void onPenNameSetupResponse(boolean b) {

    }

    @Override
    public void onReceivePenName(String s) {

    }

    @Override
    public void onReceivePenMac(String s) {

    }

    @Override
    public void onReceivePenBtFirmware(String s) {

    }

    @Override
    public void onReceivePenBattery(int i, boolean b) {

    }

    @Override
    public void onPenTimetickSetupResponse(boolean b) {

    }

    @Override
    public void onReceivePenTime(long l) {

    }

    @Override
    public void onPenAutoShutdownSetUpResponse(boolean b) {

    }

    @Override
    public void onReceivePenAutoOffTime(int i) {

    }

    @Override
    public void onPenFactoryResetSetUpResponse(boolean b) {

    }

    @Override
    public void onReceivePenMemory(int i) {

    }

    @Override
    public void onPenAutoPowerOnSetUpResponse(boolean b) {

    }

    @Override
    public void onReceivePenAutoPowerOnModel(boolean b) {

    }

    @Override
    public void onPenBeepSetUpResponse(boolean b) {

    }

    @Override
    public void onReceivePenBeepModel(boolean b) {

    }

    @Override
    public void onPenSensitivitySetUpResponse(boolean b) {

    }

    @Override
    public void onReceivePenSensitivity(int i) {

    }

    @Override
    public void onPenLedConfigResponse(boolean b) {

    }

    @Override
    public void onReceivePenLedConfig(int i) {

    }

    @Override
    public void onPenDotTypeResponse(boolean b) {

    }

    @Override
    public void onPenChangeLedColorResponse(boolean b) {

    }

    @Override
    public void onReceivePresssureValue(int i, int i1) {

    }

    @Override
    public void onReceivePenMcuVersion(String s) {

    }

    @Override
    public void onReceivePenDotType(int i) {

    }

    @Override
    public void onReceivePenAllStatus(PenStatus penStatus) {

    }

    @Override
    public void onReceivePenLED(int color) {

    }

    @Override
    public void onWriteCmdResult(int code) {

    }

    @Override
    public void onException(BLEException e) {

    }

    @Override
    public void onReceivePenType(String type) {

    }

    @Override
    public void onReceivePenEnableLed(Boolean aBoolean) {

    }

    @Override
    public void onReceivePenHandwritingColor(int i) {

    }

    @Override
    public void onReceiveElementCode(ElementCode elementCode) {

    }

    public void scanLeDevice() {
        everPens.clear();
        if (agent != null) {
            Log.e("EverPenManger","FindAllDevices");
            agent.FindAllDevices(this);
        }
    }

    @Override
    public void onScanResult(BluetoothDevice bluetoothDevice, int i, byte[] bytes) {
        Log.e("EverPenManger","onScanResult,device:" + bluetoothDevice);
        addScanedEverPen(bluetoothDevice.getAddress());
    }

    @Override
    public void onScanFailed(BLEException e) {
        Log.e("EverPenManger","onScanFailed,exception:" + e.getMessage());
    }

    private synchronized void addScanedEverPen(String macAddress){
        EverPen pen = new EverPen(macAddress);
        if(!everPens.contains(pen)){
            everPens.add(pen);
        }
    }

    public void connect(){
        if(currentConnectedPen != null){
            if(agent != null){
                agent.stopFindAllDevices();
                agent.connect(currentConnectedPen.getMacAddress());
            }
        }
    }
}
