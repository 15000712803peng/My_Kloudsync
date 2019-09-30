package com.kloudsync.techexcel.dialog.message;

import android.content.Context;
import android.content.Intent;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ub.service.activity.ServiceDetailActivity;
import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.ui.MainActivity;

import io.rong.imkit.model.ProviderTag;
import io.rong.imkit.model.UIMessage;
import io.rong.imkit.widget.provider.IContainerItemProvider;
import io.rong.imlib.model.Conversation.ConversationType;

@ProviderTag(messageContent = CustomizeMessage.class)
public class CustomizeMessageItemProvider extends IContainerItemProvider.MessageProvider<CustomizeMessage> {

    class ViewHolder {
        TextView tv_targetName;
        TextView tv_price;
        LinearLayout lin_tags;
        RelativeLayout rl_citem;
    }

    @Override
    public View newView(Context context, ViewGroup group) {
        View view = LayoutInflater.from(context).inflate(R.layout.tickets, null);
        ViewHolder holder = new ViewHolder();
//        holder.message = (TextView) view.findViewById(android.R.id.tv);
        holder.tv_targetName = (TextView) view.findViewById(R.id.tv_targetName);
        holder.tv_price = (TextView) view.findViewById(R.id.tv_price);
        holder.lin_tags = (LinearLayout) view.findViewById(R.id.lin_tags);
        view.setTag(holder);
        return view;
    }

    @Override
    public void bindView(View v, int position, CustomizeMessage content,
                         UIMessage arg3) {
        ViewHolder holder = (ViewHolder) v.getTag();
        holder.tv_targetName.setText(content.getTargetName());
        holder.tv_price.setText(content.getPrice() + "元");
    }

    @Override
    public Spannable getContentSummary(CustomizeMessage content) {

        return new SpannableString(MainActivity.instance.getResources().getString(R.string.course_list));
    }

    @Override
    public void onItemClick(View arg0, int arg1, CustomizeMessage arg2,
                            UIMessage arg3) {
        Intent i = new Intent(arg0.getContext(), ServiceDetailActivity.class);
        int id = Integer.parseInt(arg2.getServiceID());
        if (0 == id) {
            Toast.makeText(arg0.getContext(), "不支持此课程", Toast.LENGTH_SHORT).show();
        } else {
            i.putExtra("id", id);
            i.putExtra("isDialogue", true);
            int ctype = ConversationType.GROUP == arg3.getConversationType() ? 2 : 1;
            i.putExtra("conversationtype", ctype);
            AppConfig.isNewService = true;
            arg0.getContext().startActivity(i);
        }
    }

    @Override
    public void onItemLongClick(View arg0, int arg1, CustomizeMessage arg2,
                                UIMessage arg3) {
        // TODO Auto-generated method stub

    }


}
