package com.nvk.customview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import static android.graphics.PorterDuff.Mode;

/**
 * Created by KhanhNV10 on 21/03/2016.
 */
public class DrawingView extends View {
    private static final String TAG = DrawingView.class.getSimpleName();
    private static final float TOUCH_TOLERANCE = 4;

    private Bitmap mBitmap;
    private Canvas mCanvas;
    private Path mPath;
    private Paint mBitmapPaint;
    private Paint circlePaint;
    private Path circlePath;
    private Paint mPaint;

    private boolean isClear = true;

    public int width;
    public int height;
    private float mX, mY;

    private int mCurrentColor;
    private int mCurrentSize;

//    private ArrayList<Path> mPaths = new ArrayList<>();
//    private ArrayList<Integer> mColorPaths = new ArrayList<>();

//    private ArrayList<Path> undonePaths = new ArrayList<>();
//    private ArrayList<Integer> mColorUndoPaths = new ArrayList<>();

    private List<DrawObject> mDrawList = new ArrayList<>();
    private List<DrawObject> mUndoDrawList = new ArrayList<>();

    public DrawingView(Context context) {
        super(context);
    }

    public DrawingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setContentView();
    }

    public DrawingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setContentView();
    }

    private void setContentView() {
        mPath = new Path();

        mBitmapPaint = new Paint(Paint.DITHER_FLAG);
        circlePaint = new Paint();
        circlePath = new Path();
        circlePaint.setAntiAlias(true);
        circlePaint.setColor(Color.BLUE);
        circlePaint.setStyle(Paint.Style.STROKE);
        circlePaint.setStrokeJoin(Paint.Join.MITER);
        circlePaint.setStrokeWidth(4f);

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
//        setParams(Color.GREEN, 12);

    }

    public void setCurrentColor(int color) {
        mCurrentColor = color;
        mPaint.setColor(color);
    }

    public void setStrokeSize(int size) {
        mCurrentSize = size;
        mPaint.setStrokeWidth(size);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);

        if (BuildConfig.DEBUG) {
            Log.d(TAG, "onSizeChanged");
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        for (DrawObject draw : mDrawList){
            mPaint.setColor(draw.mColor);
            mPaint.setStrokeWidth(draw.mSize);
            canvas.drawPath(draw.path, mPaint);
        }

        mPaint.setColor(mCurrentColor);
        mPaint.setStrokeWidth(mCurrentSize);
        canvas.drawPath(mPath, mPaint);
//        canvas.drawPath(mPath, paint);
//        canvas.drawPath(circlePath, circlePaint);
        Log.d(TAG, "onDraw");
    }

    private void touch_start(float x, float y) {
        mUndoDrawList.clear();
        mPath.reset();
        mPath.moveTo(x, y);
        mX = x;
        mY = y;
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "touch_start");
        }
    }

    private void touch_move(float x, float y) {
        float dx = Math.abs(x - mX);
        float dy = Math.abs(y - mY);
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
            mX = x;
            mY = y;

            circlePath.reset();
            circlePath.addCircle(mX, mY, 30, Path.Direction.CW);
        }

        if (BuildConfig.DEBUG) {
            Log.d(TAG, "touch_move");
        }
    }

    private void touch_up() {
        mPath.lineTo(mX, mY);
        circlePath.reset();
        // commit the path to our offscreen
        mCanvas.drawPath(mPath, mPaint);
        // kill this so we don't double draw
//        mPath.reset();
        Log.d(TAG, "mPaint.getColor():" + mPaint.getColor());
//        mPaths.add(mPath);
        mDrawList.add(new DrawObject(mPath, mCurrentColor, mCurrentSize));

        mPath = new Path();
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "touch_up");
        }
        isClear = false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touch_start(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                touch_move(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                touch_up();
                invalidate();
                break;
        }
        return true;
    }


    public void undo() {
        Log.d(TAG, "doBack");
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "doBack");
        }

        if (mDrawList.size()>0)
        {
            mPath = new Path();
            mUndoDrawList.add(mDrawList.remove(mDrawList.size() - 1));
//            undonePaths.add(mPaths.remove(mPaths.size() - 1));
//            mColorUndoPaths.add(mColorPaths.remove(mColorPaths.size() - 1));

            invalidate();
        }
    }

    public void redo() {
        if (mUndoDrawList.size() > 0)
        {
            mDrawList.add(mUndoDrawList.remove(mUndoDrawList.size()-1));
//            mColorPaths.add((mColorUndoPaths.remove(mColorUndoPaths.size() - 1)));
            invalidate();
        }
    }

    public void clearAll() {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "clearAll");
        }

        if (!isClear) {
            mPath.reset();
            mDrawList.clear();
            mUndoDrawList.clear();
            isClear = true;
//        mColorPaths.clear();
//        mColorUndoPaths.clear();
            mCanvas.drawColor(Color.TRANSPARENT, Mode.CLEAR);
            invalidate();
        }
    }

    public boolean isClear() {
        return isClear;
    }
}
