package com.kloudsync.techexcel.help;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.adapter.DeleteSpaceAdapter;
import com.kloudsync.techexcel.info.Customer;

import java.util.ArrayList;

public class DialogTSDelete {

    private AlertDialog dlgGetWindow = null;// 对话框
    private Window window;
    private RecyclerView rv_sp;
    private Context mContext;
    private TextView tv_cancel;

    private ArrayList<Customer> cuslist = new ArrayList<Customer>();
    private DeleteSpaceAdapter dAdapter;

    int type;//0:team 1:space
    int spaceid;

    private int width;

    private static DialogDismissListener dialogdismissListener;

    public interface DialogDismissListener {
        void PopSelect(Customer cus);
    }

    public void setPoPDismissListener(
            DialogDismissListener dialogdismissListener) {
        DialogTSDelete.dialogdismissListener = dialogdismissListener;
    }

    public void SetType(int type, int spaceid) {
        this.type = type;
        this.spaceid = spaceid;
    }

    public void EditCancel(Context context, ArrayList<Customer> cuslist) {
        this.mContext = context;
        this.cuslist = cuslist;

        width = context.getResources().getDisplayMetrics().widthPixels;

        dlgGetWindow = new AlertDialog.Builder(context).create();
        dlgGetWindow.show();
        window = dlgGetWindow.getWindow();
        window.setWindowAnimations(R.style.PopupAnimation3);
        window.setContentView(R.layout.dialog_tsd);
        window.setBackgroundDrawable(new ColorDrawable(mContext.getResources().getColor(R.color.white)));
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);



        WindowManager.LayoutParams layoutParams = dlgGetWindow.getWindow()
                .getAttributes();
//        layoutParams.width = width / 2;
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;
        dlgGetWindow.getWindow().setAttributes(layoutParams);

        ShowTSInfo();
    }

    private void ShowTSInfo() {
        rv_sp = (RecyclerView) window.findViewById(R.id.rv_sp);
        tv_cancel = (TextView) window.findViewById(R.id.tv_cancel);

        LinearLayoutManager manager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        rv_sp.setLayoutManager(manager);
        dAdapter = new DeleteSpaceAdapter(cuslist);
        dAdapter.setOnItemClickListener(new DeleteSpaceAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Customer cus = cuslist.get(position);
                dialogdismissListener.PopSelect(cus);
                dlgGetWindow.dismiss();
            }
        });
        rv_sp.setAdapter(dAdapter);

        tv_cancel.setOnClickListener(new MyOnClick());
    }

    protected class MyOnClick implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.tv_cancel:
                    dlgGetWindow.dismiss();
                    break;
                default:
                    break;
            }
        }
    }


}
