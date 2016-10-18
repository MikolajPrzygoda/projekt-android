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

    private DisplayMetrics screenSize;
    private Context _context;
    private Paint paint;

    public Circle(Context context, DisplayMetrics screen) {
        super(context);
        _context = context;

        screenSize = screen;
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(2);
        paint.setColor(Color.WHITE);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawCircle(screenSize.widthPixels/2, screenSize.heightPixels/2, Utils.getCameraCircleRadius(_context), paint);
    }
}
