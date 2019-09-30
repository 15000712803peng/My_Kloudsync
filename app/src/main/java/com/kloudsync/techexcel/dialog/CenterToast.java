package com.kloudsync.techexcel.dialog;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.kloudsync.techexcel.R;

public class CenterToast extends Toast {

    public CenterToast(Context context) {
        super(context);
    }

    public static class Builder {
        private Context context;
        private String message;
        private boolean isSuccess = true;

        public Builder(Context context) {
            this.context = context;
        }

        public Builder setMessage(String message) {
            this.message = message;
            return this;
        }


        public Builder setSuccess(boolean isSuccess) {
            this.isSuccess = isSuccess;
            return this;
        }

        public CenterToast create() {
            LayoutInflater inflater = LayoutInflater.from(context);
            View view = inflater.inflate(R.layout.center_toast, null);
            CenterToast mmToast = new CenterToast(context);
            TextView msgText = view.findViewById(R.id.txt_toast_msg);
            ImageView imageView = view.findViewById(R.id.image_toast);
            if (!message.isEmpty()) {
                msgText.setText(message);
            }
            if (isSuccess) {
                imageView.setImageResource(R.drawable.ic_success);
            } else {
                imageView.setImageResource(R.drawable.ic_failure);
            }

            mmToast.setView(view);
            mmToast.setDuration(Toast.LENGTH_SHORT);
            mmToast.setGravity(Gravity.CENTER, 0, 0);
            return mmToast;
        }
    }
}
