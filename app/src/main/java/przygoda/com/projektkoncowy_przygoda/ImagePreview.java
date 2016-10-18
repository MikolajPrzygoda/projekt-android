package przygoda.com.projektkoncowy_przygoda;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.io.File;

/**
 * Created by 4ia1 on 2016-10-12.
 */
public class ImagePreview extends ImageView {

    private Context _context;
    private Bitmap _bitmap;
    private Bitmap _smallBitmap;
    private Point size;
    private Paint paint;
    private Rect rect;

    public ImagePreview(Context context, Bitmap bitmap, DisplayMetrics screen) {
        super(context);

        _context = context;
        _bitmap = bitmap;

        size = new Point(Utils.getPreviewSize(_context), Utils.getPreviewSize(_context));
        _smallBitmap = Utils.resize(_bitmap, size);
        _smallBitmap = Utils.RotateBitmap(_smallBitmap, 90);

        int strokeWidth = 1;
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStrokeWidth(strokeWidth);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.WHITE);

        rect = new Rect(
            0,   //left
            0,  //top
            _smallBitmap.getWidth()-strokeWidth*2,   //right
            _smallBitmap.getHeight()-strokeWidth*2   //bottom
        );
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        this.setMeasuredDimension(size.x, size.y);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawRect(rect, paint);
        this.setImageBitmap(_smallBitmap);
    }

    public void saveToFile(File file){

    }
}
