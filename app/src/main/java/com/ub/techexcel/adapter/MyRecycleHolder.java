package com.ub.techexcel.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.kloudsync.techexcel.R;

/**
 * Created by wang on 2018/2/8.
 */

public class MyRecycleHolder extends RecyclerView.ViewHolder {

    TextView name;
    ImageView imageView;

    public MyRecycleHolder(View itemView) {
        super(itemView);
        name = (TextView) itemView.findViewById(R.id.name);
        imageView = (ImageView) itemView.findViewById(R.id.delete);
    }

}
