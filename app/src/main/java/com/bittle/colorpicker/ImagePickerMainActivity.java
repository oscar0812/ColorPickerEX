package com.bittle.colorpicker;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.bittle.colorpicker.dialogs.ColorInfoDialog;
import com.bittle.colorpicker.dialogs.ImageOptionsDialog;
import com.bittle.colorpicker.utils.ColorUtil;
import com.bittle.colorpicker.utils.ImageUtil;
import com.bittle.colorpicker.utils.ScreenUtil;
import com.bittle.colorpicker.utils.StringUtil;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;

import java.io.File;

public class ImagePickerMainActivity extends BaseDrawerActivity {

    private CustomImageView mainImageView;

    private static int currentColor = 0;

    final int CAMERA_ACTION = 0;
    final int GALLEY_ACTION = 1;

    Context context;
    private static int mostDomColor;    // dominant color of image

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_picker_main);

        // fix camera error on newer devices
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        context = this;

        mainImageView = findViewById(R.id.mainImageView);

        holdFAB();

        final ImageView colorImageView = findViewById(R.id.currentColorImageView);

        mainImageView.setMaxScale(9000.0f);

        mainImageView.setOnTouchImageViewListener(new CustomImageView.OnTouchImageViewListener() {

            @Override
            public void onMove(int color) {
                // set color
                ((GradientDrawable)colorImageView.getBackground()).setColor(color);
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

                // set color
                ((GradientDrawable)colorImageView.getBackground()).setColor(currentColor);
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

        requestPermissions(this);

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
                        err.printStackTrace();
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
        final int maxTexture = ScreenUtil.getMaxTexture();

        ColorPickerMainActivity.imageUri = uri;

        Bitmap bitmap = ImageUtil.loadImageFromGallery(context, uri);

        if (bitmap != null) {
            try {
                bitmap = ImageUtil.makeBitmapFit(bitmap, maxTexture);

                if (bitmap.getWidth() > bitmap.getHeight()) {
                    bitmap = ImageUtil.rotateImage(bitmap, ImageUtil.ROTATE_RIGHT_90);
                }

                mainImageView.setImageBitmap(bitmap);
                setDominantColor();

            } catch (java.lang.OutOfMemoryError err) {
                try {
                    String path = StringUtil.getPathFromUri(context, uri);

                    bitmap = ImageUtil.fixOutOfMemoryError(path);

                    bitmap = ImageUtil.makeBitmapFit(bitmap, maxTexture);

                    if (bitmap.getWidth() > bitmap.getHeight()) {
                        bitmap = ImageUtil.rotateImage(bitmap, ImageUtil.ROTATE_RIGHT_90);
                    }

                    mainImageView.setImageBitmap(bitmap);
                    setDominantColor();
                    ImageUtil.deleteBitmap(path);

                } catch (Exception e) {
                    Log.e("ERROR", e.toString());
                }
            }
        }
    }

    // check if the app has permission to read and write to the sd card and access camera
    // if not, the user will be prompted to allow it
    public static void requestPermissions(Activity activity) {
        String[] PERMISSIONS = {
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };

        int PERMISSION_ALL = 1;

        if(!hasPermissions(activity, PERMISSIONS)){
            ActivityCompat.requestPermissions(activity, PERMISSIONS, PERMISSION_ALL);
        }
    }

    private static boolean hasPermissions(Context context, String[] permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    public void showColorInfoDialog() {

        Intent intent = new Intent(ImagePickerMainActivity.this, ColorInfoDialog.class);
        int hex = ColorUtil.getAverageColor(ImageUtil.drawableToBitmap(
                mainImageView.getDrawable()));
        intent.putExtra("average", ColorUtil.colorToHex(hex));

        startActivity(intent);
    }

    private void setDominantColor() {
        if (mainImageView.getDrawable() != null) {
            mostDomColor = ColorUtil.getDominantColor(
                    ImageUtil.drawableToBitmap(mainImageView.getDrawable()));
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

        final FloatingActionsMenu menu = findViewById(R.id.multiple_actions_down);
        menu.bringToFront();

        FloatingActionButton optionsButton = findViewById(R.id.optionsFab);
        optionsButton.setSize(FloatingActionButton.SIZE_MINI);

        // ======= catch out of memory errors ======
        Bitmap bitmap = ImageUtil.drawableToBitmap
                (ContextCompat.getDrawable(this, R.drawable.rotateright));
        try {
            optionsButton.setIconDrawable(ImageUtil.bitmapToDrawable(context, bitmap));
        } catch (java.lang.OutOfMemoryError e1) {
            String path = ImageUtil.bitmapToPath(context, bitmap);
            bitmap = ImageUtil.fixOutOfMemoryError(path);
            optionsButton.setIconDrawable(ImageUtil.bitmapToDrawable(context, bitmap));
            ImageUtil.deleteBitmap(path);
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
        }
        return super.onOptionsItemSelected(item);
    }
}
