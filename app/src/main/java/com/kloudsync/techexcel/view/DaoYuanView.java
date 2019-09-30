package com.kloudsync.techexcel.view;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.kloudsync.techexcel.R;


@SuppressLint("AppCompatCustomView")
public class DaoYuanView extends ImageView {


    private boolean lefttop;
    private boolean leftbottom;
    private boolean righttop;
    private boolean rightbottom;

    private int coner;
    private int color = Color.WHITE;

    private Paint paint;

    public DaoYuanView(Context context) {
        super(context);
        init();
    }

    public DaoYuanView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DaoYuanView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.RoundImageView, defStyle, 0);

        lefttop = a.getBoolean(R.styleable.RoundImageView_left_top, false);
        leftbottom = a.getBoolean(R.styleable.RoundImageView_left_bottom, false);
        righttop = a.getBoolean(R.styleable.RoundImageView_right_top, false);
        rightbottom = a.getBoolean(R.styleable.RoundImageView_right_bottom, false);
        coner = a.getDimensionPixelSize(R.styleable.RoundImageView_Round_coner, 0);
        color = a.getColor(R.styleable.RoundImageView_Round_color, color);

        a.recycle();

        init();
    }

    private void init() {
        paint = new Paint();
        paint.setColor(color);
        paint.setAntiAlias(true);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (lefttop) {
            drawLeftTop(canvas);
        }
        if (leftbottom) {
            drawLeftBottom(canvas);
        }
        if (righttop) {
            drawRightTop(canvas);
        }
        if (rightbottom) {
            drawRightBottom(canvas);
        }


    }

    private void drawLeftTop(Canvas canvas) {
        Path path = new Path();
        path.moveTo(0, coner);
        path.lineTo(0, 0);
        path.lineTo(coner, 0);
        path.arcTo(new RectF(0, 0, coner * 2, coner * 2), -90, -90);
        path.close();
        canvas.drawPath(path, paint);
    }

    private void drawLeftBottom(Canvas canvas) {
        Path path = new Path();
        path.moveTo(0, getHeight() - coner);
        path.lineTo(0, getHeight());
        path.lineTo(coner, getHeight());
        path.arcTo(new RectF(0, getHeight() - coner * 2, coner * 2, getHeight()), 90, 90);
        path.close();
        canvas.drawPath(path, paint);
    }

    private void drawRightBottom(Canvas canvas) {
        Path path = new Path();
        path.moveTo(getWidth() - coner, getHeight());
        path.lineTo(getWidth(), getHeight());
        path.lineTo(getWidth(), getHeight() - coner);
        path.arcTo(new RectF(getWidth() - coner * 2, getHeight() - coner * 2, getWidth(), getHeight()), 0, 90);
        path.close();
        canvas.drawPath(path, paint);
    }


    private void drawRightTop(Canvas canvas) {
        Path path = new Path();
        path.moveTo(getWidth(), coner);
        path.lineTo(getWidth(), 0);
        path.lineTo(getWidth() - coner, 0);
        path.arcTo(new RectF(getWidth() - coner * 2, 0, getWidth(), 0 + coner * 2), -90, 90);
        path.close();
        canvas.drawPath(path, paint);
    }

    /**
     * 改变圆角位置
     *
     * @param lefttop
     * @param leftbottom
     * @param righttop
     * @param rightbottom
     */
    public void Change(boolean lefttop,
                       boolean leftbottom,
                       boolean righttop,
                       boolean rightbottom) {
        this.lefttop = lefttop;
        this.leftbottom = leftbottom;
        this.righttop = righttop;
        this.rightbottom = rightbottom;
        invalidate();
    }

    /**
     * 改变圆角大小
     *
     * @param coner
     */
    public void ChangeCorner(int coner) {
        this.coner = coner;
        invalidate();
    }

}