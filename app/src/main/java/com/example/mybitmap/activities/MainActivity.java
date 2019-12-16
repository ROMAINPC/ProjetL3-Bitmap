package com.example.mybitmap.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.renderscript.RenderScript;

import com.example.mybitmap.R;
import com.example.mybitmap.imageprocessing.Picture;

public class MainActivity extends AppCompatActivity {

    ////////////////////// choose here picture to load ///////////////////
    private static final int PICTURE = R.drawable.grande;
    private static final int WIDTH = 0;
    private static final int HEIGHT = 1080;
    private static final int SAMPLE_SIZE = 200;


    // private Bitmap bitmap;
    private Picture picture;
    private Picture pictureSample;
    private ImageView iv;
    RenderScript rs;

    private FrameLayout underLayout;
    private View effectsLayout;
    private View effectSettingsLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Load picture:
        picture = new Picture(getApplicationContext(), PICTURE, WIDTH, HEIGHT);
        iv = findViewById(R.id.imageView);
        iv.setImageBitmap(picture.getBitmap());

        //load bad quality picture for previews:
        //pictureSample = new Picture(picture, SAMPLE_SIZE, 0);
        pictureSample = new Picture(getApplicationContext(), PICTURE, SAMPLE_SIZE, 0);


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

        //draw dimensions:
        TextView tV = underLayout.findViewById(R.id.dimensionsLabel);
        String text = "Dimensions : " + picture.getWidth() + " x " + picture.getHeight() + " (dp)";
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
        //Change layout:
        underLayout.removeAllViews();
        underLayout.addView(effectSettingsLayout);
    }

    /**
     * Click handler for back button in effects settings.
     *
     * @param v
     */
    public void clickBack(View v) {
        //Change layout:
        underLayout.removeAllViews();
        underLayout.addView(effectsLayout);
    }

    /**
     * Click handler for apply button in effects settings.
     *
     * @param v
     */
    public void clickApply(View v) {
        //Change layout:
        underLayout.removeAllViews();
        underLayout.addView(effectsLayout);
    }

    @Override
    protected void onStop() {
        super.onStop();
        rs.destroy();
    }


}



