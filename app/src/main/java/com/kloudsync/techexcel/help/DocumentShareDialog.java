package com.kloudsync.techexcel.help;

import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
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
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageCache;
import com.android.volley.toolbox.Volley;
import com.kloudsync.techexcel.R;
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

public class DocumentShareDialog {

    public Context mContext;

    Document lesson;
    int Syncid;

    boolean record;

    private String linshiUrl = "https://www.baidu.com/";

    private static PopShareKloudSyncDismissListener popShareKloudSyncDismissListener;

    public interface PopShareKloudSyncDismissListener {
        void CopyLink();

        void Wechat();

        void Moment();

        void Scan();

        void PopBack();
    }

    public void setPoPDismissListener(PopShareKloudSyncDismissListener popShareKloudSyncDismissListener) {
        this.popShareKloudSyncDismissListener = popShareKloudSyncDismissListener;
    }

    public void getPopwindow(Context context, Document lesson, int Syncid) {
        this.mContext = context;
        this.lesson = lesson;
        this.Syncid = Syncid;
        getPopupWindowInstance();
        mPopupWindow.getWindow().setWindowAnimations(R.style.PopupAnimation5);
    }

    public void IsReCord() {
        record = true;
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
    private ImageView closeImage;


    @SuppressWarnings("deprecation")
    public void initPopuptWindow() {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        View popupWindow = layoutInflater.inflate(R.layout.pop_share_kloudsync, null);
        lin_copy = (LinearLayout) popupWindow.findViewById(R.id.lin_copy);
        lin_wechat = (LinearLayout) popupWindow.findViewById(R.id.lin_wechat);
        lin_moment = (LinearLayout) popupWindow.findViewById(R.id.lin_moment);
        lin_Scan = (LinearLayout) popupWindow.findViewById(R.id.lin_Scan);
        closeImage = popupWindow.findViewById(R.id.image_close);
        lin_all.add(lin_copy);
        lin_all.add(lin_wechat);
        lin_all.add(lin_moment);
        if (lesson.isMe()) {
            lin_Scan.setVisibility(View.GONE);
        } else {
            lin_all.add(lin_Scan);
        }
        mPopupWindow = new Dialog(mContext, R.style.bottom_dialog);
        mPopupWindow.setContentView(popupWindow);
        mPopupWindow.getWindow().setGravity(Gravity.BOTTOM);
        WindowManager.LayoutParams lp = mPopupWindow.getWindow().getAttributes();
        lp.width = mContext.getResources().getDisplayMetrics().widthPixels;
        mPopupWindow.getWindow().setAttributes(lp);
        lin_copy.setOnClickListener(new MyOnClick());
        lin_wechat.setOnClickListener(new MyOnClick());
        lin_moment.setOnClickListener(new MyOnClick());
        lin_Scan.setOnClickListener(new MyOnClick());
        closeImage.setOnClickListener(new MyOnClick());

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                EnterAnim();
            }
        }, 300);

    }

    private void EnterAnim() {
        for (int i = 0; i < lin_all.size(); i++) {
            final LinearLayout lin = lin_all.get(i);
            lin.setAlpha(0.0F);
            lin.setVisibility(View.VISIBLE);
            Log.e("biang", lin.getHeight() + "");
            ObjectAnimator animator1 = ObjectAnimator.ofFloat(lin,
                    "translationY", lin.getHeight(), 0F);
            ObjectAnimator animator2 = ObjectAnimator.ofFloat(lin,
                    "alpha", 0.3F, 1F);
            AnimatorSet set = new AnimatorSet();
            set.play(animator1).with(animator2);
            set.setDuration(300);
            set.setInterpolator(new OvershootInterpolator());
//            set.setStartDelay((i + 1) * 100);
            set.setStartDelay(i * 100);
            set.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(Animator animation) {
                    super.onAnimationStart(animation);
                }
            });
            set.start();
        }
    }


    private class MyOnClick implements OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.lin_copy:
                    if (popShareKloudSyncDismissListener != null) {
                        popShareKloudSyncDismissListener.CopyLink();
                    }
                    CopyLink();
                    mPopupWindow.dismiss();
                    break;
                case R.id.lin_wechat:
                    if (popShareKloudSyncDismissListener != null) {
                        popShareKloudSyncDismissListener.Wechat();
                    }
                    GetUrl(lesson, Syncid, true);
                    mPopupWindow.dismiss();
                    break;
                case R.id.lin_moment:
                    if (popShareKloudSyncDismissListener != null) {
                        popShareKloudSyncDismissListener.Moment();
                    }
                    GetUrl(lesson, Syncid, false);
                    mPopupWindow.dismiss();
                    break;
                case R.id.lin_Scan:
                    GoToShare(lesson, Syncid);
                    break;

                case R.id.image_close:
                    if (mPopupWindow != null) {
                        mPopupWindow.dismiss();
                    }
                    break;

                default:
                    break;
            }

        }


    }

    private void GoToShare(Document lesson, final int id) {
        if (popShareKloudSyncDismissListener != null) {
            popShareKloudSyncDismissListener.Scan();
        }
        Intent i = new Intent(mContext, MyFriendsActivity.class);
        i.putExtra("TeamSpaceBeanFile", lesson);
        i.putExtra("Syncid", id);
        i.putExtra("isShare", true);
        mContext.startActivity(i);
        mPopupWindow.dismiss();
    }

    private ClipboardManager mClipboard = null;

    private void CopyLink() {
        String url = AppConfig.SHARE_DOCUMENT + lesson.getItemID();
        if (Syncid > 0) {
            url = AppConfig.SHARE_SYNC + Syncid;
        }
        if (record && Syncid > 0) {
            url = AppConfig.SHARE_RECORD + Syncid;
        }
        if (lesson.isMe()) {
            url = linshiUrl;
        }
        if (null == mClipboard) {
            mClipboard = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
        }
        mClipboard.setPrimaryClip(ClipData.newPlainText(null, url));
        Toast.makeText(mContext, "Copy link success!", Toast.LENGTH_LONG).show();
    }

    private void GetUrl(final Document lesson, final int id, final boolean st) {
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


        if (lesson.isMe()) {
            weixinshare(lesson, null, id, st);
        } else {
            ImageLoader imageLoader = new ImageLoader(requestQueue, imageCache);
            String url = lesson.getSourceFileUrl();
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
                    weixinshare(lesson, null, id, st);

                }

                @Override
                public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                    // TODO Auto-generated method stub
                    if (response.getBitmap() != null) {
//                    long s2 = System.currentTimeMillis();
//                    Log.e("duang", response.getBitmap().getByteCount() + " : " + s1 + " : " + s2 + "  :   " + (s2 - s1));
                        weixinshare(lesson, response.getBitmap(), id, st);
                    }
                }

            });
        }
    }

    /**
     * 分享到微信
     *
     * @param lesson
     * @param b
     * @param id
     * @param st     true：对话 false:朋友圈
     */
    private void weixinshare(Document lesson, Bitmap b, int id, boolean st) {
        String url = AppConfig.SHARE_DOCUMENT + lesson.getItemID();
        if (id > 0) {
            url = AppConfig.SHARE_SYNC + id;
        }

        if (record && id > 0) {
            url = AppConfig.SHARE_RECORD + id;
        }
        if (lesson.isMe()) {
            url = linshiUrl;
        }
        if (isWXAppInstalledAndSupported(WeiXinApi.getInstance().GetApi())) {
            WXWebpageObject webpage = new WXWebpageObject();
            webpage.webpageUrl = url;
            WXMediaMessage msg = new WXMediaMessage(webpage);
            msg.title = lesson.getTitle();
            if (lesson.isMe()) {
                msg.description = "我分享了【KloudSync】，快来看看吧";
            } else {
                msg.description = "请点击此框跳转至" + url;
            }

            Bitmap thumb = b;
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
        return mPopupWindow.isShowing();
    }

    public void startPop() {
        mPopupWindow.show();
    }


}
