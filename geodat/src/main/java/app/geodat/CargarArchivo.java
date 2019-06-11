package app.geodat;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;

public class CargarArchivo {

	

	public static Bitmap decodeSampledBitmapFromResource(String direccion,
	        int reqWidth, int reqHeight) {

	    // First decode with inJustDecodeBounds=true to check dimensions
	    final BitmapFactory.Options options = new BitmapFactory.Options();
	    options.inJustDecodeBounds = true;
	    BitmapFactory.decodeFile(direccion, options);

	    // Calculate inSampleSize
	    options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

	    // Decode bitmap with inSampleSize set
	    options.inJustDecodeBounds = false;
	    return BitmapFactory.decodeFile(direccion, options);
	}
	
	public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
    // Raw height and width of image
    final int height = options.outHeight;
    final int width = options.outWidth;
    int inSampleSize = 1;

    if (height > reqHeight || width > reqWidth) {

        final int halfHeight = height / 2;
        final int halfWidth = width / 2;

        // Calculate the largest inSampleSize value that is a power of 2 and keeps both
        // height and width larger than the requested height and width.
        while ((halfHeight / inSampleSize) > reqHeight
                && (halfWidth / inSampleSize) > reqWidth) {
            inSampleSize *= 2;
        }
    }

    return inSampleSize;
}
	
	static Bitmap reducirImagen(String path) { 
		
		final String TAG = "Reduciendo imagen";
		final int IMAGE_MAX_SIZE = 300000; // 0.3MP 
		
		// Decode image size 
		BitmapFactory.Options o = new BitmapFactory.Options(); 
		o.inJustDecodeBounds = true; 
		BitmapFactory.decodeFile(path, o); 
		int scale = 1; 
		while ((o.outWidth * o.outHeight) * (1 / Math.pow(scale, 2)) > IMAGE_MAX_SIZE) { 
			scale++; 
		} 
		Log.d(TAG, "scale = " + scale + ", orig-width: " + o.outWidth + 
				", orig-height: " + o.outHeight); 
		Bitmap b = null; 
		if (scale > 1) { 
			scale--; 
			// scale to max possible inSampleSize that still yields an image 
			// larger than target 
			o = new BitmapFactory.Options(); 
			o.inSampleSize = scale;
			b = BitmapFactory.decodeFile(path, o); 
			// resize to desired dimensions 
			int height = b.getHeight(); 
			int width = b.getWidth(); 
			Log.d(TAG, "1th scale operation dimenions - width: " + width + ", height: " + height); 
			double y = Math.sqrt(IMAGE_MAX_SIZE / (((double) width) / height));
			double x = (y / height) * width; 
			Bitmap scaledBitmap = Bitmap.createScaledBitmap(b, (int) x, (int) y, true); 
			b.recycle(); 
			b = scaledBitmap; 
			System.gc(); 
		} else { 
				b = BitmapFactory.decodeFile(path, o); 
		} 
		Log.d(TAG, "bitmap size - width: " +b.getWidth() + ", height: " + b.getHeight()); 
		return b;
		}
	}

