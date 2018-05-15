package com.bittle.colorpicker.Dialogs;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bittle.colorpicker.ConvertMainActivity;
import com.bittle.colorpicker.ImagePickerMainActivity;
import com.bittle.colorpicker.R;

import com.bittle.colorpicker.utils.ColorUtil;
import com.bittle.colorpicker.utils.ImageUtil;
import com.bittle.colorpicker.utils.StringUtil;

public class ColorInfoDialog extends Activity {
    ImageUtil imageUtil;
    ColorUtil colorUtil = new ColorUtil();

    private final float BORDER_WIDTH = 70.0f;

    private static String allTextViewsTogether;
    public static boolean isActive = true;
    String averageColor = "";

    Context mainContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_color_info_dialog);

        imageUtil = new ImageUtil(this);
        allTextViewsTogether = "";
        mainContext = this;

        Intent i = getIntent();
        if (i.hasExtra("average")) {
            averageColor = i.getStringExtra("average");
        }

        setTheImageViews();
    }

    public void setTheImageViews() {
        ImageView mainColorImageView = (ImageView) findViewById(R.id.mainColorImageView);

        final int mainColor = ImagePickerMainActivity.getCurrentColor();
        Bitmap pic = imageUtil.colorToBitmap_CRISP(mainColor);
        pic = imageUtil.cropToCircleWithBorder(pic, Color.BLACK, BORDER_WIDTH);
        mainColorImageView.setImageBitmap(pic);

        TextView currentHexTextView = (TextView) findViewById(R.id.currentHexTextViewDialog);
        String st = ("#" + colorUtil.colorToHex(mainColor));
        currentHexTextView.setText(st);

        if(st.equals("##0")){
            finish();
        }

        currentHexTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setVisible(false);
                switchActivities(mainColor);
                finish();
            }
        });

        mainColorImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StringUtil.copyToClipboard(colorUtil.colorToHex(mainColor), mainContext);
            }
        });

        allTextViewsTogether +="Touched Color\n"+currentHexTextView.getText().toString()+"\n\n";
        setTheOptions(mainColor);
        allTextViewsTogether = allTextViewsTogether.trim();

        if(((TextView) findViewById(R.id.averageTextViewDialog))
                .getText().toString().equals("##0")){
            finish();
        }
    }

    private void setTheOptions(int mainColor) {
        setLighterOptions(mainColor);
        setDarkerOptions(mainColor);
        setInvertedOptions(mainColor);
        setDominantOptions();
        setAverageOptions();
    }

    private void setLighterOptions(int mainColor) {

        ImageView lighterImageView = (ImageView) findViewById(R.id.lighterImageViewDialog);

        final int lighterColor = colorUtil.lightenColor(mainColor, 0.25);
        Bitmap colorBitmap = imageUtil.colorToBitmap_CRISP(lighterColor);

        colorBitmap = imageUtil.cropToCircleWithBorder(colorBitmap, Color.BLACK, BORDER_WIDTH);
        lighterImageView.setImageBitmap(colorBitmap);

        TextView lightTextView = (TextView) findViewById(R.id.lighterTextViewDialog);
        String text =  lightTextView.getText().toString() + "\n#" + colorUtil.colorToHex(lighterColor);
        lightTextView.setText(text);

        lightTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setVisible(false);
                switchActivities(lighterColor);
                finish();
            }
        });

        lighterImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StringUtil.copyToClipboard(colorUtil.colorToHex(lighterColor), mainContext);
            }
        });

        allTextViewsTogether += lightTextView.getText().toString()+"\n\n";
    }

    private void setDarkerOptions(int mainColor) {

        ImageView darkerImageView = (ImageView) findViewById(R.id.darkerImageViewDialog);

        final int darkerColor = colorUtil.darkenColor(mainColor, 0.25);
        Bitmap colorBitmap = imageUtil.colorToBitmap_CRISP(darkerColor);

        colorBitmap = imageUtil.cropToCircleWithBorder(colorBitmap, Color.BLACK, BORDER_WIDTH);
        darkerImageView.setImageBitmap(colorBitmap);

        TextView darkTextView = (TextView) findViewById(R.id.darkerTextViewDialog);
        String text = darkTextView.getText().toString() + "\n#" + colorUtil.colorToHex(darkerColor);
        darkTextView.setText(text);


        darkTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setVisible(false);
                switchActivities(darkerColor);
                finish();
            }
        });

        darkerImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StringUtil.copyToClipboard(colorUtil.colorToHex(darkerColor), mainContext);
            }
        });

        allTextViewsTogether += darkTextView.getText().toString()+"\n\n";
    }

    private void setInvertedOptions(final int mainColor) {

        ImageView invertedImageView = (ImageView) findViewById(R.id.invertedImageViewDialog);

        final int invertedColor = colorUtil.invertColor(mainColor);
        Bitmap colorBitmap = imageUtil.colorToBitmap_CRISP(invertedColor);

        colorBitmap = imageUtil.cropToCircleWithBorder(colorBitmap, Color.BLACK, BORDER_WIDTH);
        invertedImageView.setImageBitmap(colorBitmap);

        TextView invertedTextView = (TextView) findViewById(R.id.invertedTextViewDialog);
        String text = invertedTextView.getText().toString() + "\n#" + colorUtil.colorToHex(invertedColor);
        invertedTextView.setText(text);

        invertedTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setVisible(false);
                switchActivities(invertedColor);
                finish();
            }
        });

        invertedImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StringUtil.copyToClipboard(colorUtil.colorToHex(invertedColor), mainContext);
            }
        });

        allTextViewsTogether += invertedTextView.getText().toString()+"\n\n";
    }

    private void setDominantOptions() {
        ImageView domView = (ImageView) findViewById(R.id.dominantImageViewDialog);

        final int domColor = ImagePickerMainActivity.getDominantColor();

        Bitmap colorBitmap = imageUtil.colorToBitmap_CRISP(domColor);
        colorBitmap = imageUtil.cropToCircleWithBorder(colorBitmap, Color.BLACK, BORDER_WIDTH);
        domView.setImageBitmap(colorBitmap);

        TextView textView = (TextView) findViewById(R.id.dominantTextViewDialog);
        String text= textView.getText().toString() + "\n#" + colorUtil.colorToHex(domColor);
        textView.setText(text);

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setVisible(false);
                switchActivities(domColor);
                finish();
            }
        });

        domView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StringUtil.copyToClipboard(colorUtil.colorToHex(domColor), mainContext);
            }
        });

        allTextViewsTogether += textView.getText().toString()+"\n\n";
    }

    private void setAverageOptions(){
        try {
            final ImageView avView = (ImageView) findViewById(R.id.averageImageViewDialog);
            final int aColor = colorUtil.hexToColor(averageColor);

            Bitmap colorBitmap = imageUtil.colorToBitmap_CRISP(aColor);
            colorBitmap = imageUtil.cropToCircleWithBorder(colorBitmap, Color.BLACK, BORDER_WIDTH);
            avView.setImageBitmap(colorBitmap);

            TextView textView = (TextView) findViewById(R.id.averageTextViewDialog);
            String text = textView.getText().toString() + "\n#" + colorUtil.colorToHex(aColor);
            textView.setText(text);

            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    setVisible(false);
                    switchActivities(aColor);
                    finish();
                }
            });

            avView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    StringUtil.copyToClipboard(colorUtil.colorToHex(aColor), mainContext);
                }
            });

            allTextViewsTogether += textView.getText().toString() + "\n\n";
        }catch (Exception e){
            Log.e("ERROR", "ColorInfoDialog->Average color method");
        }
    }

    public void switchActivities(int c) {
        Intent intent = new Intent(ColorInfoDialog.this, ConvertMainActivity.class);
        intent.putExtra("color", colorUtil.colorToHex(c));
        startActivity(intent);
    }

}
