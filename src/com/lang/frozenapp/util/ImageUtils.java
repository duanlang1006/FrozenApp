package com.lang.frozenapp.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import com.lang.frozenapp.R;


public class ImageUtils {

    public static Drawable getFrozenIconBitmap(Context context,Drawable icon){
        Bitmap mask = ((BitmapDrawable) context.getResources().getDrawable(R.drawable.icon_mask))
                        .getBitmap();
        mask = mask.copy(mask.getConfig(), true);
        
        Bitmap background = ((BitmapDrawable) context.getResources().getDrawable(R.drawable.icon_pattern))
                .getBitmap();
        background = background.copy(background.getConfig(), true);
        
        Bitmap iconBitmap = ((BitmapDrawable)icon).getBitmap();
        iconBitmap = iconBitmap.copy(mask.getConfig(), true);
        
        int height = mask.getHeight();
        int width = mask.getWidth();
        
        Bitmap src = resizeImage(iconBitmap,height,width);
        Bitmap back = getMaskBitmap(src,mask);
        src.recycle();
        src = null;
        Canvas iconcanvas = new Canvas();
        final Paint paint = new Paint();
        iconcanvas.setBitmap(background);
        iconcanvas.drawBitmap(back, 0, 0, paint);
        back.recycle();
        return new BitmapDrawable(background);
    }
    public static Bitmap resizeImage(Bitmap bitmap, int w, int h) {
        // load the origial Bitmap
        Bitmap BitmapOrg = bitmap;
        int width = BitmapOrg.getWidth();
        int height = BitmapOrg.getHeight();
        //if (width < w && height < h) {
        //    return bitmap;
        //}
        int newWidth = w;
        int newHeight = h;
        // calculate the scale
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // create a matrix for the manipulation
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap resizedBitmap = Bitmap.createBitmap(BitmapOrg, 0, 0, width,
        height, matrix, true);
        return resizedBitmap;
    }
    
    public static Bitmap getMaskBitmap(Bitmap src, Bitmap mask) {
        Canvas iconcanvas = new Canvas();
        final Paint paint = new Paint();
        iconcanvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG|Paint.FILTER_BITMAP_FLAG));
        
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        iconcanvas.setBitmap(mask);
        iconcanvas.drawBitmap(src, 0, 0, paint);
        paint.setXfermode(null);
        iconcanvas.setBitmap(null);
        return mask;
    }
}
