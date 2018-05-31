package com.bittle.colorpicker.dialogs;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.annotation.AnyRes;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bittle.colorpicker.ConvertMainActivity;
import com.bittle.colorpicker.ImagePickerMainActivity;
import com.bittle.colorpicker.R;
import com.bittle.colorpicker.utils.ColorUtil;
import com.bittle.colorpicker.utils.StringUtil;

// view with 5 circles
public class ColorInfoDialog extends Activity {
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
        ((GradientDrawable)mainColorImageView.getBackground()).setColor(mainColor);

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

        allTextViewsTogether +="Touched Color\n"+currentHexTextView.getText().toString()+"\n\n";
        setTheOptions(mainColor);
        allTextViewsTogether = allTextViewsTogether.trim();

        if(((TextView)(findViewById(R.id.averageTextViewDialog)))
                .getText().toString().equals("##0")){
            finish();
        }
    }

    private void setTheOptions(int mainColor) {
        // setLighter
        int color = ColorUtil.lightenColor(mainColor, 0.25);
        setOption(R.id.lighterImageViewDialog, R.id.lighterTextViewDialog ,color);
        // setDarker
        color = ColorUtil.darkenColor(mainColor, 0.25);
        setOption(R.id.darkerImageViewDialog, R.id.darkerTextViewDialog, color);
        // setInverted
        color = ColorUtil.invertColor(mainColor);
        setOption(R.id.invertedImageViewDialog, R.id.invertedTextViewDialog, color);
        // setDominant
        color = ImagePickerMainActivity.getDominantColor();
        setOption(R.id.dominantImageViewDialog, R.id.dominantTextViewDialog, color);
        // setAverage
        color = ColorUtil.hexToColor(averageColor);
        setOption(R.id.averageImageViewDialog, R.id.averageTextViewDialog, color);
    }

    private void setOption(@AnyRes int image_view, @AnyRes int text_view, final int color) {

        ImageView view = findViewById(image_view);

        // set color
        ((GradientDrawable)view.getBackground()).setColor(color);

        TextView textView = findViewById(text_view);
        String text =  textView.getText().toString() + "\n#" + ColorUtil.colorToHex(color);
        textView.setText(text);

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setVisible(false);
                switchActivities(color);
                finish();
            }
        });

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StringUtil.copyToClipboard(ColorUtil.colorToHex(color), mainContext);
            }
        });

        allTextViewsTogether += textView.getText().toString()+"\n\n";
    }

    public void switchActivities(int c) {
        Intent intent = new Intent(ColorInfoDialog.this, ConvertMainActivity.class);
        intent.putExtra("color", ColorUtil.colorToHex(c));
        startActivity(intent);
    }

}
