package fr.romainpc.bitmapproject.imageprocessing;


import fr.romainpc.bitmapproject.imageprocessing.rsclass.ScriptC_gray;

import androidx.renderscript.Allocation;
import androidx.renderscript.ScriptC;

/**
 * Class with methods to apply effects on Pictures using Renderscript library.
 *
 * @see android.graphics.Bitmap
 */
public class RSEffects {


    public enum RSEffect {
        GRAY_LEVEL
    }

    /**
     * Apply the selected effect on a Picture by using RenderScript.
     * An instance of RenderScript must be passed to the picture before, else this function do nothing.
     *
     * @param picture
     * @param effectType
     */
    public static void effect(Picture picture, RSEffect effectType) {
        if (picture.getRenderScript() == null)
            return;
        Allocation input = Allocation.createFromBitmap(picture.getRenderScript(), picture.getBitmap());
        Allocation output = Allocation.createTyped(picture.getRenderScript(), input.getType());
        ScriptC script = null;
        switch (effectType) {
            case GRAY_LEVEL:
                script = new ScriptC_gray(picture.getRenderScript());
                ((ScriptC_gray) script).forEach_gray(input, output);
                break;
        }
        output.copyTo(picture.getBitmap());
        input.destroy();
        output.destroy();
        script.destroy();

    }


}

