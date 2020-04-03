package com.kloudsync.techexcel.help;

import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.support.v4.util.LruCache;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.animation.OvershootInterpolator;
import android.widget.LinearLayout;
import android.widget.TextView;
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
import com.ub.kloudsync.activity.Document;
import com.ub.techexcel.bean.LineItem;
import com.ub.techexcel.bean.SoundtrackBean;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class PopShareKloudSyncV2 {
    public Context mContext;

    private static PopShareKloudSyncDismissListener popShareKloudSyncDismissListener;
    private SoundtrackBean soundtrack;

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

    public void getPopwindow(Context context, SoundtrackBean soundtrack) {
        this.mContext = context;
        this.soundtrack = soundtrack;
        getPopupWindowInstance();
        mPopupWindow.getWindow().setWindowAnimations(R.style.PopupAnimation5);
    }

    public void setQrcodeVisiable(){
        lin_qrcode.setVisibility(View.VISIBLE);
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
    private LinearLayout lin_qrcode;
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
        lin_qrcode = (LinearLayout) popupWindow.findViewById(R.id.layout_qrcode);
        closeImage = (TextView) popupWindow.findViewById(R.id.cancel);
        lin_all.add(lin_copy);
        lin_all.add(lin_wechat);
        lin_all.add(lin_moment);
        lin_Scan.setVisibility(View.GONE);
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
                    copyLink();
                    mPopupWindow.dismiss();
                    break;
                case R.id.lin_wechat:
                    if (popShareKloudSyncDismissListener != null) {
                        popShareKloudSyncDismissListener.Wechat();
                    }
                    getUrl(soundtrack, true);
                    mPopupWindow.dismiss();
                    break;
                case R.id.lin_moment:
                    if (popShareKloudSyncDismissListener != null) {
                        popShareKloudSyncDismissListener.Moment();
                    }
                    getUrl(soundtrack, false);
                    mPopupWindow.dismiss();
                    break;
                case R.id.lin_Scan:
//                    goToShare(document, Syncid);
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

    }

    private void goToShare(Document document, final int id) {
        if (popShareKloudSyncDismissListener != null) {
            popShareKloudSyncDismissListener.Scan();
        }
        Intent i = new Intent(mContext, MyFriendsActivity.class);
        i.putExtra("document", document);
        i.putExtra("Syncid", id);
        i.putExtra("isShare", true);
        mContext.startActivity(i);
        mPopupWindow.dismiss();
    }

    private ClipboardManager mClipboard = null;

    private void copyLink() {
        String url = AppConfig.SHARE_SYNC + soundtrack.getSoundtrackID();
        if (null == mClipboard) {
            mClipboard = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
        }

        mClipboard.setPrimaryClip(ClipData.newPlainText(null, url));
        Toast.makeText(mContext, "Copy link success!", Toast.LENGTH_LONG).show();
    }



    private void getUrl(SoundtrackBean soundtrack, final boolean st) {
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

        weiXinShare(soundtrack, null,st);

    }


    /**
     * 分享到微信
     *
     * @param b
     * @param st       true：对话 false:朋友圈
     */
    private void weiXinShare(SoundtrackBean soundtrack, Bitmap b ,final boolean st) {

        if (isWXAppInstalledAndSupported(WeiXinApi.getInstance().GetApi())) {

            String url = AppConfig.SHARE_SYNC + soundtrack.getSoundtrackID();


            WXWebpageObject webpage = new WXWebpageObject();
            webpage.webpageUrl = url;
            final WXMediaMessage msg = new WXMediaMessage(webpage);
            msg.title = soundtrack.getTitle();
            msg.description = "请点击此框跳转至" + url;

            Bitmap thumb = b;
            Log.e("hahaha", url + "  " + (null == thumb));
            if (null == thumb) {
                thumb = BitmapFactory.decodeResource(mContext.getResources(),
                        R.drawable.app_icon);
            }
            Log.e("hahaha", url + "  " + (null == thumb));

//            msg.thumbData = Util.bmpToByteArray(thumb, true);

            final Bitmap finalThumb = thumb;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    msg.thumbData = bmpToByteArray(finalThumb, true);
                    SendMessageToWX.Req req = new SendMessageToWX.Req();
                    req.transaction = buildTransaction("webpage");
                    req.message = msg;
                    req.scene = st ? SendMessageToWX.Req.WXSceneSession : SendMessageToWX.Req.WXSceneTimeline;

                    WeiXinApi.getInstance().GetApi().sendReq(req);
                }
            }).start();




        } else {
            Toast.makeText(mContext, "微信客户端未安装，请确认",
                    Toast.LENGTH_LONG).show();
        }
    }


    public static byte[] bmpToByteArray( Bitmap bmp, final boolean needRecycle) {

        Log.e("hahahaTT",  "  " + (null == bmp));

        bmp=Bitmap.createScaledBitmap(bmp,150,150,true);

        int i;
        int j;
        if (bmp.getHeight() > bmp.getWidth()) {
            i = bmp.getWidth();
            j = bmp.getWidth();
        } else {
            i = bmp.getHeight();
            j = bmp.getHeight();
        }

        Bitmap localBitmap = Bitmap.createBitmap(i, j, Bitmap.Config.RGB_565);
        Canvas localCanvas = new Canvas(localBitmap);

        while (true) {
            localCanvas.drawBitmap(bmp, new Rect(0, 0, i, j), new Rect(0, 0, i, j), null);
            if (needRecycle)
                bmp.recycle();
            ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
            localBitmap.compress(Bitmap.CompressFormat.JPEG, 100,
                    localByteArrayOutputStream);
            localBitmap.recycle();
            byte[] arrayOfByte = localByteArrayOutputStream.toByteArray();
            try {
                localByteArrayOutputStream.close();
                Log.e("hahahaTT",  "  " + arrayOfByte.length);
                return arrayOfByte;
            } catch (Exception e) {
                //F.out(e);
            }
            i = bmp.getHeight();
            j = bmp.getHeight();
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
