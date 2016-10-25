package przygoda.com.projektkoncowy_przygoda;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;

/**
 * Created by 4ia1 on 2016-10-12.
 */
public class Circle extends View {

    private Context _context;
    private Paint paint;

    public Circle(Context context) {
        super(context);
        _context = context;

        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(2);
        paint.setColor(Color.WHITE);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        Point center = Utils.getScreenCenter(_context);
        canvas.drawCircle(center.x, center.y, Utils.getCameraCircleRadius(_context), paint);
    }
}
