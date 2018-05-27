package com.bittle.colorpicker.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.graphics.Palette;
import android.util.Log;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;

/**
 * Created by Bittle on 12/30/16.
 */

public class ImageUtil {
    public static final int ROTATE_LEFT_90 = -90;
    public static final int ROTATE_RIGHT_90 = 90;
    public final float MAX_FONT_SIZE = 500.0f;
    private static String TAG_LOG = "IMAGE_UTIL";

    private ImageUtil(){
        // dont instantiate
    }

    private static Bitmap makeBitmapFit(Bitmap photo, final int WIDTH, final int HEIGHT) {
        // apps can only support images up to 4096x4096 (depends on device)
        if (photo.getHeight() >= WIDTH || photo.getWidth() >= HEIGHT) {
            Log.i("BITMAP WAS LARGE", "Dimensions: width = " +
                    photo.getWidth() + ", height = " + photo.getHeight());

            double large;
            if (photo.getHeight() > photo.getWidth()) {
                large = (double) photo.getHeight();
            } else {
                large = (double) photo.getWidth();
            }
            double divider = large / WIDTH;
            int w = (int) (photo.getWidth() / divider);
            int h = (int) (photo.getHeight() / divider);

            // pictures cant be 0xH or Wx0
            if (w == 0 || h == 0) {
                photo = Bitmap.createScaledBitmap(photo, 1, 1, true);
            }
            photo = Bitmap.createScaledBitmap(photo, w, h, true);

            return photo;

        } else {
            // the photo is ok size
            return photo;
        }
    }

    public static Bitmap makeBitmapFit(Bitmap photo, int wh){
        return makeBitmapFit(photo, wh, wh);
    }

    public static Bitmap fixOutOfMemoryError(String path) {
        Bitmap bitmap = null;

        BitmapFactory.Options options = new BitmapFactory.Options();
        for (options.inSampleSize = 1; options.inSampleSize <= 32; options.inSampleSize++) {
            try {
                bitmap = BitmapFactory.decodeFile(path, options);
                Log.e(TAG_LOG, "decoded successfully for samplesSize:" + options.inSampleSize);
                break;
            } catch (java.lang.OutOfMemoryError error) {
                Log.e(TAG_LOG, "outOfMemory for samplesSize:" + options.inSampleSize + " retrying..");
            }
        }
        return bitmap;
    }

    public String getImagePath(Context context, Bitmap inImage) {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, b);
        return MediaStore.Images.Media.insertImage(context.getContentResolver(), inImage, "Title", null);
    }

    // -values is left
    // +values is right
    public static void rotateImage(ImageView view, int angle) {
        try {

            Bitmap bitmap = ((BitmapDrawable) view.getDrawable()).getBitmap();
            bitmap = rotateImage(bitmap, angle);
            if(bitmap != null)
            view.setImageBitmap(bitmap);

        } catch (java.lang.OutOfMemoryError err) {
            Log.d("ERROR", err.toString());
        } catch (Exception err2) {
            err2.printStackTrace();
        }
    }

    public static Bitmap rotateImage(Bitmap bitmap, int angle) {
        try {
            Matrix matrix = new Matrix();
            matrix.postRotate(angle);

            return Bitmap.createBitmap(bitmap, 0, 0,
                    bitmap.getWidth(), bitmap.getHeight(), matrix, true);

        } catch (java.lang.OutOfMemoryError err) {
            Log.d("ERROR = ", err.toString());
            return null;
        } catch (java.lang.NullPointerException err2) {
            err2.printStackTrace();
            return null;
        }
    }

    private static Bitmap loadBitmapFromPath(String path) {
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            return BitmapFactory.decodeFile(path, options);
        } catch (java.lang.OutOfMemoryError err) {
            return fixOutOfMemoryError(path);
        }
    }

    private static Bitmap loadBitmapFromUri(Context context, Uri uri) {
        return loadBitmapFromPath(StringUtil.getPathFromUri(context, uri));
    }

    public Bitmap loadBitmapFromIntent(Context context, Intent intent) {
        return loadBitmapFromUri(context, intent.getData());
    }

    public static Bitmap loadImageFromGallery(Context context, Uri data) {
        try {
            return loadBitmapFromUri(context, data);
        } catch (Exception e) {
            Log.e("ERROR - ", e.toString());
            return null;
        }
    }

    public static Bitmap drawableToBitmap(Drawable drawable) {
        Bitmap bitmap;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if (bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }
        if (drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);

        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                    drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    public static Drawable bitmapToDrawable(Context context, Bitmap bitmap) {
        return new BitmapDrawable(context.getResources(), bitmap);
    }

    private Bitmap textToBitmap(String text, float textSize, int textColor) {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setTextSize(textSize);
        paint.setColor(textColor);
        paint.setTextAlign(Paint.Align.LEFT);
        float baseLine = -paint.ascent();
        int width = (int) (paint.measureText(text) + 0.5f);
        int height = (int) (baseLine + paint.descent() + 0.5f);
        Bitmap image = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(image);
        canvas.drawText(text, 0, baseLine, paint);
        return image;
    }

    public Bitmap textToBitmap(String text, float textSize) {
        return textToBitmap(text, textSize, Color.BLACK);
    }

    public Bitmap textToBitmap(String text, int color) {
        return textToBitmap(text, MAX_FONT_SIZE, color);
    }

    public Bitmap textToBitmap(String text) {
        return textToBitmap(text, MAX_FONT_SIZE, Color.BLACK);
    }

    public Bitmap replaceColor(Bitmap bmp, int originalColor, int newColor) {
        int w = bmp.getWidth();
        int h = bmp.getHeight();

        for(int x = 0; x< w; x++){
            for(int y =0; y<h; y++){
                int c = bmp.getPixel(x,y);
                c = (c << 24)&newColor;
                bmp.setPixel(x,y, c);
            }
        }
        return bmp;
    }

    public int[] getThePalette(Bitmap bitmap) {
        if (bitmap != null && !bitmap.isRecycled()) {
            Palette palette = Palette.from(bitmap).generate();

            int e = 0x000000;
            return new int[]{palette.getVibrantColor(e), palette.getLightVibrantColor(e),
                    palette.getDarkVibrantColor(e), palette.getMutedColor(e),
                    palette.getLightMutedColor(e), palette.getDarkMutedColor(e)};
        }
        return null;
    }


    public static Bitmap colorToBitmap(int color, int width, int height) {
        if (width <= 0) {
            width = 100;
        }
        if (height <= 0) {
            height = 100;
        }

        Bitmap bmp;
        try{
            bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        }catch (java.lang.OutOfMemoryError e){
            bmp = Bitmap.createBitmap(100,100, Bitmap.Config.ARGB_8888);
        }
        Canvas canvas = new Canvas(bmp);
        canvas.drawColor(color);
        return bmp;
    }

    public Bitmap colorToBitmap_CRISP(int color) {
        return colorToBitmap(color, 1000, 1000);
    }

    private static Uri bitmapToUri(Context context, Bitmap b){
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        b.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), b,
                "tempImage", null);

        return Uri.parse(path);
    }

    public static String bitmapToPath(Context context, Bitmap b){
        return StringUtil.getPathFromUri(context, bitmapToUri(context, b));
    }

    public static void deleteBitmap(String path){
        try{
            File f = new File(path);
            boolean b = f.delete();

            Log.w("Delete check", "File deleted -"+path+"- "+b);
        }catch (Exception e){
            Log.e("ERROR", "ImageUtil -> deleteBitmap");
        }
    }

    public static Drawable colorToDrawable(Context context, int color, int w, int h){
        Bitmap bitmap = colorToBitmap(color, w, h);
        return bitmapToDrawable(context, bitmap);
    }
}