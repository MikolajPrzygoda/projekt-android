package przygoda.com.projektkoncowy_przygoda;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

/**
 * Created on 19.10.2016.
 */

public class MyHandler extends Handler {

    private int _task;
    private Context _context;

    public MyHandler(Context context, int task) {
        _context = context;
        _task = task;
    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);

        //saving one photo
        if(_task == 1){
            //remove View from UI when saved
            RelativeLayout relativeLayout = ((CameraActivity)_context).relativeLayout;
            for(int i = 0; i < relativeLayout.getChildCount(); i++){
                View v = relativeLayout.getChildAt(i);
                if(v instanceof ImagePreview){
                    if (( (ImagePreview) v ).ID == msg.what){
                        ( (ViewGroup) v.getParent() ).removeView(v);
                        ((CameraActivity)_context).hideStatus();
                    }
                }
            }
        }

        //saving all photos
        else if(_task == 2){
            //msg => saved photo with passed ID => remove view with that ID

            int previewsLeft = 0;
            RelativeLayout relativeLayout = ((CameraActivity)_context).relativeLayout;
            for(int i = 0; i < relativeLayout.getChildCount(); i++) {
                View v = relativeLayout.getChildAt(i);
                if (v instanceof ImagePreview) {
                    previewsLeft++;
                }
            }
            for(int i = 0; i < relativeLayout.getChildCount(); i++){
                View v = relativeLayout.getChildAt(i);
                if(v instanceof ImagePreview){
                    if (( (ImagePreview) v ).ID == msg.what){
                        ( (ViewGroup) v.getParent() ).removeView(v);

                        //if removing last preview
                        if(previewsLeft == 1)
                            ((CameraActivity)_context).hideStatus();
                    }
                }
            }
        }

        //showing preview
        else if (_task == 3){
            //msg => preview loaded => show UI
            ((CameraActivity)_context).hideStatus();
            ((CameraActivity)_context).findViewById(R.id.rlPicturePreview).bringToFront();
            ((CameraActivity)_context).findViewById(R.id.rlPicturePreview).setVisibility(View.VISIBLE);
            ((CameraActivity)_context).findViewById(R.id.ivPicturePreview).setVisibility(View.VISIBLE);
            ((CameraActivity)_context).findViewById(R.id.ivClosePreview).setVisibility(View.VISIBLE);
        }
    }
}
