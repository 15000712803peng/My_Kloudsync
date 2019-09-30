package com.kloudsync.techexcel.dialog.message;

import android.content.Context;
import android.content.Intent;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ub.service.activity.WatchCourseActivity2;
import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.ui.MainActivity;

import io.rong.imkit.model.ProviderTag;
import io.rong.imkit.model.UIMessage;
import io.rong.imkit.widget.provider.IContainerItemProvider;

@ProviderTag(messageContent = CourseMessage.class , showPortrait = false , centerInHorizontal = true)
public class CourseMessageItemProvider extends IContainerItemProvider.MessageProvider<CourseMessage> {

    class ViewHolder {
        TextView tv_show;
    }

    @Override
    public View newView(Context context, ViewGroup group) {
        View view = LayoutInflater.from(context).inflate(R.layout.course_invite_provider, null);
        ViewHolder holder = new ViewHolder();
        holder.tv_show = (TextView) view.findViewById(R.id.tv_show);
        view.setTag(holder);
        return view;
    }

	@Override
	public void bindView(View v, int position, CourseMessage content,
			UIMessage arg3) {
		ViewHolder holder = (ViewHolder) v.getTag();
		Log.e("hahaha", "CourseMessage" + content.getMeetingId()+"  "+content.getAttachmentUrl()
				+"  "+content.getRongCloudUserID()
				+"  "+content.getFromName());
		holder.tv_show.setText(content.getFromName());
	
	}

	@Override
	public Spannable getContentSummary(CourseMessage content) {

        return new SpannableString(content.getFromName() + MainActivity.instance.getResources().getString(R.string.Course_invite));
	}

	@Override
	public void onItemClick(View arg0, int arg1, CourseMessage cc,
			UIMessage arg3) {
		Intent intent=new Intent(arg0.getContext(), WatchCourseActivity2.class);
		intent.putExtra("url", cc.getAttachmentUrl());
		intent.putExtra("meetingId", cc.getMeetingId());
		intent.putExtra("CustomerRongCloudID",cc.getRongCloudUserID());
		intent.putExtra("attachmentid",cc.getItemId());
		intent.putExtra("identity",2);
		arg0.getContext().startActivity(intent);
		
	}

	@Override
	public void onItemLongClick(View arg0, int arg1, CourseMessage arg2,
			UIMessage arg3) {
		// TODO Auto-generated method stub
		
	}




}
