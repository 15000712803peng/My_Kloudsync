package com.kloudsync.techexcel.dialog.message;

import android.content.Context;
import android.content.Intent;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.dialog.ShowKnowledgeDetail;
import com.kloudsync.techexcel.httpgetimage.ImageLoader;

import io.rong.imkit.model.ProviderTag;
import io.rong.imkit.model.UIMessage;
import io.rong.imkit.widget.provider.IContainerItemProvider;

@ProviderTag(messageContent = KnowledgeMessage.class)
public class KnowledgeMessageItemProvider extends IContainerItemProvider.MessageProvider<KnowledgeMessage> {

    public ImageLoader imageLoader;
    
    class ViewHolder {
        TextView tv_IssueTitle;
        TextView tv_Description;
        ImageView img_show;
    }

    @Override
    public View newView(Context context, ViewGroup group) {
        View view = LayoutInflater.from(context).inflate(R.layout.knowledge_item, null);
        ViewHolder holder = new ViewHolder();
//        holder.message = (TextView) view.findViewById(android.R.id.tv);
        holder.tv_IssueTitle = (TextView) view.findViewById(R.id.tv_IssueTitle);
        holder.tv_Description = (TextView) view.findViewById(R.id.tv_Description);
        holder.img_show = (ImageView) view.findViewById(R.id.img_show);
        view.setTag(holder);
        imageLoader=new ImageLoader(context.getApplicationContext()); 
        return view;
    }

	@Override
	public void bindView(View v, int position, KnowledgeMessage content,
			UIMessage arg3) {
		ViewHolder holder = (ViewHolder) v.getTag();
		holder.tv_IssueTitle.setText(content.getTitle() + "");
		holder.tv_Description.setText(content.getContent() + "");
		
		int imageID = Integer.parseInt(content.getImageID());
		if(imageID > 0){
			holder.img_show.setVisibility(View.VISIBLE);
			imageLoader.DisplayImage(AppConfig.URL_IMAGE + content.getImageID() , holder.img_show);
		}else{
			holder.img_show.setVisibility(View.GONE);
		}
	
	}

	@Override
	public Spannable getContentSummary(KnowledgeMessage arg0) {

        return new SpannableString(arg0.getTitle());
	}

	@Override
	public void onItemClick(View arg0, int arg1, KnowledgeMessage arg2,
			UIMessage arg3) {
		/*if (arg3.getMessageDirection() == Message.MessageDirection.SEND) {//消息方向，自己发送的
		} */
		// TODO Auto-generated method stub
		Intent intent = new Intent(arg0.getContext(), ShowKnowledgeDetail.class);
		intent.putExtra("content", arg2.getContent());
		intent.putExtra("title", arg2.getTitle());
		intent.putExtra("knowledgeID", arg2.getKnowledgeID());
		intent.putExtra("imageID", arg2.getImageID());
		intent.putExtra("videoInfo", arg2.getVideoInfo());
		arg0.getContext().startActivity(intent);
		
	}

	@Override
	public void onItemLongClick(View arg0, int arg1, KnowledgeMessage arg2,
			UIMessage arg3) {
		// TODO Auto-generated method stub
		
	}




}
