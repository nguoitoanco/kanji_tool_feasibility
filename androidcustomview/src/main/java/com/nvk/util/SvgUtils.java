package com.nvk.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.util.Log;

import com.caverock.androidsvg.PreserveAspectRatio;
import com.caverock.androidsvg.SVG;
import com.caverock.androidsvg.SVGParseException;

/**
 * Util class to init and get paths from svg.
 */
public class SvgUtils {
    /**
     * It is for logging purposes.
     */
    private static final String LOG_TAG = "SVGUtils";
    /**
     * All the paths with their attributes from the svg.
     */
    private final List<SvgPath> mPaths = new ArrayList<>();
    /**
     * The paint provided from the view.
     */
    private final Paint mSourcePaint;
    /**
     * The init svg.
     */
    private SVG mSvg;

    /**
     * Init the SVGUtils with a paint for coloring.
     *
     * @param sourcePaint - the paint for the coloring.
     */
    public SvgUtils(final Paint sourcePaint) {
        mSourcePaint = sourcePaint;
    }

    /**
     * Loading the svg from the resources.
     *
     * @param context     Context object to get the resources.
     * @param svgResource int resource id of the svg.
     */
    public void load(Context context, int svgResource) {
        if (mSvg != null) return;
        try {
            mSvg = SVG.getFromResource(context, svgResource);
//            String str = "<svg style=\"overflow: hidden; position: relative; left: -0.03125px;\" height=\"100%\" preserveAspectRatio=\"xMinYMin\" version=\"1.1\" viewBox=\"0 0 109 109\" width=\"100%\" xmlns=\"http://www.w3.org/2000/svg\"><desc style=\"-webkit-tap-highlight-color: rgba(0, 0, 0, 0);\">Created with Raphaël 2.0.2</desc><defs style=\"-webkit-tap-highlight-color: rgba(0, 0, 0, 0);\"></defs><path style=\"-webkit-tap-highlight-color: rgba(0, 0, 0, 0); stroke-linecap: round; stroke-linejoin: round;\" d=\"M11,54.25C14.19,54.87,17.25,55,20.73,54.75C41.370000000000005,53.25,71.12,49.63,89.31,49.51C92.91,49.489999999999995,95.08,49.75,96.88,50\" fill=\"none\" stroke=\"#bf0000\" stroke-linecap=\"round\" stroke-linejoin=\"round\" stroke-width=\"3\"></path><text style=\"-webkit-tap-highlight-color: rgba(0, 0, 0, 0); text-anchor: middle; font-style: normal; font-variant: normal; font-weight: normal; font-stretch: normal; font-size: 5px; line-height: normal; font-family: Arial;\" fill=\"#808080\" font=\"10px &quot;Arial&quot;\" font-size=\"5px\" stroke=\"none\" text-anchor=\"middle\" x=\"4.25\" y=\"54.13\"><tspan style=\"-webkit-tap-highlight-color: rgba(0, 0, 0, 0);\" dy=\"1.8643750000000026\">1</tspan></text></svg>";
//            mSvg = SVG.getFromString(str);
            mSvg.setDocumentPreserveAspectRatio(PreserveAspectRatio.UNSCALED);
        } catch (SVGParseException e) {
            Log.e(LOG_TAG, "Could not load specified SVG resource", e);
        }
    }

    /**
     * Draw the svg to the canvas.
     *
     * @param canvas The canvas to be drawn.
     * @param width  The width of the canvas.
     * @param height The height of the canvas.
     */
    public void drawSvgAfter(final Canvas canvas, final int width, final int height) {
        final float strokeWidth = mSourcePaint.getStrokeWidth();
        rescaleCanvas(width, height, strokeWidth, canvas);
    }

    /**
     * Render the svg to canvas and catch all the paths while rendering.
     *
     * @param width  - the width to scale down the view to,
     * @param height - the height to scale down the view to,
     * @return All the paths from the svg.
     */
    public List<SvgPath> getPathsForViewport(final int width, final int height) {
        final float strokeWidth = mSourcePaint.getStrokeWidth();
        Canvas canvas = new Canvas() {
            private final Matrix mMatrix = new Matrix();

            @Override
            public int getWidth() {
                return width;
            }

            @Override
            public int getHeight() {
                return height;
            }

            @Override
            public void drawPath(Path path, Paint paint) {
                Path dst = new Path();

                //noinspection deprecation
                getMatrix(mMatrix);
                path.transform(mMatrix, dst);
                paint.setAntiAlias(true);
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(strokeWidth);
                mPaths.add(new SvgPath(dst, paint));
            }
        };

        // Spend a lot of device memory => should not use it on device have low memory
        rescaleCanvas(width, height, strokeWidth, canvas);

        return mPaths;
    }

    /**
     * Rescale the canvas with specific width and height.
     *
     * @param width       The width of the canvas.
     * @param height      The height of the canvas.
     * @param strokeWidth Width of the path to add to scaling.
     * @param canvas      The canvas to be drawn.
     */
    private void rescaleCanvas(int width, int height, float strokeWidth, Canvas canvas) {
        if (mSvg == null) return;
        final RectF viewBox = mSvg.getDocumentViewBox();

        final float scale = Math.min(width
                        / (viewBox.width() + strokeWidth),
                height / (viewBox.height() + strokeWidth));

        canvas.translate((width - viewBox.width() * scale) / 2.0f,
                (height - viewBox.height() * scale) / 2.0f);
        canvas.scale(scale, scale);

        mSvg.renderToCanvas(canvas);
    }

    /**
     * Path with bounds for scalling , length and paint.
     */
    public static class SvgPath {

        /**
         * Region of the path.
         */
        private static final Region REGION = new Region();
        /**
         * This is done for clipping the bounds of the path.
         */
        private static final Region MAX_CLIP =
                new Region(Integer.MIN_VALUE, Integer.MIN_VALUE,
                        Integer.MAX_VALUE, Integer.MAX_VALUE);
        /**
         * The path itself.
         */
        public final Path path;
        /**
         * The paint to be drawn later.
         */
        public final Paint paint;
        /**
         * The length of the path.
         */
        public float length;
        /**
         * Listener to notify that an animation step has happened.
         */
        AnimationStepListener animationStepListener;
        /**
         * The bounds of the path.
         */
        public final Rect bounds;
        /**
         * The measure of the path, we can use it later to get segment of it.
         */
        public final PathMeasure measure;

        /**
         * Constructor to add the path and the paint.
         *
         * @param path  The path that comes from the rendered svg.
         * @param paint The result paint.
         */
        public SvgPath(Path path, Paint paint) {
            this.path = path;
            this.paint = paint;

            measure = new PathMeasure(path, false);
            this.length = measure.getLength();

            REGION.setPath(path, MAX_CLIP);
            bounds = REGION.getBounds();
        }

        /**
         * Sets the animation step listener.
         *
         * @param animationStepListener AnimationStepListener.
         */
        public void setAnimationStepListener(AnimationStepListener animationStepListener) {
            this.animationStepListener = animationStepListener;
        }

        /**
         * Sets the length of the path.
         *
         * @param length The length to be set.
         */
        public void setLength(float length) {
            path.reset();
            measure.getSegment(0.0f, length, path, true);
            path.rLineTo(0.0f, 0.0f);

            if (animationStepListener != null) {
                animationStepListener.onAnimationStep();
            }
        }

        /**
         * @return The length of the path.
         */
        public float getLength() {
            return length;
        }
    }

    public interface AnimationStepListener {

        /**
         * Called when an animation step happens.
         */
        void onAnimationStep();
    }
}
