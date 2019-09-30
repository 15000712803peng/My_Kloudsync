package com.kloudsync.techexcel.dialog.message;

import io.rong.imkit.model.ProviderTag;
import io.rong.imkit.model.UIMessage;
import io.rong.imkit.widget.provider.IContainerItemProvider;
import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kloudsync.techexcel.R;

@ProviderTag(messageContent = GroupMessage.class , showPortrait = false , centerInHorizontal = true)
public class GroupMessageItemProvider extends IContainerItemProvider.MessageProvider<GroupMessage> {

    class ViewHolder {
        TextView tv_show;
    }

    @Override
    public View newView(Context context, ViewGroup group) {
        View view = LayoutInflater.from(context).inflate(R.layout.group_message, null);
        ViewHolder holder = new ViewHolder();
        holder.tv_show = (TextView) view.findViewById(R.id.tv_show);
        view.setTag(holder);
        return view;
    }

	@Override
	public void bindView(View v, int position, GroupMessage content, UIMessage arg3) {
		ViewHolder holder = (ViewHolder) v.getTag();
		holder.tv_show.setText(content.getMessageContent());
	}

	@Override
	public Spannable getContentSummary(GroupMessage content) {
		// TODO Auto-generated method stub
		return new SpannableString(content.getMessageContent() + "");
	}

	@Override
	public void onItemClick(View arg0, int arg1, GroupMessage arg2,
			UIMessage arg3) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onItemLongClick(View arg0, int arg1, GroupMessage arg2,
			UIMessage arg3) {
		// TODO Auto-generated method stub
		
	}




}
