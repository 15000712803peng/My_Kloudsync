package com.kloudsync.techexcel.dialog;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.bean.EventFilterContact;
import com.kloudsync.techexcel.bean.MeetingConfig;
import com.kloudsync.techexcel.bean.MeetingMember;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by tonyan on 2019/12/20.
 */

public class PopFilterContact extends PopupWindow implements View.OnClickListener {

    private Context context;
    private TextView companyText,allText;
    private RelativeLayout companyLayout,allLayout;
    private ImageView companyImage,allImage;


    public interface OnMemberSettingChanged{
        void setPresenter(MeetingMember meetingMember);
        void setAuditor(MeetingMember meetingMember);
    }

    private OnMemberSettingChanged onMemberSettingChanged;

    public void setOnMemberSettingChanged(OnMemberSettingChanged onMemberSettingChanged) {
        this.onMemberSettingChanged = onMemberSettingChanged;
    }

    public PopFilterContact(Context context) {
        super(context);
        this.context = context;
        initalize();
    }

    private void initalize() {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.pop_contact_filter, null);
        companyText = view.findViewById(R.id.txt_company);
        allText = view.findViewById(R.id.txt_all);
        companyLayout = view.findViewById(R.id.layout_company);
        allLayout = view.findViewById(R.id.layout_all);
        companyImage = view.findViewById(R.id.image_company);
        allImage = view.findViewById(R.id.image_all);
        allLayout.setOnClickListener(this);
        companyLayout.setOnClickListener(this);
        setContentView(view);
        initWindow();
    }

    private void initWindow() {
        this.setWidth(context.getResources().getDimensionPixelOffset(R.dimen.filter_contact_width));
        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        this.setFocusable(true);
        this.setOutsideTouchable(true);
        this.setBackgroundDrawable(new BitmapDrawable());
        this.update();

    }

    public void showAtBottom(View view,int type) {
        showAsDropDown(view, -context.getResources().getDimensionPixelOffset(R.dimen.filter_contact_width) + context.getResources().getDimensionPixelOffset(R.dimen.pop_setting_left_margin), 10);
        if(type == 0){
            companyImage.setVisibility(View.VISIBLE);
            allImage.setVisibility(View.GONE);
        }else if(type == 1){
            companyImage.setVisibility(View.GONE);
            allImage.setVisibility(View.VISIBLE);
        }
    }



    @Override
    public void onClick(View view) {
        Log.e("PopFilterContact","on_click");
        switch (view.getId()) {
            case R.id.layout_company:{
                Log.e("PopFilterContact","layout_company_on_click");
                EventFilterContact filterContact = new EventFilterContact();
                filterContact.setTpye(0);
                EventBus.getDefault().post(filterContact);
                dismiss();
            }

                break;
            case R.id.layout_all:{
                Log.e("PopFilterContact","layout_all_on_click");
                EventFilterContact filterContact = new EventFilterContact();
                filterContact.setTpye(1);
                EventBus.getDefault().post(filterContact);
                dismiss();
            }

                break;
            default:
                break;
        }
    }


}
