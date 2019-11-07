package com.example.mybitmap.imageprocessing;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;

import java.util.HashMap;

/**
 * Class to manage an Picture, this class wrap a Bitmap instance and several others informations about the image.
 */
public class Picture {

    public enum Histogram {
        LUMINANCE,
        GRAY_LEVEL_NATURAL
    }


    private Bitmap bitmap;
    private BitmapFactory.Options options;
    private int src;
    private Context ctx;

    private int width;
    private int height;
    private int sourceWidth;
    private int sourceHeight;
    private int[] original;

    private HashMap<Histogram, int[]> histograms = new HashMap<>();

    /**
     * Just copy a Picture instance.
     *
     * @param pic
     * @deprecated
     */
    public Picture(Picture pic) {
        this(pic, 0, 0);
    }

    /**
     * Picture constructor by copying another Picture.
     *
     * @param pic          Picture to copy.
     * @param newReqHeight New size of the new picture.
     * @param newReqWidth  New size of the new picture.
     * @deprecated Still small unit bug, don't use, there are risks when using "reset" after.
     */
    public Picture(Picture pic, int newReqWidth, int newReqHeight) {
        this.src = pic.getSource();
        this.ctx = pic.ctx;
        options = new BitmapFactory.Options();

        //generate empty bitmap:
        sourceHeight = pic.getsourceHeight();
        sourceWidth = pic.getsourceWidth();

        int sampleRatio = Utils.calculateInSampleSize(sourceWidth, sourceHeight, newReqWidth, newReqHeight);
        height = sourceHeight / sampleRatio;
        width = sourceWidth / sampleRatio;

        //generate bitmap:
        bitmap = Bitmap.createScaledBitmap(pic.getBitmap(), pic.getBitmap().getWidth() / sampleRatio, pic.getBitmap().getHeight() / sampleRatio, true);

        original = new int[bitmap.getWidth() * bitmap.getHeight()];
        quickSave();
    }


    /**
     * Picture constuctor.
     *
     * @param ctx Context in which the Picture evolve.
     * @param src Resource identifier of the picture.
     */
    public Picture(Context ctx, int src) {
        this(ctx, src, 0, 0);
    }

    /**
     * Picture constuctor.
     *
     * @param ctx       Context in which the Picture evolve.
     * @param src       Resource identifier of the picture.
     * @param reqWidth  Required width, usefull to reduce the size of the loaded image. (pixels)
     * @param reqHeight Required height. (pixels)
     */
    public Picture(Context ctx, int src, int reqWidth, int reqHeight) {
        this.src = src;
        this.ctx = ctx;
        options = new BitmapFactory.Options();
        options.inMutable = true;

        //first decode:
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(ctx.getResources(), src, options);

        sourceHeight = options.outHeight; //pixel value
        sourceWidth = options.outWidth; //pixel value
        options.inSampleSize = Utils.calculateInSampleSize(sourceWidth, sourceHeight, Utils.pxToDp(reqWidth, ctx), Utils.pxToDp(reqHeight, ctx));


        //final decode:
        options.inJustDecodeBounds = false;
        bitmap = BitmapFactory.decodeResource(ctx.getResources(), src, options);
        height = options.outHeight;
        width = options.outWidth;

        //save original:
        original = new int[bitmap.getWidth() * bitmap.getHeight()];
        quickSave();
    }

    /**
     * Reset all pixels to the last quicksave values, keeping required dimensions.
     */
    public void reset() {
        bitmap.setPixels(original, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
    }

    /**
     * Save the current state of the picture (erase last quicksave)
     */
    public void quickSave() {
        bitmap.getPixels(original, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
    }

    /**
     * Similar to reset() but re read source file to fully reset the picture, even though there have been quicksaves.
     */
    public void reload() {

        //first decode:
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(ctx.getResources(), src, options);
        //no modification on options.inSampleSize;

        //final decode:
        options.inJustDecodeBounds = false;
        options.inMutable = true;
        bitmap = BitmapFactory.decodeResource(ctx.getResources(), src, options);

        //resave original:
        quickSave();
    }

    /**
     * @return Width in pixels.
     */
    public int getWidth() {
        return width;
    }

    /**
     * @return Height in pixels.
     */
    public int getHeight() {
        return height;
    }

    /**
     * @return Original file width in pixels.
     */
    public int getsourceWidth() {
        return sourceWidth;
    }

    /**
     * @return Original file height in pixels.
     */
    public int getsourceHeight() {
        return sourceHeight;
    }

    /**
     * @return Bitmap associated to the picture.
     */
    public Bitmap getBitmap() {
        return bitmap;
    }

    /**
     * @return R source integer associated to the picture.
     */
    public int getSource() {
        return src;
    }

    /**
     * To know if a precise type of histogram was genereate for this picture.
     *
     * @param type Histogram type
     * @return true if generated.
     */
    public boolean isHistogramGenerated(Histogram type) {
        return histograms.containsKey(type);
    }

    /**
     * @param type The type of the selected histogram.
     * @return Integer array (256 values) corresponding to the histogram.
     */
    public int[] getHistogram(Histogram type) {
        return histograms.get(type);
    }


    /**
     * Compute an histogram.
     *
     * @param type The histogram type.
     */
    public void generateHistogram(Histogram type) {
        int[] histogram = new int[256];
        int[] pixels = new int[bitmap.getWidth() * bitmap.getHeight()];
        bitmap.getPixels(pixels, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
        for (int i = 0; i < pixels.length; i++) {
            //compute histograms, depending the historgram type:
            int px = pixels[i];
            switch (type) {
                case LUMINANCE:
                    float[] hsv = new float[3];
                    Utils.RGBToHSV(Color.red(px), Color.green(px), Color.blue(px), hsv);
                    histogram[(int) (hsv[2] * 255f)]++;
                    break;
                case GRAY_LEVEL_NATURAL:
                    int gray = (int) (0.3 * (double) Color.red(px) + 0.59 * (double) Color.blue(px) + 0.11 * (double) Color.green(px));
                    histogram[gray]++;
                    break;
            }
        }

        histograms.put(type, histogram);
    }

}
