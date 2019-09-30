package com.kloudsync.techexcel.tool;

import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.info.Customer;
import com.kloudsync.techexcel.service.UploadService;
import com.kloudsync.techexcel.ui.ReceiveWeChatDataActivity;

import java.util.Collections;
import java.util.List;

public class Jianbuderen {

    /**
     * 消灭隐形activity
     */
    public static void Heihei(){
        AppConfig.OUTSIDE_PATH = "";
        if (ReceiveWeChatDataActivity.instance != null && !ReceiveWeChatDataActivity.instance.isFinishing()) {
            ReceiveWeChatDataActivity.instance.finish();
        }
        if(UploadService.instance != null) {
            UploadService.instance.stopSelf();
        }
    }

    /**
     * Customer排序
     * @param mlist
     */
    public static void SortCustomers(List<Customer> mlist) {
        Collections.sort(mlist, new PinyinComparator());

    }

}
