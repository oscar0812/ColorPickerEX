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

    private final float BORDER_WIDTH = 70.0f;

    private static String allTextViewsTogether;
    public static boolean isActive = true;
    String averageColor = "";

    Context mainContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_color_info_dialog);

        allTextViewsTogether = "";
        mainContext = this;

        Intent i = getIntent();
        if (i.hasExtra("average")) {
            averageColor = i.getStringExtra("average");
        }

        setTheImageViews();
    }

    public void setTheImageViews() {
        ImageView mainColorImageView = findViewById(R.id.mainColorImageView);

        final int mainColor = ImagePickerMainActivity.getCurrentColor();
        Bitmap pic = ImageUtil.getInstance(this).colorToBitmap_CRISP(mainColor);
        pic = ImageUtil.getInstance(this).cropToCircleWithBorder(pic, Color.BLACK, BORDER_WIDTH);
        mainColorImageView.setImageBitmap(pic);

        TextView currentHexTextView = findViewById(R.id.currentHexTextViewDialog);
        String st = ("#" + ColorUtil.colorToHex(mainColor));
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
                StringUtil.copyToClipboard(ColorUtil.colorToHex(mainColor), mainContext);
            }
        });

        allTextViewsTogether +="Touched ColorModel\n"+currentHexTextView.getText().toString()+"\n\n";
        setTheOptions(mainColor);
        allTextViewsTogether = allTextViewsTogether.trim();

        if(((TextView)(findViewById(R.id.averageTextViewDialog)))
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

        ImageView lighterImageView = findViewById(R.id.lighterImageViewDialog);

        final int lighterColor = ColorUtil.getInstance().lightenColor(mainColor, 0.25);
        Bitmap colorBitmap = ImageUtil.getInstance(this).colorToBitmap_CRISP(lighterColor);

        colorBitmap = ImageUtil.getInstance(this).cropToCircleWithBorder(colorBitmap, Color.BLACK, BORDER_WIDTH);
        lighterImageView.setImageBitmap(colorBitmap);

        TextView lightTextView = findViewById(R.id.lighterTextViewDialog);
        String text =  lightTextView.getText().toString() + "\n#" + ColorUtil.colorToHex(lighterColor);
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
                StringUtil.copyToClipboard(ColorUtil.colorToHex(lighterColor), mainContext);
            }
        });

        allTextViewsTogether += lightTextView.getText().toString()+"\n\n";
    }

    private void setDarkerOptions(int mainColor) {

        ImageView darkerImageView = findViewById(R.id.darkerImageViewDialog);

        final int darkerColor = ColorUtil.getInstance().darkenColor(mainColor, 0.25);
        Bitmap colorBitmap = ImageUtil.getInstance(this).colorToBitmap_CRISP(darkerColor);

        colorBitmap = ImageUtil.getInstance(this).cropToCircleWithBorder(colorBitmap, Color.BLACK, BORDER_WIDTH);
        darkerImageView.setImageBitmap(colorBitmap);

        TextView darkTextView = findViewById(R.id.darkerTextViewDialog);
        String text = darkTextView.getText().toString() + "\n#" + ColorUtil.colorToHex(darkerColor);
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
                StringUtil.copyToClipboard(ColorUtil.colorToHex(darkerColor), mainContext);
            }
        });

        allTextViewsTogether += darkTextView.getText().toString()+"\n\n";
    }

    private void setInvertedOptions(final int mainColor) {

        ImageView invertedImageView = findViewById(R.id.invertedImageViewDialog);

        final int invertedColor = ColorUtil.getInstance().invertColor(mainColor);
        Bitmap colorBitmap = ImageUtil.getInstance(this).colorToBitmap_CRISP(invertedColor);

        colorBitmap = ImageUtil.getInstance(this).cropToCircleWithBorder(colorBitmap, Color.BLACK, BORDER_WIDTH);
        invertedImageView.setImageBitmap(colorBitmap);

        TextView invertedTextView = findViewById(R.id.invertedTextViewDialog);
        String text = invertedTextView.getText().toString() + "\n#" + ColorUtil.colorToHex(invertedColor);
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
                StringUtil.copyToClipboard(ColorUtil.colorToHex(invertedColor), mainContext);
            }
        });

        allTextViewsTogether += invertedTextView.getText().toString()+"\n\n";
    }

    private void setDominantOptions() {
        ImageView domView = findViewById(R.id.dominantImageViewDialog);

        final int domColor = ImagePickerMainActivity.getDominantColor();

        Bitmap colorBitmap = ImageUtil.getInstance(this).colorToBitmap_CRISP(domColor);
        colorBitmap = ImageUtil.getInstance(this).cropToCircleWithBorder(colorBitmap, Color.BLACK, BORDER_WIDTH);
        domView.setImageBitmap(colorBitmap);

        TextView textView = findViewById(R.id.dominantTextViewDialog);
        String text= textView.getText().toString() + "\n#" + ColorUtil.colorToHex(domColor);
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
                StringUtil.copyToClipboard(ColorUtil.colorToHex(domColor), mainContext);
            }
        });

        allTextViewsTogether += textView.getText().toString()+"\n\n";
    }

    private void setAverageOptions(){
        try {
            final ImageView avView = findViewById(R.id.averageImageViewDialog);
            final int aColor = ColorUtil.getInstance().hexToColor(averageColor);

            Bitmap colorBitmap = ImageUtil.getInstance(this).colorToBitmap_CRISP(aColor);
            colorBitmap = ImageUtil.getInstance(this).cropToCircleWithBorder(colorBitmap, Color.BLACK, BORDER_WIDTH);
            avView.setImageBitmap(colorBitmap);

            TextView textView = findViewById(R.id.averageTextViewDialog);
            String text = textView.getText().toString() + "\n#" + ColorUtil.colorToHex(aColor);
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
                    StringUtil.copyToClipboard(ColorUtil.colorToHex(aColor), mainContext);
                }
            });

            allTextViewsTogether += textView.getText().toString() + "\n\n";
        }catch (Exception e){
            Log.e("ERROR", "ColorInfoDialog->Average color method");
        }
    }

    public void switchActivities(int c) {
        Intent intent = new Intent(ColorInfoDialog.this, ConvertMainActivity.class);
        intent.putExtra("color", ColorUtil.colorToHex(c));
        startActivity(intent);
    }

}
