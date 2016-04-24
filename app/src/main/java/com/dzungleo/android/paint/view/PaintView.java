package com.dzungleo.android.paint.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;

import com.dzungleo.android.paint.R;

/**
 * Created by 5410 on 4/23/2016.
 */
public class PaintView extends View {
    private static final String LOG_TAG = PaintView.class.getSimpleName();

    private float mStrokeWidth;
    private int mStrokeColor;

    private Paint mPaint;
    private Path mPath;

    /**
     * We use a Sparse Array to store pointers by mapping their IDs (integer)
     * with their coordinates (PointF)
     * We should use Sparse Array instead of HashMap because it is optimized by Android
     */
    private SparseArray<PointF> mActivePointer;

    /**
     * We use this array to store the colors (total of 6) of the touch points
     */
    private int [] colors = {
        Color.BLUE, Color.GREEN, Color.MAGENTA,
        Color.BLACK, Color.CYAN, Color.GRAY
    };

    public PaintView(Context context) {
        super(context);

        init();
    }

    public PaintView(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.PaintView, 0, 0);

        try {
            mStrokeWidth = a.getFloat(R.styleable.PaintView_strokeWidth, 1.0f);
            mStrokeColor = a.getColor(R.styleable.PaintView_strokeColor, 0xffffff);
        } finally {
            a.recycle();
        }

        init();
    }

    /**
     * We should have a function to initialize the resource
     */
    private void init() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
//        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(mStrokeWidth);
        mPaint.setColor(mStrokeColor);

        mPath = new Path();

        mActivePointer = new SparseArray<>();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // With each active pointer we draw the corresponding circle at its location
        int size = mActivePointer.size();
        for(int i = 0; i < size; i++) {
            PointF pointF = mActivePointer.valueAt(i);

            // With each point we draw with a different color.
            // Notice that we only have 6 different colors
            mPaint.setColor(colors[i % 6]);
            canvas.drawCircle(pointF.x, pointF.y, mStrokeWidth, mPaint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int pointerIndex = event.getActionIndex();
        Log.d(LOG_TAG, "Pointer index: " + pointerIndex);
        int pointerId = event.getPointerId(pointerIndex);
        Log.d(LOG_TAG, "Pointer id: " + pointerId);

        int maskedAction = event.getActionMasked();
        switch(maskedAction) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_POINTER_DOWN: {
                PointF pointF = new PointF();
                pointF.x = event.getX();
                pointF.y = event.getY();

                mActivePointer.put(pointerId, pointF);
                break;
            }

            case MotionEvent.ACTION_MOVE: {
                int size = mActivePointer.size();

                for(int i = 0; i < size; i++) {
                    PointF pointF = mActivePointer.get(event.getPointerId(i));
                    if(pointF != null) {
                        pointF.x = event.getX(i);
                        pointF.y = event.getY(i);
                    }
                }
            }

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
            case MotionEvent.ACTION_CANCEL: {
                mActivePointer.remove(pointerId);
                break;
            }
        }

        invalidate();
        return true;
//        float eventX = event.getX();
//        float eventY = event.getY();
//
//        switch (event.getAction()) {
//            case MotionEvent.ACTION_DOWN:
//                Log.d(LOG_TAG, "Touch down at x:" + eventX + ", y:" + eventY);
//                mPath.moveTo(eventX, eventY);
//                invalidate();
//                return true;
//            case MotionEvent.ACTION_UP:
//                Log.d(LOG_TAG, "Touch up at x:" + eventX + ", y:" + eventY);
//                break;
//            case MotionEvent.ACTION_MOVE:
//                Log.d(LOG_TAG, "Move at x:" + eventX + ", y:" + eventY);
//                mPath.lineTo(eventX, eventY);
//                invalidate();
//                return true;
//
//            default:
//                return true;
//        }
//        return true;
    }
}
