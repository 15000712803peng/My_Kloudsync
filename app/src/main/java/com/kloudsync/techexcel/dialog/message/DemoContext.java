package com.kloudsync.techexcel.dialog.message;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.kloudsync.techexcel.dialog.model.SealExtensionModule;
import com.kloudsync.techexcel.help.ApiTask;
import com.kloudsync.techexcel.help.ThreadManager;

import java.io.IOException;
import java.util.List;

import io.rong.imkit.DefaultExtensionModule;
import io.rong.imkit.IExtensionModule;
import io.rong.imkit.RongExtensionManager;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;
import io.rong.message.CommandNotificationMessage;
import io.rong.message.ContactNotificationMessage;
import io.rong.message.ImageMessage;
import io.rong.message.InformationNotificationMessage;
import io.rong.message.LocationMessage;
import io.rong.message.ProfileNotificationMessage;
import io.rong.message.TextMessage;
import io.rong.message.VoiceMessage;

public class DemoContext {

    private static final String TAG = "DemoContext";

    private static DemoContext self;

    private SharedPreferences sharedPreferences;

    public Context mContext;

    public RongIMClient mRongIMClient;

    public String userId;


    public static DemoContext getInstance() {

        if (self == null) {
            self = new DemoContext();
        }

        return self;
    }

    public DemoContext() {
    }

    public DemoContext(Context context) {
        self = this;
    }

    public void init(Context context) {

        mContext = context;

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
//        registerReceiveMessageListener();
        setInputprovider();


    }


    public void setRongIMClient(RongIMClient rongIMClient) {
        mRongIMClient = rongIMClient;
    }

    public void registerReceiveMessageListener() {
        mRongIMClient.setOnReceiveMessageListener(onReceiveMessageListener);
    }

    private void setInputprovider() {
        List<IExtensionModule> moduleList = RongExtensionManager.getInstance().getExtensionModules();
        IExtensionModule defaultModule = null;
        if (moduleList != null) {
            for (IExtensionModule module : moduleList) {
                if (module instanceof DefaultExtensionModule) {
                    defaultModule = module;
                    break;
                }
            }
            if (defaultModule != null) {
                RongExtensionManager.getInstance().unregisterExtensionModule(defaultModule);
                RongExtensionManager.getInstance().registerExtensionModule(new SealExtensionModule());
            }
        }
    }

    RongIMClient.OnReceiveMessageListener onReceiveMessageListener = new RongIMClient.OnReceiveMessageListener() {

        @Override
        public boolean onReceived(Message message, int left) {

            Log.e("------", "0605--------onReceived--------------------");


            if (message.getContent() instanceof TextMessage) {
                TextMessage textMessage = (TextMessage) message.getContent();

                Log.d("onReceived", "TextMessage---收收收收--接收到一条【文字消息】-----" + textMessage.getContent() + ",getExtra:" + textMessage.getExtra());

            } else if (message.getContent() instanceof ImageMessage) {

                final ImageMessage imageMessage = (ImageMessage) message.getContent();
                Log.d("onReceived", "ImageMessage--收收收收--接收到一条【图片消息】---ThumUri--" + imageMessage.getLocalUri());
                Log.d("onReceived", "ImageMessage--收收收收--接收到一条【图片消息】----Uri--" + imageMessage.getRemoteUri());

                new ApiTask(new Runnable() {
                    @Override
                    public void run() {

                        mRongIMClient.downloadMedia(Conversation.ConversationType.PRIVATE, userId, RongIMClient.MediaType.IMAGE, imageMessage.getRemoteUri().toString(), new RongIMClient.DownloadMediaCallback() {

                            @Override
                            public void onProgress(int i) {
                                Log.d("downloadMedia", "onProgress:" + i);
                            }

                            @Override
                            public void onSuccess(String s) {
                                Log.d("downloadMedia", "onSuccess:" + s);
                            }

                            @Override
                            public void onError(RongIMClient.ErrorCode errorCode) {
                                Log.d("downloadMedia", "onError:" + errorCode.getValue());
                            }
                        });
                    }
                }).start(ThreadManager.getManager());

            } else if (message.getContent() instanceof VoiceMessage) {

                final VoiceMessage voiceMessage = (VoiceMessage) message.getContent();

//                Log.d("onReceived", "VoiceMessage--收收收收--接收到一条【语音消息】-----" + voiceMessage.getUri());
                Log.e("onReceived", "VoiceMessage--收收收收--接收到一条【语音消息】 voiceMessage.getExtra-----" + voiceMessage.getExtra());

                new ApiTask(new Runnable() {

                    @Override
                    public void run() {

                        MediaPlayer mMediaPlayer = new MediaPlayer();
                        mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

                            @Override
                            public void onPrepared(MediaPlayer mp) {
                                mp.start();
                            }
                        });

                        try {
                            mMediaPlayer.setDataSource(mContext, voiceMessage.getUri());
                            mMediaPlayer.prepare();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start(ThreadManager.getManager());
            } else if (message.getContent() instanceof CustomizeMessage) {

                CustomizeMessage customizemessage = (CustomizeMessage) message.getContent();

                Log.d("onReceived", "CustomizeMessage--收收收收--接收到一条【群组邀请消息】-----" + customizemessage.getTargetName());

            } else if (message.getContent() instanceof ContactNotificationMessage) {
                ContactNotificationMessage mContactNotificationMessage = (ContactNotificationMessage) message.getContent();
                Log.d("onReceived", "mContactNotificationMessage--收收收收--接收到一条【联系人（好友）操作通知消息】-----" + mContactNotificationMessage.getMessage() + ",getExtra:" + mContactNotificationMessage.getExtra());

            } else if (message.getContent() instanceof ProfileNotificationMessage) {
                ProfileNotificationMessage mProfileNotificationMessage = (ProfileNotificationMessage) message.getContent();
                Log.d("onReceived", "GroupNotificationMessage--收收收收--接收到一条【资料变更通知消息】-----" + mProfileNotificationMessage.getData() + ",getExtra:" + mProfileNotificationMessage.getExtra());

            } else if (message.getContent() instanceof CommandNotificationMessage) {
                CommandNotificationMessage mCommandNotificationMessage = (CommandNotificationMessage) message.getContent();
                Log.d("onReceived", "GroupNotificationMessage--收收收收--接收到一条【命令通知消息】-----" + mCommandNotificationMessage.getData() + ",getName:" + mCommandNotificationMessage.getName());
            } else if (message.getContent() instanceof InformationNotificationMessage) {
                InformationNotificationMessage mInformationNotificationMessage = (InformationNotificationMessage) message.getContent();
                Log.d("onReceived", "InformationNotificationMessage--收收收收--接收到一条【小灰条消息】-----" + mInformationNotificationMessage.getMessage() + ",getName:" + mInformationNotificationMessage.getExtra());

            } else if (message.getContent() instanceof LocationMessage) {
                LocationMessage cc = (LocationMessage) message.getContent();
                Log.d("onReceived", "LocationMessage" + cc.getPoi());
            } else if (message.getContent() instanceof KnowledgeMessage) {
                KnowledgeMessage cc = (KnowledgeMessage) message.getContent();
                Log.d("onReceived", "KnowledgeMessage" + cc.getContent());
            } else if (message.getContent() instanceof SystemMessage) {
                SystemMessage cc = (SystemMessage) message.getContent();
                Log.d("onReceived", "SystemMessage" + cc.getExtra());
            } else if (message.getContent() instanceof FriendMessage) {
                FriendMessage cc = (FriendMessage) message.getContent();
                Log.d("onReceived", "FriendMessage" + cc.getType());
            } else if (message.getContent() instanceof GroupMessage) {
                GroupMessage cc = (GroupMessage) message.getContent();
                Log.d("onReceived", "GroupMessage" + cc.getMessageContent());
            } else if (message.getContent() instanceof ShareMessage) {
                ShareMessage cc = (ShareMessage) message.getContent();
                Log.d("onReceived", "ShareMessage" + cc.getShareDocTitle());
            } else if (message.getContent() instanceof CourseMessage) {
                CourseMessage cc = (CourseMessage) message.getContent();
                Log.e("onReceived", "CourseMessage" + cc.getMeetingId());
                Toast.makeText(mContext, "ssssss", Toast.LENGTH_LONG).show();


            }


            return false;
        }

    };


}