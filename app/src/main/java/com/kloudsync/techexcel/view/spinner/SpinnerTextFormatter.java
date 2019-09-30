package com.kloudsync.techexcel.view.spinner;

import android.text.Spannable;

public interface SpinnerTextFormatter<T> {

    Spannable format(T item);
}
