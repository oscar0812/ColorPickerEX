package com.bittle.colorpicker;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bittle.colorpicker.dialogs.ColorInfoDialog;
import com.bittle.colorpicker.realm.DBRealm;
import com.bittle.colorpicker.utils.ColorUtil;
import com.bittle.colorpicker.utils.ImageUtil;
import com.bittle.colorpicker.utils.RegexInputFilter;
import com.bittle.colorpicker.utils.ScreenUtil;
import com.bittle.colorpicker.utils.StringUtil;
import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.OnColorSelectedListener;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;

import java.util.Objects;

public class ColorPickerMainActivity extends BaseDrawerActivity implements View.OnTouchListener {
    private RelativeLayout mainAppLayout;
    private EditText mainEditText;
    public static final int SEARCH_COMPLETE = 0;

    int currentColor = Color.parseColor("#EEEEEE");
    protected static Uri imageUri = null;

    // for the touch listener
    private boolean is_being_touched = false;

    // onTouchEvent makes onClick vague, must add this, everything is gonna be alright
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_color_picker_main);
        FABFunctions();
        final EditText mainTextBox = findViewById(R.id.hexTextBoxConvert);
        mainAppLayout = findViewById(R.id.mainRelativeLayout);

        try {
            // try to set the last color before app was closed
            currentColor = Objects.requireNonNull
                    (DBRealm.getInstance(this).findAll().first()).getColor();
        } catch (Exception ignored) {
        }

        mainAppLayout.performClick();
        mainAppLayout.setOnTouchListener(this);

        mainAppLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //hideFAB();
                if (!menu.isExpanded()) {
                    hideKeyboard();
                    ColorInfoDialog.isActive = false;
                    switchActivities(currentColor);
                } else {
                    hideFAB();
                }
            }
        });

        // the edittext will only accept Hexidecimal values with a max length of 6,
        // it will also capitalize all input
        mainTextBox.setFilters(new InputFilter[]{
                new RegexInputFilter.HexInputFilter(),
                new InputFilter.AllCaps(),
                new InputFilter.LengthFilter(6)
        });

        mainTextBox.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String hex = editable.toString();
                if (hex.length() == 6) {
                    hideKeyboard();
                    colorTheLayout(ColorUtil.hexToColor(hex), false);
                }
            }
        });

        mainEditText = mainTextBox;

        // check if another app has sent over data
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();
        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if (type.startsWith("image/")) {
                handleSendImage(intent);
            }
        }
    }

    private void handleSendImage(Intent intent) {
        switchActivities(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        DBRealm.getInstance(this).start();
        mainEditText.setText(ColorUtil.colorToHex(currentColor));
    }

    @Override
    protected void onPause() {
        super.onPause();
        DBRealm.getInstance(this).close();
    }

    @Override
    public void onBackPressed() {
        hideFAB();
        super.onBackPressed();
    }

    // create the menu toolbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.hometoolbar, menu);
        return true;
    }

    // when something is touched on the toolbar, such as the pipette icon
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //hideFAB();

        switch (item.getItemId()) {
            // if pump is touched on toolbar
            case R.id.colorPickMenuAction:
                showColorDialog();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void colorTheLayout(int color, boolean changeText) {
        currentColor = color;
        mainAppLayout.setBackgroundColor(color);

        // to avoid infinite recursion, the onchangedtext calls this method, setting the
        // text here will then call onchangedtext back, etc..
        if (changeText) {
            String hex = ColorUtil.colorToHex(color).toUpperCase();
            if (hex.startsWith("#")) hex = hex.substring(1);
            mainEditText.setText(hex);
        }
        setClosestColor(color);

        if (!is_being_touched) {
            //  write color to db
            DBRealm.getInstance(context).insert(ColorUtil.colorToHex(color));
        }
    }

    public void colorTheLayout(int color) {
        colorTheLayout(color, true);
    }

    public void setClosestColor(int color) {
        final TextView closestColorTextView = findViewById(R.id.closestColorTextView);

        closestColorTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchActivities(currentColor);
                hideFAB();
            }
        });

        closestColorTextView.setTypeface(StringUtil.getFont(this));

        final TextView hexSignTextView = findViewById(R.id.hexSignTextView);
        changeColors(mainEditText, hexSignTextView, closestColorTextView, color);

        closestColorTextView.setText(ColorUtil.getClosestColor(color));
    }

    // change the color of textviews to be more visible
    public void changeColors(final EditText editText, final TextView hexSign, final TextView closestColor,
                             final int color) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (ColorUtil.isDarkColor(color)) {
                    editText.setTextColor(Color.WHITE);
                    hexSign.setTextColor(Color.WHITE);
                    closestColor.setTextColor(Color.WHITE);
                } else {
                    // is light color
                    editText.setTextColor(Color.BLACK);
                    hexSign.setTextColor(Color.BLACK);
                    closestColor.setTextColor(Color.BLACK);
                }
            }
        };
        runOnUiThread(runnable);
    }

    // switch to imagePickerActivity (is called when data is passed into this activity,
    // maybe a picture was dragged in)
    public void switchActivities(final Intent in) {
        Intent intent = new Intent(ColorPickerMainActivity.this, ImagePickerMainActivity.class);
        if (in == null) {
            intent.setData(null);
        } else {
            Uri imageUri = in.getParcelableExtra(Intent.EXTRA_STREAM);
            intent.setData(imageUri);
        }
        startActivity(intent);
    }

    public void switchActivities(int c) {
        Intent intent = new Intent(ColorPickerMainActivity.this, ConvertMainActivity.class);
        intent.putExtra("color", ColorUtil.colorToHex(c));
        startActivity(intent);
    }

    public void openSearch() {
        Intent intent = new Intent(ColorPickerMainActivity.this, SearchColorActivity.class);
        startActivityForResult(intent, SEARCH_COMPLETE);
    }

    public void openHistory() {
        Intent intent = new Intent(ColorPickerMainActivity.this, HistoryMainActivity.class);
        startActivityForResult(intent, SEARCH_COMPLETE);
    }

    @Override
    protected void onActivityResult(int aRequest, int aResult, Intent aData) {
        super.onActivityResult(aRequest, aResult, aData);
        if (aRequest == SEARCH_COMPLETE) {
            try {
                String returnValue = aData.getStringExtra("HEX");
                colorTheLayout(ColorUtil.hexToColor(returnValue));
            } catch (Exception ignored) {
            }
        }
        hideFAB();
    }


    public void showColorDialog() {

        Context context = this;
        ColorPickerDialogBuilder
                .with(context)
                .setTitle("Choose color")
                .lightnessSliderOnly()
                .initialColor(currentColor)
                .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                .density(12)
                .setOnColorSelectedListener(new OnColorSelectedListener() {
                    @Override
                    public void onColorSelected(int selectedColor) {
                        //Toaster.toast("onColorSelected: 0x" + Integer.toHexString(selectedColor));
                    }
                })
                .setPositiveButton("ok", new ColorPickerClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int selectedColor, Integer[] allColors) {
                        colorTheLayout(selectedColor);

                    }
                })
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .build()
                .show();
    }

    FloatingActionsMenu menu;
    Context context = this;

    public void FABFunctions() {
        this.menu = findViewById(R.id.multiple_actions);

        FloatingActionButton lookUpButton = findViewById(R.id.action_a);
        lookUpButton.setSize(FloatingActionButton.SIZE_MINI);
        lookUpButton.setIconDrawable(ContextCompat.getDrawable(context, R.drawable.magnify));

        FloatingActionButton historyButton = findViewById(R.id.action_c);
        historyButton.setSize(FloatingActionButton.SIZE_MINI);
        historyButton.setIconDrawable(ContextCompat.getDrawable(context, R.drawable.letterh));

        lookUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openSearch();
            }
        });

        historyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openHistory();

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        hideFAB();
                    }
                }).start();
            }
        });
    }

    public void hideFAB() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                menu.collapse();
            }
        });
    }

    public void showSetWallpaperDialog(final int color) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Set Wallpaper");
        builder.setMessage("Want to set this color as your Wallpaper?");
        builder.setIcon(ImageUtil.colorToDrawable(context, currentColor, 100, 100));
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                ScreenUtil.setWallpaperFromColor(color, context);
                Toast.makeText(context, "Wallpaper set", Toast.LENGTH_LONG).show();
            }
        });
        builder.show();
    }

    float start_y = 0;
    int start_color = 0;

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // record the Y coordinate when view is clicked
                start_y = motionEvent.getY();
                start_color = currentColor;
                is_being_touched = true;
                break;
            case MotionEvent.ACTION_UP:
                // because it requires it, perform a click
                mainAppLayout.performClick();
                is_being_touched = false;
                colorTheLayout(currentColor);
                break;
            case MotionEvent.ACTION_MOVE:
                float diff = start_y - motionEvent.getY();

                // diff increases as u go up, and decreases as u go down (goes into negatives)
                float percentage = diff / 1000;
                Log.e("MOVING", percentage + "");

                if (percentage > 0) {
                    currentColor = ColorUtil.lightenColor(start_color, percentage);
                } else {
                    currentColor = ColorUtil.darkenColor(start_color, Math.abs(percentage));
                }
                colorTheLayout(currentColor);
                break;

        }
        return true;
    }
}