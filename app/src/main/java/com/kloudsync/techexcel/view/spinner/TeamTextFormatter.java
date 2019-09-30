package com.kloudsync.techexcel.view.spinner;

import android.text.Spannable;
import android.text.SpannableString;

import com.kloudsync.techexcel.bean.Team;

public class TeamTextFormatter implements SpinnerTextFormatter<Team> {

    @Override
    public Spannable format(Team item) {
        return new SpannableString(item.getName());
    }
}
