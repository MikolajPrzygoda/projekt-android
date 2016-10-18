package przygoda.com.projektkoncowy_przygoda;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Point;
import android.telecom.DisconnectCause;
import android.util.DisplayMetrics;
import android.view.Display;

/**
 * Created by miki on 08.10.2016.
 */

public class Utils {

    static Bitmap resize(Bitmap map, Point size){
        return Bitmap.createScaledBitmap(map, size.x, size.y, false);
    }

    static public int getCameraCircleRadius(Context context){
        return context.getResources().getDisplayMetrics().widthPixels/4;
    }

    static public int getPreviewSize(Context context){
        return context.getResources().getDisplayMetrics().widthPixels/8;
    }

    static public Point getScreenCenter(Context context){
        DisplayMetrics m = context.getResources().getDisplayMetrics();
        return new Point(m.widthPixels/2, m.heightPixels/2);
    }

    static public Bitmap RotateBitmap(Bitmap source, float angle)
    {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }
}
