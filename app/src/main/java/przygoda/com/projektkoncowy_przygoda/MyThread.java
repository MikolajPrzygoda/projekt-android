package przygoda.com.projektkoncowy_przygoda;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.widget.ImageView;

import java.util.LinkedList;

/**
 * Created on 19.10.2016.
 */

public class MyThread extends Thread {

    private int _task;
    private Context _context;
    private Handler handler;

    public MyThread(Context context, int task, Handler handler) {
        _task = task;
        _context = context;
        this.handler = handler;
    }

    @Override
    public void run() {
        super.run();

        if(_task == Tasks.SAVEPHOTO){
            //Get Preview
            ImagePreview clickedPreview = ((CameraActivity)_context).lastLongClickedView;
            //Send msg to remove from UI
            handler.sendEmptyMessage(clickedPreview.ID);
            //Save
            clickedPreview.saveToFile(handler);
            //Find in list and remove
            LinkedList<ImagePreview> list = ((CameraActivity)_context).previewList;
            for(int i = 0; i < list.size(); i++){
                if(list.get(i).ID == clickedPreview.ID){
                    list.remove(i);
                    break;
                }
            }

        }

        else if(_task == Tasks.SAVEPHOTOS){
            LinkedList<ImagePreview> list = ((CameraActivity)_context).previewList;
            int count = list.size();
            for(int i = 0; i < count; i++){
                list.getFirst().saveToFile(handler);
                list.removeFirst();
            }
        }

        else if (_task == Tasks.SHOWPREVIEW){
            ImagePreview clickedView = ((CameraActivity)_context).lastLongClickedView;
            Bitmap temp = BitmapFactory.decodeByteArray(clickedView._data, 0, clickedView._data.length);
            final Bitmap bitmap = Utils.rotateBitmap(temp, 90);
            handler.post(new Runnable() {
                @Override
                public void run() {
                    ((ImageView) ((CameraActivity)_context).findViewById(R.id.ivPicturePreview) ).setImageBitmap(bitmap);
                }
            });
            ((CameraActivity)_context).handler.sendEmptyMessage(0);
        }
    }
}
