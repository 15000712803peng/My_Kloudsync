package com.kloudsync.techexcel.docment;

import android.content.Context;
import android.widget.Toast;

import com.tencent.mm.sdk.openapi.BaseReq;
import com.tencent.mm.sdk.openapi.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;

public class WeiXinApi  implements IWXAPIEventHandler {
    private static WeiXinApi self;
    public IWXAPI api;
    public Context mContext;

    public static WeiXinApi getInstance() {

        if (self == null) {
            self = new WeiXinApi();
        }

        return self;
    }

    public WeiXinApi() {
    }

    public void init(Context context) {

        mContext = context;
    }

    public void setApi(IWXAPI mapi){
        api = mapi;
    }

    public IWXAPI GetApi(){

        return api;
    }

    // 微信发送请求到第三方应用时，会回调到该方法
    @Override
    public void onReq(BaseReq arg0) {
        // TODO Auto-generated method stub
    }

    // 第三方应用发送到微信的请求处理后的响应结果，会回调到该方法
    @Override
    public void onResp(BaseResp resp) {
        switch (resp.errCode) {
            case BaseResp.ErrCode.ERR_OK:
                Toast.makeText(mContext, "分享成功", Toast.LENGTH_LONG)
                        .show();
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                Toast.makeText(mContext, "分享被取消", Toast.LENGTH_LONG)
                        .show();
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                Toast.makeText(mContext, "分享失败", Toast.LENGTH_LONG)
                        .show();
                break;
            default:
                Toast.makeText(mContext, "分享失败", Toast.LENGTH_LONG)
                        .show();
                break;
        }
    }
}
