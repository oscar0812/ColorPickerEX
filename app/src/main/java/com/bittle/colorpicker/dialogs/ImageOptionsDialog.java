package com.bittle.colorpicker.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.bittle.colorpicker.CustomImageView;
import com.bittle.colorpicker.R;
import com.bittle.colorpicker.utils.ImageUtil;

/**
 * Created by Bittle on 1/2/17.
 */

public class ImageOptionsDialog extends Dialog implements View.OnClickListener {

    public final int LENGTH_FROM_TOP = 150;
    private ImageView left, right;
    private CustomImageView mainImageView;

    public ImageOptionsDialog(Context context, int style, CustomImageView imageView) {
        super(context, style);
        mainImageView = imageView;
    }

    @Override
    protected void onCreate(Bundle saved) {
        super.onCreate(saved);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialoglayout);

        left = findViewById(R.id.rotateLeftImageView);
        right = findViewById(R.id.rotateRightImageView);

        left.setOnClickListener(this);
        right.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rotateLeftImageView:
                Bitmap b = ImageUtil.getInstance(getContext()).rotateImage(
                        ImageUtil.getInstance(getContext()).drawableToBitmap(left.getDrawable()),
                        ImageUtil.ROTATE_LEFT_90);

                if(b != null){
                    left.setImageBitmap(b);
                }

                mainImageView.rotateImageLeft();
                break;

            case R.id.rotateRightImageView:
                Bitmap c = ImageUtil.getInstance(getContext()).rotateImage(
                        ImageUtil.getInstance(getContext()).drawableToBitmap(right.getDrawable()),
                        ImageUtil.ROTATE_RIGHT_90);

                if(c!=null){
                    right.setImageBitmap(c);
                }

                mainImageView.rotateImageRight();
                break;
            default:
                break;
        }
    }

    public void moveDialogToTop(int y) {
        try {
            Window window = getWindow();
            assert window != null;
            WindowManager.LayoutParams wlp = window.getAttributes();
            wlp.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
            wlp.y = y;
            window.setAttributes(wlp);
            window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        } catch (java.lang.NullPointerException err) {
            Log.e("ERROR", "ImageOptionsDialog -> moveDialogToTop: " + err.toString());
        }
    }
}