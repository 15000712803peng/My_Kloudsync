package com.kloudsync.techexcel.dialog.message;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.ui.MainActivity;
import com.ub.techexcel.tools.Tools;

import io.rong.imkit.model.ProviderTag;
import io.rong.imkit.model.UIMessage;
import io.rong.imkit.widget.provider.IContainerItemProvider;
import io.rong.message.TextMessage;

@ProviderTag(messageContent = HelloFriendMessage.class)
public class HelloFriendMessageItemProvider extends IContainerItemProvider.MessageProvider<HelloFriendMessage> {

    public static Context mContext;

    class ViewHolder {

    }

    @Override
    public View newView(Context context, ViewGroup group) {
        View view = LayoutInflater.from(context).inflate(R.layout.message_friend, null);
        mContext = context.getApplicationContext();
        ViewHolder holder = new ViewHolder();
        view.setTag(holder);

        return view;
    }

    @Override
    public void bindView(View v, int position, HelloFriendMessage content,
                         UIMessage arg3) {
        ViewHolder holder = (ViewHolder) v.getTag();
    }

    @Override
    public Spannable getContentSummary(HelloFriendMessage content) {
        return new SpannableString(MainActivity.instance.getResources().getString(R.string.hello_friend_summary));
    }

    @Override
    public void onItemClick(final View arg0, int arg1, HelloFriendMessage cc,
                            UIMessage arg3) {
        mContext = arg0.getContext();
        sendHelloMessage(cc);
    }

    private void sendHelloMessage(HelloFriendMessage friendMessage){
        TextMessage myTextMessage = TextMessage.obtain("hello");
        myTextMessage.setExtra(AppConfig.UserID);
        Tools.sendMessageToMember(mContext, myTextMessage, friendMessage.getRongCloudId(),AppConfig.UserID);
    }

}
