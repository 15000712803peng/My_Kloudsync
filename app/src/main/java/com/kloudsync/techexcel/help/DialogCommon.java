package com.kloudsync.techexcel.help;

import android.app.AlertDialog;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.kloudsync.techexcel.R;

public class DialogCommon {

	private AlertDialog dlgGetWindow = null;// 对话框
	private Window window;
	private TextView tv_msg, tv_yes;

	private static DialogDismissListener dialogdismissListener;

	public interface DialogDismissListener {
		void DialogDismiss();
	}

	public void setPoPDismissListener(
			DialogDismissListener dialogdismissListener) {
		DialogCommon.dialogdismissListener = dialogdismissListener;
	}

	public void EditCancel(Context context, String msg) {

		int width = context.getResources().getDisplayMetrics().widthPixels;
		int height = context.getResources().getDisplayMetrics().heightPixels;

		dlgGetWindow = new AlertDialog.Builder(context).create();
		dlgGetWindow.show();
		window = dlgGetWindow.getWindow();
		window.setWindowAnimations(R.style.DialogAnimation);
		window.setContentView(R.layout.dialog_common);

		WindowManager.LayoutParams layoutParams = dlgGetWindow.getWindow()
				.getAttributes();
		layoutParams.width = width * 5 / 7;
		layoutParams.height = height * 2 / 3;
		dlgGetWindow.getWindow().setAttributes(layoutParams);
		
		tv_msg = (TextView) window.findViewById(R.id.tv_msg);
		tv_yes = (TextView) window.findViewById(R.id.tv_yes);
		
		tv_msg.setText(msg);

		tv_yes.setOnClickListener(new myOnclick());

	}
	
	private class myOnclick implements OnClickListener{

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.tv_yes:
				dlgGetWindow.dismiss(); 
				dialogdismissListener.DialogDismiss();
				break;

			default:
				break;
			}
			
		}
		
	}

}
