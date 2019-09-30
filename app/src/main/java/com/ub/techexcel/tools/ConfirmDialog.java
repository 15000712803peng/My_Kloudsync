package com.ub.techexcel.tools;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.kloudsync.techexcel.R;

public class ConfirmDialog extends Dialog {

    private Context context;
    private String title;
    private String confirmButtonText;
    private String cacelButtonText;
    private ClickListenerInterface clickListenerInterface;

    public interface ClickListenerInterface {

        void doConfirm();

        void doCancel();
    }

    public ConfirmDialog(Context context, String title, String confirmButtonText, String cacelButtonText) {
        super(context, R.style.confirmDialog);
        this.context = context;
        this.title = title;
        this.confirmButtonText = confirmButtonText;
        this.cacelButtonText = cacelButtonText;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        init();
    }

    public void init() {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.customdialoglayout, null);
        setContentView(view);
        TextView tvTitle = (TextView) view.findViewById(R.id.title);
        TextView tvConfirm = (TextView) view.findViewById(R.id.confirm);
        TextView tvCancel = (TextView) view.findViewById(R.id.cancel);
        tvTitle.setText(title);
        tvConfirm.setText(confirmButtonText);
        tvCancel.setText(cacelButtonText);
        tvConfirm.setOnClickListener(new ClickListener());
        tvCancel.setOnClickListener(new ClickListener());
        Window dialogWindow = getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        DisplayMetrics d = context.getResources().getDisplayMetrics();
        lp.width = (int) (d.widthPixels * 0.8);
        dialogWindow.setAttributes(lp);
    }

    public void setClicklistener(ClickListenerInterface clickListenerInterface) {
        this.clickListenerInterface = clickListenerInterface;
    }

    private class ClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            switch (id) {
                case R.id.confirm:
//                    if (isShowing()) {
//                        dismiss();
//                    }
                    clickListenerInterface.doConfirm();
                    break;
                case R.id.cancel:
//                    if (isShowing()) {
//                        dismiss();
//                    }
                    clickListenerInterface.doCancel();
                    break;
            }
        }
    }

}