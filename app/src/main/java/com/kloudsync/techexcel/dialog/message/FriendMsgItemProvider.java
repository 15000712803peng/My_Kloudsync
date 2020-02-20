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
import com.kloudsync.techexcel.ui.MainActivity;
import io.rong.imkit.model.ProviderTag;
import io.rong.imkit.model.UIMessage;
import io.rong.imkit.widget.provider.IContainerItemProvider;

@ProviderTag(messageContent = HelloFriendMessage.class)
public class FriendMsgItemProvider extends IContainerItemProvider.MessageProvider<HelloFriendMessage> {

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
        return new SpannableString(MainActivity.instance.getResources().getString(R.string.Share_invite));
    }

    @Override
    public void onItemClick(final View arg0, int arg1, HelloFriendMessage cc,
                            UIMessage arg3) {
        mContext = arg0.getContext();
    }

}
