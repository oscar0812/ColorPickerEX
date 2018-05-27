package com.bittle.colorpicker;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.TextKeyListener;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bittle.colorpicker.dialogs.ColorInfoDialog;
import com.bittle.colorpicker.realm.DBRealm;
import com.bittle.colorpicker.utils.ColorUtil;
import com.bittle.colorpicker.utils.ImageUtil;
import com.bittle.colorpicker.utils.ScreenUtil;
import com.bittle.colorpicker.utils.StringUtil;
import com.bittle.colorpicker.utils.Toaster;
import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.OnColorSelectedListener;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;

public class ColorPickerMainActivity extends BaseDrawerActivity {
    private RelativeLayout mainAppLayout;
    private EditText mainEditText;
    public static final int SEARCH_COMPLETE = 0;

    int currentColor = Color.parseColor("#EEEEEE");
    protected static Uri imageUri = null;
    private ScreenUtil screenUtil = new ScreenUtil();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_color_picker_main);
        FABFunctions();

        final EditText mainTextBox = findViewById(R.id.hexTextBoxConvert);
        final TextView hexSignTextView = findViewById(R.id.hexSignTextView);
        final RelativeLayout mainLayout = findViewById(R.id.mainRelativeLayout);

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

                        DBRealm.getInstance(context).insert(ColorUtil.colorToHex(color));
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
        super.onResume();
        DBRealm.getInstance(this).start();
        mainAppLayout.setBackgroundColor(currentColor);
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
        }
        return super.onOptionsItemSelected(item);
    }


    public boolean isLegalChar(char a) {
        return (a >= '0' && a <= '9') || (a >= 'a' && a <= 'f') || (a >= 'A' && a <= 'F');
    }

    public void changeColors(final EditText editText, final TextView hexSign, final int color) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (ColorUtil.getInstance().isDarkColor(color)) {

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
        mainEditText.setText(ColorUtil.colorToHex(color));
        setClosestColor(color);

        //  write color to db
        DBRealm.getInstance(context).insert(ColorUtil.colorToHex(color));
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

        if (ColorUtil.getInstance().isDarkColor(color)) {
            closestColorTextView.setTextColor(Color.WHITE);
        } else {
            closestColorTextView.setTextColor(Color.BLACK);
        }

        closestColorTextView.setText(ColorUtil.getInstance().getClosestColor(color));
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
                colorTheLayout(ColorUtil.getInstance().hexToColor(returnValue));
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
            Toaster.toast("Invalid Hex ColorModel", context);
        }
    }

    FloatingActionsMenu menu;
    Context context = this;

    public void FABFunctions() {
        final FloatingActionsMenu menu = findViewById(R.id.multiple_actions);
        this.menu = menu;

        FloatingActionButton lookUpButton = findViewById(R.id.action_a);
        lookUpButton.setSize(FloatingActionButton.SIZE_MINI);
        lookUpButton.setIconDrawable(ContextCompat.getDrawable(context, R.drawable.magnify));

        FloatingActionButton shareButton = findViewById(R.id.action_b);
        shareButton.setSize(FloatingActionButton.SIZE_MINI);
        shareButton.setIconDrawable(ContextCompat.getDrawable(context, R.drawable.blackshare));

        FloatingActionButton historyButton = findViewById(R.id.action_c);
        historyButton.setSize(FloatingActionButton.SIZE_MINI);
        historyButton.setIconDrawable(ContextCompat.getDrawable(context, R.drawable.letterh));

        lookUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openSearch();
            }
        });

        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String str = "Check out \"ColorModel Picker EX\" -\nhttps://play.google.com/store" +
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
        builder.setIcon(ImageUtil.colorToDrawable(context, currentColor, 100, 100));
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