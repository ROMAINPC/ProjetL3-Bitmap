package com.example.mybitmap.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.renderscript.RenderScript;

import com.example.mybitmap.R;
import com.example.mybitmap.imageprocessing.Effects;
import com.example.mybitmap.imageprocessing.Picture;

public class MainActivity extends AppCompatActivity {

    ////////////////////// CHOOSE HERE PICTURE TO LOAD ///////////////////
    private static final int PICTURE = R.drawable.grande;
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

        //generateRenderScript
        rs = RenderScript.create(this);
        picture.setRenderScript(rs);
        pictureSample.setRenderScript(rs);


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




/*

        colorBar = findViewById(R.id.colorBar);
        colorBar.setMax(359);
        colorBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (!keepColor) {
                    pictureSample.reset();
                    Effects.keepColor(pictureSample, seekBar.getProgress(), toleranceBar.getProgress());
                }

            }

            public void onStartTrackingTouch(SeekBar seekBar) {
                if (!keepColor) {
                    iv.setImageBitmap(pictureSample.getBitmap());
                }

            }

            public void onStopTrackingTouch(SeekBar seekBar) {
                if (!keepColor) {
                    iv.setImageBitmap(picture.getBitmap());
                }

            }
        });

        toleranceBar = findViewById(R.id.toleranceBar);
        toleranceBar.setMax(179);
        toleranceBar.setProgress(30);
        toleranceBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (!keepColor) {
                    pictureSample.reset();
                    Effects.keepColor(pictureSample, colorBar.getProgress(), seekBar.getProgress());
                }
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
                if (!keepColor) {
                    iv.setImageBitmap(pictureSample.getBitmap());
                }
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
                if (!keepColor) {
                    iv.setImageBitmap(picture.getBitmap());
                }
            }
        });

        SeekBar hueBar = findViewById(R.id.hueBar);
        hueBar.setMax(359);
        hueBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {


            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (!colorize) {
                    pictureSample.reset();
                    Effects.colorize(pictureSample, progress);
                }
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
                if (!colorize) {
                    iv.setImageBitmap(pictureSample.getBitmap());
                }
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
                if (!colorize) {
                    Effects.colorize(picture, seekBar.getProgress());
                    iv.setImageBitmap(picture.getBitmap());
                    Effects.colorize(pictureSample, seekBar.getProgress());
                    pictureSample.quickSave();
                    colorize = true;
                }
            }
        });*/


    }
/*
    public void buttonClickReset(View view) {
        gray = false;
        keepColor = false;
        colorize = false;
        contrast = false;
        picture.reset();
        pictureSample.reload();
    }

    public void buttonClickGray(View view) {
        if (!gray) {
            Effects.grayLevel(picture);
            Effects.grayLevel(pictureSample);
            pictureSample.quickSave();
            gray = true;
        }
    }

    public void buttonClickApplyColor(View view) {
        if (!keepColor) {
            Effects.keepColor(picture, colorBar.getProgress(), toleranceBar.getProgress());
            Effects.keepColor(pictureSample, colorBar.getProgress(), toleranceBar.getProgress());
            pictureSample.quickSave();
            keepColor = true;
        }
    }


    public void buttonClickContrast(View view) {

        if (!contrast) {
            Picture.Histogram type = gray ? Picture.Histogram.GRAY_LEVEL_NATURAL : Picture.Histogram.LUMINANCE;
            Effects.histogramFlattening(picture, type);
            Effects.histogramFlattening(pictureSample, type);
            pictureSample.quickSave();
            contrast = true;
        }

    }*/

    /**
     * Click handler for effects buttons.
     *
     * @param v Button which call the method
     */
    public void onClickEffect(View v) {

        // assign seekbars:
        switch (v.getId()) {
            case R.id.bGray:
                setSeekBars(false, "", 100, false, "", 100, false, "", 100);
                currentEffect = Effects.EffectType.GRAY;
                break;
            case R.id.bColor:
                setSeekBars(true, "Teinte:", 359, true, "Tolérance:", 179, false, "", 100);
                currentEffect = Effects.EffectType.KEEP_COLOR;
                break;
            case R.id.bHue:
                setSeekBars(true, "Teinte:", 359, false, "", 100, false, "", 100);
                currentEffect = Effects.EffectType.HUE;
                break;
            case R.id.bLinearContrast:
                setSeekBars(false, "", 100, false, "", 100, false, "", 100);
                currentEffect = Effects.EffectType.LINEAR_EXTENSION;
                break;
            case R.id.bFlatteningContrast:
                setSeekBars(false, "", 100, false, "", 100, false, "", 100);
                currentEffect = Effects.EffectType.FLATTENING;
                break;
        }
        pictureSample.quickSave();
        iv.setImageBitmap(pictureSample.getBitmap());
        //Change layout:
        underLayout.removeAllViews();
        underLayout.addView(effectSettingsLayout);
    }

    private void setSeekBars(boolean visible1, String name1, int max1, boolean visible2, String name2, int max2, boolean visible3, String name3, int max3) {
        SeekBar sB1 = effectSettingsLayout.findViewById(R.id.seekBar1);
        TextView tV1 = effectSettingsLayout.findViewById(R.id.textView1);
        SeekBar sB2 = effectSettingsLayout.findViewById(R.id.seekBar2);
        TextView tV2 = effectSettingsLayout.findViewById(R.id.textView2);
        SeekBar sB3 = effectSettingsLayout.findViewById(R.id.seekBar3);
        TextView tV3 = effectSettingsLayout.findViewById(R.id.textView3);
        sB1.setVisibility(visible1 ? View.VISIBLE : View.INVISIBLE);
        sB1.setMax(max1);
        tV1.setText(name1);
        sB2.setVisibility(visible2 ? View.VISIBLE : View.INVISIBLE);
        sB1.setMax(max2);
        tV2.setText(name2);
        sB3.setVisibility(visible3 ? View.VISIBLE : View.INVISIBLE);
        sB1.setMax(max3);
        tV3.setText(name3);
    }


    /**
     * Click handler for back button in effects settings.
     *
     * @param v
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
     * @param v
     */
    public void clickApply(View v) {
        SeekBar sB1 = effectSettingsLayout.findViewById(R.id.seekBar1);
        SeekBar sB2 = effectSettingsLayout.findViewById(R.id.seekBar2);
        //SeekBar sB3 = effectSettingsLayout.findViewById(R.id.seekBar3);

        //Apply effect:
        switch (currentEffect) {
            case GRAY:
                Effects.grayLevel(picture);
                Effects.grayLevel(pictureSample);
                break;
            case HUE:
                Effects.colorize(picture, sB1.getProgress());
                Effects.colorize(pictureSample, sB1.getProgress());
                break;
            case KEEP_COLOR:
                Effects.keepColor(picture, sB1.getProgress(), sB2.getProgress());
                Effects.keepColor(pictureSample, sB1.getProgress(), sB2.getProgress());
                break;
            case LINEAR_EXTENSION:
                Effects.linearDynamicExtension(picture, Picture.Histogram.LUMINANCE);
                Effects.linearDynamicExtension(pictureSample, Picture.Histogram.LUMINANCE);
                break;
            case FLATTENING:
                Effects.histogramFlattening(picture, Picture.Histogram.LUMINANCE);
                Effects.histogramFlattening(pictureSample, Picture.Histogram.LUMINANCE);
                break;
        }
        pictureSample.quickLoad();
        iv.setImageBitmap(picture.getBitmap());

        //Change layout:
        underLayout.removeAllViews();
        underLayout.addView(effectsLayout);
        currentEffect = null;
    }

    @Override
    protected void onStop() {
        super.onStop();
        rs.destroy();
    }


}



