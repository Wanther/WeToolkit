package cn.wanther.toolkit.ui.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.ValueAnimator;

public class QRScanOverlay extends View implements ValueAnimator.AnimatorUpdateListener{

    private static final String TAG = "QRScanOverlay";

    private static final int LAYER_FLAGS = Canvas.MATRIX_SAVE_FLAG | Canvas.CLIP_SAVE_FLAG
            | Canvas.HAS_ALPHA_LAYER_SAVE_FLAG | Canvas.FULL_COLOR_LAYER_SAVE_FLAG
            | Canvas.CLIP_TO_LAYER_SAVE_FLAG;

    private static float WIN_SCALE = 0.618f;

    private Paint mWinPaint;
    private Paint mBackgroundPaint;
    private Paint mWinCenterPaint;

    private Rect mWinRect;
    private Animator mScanAnimator;
    private float mAnimProgress;

//    private Bitmap mBitmap;
//    private Paint mBitmapPaint;

    public QRScanOverlay(Context context) {
        super(context);
        init();
    }

    public QRScanOverlay(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public QRScanOverlay(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mWinRect = new Rect();

        mWinPaint = new Paint();
        mWinPaint.setColor(Color.GREEN);
        mWinPaint.setStrokeWidth(1f);

        mBackgroundPaint = new Paint();
        mBackgroundPaint.setColor(0x88000000);

        mWinCenterPaint = new Paint();
        mWinCenterPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));

        mScanAnimator = ValueAnimator.ofFloat(0, 1);
        mScanAnimator.setDuration(2000);
        ((ValueAnimator)mScanAnimator).setRepeatCount(ValueAnimator.INFINITE);
        ((ValueAnimator)mScanAnimator).addUpdateListener(this);
        mScanAnimator.setStartDelay(200);
        mScanAnimator.start();

        //mBitmapPaint = new Paint();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = getWidth();
        int height = getHeight();

        int winLen = (int)(Math.min(width, height) * WIN_SCALE);

        int left = (int)((width - winLen) / 2f);
        int top = (int)((height - winLen) / 2f);

        mWinRect.set(left, top, left + winLen, top + winLen);

        // 背景
        canvas.drawRect(0, 0, width, height, mBackgroundPaint);

        canvas.drawRect(mWinRect.left, mWinRect.top, mWinRect.right, mWinRect.bottom, mWinCenterPaint);

        // 扫描线
        final int animTop = (int)(mWinRect.height() * mAnimProgress + mWinRect.top);
        canvas.drawLine(mWinRect.left, animTop, mWinRect.right, animTop, mWinPaint);

        // 窗口
        canvas.drawLine(mWinRect.left, mWinRect.top, mWinRect.right, mWinRect.top, mWinPaint);
        canvas.drawLine(mWinRect.right, mWinRect.top, mWinRect.right, mWinRect.bottom, mWinPaint);
        canvas.drawLine(mWinRect.right, mWinRect.bottom, mWinRect.left, mWinRect.bottom, mWinPaint);
        canvas.drawLine(mWinRect.left, mWinRect.bottom, mWinRect.left, mWinRect.top, mWinPaint);

//        if (mBitmap != null && !mBitmap.isRecycled()) {
//            canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);
//        }

    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        if (mScanAnimator != null) {
            mScanAnimator.cancel();
        }
    }

    @Override
    public void onAnimationUpdate(ValueAnimator animation) {
        mAnimProgress = (Float)animation.getAnimatedValue();
        invalidate();
    }

    public Rect getWinRect() {
        return mWinRect;
    }

    /*public void drawBitmap(final Bitmap bitmap) {
        post(new Runnable() {
            @Override
            public void run() {
                if (mBitmap != null) {
                    mBitmap.recycle();
                }
                mBitmap = bitmap;
                invalidate();
            }
        });
    }*/
}
