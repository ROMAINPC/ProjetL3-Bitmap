package fr.romainpc.bitmapproject.imageprocessing;

import android.graphics.Bitmap;
import android.graphics.Color;


/**
 * Class with methods to apply effects on Bitmap pictures.
 *
 * @see android.graphics.Bitmap
 */
public class Effects {

    /**
     * Enumeration of all effects type.
     */
    public enum EffectType {
        GRAY,
        HUE,
        KEEP_COLOR,
        LINEAR_EXTENSION,
        FLATTENING
    }

    /**
     * @param bmp Bitmap
     * @deprecated Less effective than {@link #grayLevel(Bitmap, double, double, double)}
     */
    public static void grayLevelOld(Bitmap bmp, double red, double green, double blue) {
        red = red > 1.0 ? 1.0 : red;
        red = red < 0.0 ? 0.0 : red;
        green = green > 1.0 ? 1.0 : green;
        green = green < 0.0 ? 0.0 : green;
        blue = blue > 1.0 ? 1.0 : blue;
        blue = blue < 0.0 ? 0.0 : blue;
        for (int y = 0; y < bmp.getHeight(); y++) {
            for (int x = 0; x < bmp.getWidth(); x++) {
                int px = bmp.getPixel(x, y);
                int gray = (int) (red * (double) Color.red(px) + blue * (double) Color.blue(px) + green * (double) Color.green(px));
                bmp.setPixel(x, y, Color.rgb(gray, gray, gray));
            }
        }
    }

    /**
     * Apply effect on the bitmap picture passed in parameter: put the picture in gray level. Adjust red green and blue value to adjust which of them will impact the more the gray level.
     *
     * @param bmp   Bitmap
     * @param red   Red proportion (between 0.0 and 1.0)
     * @param green Green proportion (between 0.0 and 1.0)
     * @param blue  Blue proportion (between 0.0 and 1.0)
     */
    public static void grayLevel(Bitmap bmp, double red, double green, double blue) {
        red = red > 1.0 ? 1.0 : red;
        red = red < 0.0 ? 0.0 : red;
        green = green > 1.0 ? 1.0 : green;
        green = green < 0.0 ? 0.0 : green;
        blue = blue > 1.0 ? 1.0 : blue;
        blue = blue < 0.0 ? 0.0 : blue;
        int[] pixels = new int[bmp.getWidth() * bmp.getHeight()];
        bmp.getPixels(pixels, 0, bmp.getWidth(), 0, 0, bmp.getWidth(), bmp.getHeight());
        for (int i = 0; i < pixels.length; i++) {
            int px = pixels[i];
            int gray = (int) (red * (double) Color.red(px) + blue * (double) Color.blue(px) + green * (double) Color.green(px));
            pixels[i] = Color.argb(Color.alpha(px), gray, gray, gray);
        }
        bmp.setPixels(pixels, 0, bmp.getWidth(), 0, 0, bmp.getWidth(), bmp.getHeight());
    }

    /**
     * See {@link #grayLevel(Bitmap, double, double, double)} method
     */
    public static void grayLevel(Picture p, double red, double green, double blue) {
        grayLevel(p.getBitmap(), red, green, blue);
    }

    /**
     * Apply effect on the bitmap picture passed in parameter: colorize the picture with the specified hue.
     *
     * @param bmp      Bitmap
     * @param hueAngle Hue value, represented by an angle on the hue wheel [0;360]
     */
    public static void colorize(Bitmap bmp, int hueAngle) {
        int[] pixels = new int[bmp.getWidth() * bmp.getHeight()];
        bmp.getPixels(pixels, 0, bmp.getWidth(), 0, 0, bmp.getWidth(), bmp.getHeight());
        float hue = (float) hueAngle;
        for (int i = 0; i < pixels.length; i++) {
            int px = pixels[i];
            float[] hsv = new float[3];
            Utils.RGBToHSV(Color.red(px), Color.green(px), Color.blue(px), hsv);
            hsv[0] = hue;
            pixels[i] = Utils.HSVToColor(hsv, Color.alpha(px));
        }
        bmp.setPixels(pixels, 0, bmp.getWidth(), 0, 0, bmp.getWidth(), bmp.getHeight());
    }

    /**
     * See {@link #colorize(Bitmap, int)} method
     */
    public static void colorize(Picture p, int hueAngle) {
        colorize(p.getBitmap(), hueAngle);
    }

    /**
     * Apply effect on the bitmap picture passed in parameter: Only conserve specific colors , other colors become gray by minimise saturation.
     *
     * @param bmp            Bitmap
     * @param hueAngle       Hue value to keep, represented by an angle on the hue wheel [0;360]
     * @param toleranceAngle Colors in range "hueAngle" +/- this angle are kept.
     */
    public static void keepColor(Bitmap bmp, float hueAngle, float toleranceAngle) {
        hueAngle = hueAngle % 360f;
        toleranceAngle = toleranceAngle % 180f;
        int[] pixels = new int[bmp.getWidth() * bmp.getHeight()];
        bmp.getPixels(pixels, 0, bmp.getWidth(), 0, 0, bmp.getWidth(), bmp.getHeight());

        for (int i = 0; i < pixels.length; i++) {
            int px = pixels[i];
            float[] hsv = new float[3];
            Utils.RGBToHSV(Color.red(px), Color.green(px), Color.blue(px), hsv);
            float diff = Math.abs(hsv[0] - hueAngle);
            if (!(Math.min(diff, 360 - diff) <= toleranceAngle)) {
                hsv[1] = 0;
            }
            pixels[i] = Utils.HSVToColor(hsv, Color.alpha(px));
        }
        bmp.setPixels(pixels, 0, bmp.getWidth(), 0, 0, bmp.getWidth(), bmp.getHeight());
    }

    /**
     * See {@link #keepColor(Bitmap, float, float)} method
     */
    public static void keepColor(Picture p, float hueAngle, float toleranceAngle) {
        keepColor(p.getBitmap(), hueAngle, toleranceAngle);
    }

    /**
     * Aplly a linear egalization on the specified histogram of the picture.
     *
     * @param bmp       Bitmap
     * @param type      The type of the histogram.
     * @param histogram The histogram array.
     */
    public static void linearDynamicExtension(Bitmap bmp, Picture.Histogram type, int[] histogram) {
        //make LUT:
        int[] LUT = new int[histogram.length];
        int[] extr = Utils.getHistogramMinMaxValue(histogram);
        int min = extr[0];
        int max = extr[1];
        if (max != min) { // if bitmap is uniform there will be a division by zero, to avoid it and because there should be no visual effect, the algorithm is skipped.
            for (int i = 0; i < histogram.length; i++) {
                LUT[i] = 255 * (i - min) / (max - min);
            }

            //apply LUT:
            int[] pixels = new int[bmp.getWidth() * bmp.getHeight()];
            bmp.getPixels(pixels, 0, bmp.getWidth(), 0, 0, bmp.getWidth(), bmp.getHeight());
            for (int i = 0; i < pixels.length; i++) {
                int px = pixels[i];
                switch (type) {
                    case LUMINANCE:
                        float[] hsv = new float[3];
                        Utils.RGBToHSV(Color.red(px), Color.green(px), Color.blue(px), hsv);
                        hsv[2] = (float) LUT[(int) (hsv[2] * 255f)] / 255f;
                        pixels[i] = Utils.HSVToColor(hsv, Color.alpha(px));
                        break;
                    case GRAY_LEVEL_NATURAL: // Decolorize the bitmap.
                        int gray = (int) (0.3 * (double) Color.red(px) + 0.59 * (double) Color.blue(px) + 0.11 * (double) Color.green(px));
                        gray = LUT[gray];
                        pixels[i] = Color.argb(Color.alpha(px), gray, gray, gray);
                        break;
                }

            }
            bmp.setPixels(pixels, 0, bmp.getWidth(), 0, 0, bmp.getWidth(), bmp.getHeight());
        }
    }

    /**
     * See {@link #linearDynamicExtension(Bitmap, Picture.Histogram, int[])} method
     *
     * @param p    Picture
     * @param type Histogram type to egalize.
     */
    public static void linearDynamicExtension(Picture p, Picture.Histogram type) {
        linearDynamicExtension(p.getBitmap(), type, p.getHistogram(type));
    }

    /**
     * Flatten the histogram
     *
     * @param bmp       Bitmap
     * @param type      The type of the histogram.
     * @param histogram The histogram array.
     */
    public static void histogramFlattening(Bitmap bmp, Picture.Histogram type, int[] histogram) {
        //Compute cumulated histogram:
        int[] cumu = new int[histogram.length];
        int sum = 0;
        for (int i = 0; i < histogram.length; i++) {
            sum += histogram[i];
            cumu[i] = sum;
        }


        //apply LUT:
        int[] pixels = new int[bmp.getWidth() * bmp.getHeight()];
        bmp.getPixels(pixels, 0, bmp.getWidth(), 0, 0, bmp.getWidth(), bmp.getHeight());
        int N = pixels.length;
        for (int i = 0; i < pixels.length; i++) {
            int px = pixels[i];
            switch (type) {
                case LUMINANCE:
                    float[] hsv = new float[3];
                    Utils.RGBToHSV(Color.red(px), Color.green(px), Color.blue(px), hsv);
                    hsv[2] = (float) (cumu[(int) (hsv[2] * 255f)] * 255 / N) / 255f;
                    pixels[i] = Utils.HSVToColor(hsv, Color.alpha(px));
                    break;
                case GRAY_LEVEL_NATURAL: // Decolorize the bitmap.
                    int gray = (int) (0.3 * (double) Color.red(px) + 0.59 * (double) Color.blue(px) + 0.11 * (double) Color.green(px));
                    gray = cumu[gray] * 255 / N;
                    pixels[i] = Color.argb(Color.alpha(px), gray, gray, gray);
                    break;
            }

        }
        bmp.setPixels(pixels, 0, bmp.getWidth(), 0, 0, bmp.getWidth(), bmp.getHeight());
    }

    /**
     * See {@link #histogramFlattening(Bitmap, Picture.Histogram, int[])} method
     *
     * @param p    Picture
     * @param type Histogram type to egalize.
     */
    public static void histogramFlattening(Picture p, Picture.Histogram type) {
        histogramFlattening(p.getBitmap(), type, p.getHistogram(type));
    }
}