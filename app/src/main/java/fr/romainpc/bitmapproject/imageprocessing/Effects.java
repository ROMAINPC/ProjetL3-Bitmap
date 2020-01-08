package fr.romainpc.bitmapproject.imageprocessing;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;


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
        HUE_SHIFT,
        KEEP_COLOR,
        LINEAR_EXTENSION,
        FLATTENING,
        SIMPLE_BLURRING
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
            pixels[i] = Color.argb(Color.alpha(px), gray, gray, gray); //Also limits values if out of [0:255]
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
        hueOperation(bmp, hueAngle, false);
    }

    /**
     * See {@link #colorize(Bitmap, int)} method
     */
    public static void colorize(Picture p, int hueAngle) {
        colorize(p.getBitmap(), hueAngle);
    }

    /**
     * Apply effect on the bitmap picture passed in parameter: Translate the hue of pixels.
     *
     * @param bmp      Bitmap
     * @param hueShift Hue value, represented by an angle on the hue wheel [0;360]
     */
    public static void colorShift(Bitmap bmp, int hueShift) {
        hueOperation(bmp, hueShift, true);
    }

    /**
     * See {@link #colorShift(Bitmap, int)} method
     */
    public static void colorShift(Picture p, int hueAngle) {
        colorShift(p.getBitmap(), hueAngle);
    }

    /**
     * See {@link #colorShift(Bitmap, int)} and See {@link #colorize(Bitmap, int)}
     *
     * @param bmp      Bitmap to modify
     * @param hueAngle hue shift or choice
     * @param shift    Shift hue or just replace it
     */
    private static void hueOperation(Bitmap bmp, int hueAngle, boolean shift) {
        int[] pixels = new int[bmp.getWidth() * bmp.getHeight()];
        bmp.getPixels(pixels, 0, bmp.getWidth(), 0, 0, bmp.getWidth(), bmp.getHeight());
        float hue = (float) hueAngle;
        for (int i = 0; i < pixels.length; i++) {
            int px = pixels[i];
            float[] hsv = new float[3];
            Utils.RGBToHSV(Color.red(px), Color.green(px), Color.blue(px), hsv);
            hsv[0] = shift ? hsv[0] + hue : hue;
            pixels[i] = Utils.HSVToColor(Color.alpha(px), hsv); // already limit values out of ranges.
        }
        bmp.setPixels(pixels, 0, bmp.getWidth(), 0, 0, bmp.getWidth(), bmp.getHeight());
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
            pixels[i] = Utils.HSVToColor(Color.alpha(px), hsv);
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
     * @param bmp        Bitmap
     * @param type       The type of the histogram.
     * @param histograms The list of histogram arrays.
     */
    public static void linearDynamicExtension(Bitmap bmp, Picture.Histogram type, List<int[]> histograms) {
        ArrayList<int[]> LUTs = new ArrayList<>();
        for (int[] histogram : histograms) {
            //make LUT:
            int[] LUT = new int[histogram.length];
            int[] extr = Utils.getHistogramMinMaxValue(histogram);
            int min = extr[0];
            int max = extr[1];
            if (min == max)// if bitmap is uniform there will be a division by zero, to avoid it and because there should be no visual effect, the algorithm is skipped.
                return;
            for (int i = 0; i < histogram.length; i++)
                LUT[i] = 255 * (i - min) / (max - min);
            LUTs.add(LUT);
        }
        //apply LUT:
        int[] pixels = new int[bmp.getWidth() * bmp.getHeight()];
        bmp.getPixels(pixels, 0, bmp.getWidth(), 0, 0, bmp.getWidth(), bmp.getHeight());


        if (type == Picture.Histogram.LUMINANCE) {


            int[] LUT = LUTs.get(0);
            for (int i = 0; i < pixels.length; i++) {
                int px = pixels[i];
                float[] hsv = new float[3];
                Utils.RGBToHSV(Color.red(px), Color.green(px), Color.blue(px), hsv);
                hsv[2] = (float) LUT[(int) (hsv[2] * 255f)] / 255f;
                pixels[i] = Utils.HSVToColor(Color.alpha(px), hsv);
            }


        } else if (type == Picture.Histogram.GRAY_LEVEL_NATURAL) {


            int[] LUT = LUTs.get(0);
            for (int i = 0; i < pixels.length; i++) {
                int px = pixels[i];
                int gray = (int) (0.3 * (double) Color.red(px) + 0.59 * (double) Color.blue(px) + 0.11 * (double) Color.green(px));
                gray = LUT[gray];
                pixels[i] = Color.argb(Color.alpha(px), gray, gray, gray);
            }


        } else if (type == Picture.Histogram.RGB) {
            int[] LUTR = LUTs.get(0);
            int[] LUTG = LUTs.get(1);
            int[] LUTB = LUTs.get(2);
            for (int i = 0; i < pixels.length; i++) {
                int px = pixels[i];
                pixels[i] = Color.argb(Color.alpha(px), LUTR[Color.red(px)], LUTG[Color.green(px)], LUTB[Color.blue(px)]);
            }
        } else {
            return;
        }


        bmp.setPixels(pixels, 0, bmp.getWidth(), 0, 0, bmp.getWidth(), bmp.getHeight());
    }

    /**
     * See {@link #linearDynamicExtension(Bitmap, Picture.Histogram, List)} method
     *
     * @param p    Picture
     * @param type Histogram type to egalize.
     */
    public static void linearDynamicExtension(Picture p, Picture.Histogram type) {
        linearDynamicExtension(p.getBitmap(), type, p.getHistograms(type));
    }

    /**
     * Flatten the histogram
     *
     * @param bmp        Bitmap
     * @param type       The type of the histogram.
     * @param histograms The list of histogram arrays.
     */
    public static void histogramFlattening(Bitmap bmp, Picture.Histogram type, List<int[]> histograms) {
        //Compute cumulated histograms:
        ArrayList<long[]> cumus = new ArrayList<>();
        for (int[] histogram : histograms) {
            long[] cumu = new long[histogram.length]; //use long because with multiplication by 255 than can overflow the integer size.
            long sum = 0;
            for (int i = 0; i < histogram.length; i++) {
                sum += histogram[i];
                cumu[i] = sum;
            }
            cumus.add(cumu);
        }

        //apply LUT:
        int[] pixels = new int[bmp.getWidth() * bmp.getHeight()];
        bmp.getPixels(pixels, 0, bmp.getWidth(), 0, 0, bmp.getWidth(), bmp.getHeight());
        int N = pixels.length;

        if (type == Picture.Histogram.LUMINANCE) {


            long[] cumu = cumus.get(0);
            for (int i = 0; i < N; i++) {
                int px = pixels[i];
                float[] hsv = new float[3];
                Utils.RGBToHSV(Color.red(px), Color.green(px), Color.blue(px), hsv);
                hsv[2] = (float) ((int) (cumu[(int) (hsv[2] * 255f)] * 255 / N)) / 255f;
                pixels[i] = Utils.HSVToColor(Color.alpha(px), hsv);
            }


        } else if (type == Picture.Histogram.GRAY_LEVEL_NATURAL) {


            long[] cumu = cumus.get(0);
            for (int i = 0; i < N; i++) {
                int px = pixels[i];
                int gray = (int) (0.3 * (double) Color.red(px) + 0.59 * (double) Color.blue(px) + 0.11 * (double) Color.green(px));// Decolorize the bitmap.
                gray = (int) (cumu[gray] * 255) / N;
                pixels[i] = Color.argb(Color.alpha(px), gray, gray, gray);
            }


        } else if (type == Picture.Histogram.RGB) {
            long[] cumuR = cumus.get(0);
            long[] cumuG = cumus.get(1);
            long[] cumuB = cumus.get(2);
            for (int i = 0; i < pixels.length; i++) {
                int px = pixels[i];
                pixels[i] = Color.argb(Color.alpha(px), (int) ((cumuR[Color.red(px)] * 255L) / N), (int) ((cumuG[Color.green(px)] * 255L) / N), (int) ((cumuB[Color.blue(px)] * 255L) / N));
            }
        } else {
            return;
        }

        bmp.setPixels(pixels, 0, bmp.getWidth(), 0, 0, bmp.getWidth(), bmp.getHeight());
    }

    /**
     * See {@link #histogramFlattening(Bitmap, Picture.Histogram, List)} method
     *
     * @param p    Picture
     * @param type Histogram type to egalize.
     */
    public static void histogramFlattening(Picture p, Picture.Histogram type) {
        histogramFlattening(p.getBitmap(), type, p.getHistograms(type));
    }

    /**
     * Apply simple blurring ont the image.
     *
     * @param bmp       Bitmap
     * @param intensity Size of the convolution kernel, minimum 1, maximum : depends on the power of your hardware.
     */
    public static void simpleBlurr(Bitmap bmp, int intensity) {
        intensity = intensity > 0 ? intensity : 1;
        if (intensity % 2 == 0)
            intensity--;


        float[][] kernel = new float[intensity][intensity];
        for (int i = 0; i < intensity; i++)
            for (int j = 0; j < intensity; j++)
                kernel[i][j] = 1f / (intensity * intensity);

        convolute(bmp, kernel);

    }

    /**
     * See {@link #simpleBlurr(Bitmap, int)} method
     *
     * @param p Picture to modify
     */
    public static void simpleBlurr(Picture p, int intensity) {
        System.out.println(intensity);
        simpleBlurr(p.getBitmap(), intensity);
    }


    /**
     * Apply convolution effect on the Bitmap by ponderate values around each pixels by following weights in the kernel array.
     *
     * @param bmp    Bitmap to convolute
     * @param kernel Constrainsts: square odd array && sum of each values == 1.0
     */
    private static void convolute(Bitmap bmp, float[][] kernel) {
        //float[x][y], y-down, x-right
        //security:
        int width = kernel.length;
        boolean ok = width > 0 ? true : false;
        for (int i = 0; i < width; i++)
            if (kernel[i].length != width) {
                ok = false;
                i = width;
            }
        if (!ok || width % 2 == 0) {
            Log.e("Convolution", "Invalid kernel size");
            return;
        }
        /*
        float sum = 0f;
        for (int i = 0; i < width; i++)
            for (int j = 0; j < width; j++)
                sum += kernel[i][j];


        if (sum != 1f) {
            Log.e("Convolution", "Invalid kernel values with sum of " + sum);
            return;
        }*/

        //apply kernel on bitmap:
        int[] pixels = new int[bmp.getWidth() * bmp.getHeight()];
        bmp.getPixels(pixels, 0, bmp.getWidth(), 0, 0, bmp.getWidth(), bmp.getHeight());
        int N = pixels.length;
        int hW = width / 2; //half-width kernel

        for (int p = 0; p < N; p++) {

            //kernel average:
            int r = 0, g = 0, b = 0;
            int X = p % bmp.getWidth();
            int Y = p / bmp.getWidth();
            if (X >= hW && X < bmp.getWidth() - hW && Y >= hW && Y < bmp.getHeight() - hW) { //does't do borders of picture.
                for (int i = -hW; i <= hW; i++) {
                    for (int j = -hW; j <= hW; j++) {
                        int coord = (Y + j) * bmp.getWidth() + (X + i);
                        r += Color.red(pixels[coord]) * kernel[i + hW][j + hW];
                        g += Color.green(pixels[coord]) * kernel[i + hW][j + hW];
                        b += Color.blue(pixels[coord]) * kernel[i + hW][j + hW];

                    }
                }
            }

            pixels[p] = Color.argb(Color.alpha(pixels[p]), r, g, b);

        }

        bmp.setPixels(pixels, 0, bmp.getWidth(), 0, 0, bmp.getWidth(), bmp.getHeight());


    }


}
