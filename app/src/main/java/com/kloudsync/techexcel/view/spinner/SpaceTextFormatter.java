package com.kloudsync.techexcel.view.spinner;

import android.text.Spannable;
import android.text.SpannableString;

import com.ub.kloudsync.activity.TeamSpaceBean;

public class SpaceTextFormatter implements SpinnerTextFormatter<TeamSpaceBean> {

    @Override
    public Spannable format(TeamSpaceBean space) {
        return new SpannableString(space.getName());
    }
}
