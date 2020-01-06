package com.example.mybitmap.imageprocessing;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;

import androidx.renderscript.RenderScript;

/**
 * Class to manage an Picture, this class wrap a Bitmap instance and several others informations about the image.
 */
public class Picture {

    /**
     * Enumeration of several types of Histograms.
     */
    public enum Histogram {
        /**
         * Luminance is the V in the color system HSV.
         */
        LUMINANCE,
        /**
         * Gray level is an average of the red green and blue value of the pixel : 0.3 * R + + 0.11 * G + 0.59 * B
         */
        GRAY_LEVEL_NATURAL
    }


    private Bitmap bitmap;
    private BitmapFactory.Options options;
    private int src;
    private Context ctx;

    private int sourceWidth;
    private int sourceHeight;
    private int[] original;
    private int[] save;


    private RenderScript renderScript;

    /**
     * Just copy a Picture instance.
     *
     * @param pic
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
     */
    public Picture(Picture pic, int newReqWidth, int newReqHeight) {
        this.src = pic.getSource();
        this.ctx = pic.ctx;

        //generate empty bitmap:
        sourceHeight = pic.getSourceHeight();
        sourceWidth = pic.getSourceWidth();

        int sampleRatio = Utils.calculateInSampleSize(sourceWidth, sourceHeight, newReqWidth, newReqHeight);

        //generate bitmap:
        bitmap = Bitmap.createScaledBitmap(pic.getBitmap(), sourceWidth / sampleRatio, sourceHeight / sampleRatio, false);
        original = new int[bitmap.getWidth() * bitmap.getHeight()];
        bitmap.getPixels(original, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());

        //set option for eventual reload:
        options = new BitmapFactory.Options();
        options.inMutable = true;
        options.inSampleSize = sampleRatio;
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
        options.inSampleSize = Utils.calculateInSampleSize(sourceWidth, sourceHeight, reqWidth, reqHeight);

        //final decode:
        options.inJustDecodeBounds = false;
        bitmap = BitmapFactory.decodeResource(ctx.getResources(), src, options);

        //save original:
        original = new int[bitmap.getWidth() * bitmap.getHeight()];
        bitmap.getPixels(original, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
    }

    /**
     * Reset all pixels to the loaded version of the bitmap, keeping required dimensions.
     */
    public void reset() {
        bitmap.setPixels(original, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
    }

    /**
     * Reset all pixels to the last quicksave values.
     */
    public void quickLoad() {
        if (save != null)
            bitmap.setPixels(save, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
    }

    /**
     * Save the current state of the picture (erase last quicksave). Remember original version is already stocked in memory and you can use {@link #reset()}.
     * First call to quicksave will create a new copy of all pixels in  memory.
     */
    public void quickSave() {
        if (save == null)
            save = new int[bitmap.getWidth() * bitmap.getHeight()];
        bitmap.getPixels(save, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
    }

    /**
     * Full reload of the bitmap from file.
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
        bitmap.getPixels(original, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
    }

    /**
     * @return Width in pixels.
     */
    public int getWidth() {
        return bitmap.getWidth();
    }

    /**
     * @return Height in pixels.
     */
    public int getHeight() {
        return bitmap.getHeight();
    }

    /**
     * @return Original file width in pixels.
     */
    public int getSourceWidth() {
        return sourceWidth;
    }

    /**
     * @return Original file height in pixels.
     */
    public int getSourceHeight() {
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
     * Compute an histogram.
     *
     * @param type The histogram type.
     * @return Histogram in an array with 256 values.
     */
    public int[] getHistogram(Histogram type) {
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

        return histogram;
    }

    /**
     * RenderScript must be setted.
     *
     * @return RenderScript associated with the Picture
     */
    public RenderScript getRenderScript() {
        return renderScript;
    }

    /**
     * To use RenderScript functions on Picture you need to associated a RenderScript instance to the picture, Instantiate it from a Context class (or extended classes)
     *
     * @param renderScript
     */
    public void setRenderScript(RenderScript renderScript) {
        this.renderScript = renderScript;
    }
}
