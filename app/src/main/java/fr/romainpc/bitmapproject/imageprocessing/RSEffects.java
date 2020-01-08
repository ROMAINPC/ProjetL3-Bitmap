package fr.romainpc.bitmapproject.imageprocessing;


import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;

import fr.romainpc.bitmapproject.imageprocessing.rsclass.ScriptC_gray;
import fr.romainpc.bitmapproject.imageprocessing.rsclass.ScriptC_hue;

import androidx.renderscript.Allocation;
import androidx.renderscript.ScriptC;

/**
 * Class with methods to apply effects on Pictures using Renderscript library.
 *
 * @see android.graphics.Bitmap
 * @see Picture
 */
public class RSEffects {

    private static Allocation input;
    private static Allocation output;
    private static ScriptC script;


    /**
     * Apply effect on Picture passed in parameter: put the picture in gray level.
     * Adjust red green and blue value to adjust which of them will impact the more the gray level.
     *
     * @param picture Picture to modify
     * @param red     Red proportion (between 0.0 and 1.0)
     * @param green   Green proportion (between 0.0 and 1.0)
     * @param blue    Blue proportion (between 0.0 and 1.0)
     */
    public static void grayLevel(Picture picture, float red, float green, float blue) {
        if (picture.getRenderScript() == null)
            return;
        allocate(picture);

        script = new ScriptC_gray(picture.getRenderScript());
        ((ScriptC_gray) script).set_redWeight(red);
        ((ScriptC_gray) script).set_greenWeight(green);
        ((ScriptC_gray) script).set_blueWeight(blue);
        ((ScriptC_gray) script).forEach_gray(input, output);
        output.copyTo(picture.getBitmap());

        destroy();
    }

    /**
     * Apply effect on the bitmap picture passed in parameter: colorize the picture with the specified hue.
     *
     * @param picture Picture to modify
     * @param hueAngle Hue value, represented by an angle on the hue wheel [0;360]
     */
    public static void colorize(Picture picture, int hueAngle) {
        if (picture.getRenderScript() == null)
            return;
        allocate(picture);

        hueAngle = hueAngle % 360;

        script = new ScriptC_hue(picture.getRenderScript());
        ((ScriptC_hue) script).set_hueAngle(hueAngle);
        ((ScriptC_hue) script).forEach_hue(input, output);
        output.copyTo(picture.getBitmap());

        destroy();
    }

    /**
     * Instantiate and load Allocation for RenderScript in static variables.
     *
     * @param picture Picture to allocate
     */
    private static void allocate(Picture picture) {
        input = Allocation.createFromBitmap(picture.getRenderScript(), picture.getBitmap());
        output = Allocation.createTyped(picture.getRenderScript(), input.getType());
        script = null;
    }

    /**
     * Destroy RenderScript allocations in static variables.
     */
    private static void destroy() {
        input.destroy();
        output.destroy();
        script.destroy();
    }

}

