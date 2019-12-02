package com.kloudsync.techexcel.help;

import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.util.LruCache;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageCache;
import com.android.volley.toolbox.Volley;
import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.bean.MeetingDocument;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.contact.MyFriendsActivity;
import com.kloudsync.techexcel.docment.WeiXinApi;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorListenerAdapter;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.SendMessageToWX;
import com.tencent.mm.sdk.openapi.WXMediaMessage;
import com.tencent.mm.sdk.openapi.WXWebpageObject;
import com.tencent.mm.sdk.platformtools.Util;
import com.ub.kloudsync.activity.Document;

import java.util.ArrayList;
import java.util.List;

public class ShareDocumentDialog implements OnClickListener{
    private Context mContext;
    private MeetingDocument document;


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.lin_copy:
                CopyLink();
                mPopupWindow.dismiss();
                break;
            case R.id.lin_wechat:

                getUrl(document,true);
                mPopupWindow.dismiss();
                break;
            case R.id.lin_moment:
                getUrl(document, false);
                mPopupWindow.dismiss();
                break;

            case R.id.cancel:
                if (mPopupWindow != null) {
                    mPopupWindow.dismiss();
                }
                break;

            default:
                break;
        }
    }

    public void getPopwindow(Context context, MeetingDocument document) {
        this.mContext = context;
        this.document = document;
        getPopupWindowInstance();
        mPopupWindow.getWindow().setWindowAnimations(R.style.PopupAnimation5);
    }


    public Dialog mPopupWindow;

    public void getPopupWindowInstance() {
        if (null != mPopupWindow) {
            mPopupWindow.dismiss();
            return;
        } else {
            initPopuptWindow();
        }
    }

    private LinearLayout lin_copy;
    private LinearLayout lin_wechat;
    private LinearLayout lin_moment;
    private LinearLayout lin_Scan;
    private List<LinearLayout> lin_all = new ArrayList<>();
    private TextView closeImage;


    @SuppressWarnings("deprecation")
    public void initPopuptWindow() {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        View popupWindow = layoutInflater.inflate(R.layout.pop_share_kloudsync, null);
        lin_copy = (LinearLayout) popupWindow.findViewById(R.id.lin_copy);
        lin_wechat = (LinearLayout) popupWindow.findViewById(R.id.lin_wechat);
        lin_moment = (LinearLayout) popupWindow.findViewById(R.id.lin_moment);
        lin_Scan = (LinearLayout) popupWindow.findViewById(R.id.lin_Scan);
        closeImage = (TextView) popupWindow.findViewById(R.id.cancel);
        lin_all.add(lin_copy);
        lin_all.add(lin_wechat);
        lin_all.add(lin_moment);
        mPopupWindow = new Dialog(mContext, R.style.bottom_dialog);
        mPopupWindow.setContentView(popupWindow);
        mPopupWindow.getWindow().setGravity(Gravity.BOTTOM);
        mPopupWindow.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                dialog = null;
            }
        });
        WindowManager.LayoutParams lp = mPopupWindow.getWindow().getAttributes();
        lp.width = mContext.getResources().getDisplayMetrics().widthPixels;
        mPopupWindow.getWindow().setAttributes(lp);
        lin_copy.setOnClickListener(this);
        lin_wechat.setOnClickListener(this);
        lin_moment.setOnClickListener(this);
        lin_Scan.setOnClickListener(this);
        closeImage.setOnClickListener(this);
    }



    private ClipboardManager mClipboard = null;

    private void CopyLink() {
        String url = AppConfig.SHARE_DOCUMENT + document.getItemID();
        if (null == mClipboard) {
            mClipboard = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
        }
        mClipboard.setPrimaryClip(ClipData.newPlainText(null, url));
        Toast.makeText(mContext, "Copy link success!", Toast.LENGTH_LONG).show();
    }

    private void getUrl(final MeetingDocument document,final boolean st) {
        RequestQueue requestQueue = Volley.newRequestQueue(mContext);
        final LruCache<String, Bitmap> lruCache = new LruCache<String, Bitmap>(
                20);
        ImageCache imageCache = new ImageCache() {
            @Override
            public void putBitmap(String key, Bitmap value) {
                lruCache.put(key, value);
            }

            @Override
            public Bitmap getBitmap(String key) {
                return lruCache.get(key);
            }
        };

        ImageLoader imageLoader = new ImageLoader(requestQueue, imageCache);
        String url = document.getSourceFileUrl();
        Log.e("url", url + "      ");
        if (url.contains("<") && url.contains(">")) {
//            url = url.substring(0, url.lastIndexOf("<")) + "1_thumbnail" + url.substring(url.lastIndexOf("."), url.length());
        } else {
            url = url.substring(0, url.lastIndexOf(".")) + "_1_thumbnail" + url.substring(url.lastIndexOf("."), url.length());
        }
        Log.e("url", url + "      ");
//        final long s1 = System.currentTimeMillis();
        imageLoader.get(url, new ImageLoader.ImageListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO Auto-generated method stub
                Log.e("error", error + "");
                weixinshare(document, null, st);

            }

            @Override
            public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                // TODO Auto-generated method stub
                if (response.getBitmap() != null) {
//                    long s2 = System.currentTimeMillis();
//                    Log.e("duang", response.getBitmap().getByteCount() + " : " + s1 + " : " + s2 + "  :   " + (s2 - s1));
                    weixinshare(document, response.getBitmap(), st);
                }
            }

        });
    }


    private void weixinshare(MeetingDocument document, Bitmap bitmap,boolean st) {
        String url = AppConfig.SHARE_DOCUMENT + document.getItemID();
        if (isWXAppInstalledAndSupported(WeiXinApi.getInstance().GetApi())) {
            WXWebpageObject webpage = new WXWebpageObject();
            webpage.webpageUrl = url;
            WXMediaMessage msg = new WXMediaMessage(webpage);
            msg.title = document.getTitle();
            msg.description = "请点击此框跳转至" + url;
            Bitmap thumb = bitmap;
            Log.e("hahaha", url + "  " + (null == thumb));
            if (null == thumb) {
                thumb = BitmapFactory.decodeResource(mContext.getResources(),
                        R.drawable.logo);
            }
            Log.e("hahaha", url + "  " + (null == thumb));

            msg.thumbData = Util.bmpToByteArray(thumb, true);
            SendMessageToWX.Req req = new SendMessageToWX.Req();
            req.transaction = buildTransaction("webpage");

            req.message = msg;
            req.scene = st ? SendMessageToWX.Req.WXSceneSession : SendMessageToWX.Req.WXSceneTimeline;
            WeiXinApi.getInstance().GetApi().sendReq(req);
        } else {
            Toast.makeText(mContext, "微信客户端未安装，请确认",
                    Toast.LENGTH_LONG).show();
        }
    }

    private boolean isWXAppInstalledAndSupported(IWXAPI api) {
        return api.isWXAppInstalled() && api.isWXAppSupportAPI();
    }

    private String buildTransaction(final String type) {
        return (type == null) ? String.valueOf(System.currentTimeMillis())
                : type + System.currentTimeMillis();
    }

    public boolean isShow() {
        if(mPopupWindow != null){
            return mPopupWindow.isShowing();
        }
        return false;
    }

    public void dismiss(){
        if(mPopupWindow != null && mPopupWindow.isShowing()){
            mPopupWindow.dismiss();
        }
    }

    public void show() {
        if(mPopupWindow != null && !mPopupWindow.isShowing())
        mPopupWindow.show();
    }


}
