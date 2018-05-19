package com.bittle.colorpicker;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.bittle.colorpicker.Dialogs.ColorInfoDialog;
import com.bittle.colorpicker.Dialogs.ImageOptionsDialog;
import com.bittle.colorpicker.ImageView.CustomImageView;
import com.bittle.colorpicker.utils.ColorUtil;
import com.bittle.colorpicker.utils.ImageUtil;
import com.bittle.colorpicker.utils.ScreenUtil;
import com.bittle.colorpicker.utils.StringUtil;
import com.bittle.colorpicker.utils.Toaster;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;

import java.io.File;

public class ImagePickerMainActivity extends AppCompatActivity {

    private ImageUtil imageUtil;
    ColorUtil colorUtil = new ColorUtil();
    ScreenUtil screenUtil = new ScreenUtil();

    private CustomImageView mainImageView;

    private static int currentColor = 0;

    final int CAMERA_ACTION = 0;
    final int GALLEY_ACTION = 1;

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static final String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private final int GREATEST_WIDTH_FOR_CIRCLE = 200;
    private final int GREATEST_HEIGHT_FOR_CIRCLE = 200;

    Context mainContext;
    private int maxTexture;
    private static int mostDomColor;    // dominant color of image

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_picker_main);
        imageUtil = new ImageUtil(this);
        mainContext = this;
        maxTexture = screenUtil.getMaxTexture();

        mainImageView = (CustomImageView) findViewById(R.id.mainImageView);

        holdFAB();

        final ImageView colorImageView = (ImageView) findViewById(R.id.currentColorImageView);

        mainImageView.setMaxScale(9000.0f);

        mainImageView.setOnTouchImageViewListener(new CustomImageView.OnTouchImageViewListener() {
            Bitmap pic = null;

            @Override
            public void onMove(int color) {
                colorImageView.setVisibility(View.VISIBLE);

                pic = imageUtil.colorToBitmap(color,
                        GREATEST_WIDTH_FOR_CIRCLE, GREATEST_HEIGHT_FOR_CIRCLE);
                pic = imageUtil.cropToCircleWithBorder(pic, Color.BLACK,
                        12.0f, !ImageUtil.CHANGE_BORDER);

                colorImageView.setImageBitmap(pic);
                currentColor = color;
            }
        });

        if (getIntent().getData() != null) {
            // picture was sent from another activity
            setImageFromGallery(getIntent());
        } else {
            if (ColorPickerMainActivity.imageUri == null) {
                selectImage();
            } else {
                setImageFromCamera(ColorPickerMainActivity.imageUri);
                colorImageView.setVisibility(View.VISIBLE);

                Bitmap pic = imageUtil.colorToBitmap(currentColor,
                        GREATEST_WIDTH_FOR_CIRCLE, GREATEST_HEIGHT_FOR_CIRCLE);
                pic = imageUtil.cropToCircleWithBorder(pic, Color.BLACK,
                        12.0f);
                colorImageView.setImageBitmap(pic);
            }
        }


        colorImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ColorInfoDialog.isActive = true;
                showColorInfoDialog();
            }
        });
    }


    private Uri outputUri;

    private void selectImage() {

        verifyStoragePermissions(this);


        final CharSequence[] items = {"Take Photo", "Choose from Library",
                "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(ImagePickerMainActivity.this);
        builder.setTitle("Get Color From Image");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Take Photo")) {

                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    File file = new File(Environment.getExternalStorageDirectory(),
                            "/ColorPicker/img_" + System.currentTimeMillis() + ".jpg");
                    try {
                        outputUri = Uri.fromFile(file);
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, outputUri);
                        startActivityForResult(intent, CAMERA_ACTION);
                    } catch (Exception err) {
                        Toaster.toast("Error in dialog!", mainContext);
                    }

                } else if (items[item].equals("Choose from Library")) {
                    Intent gallery = new Intent();
                    gallery.setType("image/*");
                    gallery.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(gallery, "Select Picture"), GALLEY_ACTION);
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == CAMERA_ACTION) {
                setImageFromCamera(outputUri);

            } else if (requestCode == GALLEY_ACTION) {
                setImageFromGallery(data);
            }
        }
    }

    public void setImageFromCamera(Uri uri) {
        setViewImage(uri);
    }

    public void setImageFromGallery(Intent data) {
        setViewImage(data.getData());

    }

    private void setViewImage(final Uri uri) {

        ColorPickerMainActivity.imageUri = uri;

        Bitmap bitmap = imageUtil.loadImageFromGallery(uri);

        if (bitmap != null) {
            try {
                bitmap = imageUtil.makeBitmapFit(bitmap, maxTexture);

                if (bitmap.getWidth() > bitmap.getHeight()) {
                    bitmap = imageUtil.rotateImage(bitmap, ImageUtil.ROTATE_RIGHT_90);
                }

                mainImageView.setImageBitmap(bitmap);
                setDominantColor();

            } catch (java.lang.OutOfMemoryError err) {
                try {
                    String path = StringUtil.getPathFromUri(mainContext, uri);

                    bitmap = imageUtil.fixOutOfMemoryError(path);

                    bitmap = imageUtil.makeBitmapFit(bitmap, maxTexture);

                    if (bitmap.getWidth() > bitmap.getHeight()) {
                        bitmap = imageUtil.rotateImage(bitmap, ImageUtil.ROTATE_RIGHT_90);
                    }

                    mainImageView.setImageBitmap(bitmap);
                    setDominantColor();
                    imageUtil.deleteBitmap(path);

                } catch (Exception e) {
                    Log.e("ERROR", e.toString());
                }
            }
        }
    }


    // check if the app has permission to read and write to the sd card
    // if not, the user will be prompted to allow it
    public static void verifyStoragePermissions(Activity activity) {
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // no permission
            ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);

        }
    }

    public void showColorInfoDialog() {

        Intent intent = new Intent(ImagePickerMainActivity.this, ColorInfoDialog.class);
        int hex = colorUtil.getAverageColor(imageUtil.drawableToBitmap(
                mainImageView.getDrawable()));
        intent.putExtra("average", colorUtil.colorToHex(hex));

        startActivity(intent);
    }

    private void setDominantColor() {
        if (mainImageView.getDrawable() != null) {
            mostDomColor = colorUtil.getDominantColor(
                    imageUtil.drawableToBitmap(mainImageView.getDrawable()));
        }
    }

    public static int getDominantColor() {
        return mostDomColor;
    }

    public static int getCurrentColor() {
        return currentColor;
    }

    private void holdFAB() {

        final ImageOptionsDialog imageOptionsDialog =
                new ImageOptionsDialog(this, R.style.TransparentDialog, mainImageView);

        imageOptionsDialog.moveDialogToTop(imageOptionsDialog.LENGTH_FROM_TOP);

        final FloatingActionsMenu menu = (FloatingActionsMenu) findViewById(R.id.multiple_actions_down);
        menu.bringToFront();

        FloatingActionButton optionsButton = (FloatingActionButton) findViewById(R.id.optionsFab);
        optionsButton.setSize(FloatingActionButton.SIZE_MINI);

        // ======= catch out of memory errors ======
        Bitmap bitmap = imageUtil.drawableToBitmap
                (getResources().getDrawable(R.drawable.rotateright, this.getTheme()));
        try {
            optionsButton.setIconDrawable(imageUtil.bitmapToDrawable(bitmap));
        } catch (java.lang.OutOfMemoryError e1) {
            String path = imageUtil.bitmapToPath(bitmap);
            bitmap = imageUtil.fixOutOfMemoryError(path);
            optionsButton.setIconDrawable(imageUtil.bitmapToDrawable(bitmap));
            imageUtil.deleteBitmap(path);
        }


        optionsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                menu.collapse();
                imageOptionsDialog.show();
            }
        });
    }

    // create the menu toolbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.imagepickertoolbar, menu);
        return true;
    }

    // when something is touched on the toolbar, such as the camera icon
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //hideFAB();

        switch (item.getItemId()) {
            case R.id.cameraMenuAction2:
                selectImage();
                break;
            default:
                Toaster.toast("OOPS", mainContext);
                break;
        }
        return true;
    }
}
