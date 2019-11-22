package com.example.mybitmap.imageprocessing;

import android.graphics.Bitmap;
import  android.renderscript.Allocation;
import android.renderscript.RenderScript;

/**
 * Class with methods to apply effects on Bitmap pictures using Renderscript library.
 *
 * @see android.graphics.Bitmap
 */
public class RSEffects {
    /**
     * Apply effect on the bitmap picture passed in parameter: put the picture in gray level, using
     * this formule : Gray = 0.3 * RED + 0.59 * BLUE + 0.11 * GREEN
     *
     * @param bmp
     */
    public static void grayLevel(Bitmap bmp) {


    }

    /**
     * See {@link #grayLevel(Bitmap)} method
     */
    public static void grayLevel(Picture p) {
        grayLevel(p.getBitmap());
    }
}
