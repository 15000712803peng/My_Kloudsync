package com.kloudsync.techexcel.dialog.plugin;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.widget.Toast;

import com.ub.techexcel.tools.SpliteSocket;
import com.ub.techexcel.tools.Tools;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.info.Customer;

import org.java_websocket.client.WebSocketClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import io.rong.callkit.RongCallAction;
import io.rong.callkit.RongVoIPIntent;
import io.rong.calllib.RongCallClient;
import io.rong.calllib.RongCallSession;
import io.rong.imkit.RongExtension;
import io.rong.imkit.plugin.IPluginModule;
import io.rong.imkit.utilities.PermissionCheckUtil;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;

import static com.kloudsync.techexcel.dialog.ConversationActivity.ulist;

/**
 * Created by weiqinxiao on 16/8/16.
 */
public class AudioPlugin2 implements IPluginModule {
    private static final String TAG = "AudioPlugin";
    private ArrayList<String> allMembers;
    private Context context;

    private Conversation.ConversationType conversationType;
    private String targetId;

    @Override
    public Drawable obtainDrawable(Context context) {
        return context.getResources().getDrawable(io.rong.callkit.R.drawable.rc_ic_phone_selector);
    }

    @Override
    public String obtainTitle(Context context) {
        return context.getString(io.rong.callkit.R.string.rc_voip_audio);
    }

    @Override
    public void onClick(final Fragment currentFragment, final RongExtension extension) {
        String[] permissions = {Manifest.permission.RECORD_AUDIO};
        if (!PermissionCheckUtil.requestPermissions(currentFragment, permissions)) {
            return;
        }

        context = currentFragment.getActivity().getApplicationContext();
        conversationType = extension.getConversationType();
        targetId = extension.getTargetId();
        SendSocketToUB(targetId);

        RongCallSession profile = RongCallClient.getInstance().getCallSession();
        if (profile != null && profile.getActiveTime() > 0) {
            Toast.makeText(context, currentFragment.getString(io.rong.callkit.R.string.rc_voip_call_start_fail), Toast.LENGTH_SHORT).show();
            return;
        }
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (networkInfo == null || !networkInfo.isConnected() || !networkInfo.isAvailable()) {
            Toast.makeText(context, currentFragment.getString(io.rong.callkit.R.string.rc_voip_call_network_error), Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(RongVoIPIntent.RONG_INTENT_ACTION_VOIP_SINGLEAUDIO2);
//        intent.putExtra("conversationType", conversationType.getName().toLowerCase());
//        intent.putExtra("targetId", targetId);
        intent.putExtra("Customer", cus);
        intent.putExtra("callAction", RongCallAction.ACTION_OUTGOING_CALL.getName());
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setPackage(context.getPackageName());
        context.getApplicationContext().startActivity(intent);
    }


    private WebSocketClient mWebSocketClient; //  连接客户端
    Customer cus = new Customer();
    private void SendSocketToUB(String targetId) {
        mWebSocketClient = AppConfig.webSocketClient;
        String ss;
        for (int i = 0; i < ulist.size(); i++) {
            cus = ulist.get(i);
            if (cus.getUBAOUserID().equals(targetId)) {
                break;
            }
        }
        JSONObject jsonObject = format(cus);
        String data_str = Tools.getBase64(jsonObject.toString()).replaceAll("[\\s*\t\n\r]", "");
        try {
            JSONObject loginjson = new JSONObject();
            loginjson.put("action", "SEND_MESSAGE");
            loginjson.put("sessionId", AppConfig.UserToken);
            loginjson.put("type", 1);
            loginjson.put("userList", cus.getUserID() );
            loginjson.put("data", data_str);
            ss = loginjson.toString();
            Log.e("ffffffffffffff", ss.toString());
            SpliteSocket.sendMesageBySocket(ss);

        } catch (JSONException e) {
            e.printStackTrace();
        }





    }

    private JSONObject format(Customer cus) {

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("sourceUserId", AppConfig.UserID.replace("-",""));
            jsonObject.put("sourceUserName", AppConfig.UserName);
            jsonObject.put("sourceAvatarUrl", AppConfig.MYAVATARURL);
            jsonObject.put("targetUserName", cus.getName());
            jsonObject.put("targetUserId", cus.getUserID());
            jsonObject.put("targetAvatarUrl", cus.getUrl());
            jsonObject.put("mediaType", 1);
            jsonObject.put("actionType", 4);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        Intent intent = new Intent(RongVoIPIntent.RONG_INTENT_ACTION_VOIP_MULTIAUDIO);
        ArrayList<String> userIds = data.getStringArrayListExtra("invited");
        userIds.add(RongIMClient.getInstance().getCurrentUserId());
        intent.putExtra("conversationType", conversationType.getName().toLowerCase());
        intent.putExtra("targetId", targetId);
        intent.putExtra("callAction", RongCallAction.ACTION_OUTGOING_CALL.getName());
        intent.putStringArrayListExtra("invitedUsers", userIds);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setPackage(context.getPackageName());
        context.getApplicationContext().startActivity(intent);
    }
}
