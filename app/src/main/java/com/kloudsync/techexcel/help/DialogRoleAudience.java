package com.kloudsync.techexcel.help;

import android.view.Window;
import android.widget.TextView;

import com.kloudsync.techexcel.R;

public class DialogRoleAudience extends DialogCommonV2 {
    private TextView bottomText;

    @Override
    protected int setDialogLayout() {
        return R.layout.dialog_role_audience;
    }

    @Override
    protected void fillContent(Window window) {
        bottomText = window.findViewById(R.id.tv_yes);
        bottomText.setOnClickListener(new HandleClick());
    }

}