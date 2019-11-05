package com.kloudsync.techexcel.help;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.kloudsync.techexcel.R;

public class FilterSpaceDialog implements OnClickListener {
    public Context mContext;
    public Dialog dialog;
    TextView showAllText;
    TextView showOnlyMineText;
    ImageView allSelectedImage;
    ImageView onlyMineSelectedImage;
    TextView cancelText;

    public interface SpaceOptionsLinstener {
        void showAllOption();

        void showOnlyMineOption();
    }

    private SpaceOptionsLinstener optionsLinstener;


    public void setOptionsLinstener(SpaceOptionsLinstener optionsLinstener) {
        this.optionsLinstener = optionsLinstener;
    }

    public FilterSpaceDialog(Context context) {
        mContext = context;
        initDialog();
    }

    public void initDialog() {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        View view = layoutInflater.inflate(R.layout.dialog_filter_space, null);
        dialog = new Dialog(mContext, R.style.bottom_dialog);
        dialog.setContentView(view);
        dialog.getWindow().setWindowAnimations(R.style.PopupAnimation5);
        dialog.getWindow().setGravity(Gravity.BOTTOM);
        showAllText = view.findViewById(R.id.txt_show_all);
        showOnlyMineText = view.findViewById(R.id.txt_show_only_mine);
        allSelectedImage = view.findViewById(R.id.img_choosen_all);
        onlyMineSelectedImage = view.findViewById(R.id.img_choosen_only);
        showAllText.setOnClickListener(this);
        cancelText = view.findViewById(R.id.cancel);
        cancelText.setOnClickListener(this);
        showOnlyMineText.setOnClickListener(this);
        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        params.width = mContext.getResources().getDisplayMetrics().widthPixels;
        dialog.getWindow().setAttributes(params);
    }

    public void show() {
        dialog.show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txt_show_all:
                if (optionsLinstener != null) {
                    optionsLinstener.showAllOption();
                }
                dismiss();
                break;
            case R.id.txt_show_only_mine:
                if (optionsLinstener != null) {
                    optionsLinstener.showOnlyMineOption();

                }
                dismiss();
                break;

            case R.id.cancel:
                dismiss();
                break;
            default:
                break;
        }
    }

    public void dismiss() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    public void setSelectedOption(int option) {
        if (option == 1) {
            allSelectedImage.setVisibility(View.VISIBLE);
            onlyMineSelectedImage.setVisibility(View.INVISIBLE);
        } else {
            allSelectedImage.setVisibility(View.INVISIBLE);
            onlyMineSelectedImage.setVisibility(View.VISIBLE);
        }
    }


}
