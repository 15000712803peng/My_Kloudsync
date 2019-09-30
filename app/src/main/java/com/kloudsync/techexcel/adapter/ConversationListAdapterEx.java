package com.kloudsync.techexcel.adapter;


import io.rong.imkit.model.UIConversation;
import io.rong.imkit.widget.adapter.ConversationListAdapter;
import io.rong.imlib.model.Conversation;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kloudsync.techexcel.R;


public class ConversationListAdapterEx extends ConversationListAdapter {

	public ConversationListAdapterEx(Context context) {
		super(context);
		Log.e("ConversationListAdapterEx", "ConversationListAdapterEx");
	}

	
	@Override
    protected View newView(Context context, int position, ViewGroup group) {
		Log.e("haha", "newView");
		View view = LayoutInflater.from(context).inflate(R.layout.knowledge_item, null);
//        return super.newView(context, position, group);
		return view;
    }

    @Override
    protected void bindView(View v, int position, UIConversation data) {
    	/*TextView tv_service = (TextView) v.findViewById(R.id.rc_conversation_msg_service);
    	tv_service.setText("服务不进行中");*/
    	Log.e("haha",
    			data.describeContents()
    			+ ":"
    			+ data.getConversationSenderId()
    			+ ":"
    			+ data.getConversationTargetId()
    			+ ":"
    			+ data.getDraft()
    			+ ":"
    			+ data.getLatestMessageId()
    			+ ":"
    			+ data.getUIConversationTime()
    			+ ":"
    			+ data.getUIConversationTitle()
    			+ ":"
    			+ data.getUnReadMessageCount()
    			+ ":"
    			+ data.hashCode()
    			+ ":"
    			+ data.toString()
    			+ ":"
    			+ data.getConversationContent()
    			+ ":"
    			+ data.getConversationGatherState()
    			+ ":"
    			+ data.getConversationType()
    			+ ":"
    			+ data.getExtraFlag()
    			+ ":"
    			+ data.getIconUrl()
    			+ ":"
    			+ data.getMessageContent()
    			+ ":"
    			+ data.getSentStatus()
    			+ ":"
//    			+ data.getShowDraftFlag()
    			+ ":"
    			+ data.getUnReadType()
    			+ ":"
    			+ data.hasNickname(data.getConversationTargetId() + ":"
    					+ data.isTop()));
    if(data.getConversationType().equals(Conversation.ConversationType.DISCUSSION))
        data.setUnreadType(UIConversation.UnreadRemindType.REMIND_ONLY);    
		super.bindView(v, position, data);
    }


    
    
    
    
    
    
    
    
    
    
    
    
    
    
}
