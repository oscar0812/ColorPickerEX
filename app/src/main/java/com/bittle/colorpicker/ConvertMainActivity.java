package com.bittle.colorpicker;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bittle.colorpicker.Dialogs.ColorInfoDialog;

import com.bittle.colorpicker.utils.ColorUtil;
import com.bittle.colorpicker.utils.StringUtil;

public class ConvertMainActivity extends AppCompatActivity {

    ColorUtil colorUtil = new ColorUtil();
    private String hex = "";
    Context mainContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_convert_main);

        mainContext = this;

        Intent i = getIntent();
        if (i.hasExtra("color")) {
            hex = i.getStringExtra("color");
        }

        if (hex.equals("")) {
            hex = colorUtil.colorToHex(Color.BLACK);
        }

        setTheTextBoxes(hex);

        TextView closeTheDialog = (TextView) findViewById(R.id.closeTextViewConvert);
        closeTheDialog.setTypeface(StringUtil.getFont(this));
        //closeTheDialog.setTextColor(colorUtil.hexToColor(hex));
        closeTheDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        final TextView[] mainTexts = {(TextView) findViewById(R.id.hexTextBoxConvert)
                , (TextView) findViewById(R.id.intColorTextBoxConvert)
                , (TextView) findViewById(R.id.rgbTextBoxConvert)
                , (TextView) findViewById(R.id.smaliColorTextBoxConvert)
                , (TextView) findViewById(R.id.originTextBoxConvert)};


        final TextView[] textViews = {(TextView) findViewById(R.id.textView1convert)
                , (TextView) findViewById(R.id.textView2convert)
                , (TextView) findViewById(R.id.textView3convert)
                , (TextView) findViewById(R.id.textView4convert)
                , (TextView) findViewById(R.id.textView5convert)};

        TextView copyAll = (TextView) findViewById(R.id.copyAllTextViewConvert);
        copyAll.setTypeface(StringUtil.getFont(this));
        //copyAll.setTextColor(colorUtil.hexToColor(hex));
        copyAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StringBuilder str = new StringBuilder();
                for (int x = 0; x < mainTexts.length; x++) {
                    str .append( textViews[x].getText().toString());
                    str.append(": ");
                    str.append(mainTexts[x].getText().toString());
                    str.append("\n");
                }
                StringUtil.copyToClipboard(str.toString().trim() + "\nAll Fields copied", mainContext);
            }
        });
    }

    public void setTheTextBoxes(String hex) {
        Typeface font = StringUtil.getFont(this);

        final TextView[] mainTexts = {(TextView) findViewById(R.id.hexTextBoxConvert)
                , (TextView) findViewById(R.id.intColorTextBoxConvert)
                , (TextView) findViewById(R.id.rgbTextBoxConvert)
                , (TextView) findViewById(R.id.smaliColorTextBoxConvert)
                , (TextView) findViewById(R.id.originTextBoxConvert)};


        final TextView[] textViews = {(TextView) findViewById(R.id.textView1convert)
                , (TextView) findViewById(R.id.textView2convert)
                , (TextView) findViewById(R.id.textView3convert)
                , (TextView) findViewById(R.id.textView4convert)
                , (TextView) findViewById(R.id.textView5convert)};

        RelativeLayout[] layouts = {(RelativeLayout) findViewById(R.id.firstConvert)
                , (RelativeLayout) findViewById(R.id.secondConvert)
                , (RelativeLayout) findViewById(R.id.thirdConvert)
                , (RelativeLayout) findViewById(R.id.fourthConvert)
                , (RelativeLayout) findViewById(R.id.fifthConvert)};

        for (int x = 0; x < mainTexts.length; x++) {
            final int y = x;
            mainTexts[x].setTypeface(font);
            textViews[x].setTypeface(font);
            layouts[x].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    StringUtil.copyToClipboard(
                            mainTexts[y].getText().toString().replaceAll("#", ""), mainContext);
                }
            });
        }

        setHex(hex, mainTexts[0]);
        setInt(colorUtil.hexToColor(hex), mainTexts[1]);
        setRgb(colorUtil.hexToRGB(hex), mainTexts[2]);
        setSmali(colorUtil.hexToSmaliCode(hex), mainTexts[3]);
        setOrigin(colorUtil.getClosestColor(colorUtil.hexToColor(hex)), mainTexts[4]);
    }

    private void setHex(String hex, TextView editText) {
        if (!hex.startsWith("#")) {
            hex = "#" + hex;
        }
        editText.setText(hex);
    }

    private void setInt(int num, TextView editText) {
        String str = num + "";
        editText.setText(str);
    }

    private void setRgb(int[] rgb, TextView editText) {
        String str = rgb[0] + "-" + rgb[1] + "-" + rgb[2];
        editText.setText(str);
    }

    private void setSmali(String smaliCode, TextView editText) {
        if (!smaliCode.startsWith("-")) {
            smaliCode = "-0x" + smaliCode;
        }
        editText.setText(smaliCode);
    }

    private void setOrigin(String colorCode, TextView editText) {
        editText.setText(colorCode);
    }


    public void switchActivities(int c) {
        Intent intent = new Intent(ConvertMainActivity.this, ColorInfoDialog.class);
        intent.putExtra("color", colorUtil.colorToHex(c));
        startActivity(intent);
    }

    @Override
    public boolean isFinishing() {
        if (ColorInfoDialog.isActive) {
            switchActivities(colorUtil.hexToColor(hex));
        }
        return super.isFinishing();
    }

    @Override
    public void onDestroy() {
        if (ColorInfoDialog.isActive) {
            switchActivities(colorUtil.hexToColor(hex));
        }
        super.onDestroy();
    }
}
