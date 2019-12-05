package com.kloudsync.techexcel.view.spinner;

import android.text.Spannable;
import android.text.SpannableString;

import com.kloudsync.techexcel.bean.Team;
import com.kloudsync.techexcel.bean.UserNotes;
import com.kloudsync.techexcel.dialog.plugin.UserNotesDialog;

public class UserNoteTextFormatter implements SpinnerTextFormatter<UserNotes> {

    @Override
    public Spannable format(UserNotes user) {
        return new SpannableString(user.getUserName()  +"  (" + (user.getNoteCount()) + ")");
    }
}
