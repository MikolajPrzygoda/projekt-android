package przygoda.com.projektkoncowy_przygoda;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Environment;
import android.os.Handler;
import android.widget.ImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by 4ia1 on 2016-10-12.
 */
public class ImagePreview extends ImageView {

    private Bitmap smallBitmap;
    public byte[] _data;
    private Point size;
    private Paint paint;
    private Rect rect;
//    private Context _context;

    public final int ID;

    public ImagePreview(Context context, byte[] data) {
        super(context);
//        _context = context;
        _data = data;

        //generate preview ID
        DateFormat dateFormat = new SimpleDateFormat("HHmmssSSS");
        Date date = new Date();
        String idString = dateFormat.format(date);
        ID = Integer.parseInt(idString);

        //load thumbnail from byte[]
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 4;
        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length, options);
        bitmap = Utils.rotateBitmap(bitmap, 90);

        // prepare thumbnail for given bitmap
        size = new Point(Utils.getPreviewSize(context), Utils.getPreviewSize(context));
        smallBitmap = Utils.resizeBitmap(bitmap, size);

        // prepare paint
        int strokeWidth = 1;
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStrokeWidth(strokeWidth);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.WHITE);

        // + rect object
        rect = new Rect(
            0,   //left
            0,  //top
            smallBitmap.getWidth()-strokeWidth*2,   //right
            smallBitmap.getHeight()-strokeWidth*2   //bottom
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
        this.setImageBitmap(smallBitmap);
    }

    public void saveToFile(Handler handler){
        //filename
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss_SSS");
        Date date = new Date();
        String fileName = dateFormat.format(date);

        //foldername
        String folderName;
        if(Preferences.getSaveLocation().equals("")){
            folderName = Preferences.getTempSaveLocation();
        }
        else {
            folderName = Preferences.getSaveLocation();
        }

        //File object
        String filePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath() + "/MikolajPrzygoda/" + folderName + "/" + fileName + ".jpg";
        File file = new File(filePath);

        //prep bitmap
        Bitmap bitmap = BitmapFactory.decodeByteArray(_data, 0, _data.length);
        bitmap = Utils.rotateBitmap(bitmap, 90);

        //save to file
        try {
            FileOutputStream fs = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, fs);

            fs.close();
//            Log.e("tag", "photo with ID: "+ ID + " saved.");
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        //notify IU Thread
        handler.sendEmptyMessage(ID);
    }
}
