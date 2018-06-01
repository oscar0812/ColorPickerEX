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

import com.bittle.colorpicker.dialogs.ColorInfoDialog;

import com.bittle.colorpicker.utils.ColorUtil;
import com.bittle.colorpicker.utils.StringUtil;

public class ConvertMainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.convert_layout);


    }
}
