package com.ub.service.activity;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.ub.techexcel.bean.TelePhoneCall;

import org.greenrobot.eventbus.EventBus;

public class PhoneReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_NEW_OUTGOING_CALL)) {
            String phoneNumber = intent
                    .getStringExtra(Intent.EXTRA_PHONE_NUMBER);
            Log.e("去电了", phoneNumber);
        } else {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Service.TELEPHONY_SERVICE);
            tm.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
        }
    }


    PhoneStateListener listener = new PhoneStateListener() {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            super.onCallStateChanged(state, incomingNumber);
            switch (state) {
                case TelephonyManager.CALL_STATE_IDLE:
                    Log.e("来电了", "挂断" + incomingNumber);
                    EventBus.getDefault().post(new TelePhoneCall(false));
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    Log.e("来电了", "接听" + incomingNumber);
                    // EventBus.getDefault().post(new TelePhoneCall(false) );
                    break;
                case TelephonyManager.CALL_STATE_RINGING:
                    Log.e("来电了", "响铃:来电号码" + incomingNumber);
                    EventBus.getDefault().post(new TelePhoneCall(true));
                    break;
            }
        }
    };
}
