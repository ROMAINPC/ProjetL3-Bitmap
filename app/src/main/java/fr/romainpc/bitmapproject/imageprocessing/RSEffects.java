package fr.romainpc.bitmapproject.imageprocessing;


import fr.romainpc.bitmapproject.imageprocessing.rsclass.ScriptC_gray;
import fr.romainpc.bitmapproject.imageprocessing.rsclass.ScriptC_hue;
import fr.romainpc.bitmapproject.imageprocessing.rsclass.ScriptC_hue_shift;
import fr.romainpc.bitmapproject.imageprocessing.rsclass.ScriptC_keep_color;

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
     * @param picture  Picture to modify
     * @param hueAngle Hue value, represented by an angle on the hue wheel [0;360]
     */
    public static void colorize(Picture picture, int hueAngle) {
        if (picture.getRenderScript() == null)
            return;
        allocate(picture);

        script = new ScriptC_hue(picture.getRenderScript());
        ((ScriptC_hue) script).set_hueAngle(hueAngle);
        ((ScriptC_hue) script).forEach_hue(input, output);
        output.copyTo(picture.getBitmap());

        destroy();
    }

    /**
     * Apply effect on the bitmap picture passed in parameter: Translate the hue of pixels.
     *
     * @param picture  Picture to modify
     * @param hueShift Hue value, represented by an angle on the hue wheel [0;360]
     */
    public static void colorShift(Picture picture, int hueShift) {
        if (picture.getRenderScript() == null)
            return;
        allocate(picture);


        script = new ScriptC_hue_shift(picture.getRenderScript());
        ((ScriptC_hue_shift) script).set_hueShift(hueShift);
        ((ScriptC_hue_shift) script).forEach_hue_shift(input, output);
        output.copyTo(picture.getBitmap());

        destroy();
    }

    /**
     * Apply effect on the bitmap picture passed in parameter: Only conserve specific colors , other colors become gray by minimise saturation.
     *
     * @param picture        Picture to modify
     * @param hueAngle       Hue value to keep, represented by an angle on the hue wheel [0;360]
     * @param toleranceAngle Colors in range "hueAngle" +/- this angle are kept.
     */
    public static void keepColor(Picture picture, float hueAngle, float toleranceAngle) {
        if (picture.getRenderScript() == null)
            return;
        allocate(picture);

        script = new ScriptC_keep_color(picture.getRenderScript());
        ((ScriptC_keep_color) script).set_hueAngle(hueAngle);
        ((ScriptC_keep_color) script).set_toleranceAngle(toleranceAngle);
        ((ScriptC_keep_color) script).forEach_keep_color(input, output);
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

