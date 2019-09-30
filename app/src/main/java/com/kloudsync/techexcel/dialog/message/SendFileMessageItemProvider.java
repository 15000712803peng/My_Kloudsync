package com.kloudsync.techexcel.dialog.message;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Parcelable;
import android.support.annotation.RequiresApi;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.dialog.SaveFavoritesActivity;
import com.kloudsync.techexcel.ui.MainActivity;

import io.rong.imkit.model.ProviderTag;
import io.rong.imkit.model.UIMessage;
import io.rong.imkit.widget.provider.IContainerItemProvider;
import io.rong.imlib.model.Message;

import static com.kloudsync.techexcel.R.id.lin_main;

@ProviderTag(messageContent = SendFileMessage.class)
public class SendFileMessageItemProvider extends IContainerItemProvider.MessageProvider<SendFileMessage> {

    class ViewHolder {
        TextView tv_name;
        LinearLayout lin_main;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void bindView(View view, int i, SendFileMessage content, UIMessage uiMessage) {
        SendFileMessageItemProvider.ViewHolder holder = (SendFileMessageItemProvider.ViewHolder) view.getTag();
        holder.tv_name.setText(content.getFileName());
        if (uiMessage.getMessageDirection() == Message.MessageDirection.SEND) {
            holder.lin_main.setBackgroundColor(view.getContext().getResources().getColor(R.color.green));
        }else{
            holder.lin_main.setBackgroundColor(view.getContext().getResources().getColor(R.color.white));
        }
    }

    @Override
    public Spannable getContentSummary(SendFileMessage sendFileMessage) {
        return new SpannableString(MainActivity.instance.getResources().getString(R.string.File));
    }

    @Override
    public void onItemClick(View view, int i, SendFileMessage sendFileMessage, UIMessage uiMessage) {
        Intent intent = new Intent(view.getContext(), SaveFavoritesActivity.class);
        intent.putExtra("sendFileMessage", (Parcelable) sendFileMessage);
//        intent.putExtra("isSend", (uiMessage.getMessageDirection() == Message.MessageDirection.SEND));
        view.getContext().startActivity(intent);

    }


    @Override
    public View newView(Context context, ViewGroup viewGroup) {
        View view = LayoutInflater.from(context).inflate(R.layout.sendfilemessage, null);
        SendFileMessageItemProvider.ViewHolder holder = new SendFileMessageItemProvider.ViewHolder();
        holder.tv_name = (TextView) view.findViewById(R.id.tv_name);
        holder.lin_main = (LinearLayout) view.findViewById(lin_main);
        view.setTag(holder);
        return view;
    }
}
