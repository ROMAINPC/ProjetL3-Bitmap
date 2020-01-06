package fr.romainpc.bitmapproject.imageprocessing;

import android.graphics.Color;
import android.util.Log;

/**
 * Class with tools for image processing.
 */
public class Utils {


    /**
     * Tool to convert a HSV color contain in a float array to a RGB int value.
     * Also need alpha value, transmitted to the RGB color.
     * hsv[0] -> H:[0;360]
     * hsv[1] -> S:[0;1]
     * hsv[2] -> V:[0;1]
     *
     * @param hsv
     * @param alpha
     * @return the ARGB color.
     * @see android.graphics.Color
     */
    public static int HSVToColor(float[] hsv, int alpha) {
        //use conversion describe at https://www.rapidtables.com/convert/color/hsv-to-rgb.html
        int t = (int) ((hsv[0] / 60f) % 6);
        float C = hsv[1] * hsv[2];
        float X = C * (1 - Math.abs(((hsv[0] / 60) % 2) - 1));
        float m = hsv[2] - C;

        float r = 0;
        float g = 0;
        float b = 0;

        switch (t) {
            case 0:
                r = C;
                g = X;
                b = 0;
                break;
            case 1:
                r = X;
                g = C;
                b = 0;
                break;
            case 2:
                r = 0;
                g = C;
                b = X;
                break;
            case 3:
                r = 0;
                g = X;
                b = C;
                break;
            case 4:
                r = X;
                g = 0;
                b = C;
                break;
            case 5:
                r = C;
                g = 0;
                b = X;
                break;
        }
        float r2 = (r + m) * 255;
        float g2 = (g + m) * 255;
        float b2 = (b + m) * 255;
        return Color.argb(alpha, (int) r2, (int) g2, (int) b2);
    }

    /**
     * Tool to convert RGB Color to HSV Color contain in a float array.
     * NB: Doesn't use alpha value.
     *
     * @param red   Red integer value in [0;255]
     * @param green Green integer value in [0;255]
     * @param blue  Blue integer value in [0;255]
     * @param hsv   Modified, contain at the end the HSV value : hsv[0] -> H:[0;360] / hsv[1] -> S:[0;1] / hsv[2] -> V:[0;1]
     */
    public static void RGBToHSV(int red, int green, int blue, float[] hsv) {
        //use conversion describe at https://fr.wikipedia.org/wiki/Teinte_Saturation_Valeur
        float r = red / 255f;
        float g = green / 255f;
        float b = blue / 255f;

        float max = Math.max(Math.max(r, g), b);
        float min = Math.min(Math.min(r, g), b);

        if (max == min) {
            hsv[0] = 0;
        } else if (max == r) {
            hsv[0] = (60 * ((g - b) / (max - min)) + 360);
            while (hsv[0] > 360) hsv[0] -= 360;
        } else if (max == g) {
            hsv[0] = (60 * ((b - r) / (max - min)) + 120);
        } else if (max == b) {
            hsv[0] = (60 * ((r - g) / (max - min)) + 240);
        }

        if (max == 0) {
            hsv[1] = 0;
        } else {
            hsv[1] = 1 - (min / max);
        }

        hsv[2] = max;

    }

    /**
     * Function to calculate integer to load sample of an image.
     * If required width or height is set to zero, the value returned will be 1.
     *
     * @param originalWidth  Width of the orignal picture.
     * @param originalHeight Height of the orignal picture.
     * @param reqWidth       The desired width for the sample.
     * @param reqHeight      The desired width for the sample.
     * @return An integer X corresponding to the smallest power of 2 keeping size inferior or equal to the required size, where it takes X*X pixels to make 1 pixel in the sample.
     */
    public static int calculateInSampleSize(int originalWidth, int originalHeight,
                                            int reqWidth, int reqHeight) {
        if (reqWidth <= 0 || reqHeight <= 0)
            return 1;
        // Raw height and width of image
        final int height = originalHeight;
        final int width = originalWidth;
        int inSampleSize = 1;

        // Found smallest SampleSize value which keep both with and height inferior of required size.

        while ((height / inSampleSize) >= reqHeight
                || (width / inSampleSize) >= reqWidth) {
            inSampleSize *= 2;
        }

        return inSampleSize;
    }

    /**
     * Compute minimum and maximum value in an histogram array.
     *
     * @param histogram
     * @return An array with minimum value at index 0 and maximum at index 1.
     */
    public static int[] getHistogramMinMaxValue(int[] histogram) {
        int min;
        int max;

        int i = 0;
        while (histogram[i] == 0) {
            i++;
        }
        min = i;
        i = histogram.length - 1;
        while (histogram[i] == 0) {
            i--;
        }
        max = i;
        return new int[]{min, max};
    }

    /**
     * Just prints values in the histogram separated by commas, use it to show your histogram in a tierce software.
     * Also prints total of pixels.
     *
     * @param histogram Histogram to print.
     */
    public static void printHistogram(int[] histogram) {
        int total = 0;
        StringBuilder str = new StringBuilder();
        for (int val : histogram) {
            total += val;
            str.append(val+",");
        }
        Log.v("Histogram", String.valueOf(str));
        Log.v("Histogram", "Total pixels: " + total);
    }

}
