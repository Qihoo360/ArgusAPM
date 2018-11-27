package com.argusapm.android.debug.view.component;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;

/**
 * 圆圈View
 *
 * @author ArgusAPM Team
 */
public class CircleView extends View {
    private float mDipScale = 1;
    private Paint mTextP = new Paint();
    private Paint mCirclePaint = new Paint();

    private int verticalCenter;
    private int horizontalCenter;
    private int circleRadius;

    public CircleView(Context context, float dipScale) {
        super(context);
        mDipScale = dipScale;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        verticalCenter = getHeight() / 2;
        horizontalCenter = getWidth() / 2;
        circleRadius = getHeight() / 2 - (int) (2 * mDipScale);
        mCirclePaint.setAntiAlias(true);
        mCirclePaint.setColor(0xff29a600);
        mCirclePaint.setStyle(Paint.Style.STROKE);
        mCirclePaint.setStrokeWidth(2 * mDipScale);
        canvas.drawCircle(horizontalCenter, verticalCenter, circleRadius, mCirclePaint);
        mTextP.setColor(0xff29a600);
        mTextP.setStrokeWidth(2 * mDipScale);
        mTextP.setTextSize(8 * mDipScale);
        mTextP.setTextAlign(Paint.Align.CENTER);
        Paint.FontMetrics fontMetrics = mTextP.getFontMetrics();
        int baseLineY = (int) (verticalCenter - fontMetrics.top / 2 - fontMetrics.bottom / 2);
        canvas.drawText("Apm", horizontalCenter, baseLineY, mTextP);
    }
}
