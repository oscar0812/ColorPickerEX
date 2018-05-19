package com.bittle.colorpicker;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.TextKeyListener;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bittle.colorpicker.Dialogs.ColorInfoDialog;
import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.OnColorSelectedListener;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;

import com.bittle.colorpicker.utils.ColorUtil;
import com.bittle.colorpicker.utils.ImageUtil;
import com.bittle.colorpicker.utils.PrefUtil;
import com.bittle.colorpicker.utils.ScreenUtil;
import com.bittle.colorpicker.utils.StringUtil;
import com.bittle.colorpicker.utils.Toaster;

public class ColorPickerMainActivity extends AppCompatActivity {
    private RelativeLayout mainAppLayout;
    private EditText mainEditText;
    public static final int SEARCH_COMPLETE = 0;

    int currentColor = Color.parseColor("#EEEEEE");
    ColorUtil colorUtil = new ColorUtil();
    protected static Uri imageUri = null;
    private ScreenUtil screenUtil = new ScreenUtil();
    private ImageUtil imageUtil = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_color_picker_main);
        imageUtil = new ImageUtil(this);
        FABFunctions();

        //PrefUtil.clearAll(this);

        final EditText mainTextBox = (EditText) findViewById(R.id.hexTextBoxConvert);
        final TextView hexSignTextView = (TextView) findViewById(R.id.hexSignTextView);
        final RelativeLayout mainLayout = (RelativeLayout) findViewById(R.id.mainRelativeLayout);

        mainLayout.setOnClickListener(new View.OnClickListener() {
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
        mainLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                showSetWallpaperDialog(currentColor);
                return false;
            }
        });

        mainAppLayout = mainLayout;


        mainTextBox.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //hideFAB();
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                try {
                    String str = mainTextBox.getText().toString();
                    if (containsBadChars(str)) {
                        changeTextInsideListener(mainTextBox, this, str);
                    }

                    if (mainTextBox.getText().toString().length() >= 6) {
                        changeTextInsideListener(mainTextBox, this,
                                (mainTextBox.getText().toString()).toUpperCase());

                        hideKeyboard();
                        int color = Color.parseColor("#" + str);
                        currentColor = color;
                        mainLayout.setBackgroundColor(color);
                        PrefUtil.write(colorUtil.colorToHex(color), context);
                        setClosestColor(color);

                        //colorTheLayout(color);

                        changeColors(mainTextBox, hexSignTextView,
                                color);
                    }
                } catch (Exception e) {
                    Toaster.toast("INVALID HEX", context);
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


    private void changeTextInsideListener(EditText view, TextWatcher watcher, String str) {
        view.removeTextChangedListener(watcher);
        if (mainEditText.length() > 0) {
            TextKeyListener.clear(view.getText());
        }
        view.append(fixString(str));
        view.addTextChangedListener(watcher);
    }

    private void handleSendImage(Intent intent) {
        switchActivities(intent);
    }

    @Override
    protected void onResume() {
        mainAppLayout.setBackgroundColor(currentColor);
        mainEditText.setText(colorUtil.colorToHex(currentColor));

        super.onResume();
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

    // when something is touched on the toolbar, such as the camera icon
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //hideFAB();

        switch (item.getItemId()) {
            case R.id.cameraMenuAction:
                // what to do if camera icon is touched?
                switchActivities(null);
                break;
            case R.id.colorPickMenuAction:
                showColorDialog();
                break;
            default:
                Toaster.toast("OOPS", context);
                break;
        }
        return true;
    }


    public boolean isLegalChar(char a) {
        return (a >= '0' && a <= '9') || (a >= 'a' && a <= 'f') || (a >= 'A' && a <= 'F');
    }


    public void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            try {
                InputMethodManager input = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);
                assert input != null;
                input.hideSoftInputFromWindow(view.getWindowToken(), 0);
            } catch (java.lang.NullPointerException e) {
                e.printStackTrace();
            }
        }
    }

    public void changeColors(final EditText editText, final TextView hexSign, final int color) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (colorUtil.isDarkColor(color)) {

                    editText.setTextColor(Color.WHITE);
                    hexSign.setTextColor(Color.WHITE);
                } else {
                    // is light color

                    editText.setTextColor(Color.BLACK);
                    hexSign.setTextColor(Color.BLACK);
                }
            }
        };
        runOnUiThread(runnable);
    }

    public boolean containsBadChars(String str) {
        for (int x = 0; x < str.length(); x++) {
            if (!isLegalChar(str.charAt(x))) {
                return true;
            }
        }
        return false;
    }

    public String fixString(String str) {
        StringBuilder s = new StringBuilder();
        for (int x = 0; x < str.length(); x++) {
            if (isLegalChar(str.charAt(x))) {
                s.append(str.charAt(x));
            }
        }
        return s.toString();
    }

    public void colorTheLayout(int color) {
        mainAppLayout.setBackgroundColor(color);
        mainEditText.setText(colorUtil.colorToHex(color));
        setClosestColor(color);
        PrefUtil.write(colorUtil.colorToHex(color), this);
    }

    public void setClosestColor(int color) {
        final TextView closestColorTextView = (TextView) findViewById(R.id.closestColorTextView);

        closestColorTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchActivities(currentColor);
                hideFAB();
            }
        });

        closestColorTextView.setTypeface(StringUtil.getFont(this));

        if (colorUtil.isDarkColor(color)) {
            closestColorTextView.setTextColor(Color.WHITE);
        } else {
            closestColorTextView.setTextColor(Color.BLACK);
        }

        closestColorTextView.setText(colorUtil.getClosestColor(color));
    }

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
        intent.putExtra("color", colorUtil.colorToHex(c));
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
                colorTheLayout(colorUtil.hexToColor(returnValue));
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
                        //changeBackgroundColor(selectedColor);
                        currentColor = selectedColor;
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

    public void shareButton(String text) {
        if (text.length() >= 6) {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, text);
            sendIntent.setType("text/plain");
            startActivity(Intent.createChooser(
                    sendIntent, getResources().getText(R.string.send_to)));
        } else {
            Toaster.toast("Invalid Hex Color", context);
        }
    }

    FloatingActionsMenu menu;
    Context context = this;

    public void FABFunctions() {
        final FloatingActionsMenu menu = (FloatingActionsMenu) findViewById(R.id.multiple_actions);
        this.menu = menu;

        FloatingActionButton lookUpButton = (FloatingActionButton) findViewById(R.id.action_a);
        lookUpButton.setSize(FloatingActionButton.SIZE_MINI);
        lookUpButton.setIconDrawable(getResources().getDrawable(
                R.drawable.magnify, this.getTheme()));

        FloatingActionButton shareButton = (FloatingActionButton) findViewById(R.id.action_b);
        shareButton.setSize(FloatingActionButton.SIZE_MINI);
        shareButton.setIconDrawable(getResources().getDrawable(
                R.drawable.blackshare, this.getTheme()));

        FloatingActionButton historyButton = (FloatingActionButton) findViewById(R.id.action_c);
        historyButton.setSize(FloatingActionButton.SIZE_MINI);
        historyButton.setIconDrawable(getResources().getDrawable(
                R.drawable.letterh, this.getTheme()));

        lookUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toaster.toast(PrefUtil.getAll(context) + "\n" +
                //        PrefUtil.getNumberOfEntries(context));
                openSearch();
            }
        });

        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String str = "Check out \"Color Picker EX\" -\nhttps://play.google.com/store" +
                        "/apps/details?id=com.bittle.colorpicker";
                shareButton(str);
                menu.collapse();
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
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                menu.collapse();
            }
        };
        runOnUiThread(runnable);
    }

    public void showSetWallpaperDialog(final int color) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Set Wallpaper");
        builder.setMessage("Want to set this color as your Wallpaper?");
        Bitmap bitmap = imageUtil.colorToBitmap(currentColor, 100, 100);
        builder.setIcon(imageUtil.bitmapToDrawable(bitmap));
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                screenUtil.setWallpaperImage_FROM_COLOR(color, context);
                Toaster.toast("Wallpaper set", context);
            }
        });
        builder.show();
    }

}