package com.ub.service;

import android.content.Context;
import android.util.Log;

import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.tool.Md5Tool;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Timer;
import java.util.TimerTask;

public class KloudWebClientManager implements KloudWebClient.OnClientEventListener {

    private static KloudWebClientManager instance;
    private KloudWebClient kloudWebClient;
    private URI uri;
    private boolean heartBeatStarted = false;
    private Context context;

    public interface OnMessageArrivedListener {
        void onMessage(String message);
    }

    private OnMessageArrivedListener onMessageArrivedListener;


    public void setOnMessageArrivedListener(OnMessageArrivedListener onMessageArrivedListener) {
        this.onMessageArrivedListener = onMessageArrivedListener;
    }

    public KloudWebClient getKloudWebClient() {
        return kloudWebClient;
    }

    public static KloudWebClientManager getDefault(Context context, URI uri) {

        if (instance == null) {
            synchronized (KloudWebClientManager.class) {
                if (instance == null) {
                    instance = new KloudWebClientManager(context, uri);
                }
            }
        }
        return instance;
    }

    public static KloudWebClientManager getInstance(){
        return instance;
    }


    private KloudWebClientManager(Context context, URI uri) {
        this.context = context;
        this.uri = uri;
        kloudWebClient = new KloudWebClient(uri);
        kloudWebClient.setOnClientEventListener(this);
    }

    public synchronized void connect() {
        if (kloudWebClient != null) {
            try {
                kloudWebClient.connect();
            } catch (Exception e) {
                reconnect();
            }
        }
    }

    private synchronized void reconnect() {
        if (this.uri != null) {
            Log.e("KloundWebClientManager", "reconnect");
            AppConfig.UserToken = context.getSharedPreferences(AppConfig.LOGININFO,
                    Context.MODE_PRIVATE).getString("UserToken", null);
            try {
                kloudWebClient = new KloudWebClient(new URI(AppConfig.COURSE_SOCKET + File.separator + AppConfig.UserToken
                        + File.separator + "2" + File.separator + Md5Tool.getUUID()));
                kloudWebClient.setOnClientEventListener(this);
                kloudWebClient.connect();
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }

        }

    }


    @Override

    public void onMessage(String message) {
        if (this.onMessageArrivedListener != null) {
            this.onMessageArrivedListener.onMessage(message);
        }
    }

    @Override
    public synchronized void onReconnect() {
        reconnect();
    }

    class HeartBeatTask extends TimerTask {
        @Override
        public void run() {
            Log.e("KloundWebClientManager","send heart beat,thread:" + Thread.currentThread());
            JSONObject heartBeatMessage = new JSONObject();
            try {
                heartBeatMessage.put("action", "HELLO");
                heartBeatMessage.put("sessionId", AppConfig.UserToken);
                heartBeatMessage.put("changeNumber", 0);
                if (AppConfig.IsInMeeting) {
                    heartBeatMessage.put("status", AppConfig.status);
                    heartBeatMessage.put("currentLine", AppConfig.currentLine);
                    heartBeatMessage.put("currentMode", AppConfig.currentMode);
                    heartBeatMessage.put("currentPageNumber", AppConfig.currentPageNumber);
                    heartBeatMessage.put("currentItemId", AppConfig.currentDocId);
                }

                if (kloudWebClient != null) {
                    kloudWebClient.send(heartBeatMessage.toString());
                    Log.e("KloundWebClientManager","send heart beat message:" + heartBeatMessage.toString());
                }
                heartBeatStarted = true;
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (Exception e) {

            }

        }
    }


    private Timer heartBeatTimer;
    private HeartBeatTask heartBeatTask;

    public void startHeartBeat() {
        heartBeatTimer = new Timer();
        heartBeatTask = new HeartBeatTask();
        if (!heartBeatStarted && heartBeatTimer != null && heartBeatTask != null) {
            heartBeatTimer.schedule(heartBeatTask, 3000, 5000);
            heartBeatStarted = true;
        }
    }

    public void release() {
        if (heartBeatTimer != null && heartBeatTask != null) {
            heartBeatStarted = false;
            heartBeatTask.cancel();
            heartBeatTimer.cancel();
            heartBeatTimer = null;
            heartBeatTask = null;
            heartBeatStarted = false;
            instance = null;
        }
    }
}
