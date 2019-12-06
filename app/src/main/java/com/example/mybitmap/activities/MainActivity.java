package com.example.mybitmap.activities;

import android.content.Context;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.renderscript.Allocation;
import androidx.renderscript.RenderScript;

import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mybitmap.R;
import com.example.mybitmap.imageprocessing.Effects;
import com.example.mybitmap.imageprocessing.Picture;
import com.example.mybitmap.imageprocessing.RSEffects;
import com.example.mybitmap.imageprocessing.rsclass.ScriptC_gray;

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
        FrameLayout under = findViewById(R.id.underPicture);
        View layout = View.inflate(this,R.layout.effects_list, null);
        under.addView(layout);

        //draw dimensions:
        TextView tV = under.findViewById(R.id.dimensionsLabel);
        String text = "Dimensions : " + picture.getWidth() + " x " + picture.getHeight() + " (dp)";
        tV.setText(text);


        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
/*
                RelativeLayout llDisplayData;
                LinearLayout llChangeBG;
                TextView tvtxt;

                llDisplayData = (RelativeLayout) findViewById(R.id.underPicture);
                LayoutInflater linflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View customView;

                llDisplayData.removeAllViews();

                customView = linflater.inflate(R.layout.effects_list, null);




                llDisplayData.removeAllViews();*/
            }
        });







/*
        //Listeners:
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

    protected void onStop() {
        super.onStop();
        rs.destroy();
    }


}



