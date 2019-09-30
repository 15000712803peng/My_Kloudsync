package com.kloudsync.techexcel.dialog.message;

import io.rong.imkit.model.ProviderTag;
import io.rong.imkit.model.UIMessage;
import io.rong.imkit.widget.provider.IContainerItemProvider;
import android.content.Context;
import android.content.Intent;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ub.friends.activity.NewFriendsActivity;
import com.kloudsync.techexcel.R;

@ProviderTag(messageContent = FriendMessage.class , showPortrait = false , centerInHorizontal = true)
public class FriendMessageItemProvider extends IContainerItemProvider.MessageProvider<FriendMessage> {

    class ViewHolder {
        TextView tv_show;
    }

    @Override
    public View newView(Context context, ViewGroup group) {
        View view = LayoutInflater.from(context).inflate(R.layout.friend_add_reply, null);
        ViewHolder holder = new ViewHolder();
        holder.tv_show = (TextView) view.findViewById(R.id.tv_show);
        view.setTag(holder);
        return view;
    }

	@Override
	public void bindView(View v, int position, FriendMessage content,
			UIMessage arg3) {
		ViewHolder holder = (ViewHolder) v.getTag();
		holder.tv_show.setText(content.getMessageContent());
	
	}

	@Override
	public Spannable getContentSummary(FriendMessage content) {

        return new SpannableString(content.getMessageContent() + "");
	}

	@Override
	public void onItemClick(View arg0, int arg1, FriendMessage arg2,
			UIMessage arg3) {
		Intent intent = new Intent(arg0.getContext(), NewFriendsActivity.class);
		intent.putExtra("currentposition", 1);
		arg0.getContext().startActivity(intent);
		
	}

	@Override
	public void onItemLongClick(View arg0, int arg1, FriendMessage arg2,
			UIMessage arg3) {
		// TODO Auto-generated method stub
		
	}




}
