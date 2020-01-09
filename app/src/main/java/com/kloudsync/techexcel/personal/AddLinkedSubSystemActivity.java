
package com.kloudsync.techexcel.personal;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.bean.params.EventProjectFragment;

import org.greenrobot.eventbus.EventBus;


public class AddLinkedSubSystemActivity extends AppCompatActivity {

    private ImageView tv_back;
    private RelativeLayout save;
    private EditText subsystemaddress, subsystemname;
    private String authenticateUrl = "https://techexcel.devsuite.net/DevTrackApi/api/integration/auth?appKey=bc008943dfca4aa1a6d1a2ef0b8917e8";
    private String token = "bc008943dfca4aa1a6d1a2ef0b8917e8";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_subsystem_linked_name);
        initView();
    }

    private void initView() {
        tv_back = findViewById(R.id.tv_back);
        save = findViewById(R.id.save);
        subsystemaddress = findViewById(R.id.subsystemaddress);
        subsystemname = findViewById(R.id.subsystemname);
        tv_back.setOnClickListener(new MyOnClick());
        save.setOnClickListener(new MyOnClick());
        subsystemaddress.setText(authenticateUrl);
    }


    private void updateSyncroomName() {

        EventBus.getDefault().post(new EventProjectFragment());
        finish();


//        JSONObject jsonObject = new JSONObject();
//        try {
//            jsonObject.put("authenticateUrl", subsystemaddress.getText().toString());
//            jsonObject.put("companyId", AppConfig.SchoolID);
//            jsonObject.put("subSystemName", subsystemname.getText().toString());
//            jsonObject.put("token", token);
//            jsonObject.put("type", 1);
//
//            String url = AppConfig.URL_MEETING_BASE + "subsystem/create_subsystem";
//            ServiceInterfaceTools.getinstance().createSubsystem(url, ServiceInterfaceTools.CREATESUBSYSTEM, jsonObject, new ServiceInterfaceListener() {
//                @Override
//                public void getServiceReturnData(Object object) {
//
//                    EventProjectFragment eventProjectFragment=new EventProjectFragment();
//                    eventProjectFragment.setSubSystemId((int)object);
//                    EventBus.getDefault().post(new EventProjectFragment());
//                    finish();
//                }
//            });
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }



    }


    protected class MyOnClick implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.tv_back:
                    finish();
                    break;
                case R.id.save:
                    updateSyncroomName();
                    break;
                default:
                    break;
            }
        }
    }


}
