package fr.romainpc.bitmapproject.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.renderscript.RenderScript;

import fr.romainpc.bitmapproject.R;
import fr.romainpc.bitmapproject.imageprocessing.Effects;
import fr.romainpc.bitmapproject.imageprocessing.Picture;
import fr.romainpc.bitmapproject.imageprocessing.RSEffects;

/**
 * Starting point of the application, manage all components and listeners.
 */
public class MainActivity extends AppCompatActivity {

    ////////////////////// CHOOSE HERE PICTURE TO LOAD ///////////////////
    private static final int PICTURE = R.drawable.low_contrast;
    private static final int MAX_SIZE = 3072;
    private static final int SAMPLE_SIZE = 384;


    // private Bitmap bitmap;
    private Picture picture;
    private Picture pictureSample;
    private ImageView iv;
    RenderScript rs;

    private FrameLayout underLayout;
    private View effectsLayout;
    private View effectSettingsLayout;
    private Effects.EffectType currentEffect;
    private SeekBar sB1, sB2, sB3;
    private Switch switchRS;
    private boolean sliding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Load picture:
        picture = new Picture(getApplicationContext(), PICTURE, MAX_SIZE, MAX_SIZE);
        iv = findViewById(R.id.imageView);
        iv.setImageBitmap(picture.getBitmap());

        //load bad quality picture for previews:
        pictureSample = new Picture(picture, SAMPLE_SIZE, SAMPLE_SIZE);


        //UI and layouts:
        //default bottom layout:
        underLayout = findViewById(R.id.underPicture);
        effectsLayout = View.inflate(this, R.layout.effects_list, null);
        effectSettingsLayout = View.inflate(this, R.layout.effect_sliders, null);
        underLayout.removeAllViews();
        underLayout.addView(effectsLayout);
        currentEffect = null;

        //draw dimensions:
        TextView tV = underLayout.findViewById(R.id.dimensionsLabel);
        String text = "Dimensions : " + picture.getWidth() + " x " + picture.getHeight() + " px";
        tV.setText(text);

        //generateRenderScript
        rs = RenderScript.create(this);
        picture.setRenderScript(rs);
        pictureSample.setRenderScript(rs);
        switchRS = underLayout.findViewById(R.id.switch1);


        // Seekbar listeners:
        sB1 = effectSettingsLayout.findViewById(R.id.seekBar1);
        sB2 = effectSettingsLayout.findViewById(R.id.seekBar2);
        sB3 = effectSettingsLayout.findViewById(R.id.seekBar3);
        sliding = false;


        sB1.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (sliding) { //security to avoid double effect apply, because setSeekBars() also call this function.
                    pictureSample.quickLoad();
                    applyEffect(pictureSample, currentEffect, switchRS.isChecked());
                    iv.setImageBitmap(pictureSample.getBitmap());
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                sliding = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                sliding = false;
            }

        });
        sB2.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (sliding) {
                    pictureSample.quickLoad();
                    applyEffect(pictureSample, currentEffect, switchRS.isChecked());
                    iv.setImageBitmap(pictureSample.getBitmap());
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                sliding = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                sliding = false;
            }

        });
        sB3.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (sliding) {
                    pictureSample.quickLoad();
                    applyEffect(pictureSample, currentEffect, switchRS.isChecked());
                    iv.setImageBitmap(pictureSample.getBitmap());
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                sliding = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                sliding = false;
            }

        });


    }

    /**
     * Called when click on "Réinitialiser" button.
     */
    public void clickReset(View view) {
        picture.reset();
        pictureSample.reset();
        iv.setImageBitmap(picture.getBitmap()); // Needed for refresh pixels on UI.
    }

    /**
     * Click handler for effects buttons.
     *
     * @param v Button which call the method
     */
    public void onClickEffect(View v) {
        // assign seekbars:
        switch (v.getId()) {
            case R.id.bGray:
                currentEffect = Effects.EffectType.GRAY;
                setSeekBars(true, "Rouge", 100, true, "Vert", 100, true, "Bleu", 100);
                sB1.setProgress(30);
                sB2.setProgress(11);
                sB3.setProgress(59);
                break;
            case R.id.bColor:
                currentEffect = Effects.EffectType.KEEP_COLOR;
                setSeekBars(true, "Teinte:", 359, true, "Tolérance:", 179, false, "", 1);
                break;
            case R.id.bHue:
                currentEffect = Effects.EffectType.HUE;
                setSeekBars(true, "Teinte:", 359, false, "", 1, false, "", 1);
                break;
            case R.id.bTranslateHue:
                currentEffect = Effects.EffectType.HUE_SHIFT;
                setSeekBars(true, "Teinte:", 359, false, "", 1, false, "", 1);
                break;
            case R.id.bLinearContrast:
                currentEffect = Effects.EffectType.LINEAR_EXTENSION;
                setSeekBars(false, "", 1, false, "", 1, false, "", 1);
                break;
            case R.id.bFlatteningContrast:
                currentEffect = Effects.EffectType.FLATTENING;
                setSeekBars(false, "", 1, false, "", 1, false, "", 1);
                break;
        }
        pictureSample.quickSave();
        pictureSample.quickLoad();
        applyEffect(pictureSample, currentEffect, switchRS.isChecked());
        iv.setImageBitmap(pictureSample.getBitmap());

        //Change layout:
        underLayout.removeAllViews();
        underLayout.addView(effectSettingsLayout);
    }

    /**
     * Enter for each of the three Seekbars, a boolean to set it visible, the text to print near the Seekbar, and the range value.
     *
     * @param visible1 Visible 1
     * @param name1    Label 1
     * @param max1     Range 1
     * @param visible2 Visible 2
     * @param name2    Label 2
     * @param max2     Range 2
     * @param visible3 Visible 3
     * @param name3    Label 3
     * @param max3     Range 3
     */
    private void setSeekBars(boolean visible1, String name1, int max1, boolean visible2, String name2, int max2, boolean visible3, String name3, int max3) {
        TextView tV1 = effectSettingsLayout.findViewById(R.id.textView1);
        TextView tV2 = effectSettingsLayout.findViewById(R.id.textView2);
        TextView tV3 = effectSettingsLayout.findViewById(R.id.textView3);
        sB1.setVisibility(visible1 ? View.VISIBLE : View.INVISIBLE);
        sB1.setMax(max1);
        tV1.setText(name1);
        sB2.setVisibility(visible2 ? View.VISIBLE : View.INVISIBLE);
        sB2.setMax(max2);
        tV2.setText(name2);
        sB3.setVisibility(visible3 ? View.VISIBLE : View.INVISIBLE);
        sB3.setMax(max3);
        tV3.setText(name3);
    }


    /**
     * Click handler for back button in effects settings.
     *
     * @param v Button clicked
     */
    public void clickBack(View v) {
        //Change layout:
        pictureSample.quickLoad();
        iv.setImageBitmap(picture.getBitmap());
        underLayout.removeAllViews();
        underLayout.addView(effectsLayout);
        currentEffect = null;
    }

    /**
     * Click handler for apply button in effects settings.
     *
     * @param v Button clicked
     */
    public void clickApply(View v) {

        pictureSample.quickLoad();
        //Apply effect:
        applyEffect(picture, currentEffect, switchRS.isChecked());
        applyEffect(pictureSample, currentEffect, switchRS.isChecked());
        iv.setImageBitmap(picture.getBitmap());

        //Change layout:
        underLayout.removeAllViews();
        underLayout.addView(effectsLayout);
        currentEffect = null;
    }

    /**
     * Apply an effect on a Picture.
     *
     * @param picture       The picture to modify
     * @param currentEffect Effect type to apply
     * @param renderscript  Use or not RenderScript accélération.
     */
    private void applyEffect(Picture picture, Effects.EffectType currentEffect, boolean renderscript) {

        //Apply effect:
        switch (currentEffect) {
            case GRAY:
                if (renderscript)
                    RSEffects.grayLevel(picture, sB1.getProgress() / 100f, sB2.getProgress() / 100f, sB3.getProgress() / 100f);
                else
                    Effects.grayLevel(picture, sB1.getProgress() / 100.0, sB2.getProgress() / 100.0, sB3.getProgress() / 100.0);
                break;
            case HUE:
                Effects.colorize(picture, sB1.getProgress());
                break;
            case HUE_SHIFT:
                Effects.colorShift(picture, sB1.getProgress());
                break;
            case KEEP_COLOR:
                Effects.keepColor(picture, sB1.getProgress(), sB2.getProgress());
                break;
            case LINEAR_EXTENSION:
                Effects.linearDynamicExtension(picture, Picture.Histogram.RGB);
                break;
            case FLATTENING:
                Effects.histogramFlattening(picture, Picture.Histogram.RGB);
                break;
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        rs.destroy();
    }


}



