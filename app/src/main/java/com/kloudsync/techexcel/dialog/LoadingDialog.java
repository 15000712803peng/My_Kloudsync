package com.kloudsync.techexcel.dialog;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.kloudsync.techexcel.R;


public class LoadingDialog extends Dialog {

    public LoadingDialog(Context context) {
        super(context);
    }

    public LoadingDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    public static class Builder {

        private Context context;
        private String message;
        private boolean isShowMessage = true;
        private boolean isCancelable = false;
        private boolean isCancelOutside = false;

        public Builder(Context context) {
            this.context = context;
        }


        public Builder setMessage(String message) {
            this.message = message;
            return this;
        }


        public Builder setShowMessage(boolean isShowMessage) {
            this.isShowMessage = isShowMessage;
            return this;
        }


        public Builder setCancelable(boolean isCancelable) {
            this.isCancelable = isCancelable;
            return this;
        }


        public Builder setCancelOutside(boolean isCancelOutside) {
            this.isCancelOutside = isCancelOutside;
            return this;
        }

        public LoadingDialog build() {
            LayoutInflater inflater = LayoutInflater.from(context);
            View view = inflater.inflate(R.layout.dialog_loading, null);
            LoadingDialog dialog = new LoadingDialog(context, R.style.loading_dialog_style);
            TextView msgText = (TextView) view.findViewById(R.id.txt_tips);
            if (isShowMessage) {
                if (!TextUtils.isEmpty(message)) {
                    msgText.setText(message);
                }
            } else {
                msgText.setVisibility(View.GONE);
            }
            dialog.setContentView(view);
            dialog.setCancelable(isCancelable);
            dialog.setCanceledOnTouchOutside(isCancelOutside);
            return dialog;
        }
    }


}
