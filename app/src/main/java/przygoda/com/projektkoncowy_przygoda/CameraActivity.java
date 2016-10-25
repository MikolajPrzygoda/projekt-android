package przygoda.com.projektkoncowy_przygoda;

import android.animation.ObjectAnimator;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.ImageFormat;
import android.graphics.Point;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.OrientationEventListener;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.LinkedList;
import java.util.List;

public class CameraActivity extends AppCompatActivity {

    private Camera camera;
    private CameraPreview cameraPreview;
    private Boolean isPanelShowed = false;
    private LinearLayout topPanel;
    private FrameLayout frameLayout;
    public RelativeLayout relativeLayout;
    private Camera.Parameters camParams;
    private List<Camera.Size> resolutionList;
    private OrientationEventListener orientationEventListener;
    private boolean isLeft = false;
    private boolean isRight = false;
    private boolean isUp = false;
    private boolean isDown = false;
    private boolean isOccupied;
    private View[] buttonsToAnimate;
    private float startx = 0;

    public LinkedList<ImagePreview> previewList = new LinkedList<>();
    public Handler handler;
    public ImagePreview lastLongClickedView = null;

    private final int MAX_NUMBER_OF_PREVIEWS = 8;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        topPanel = (LinearLayout) findViewById(R.id.llCameraTopPanel);
        frameLayout = (FrameLayout) findViewById(R.id.cameraFrame);
        relativeLayout = (RelativeLayout) findViewById(R.id.activity_camera);

        initCamera();
        initPreview();
        drawCircle();

        //set saveLocation if not set already
        if ( Preferences.getSaveLocation().equals("") )
            Preferences.setTempSaveLocation( getIntent().getExtras().getString("path") );

        orientationEventListener = new OrientationEventListener(getApplicationContext()) {
            @Override
            public void onOrientationChanged(int i) {
                if( (i > 315 || i < 45) && !isUp ){ //to up
                    if(isRight) { //right -> up
                        animButtons(-90, 0);
                        isRight = false;
                    }
                    else if(isDown) { //down -> up
                        animButtons(180, 0);
                        isDown = false;
                    }
                    else if(isLeft) { //left -> up
                        animButtons(90, 0);
                        isLeft = false;
                    }
                    isUp = true;
                }
                else if( (i > 45 && i < 135) && !isRight ){ //to right
                    if(isUp) { //up -> right
                        animButtons(0, -90);
                        isUp = false;
                    }
                    else if(isDown) { //down -> right
                        animButtons(180, 270);
                        isDown = false;
                    }
                    else if(isLeft) { //left -> right
                        animButtons(90, -90);
                        isLeft = false;
                    }
                    isRight = true;
                }
                else if( (i > 135 && i < 225) && !isDown ){ //to down
                    if(isUp) { //up -> down
                        animButtons(0, 180);
                        isUp = false;
                    }
                    else if(isRight) { //right -> down
                        animButtons(270, 180);
                        isRight = false;
                    }
                    else if(isLeft) { //left -> down
                        animButtons(90, 180);
                        isLeft = false;
                    }
                    isDown = true;
                }
                else if( (i > 225 && i < 315) && !isLeft ){ //to left
                    if(isUp) { //up -> left
                        animButtons(0, 90);
                        isUp = false;
                    }
                    else if(isRight) { //right -> left
                        animButtons(-90, 90);
                        isRight = false;
                    }
                    else if(isDown) { //down -> left
                        animButtons(180, 90);
                        isDown = false;
                    }
                    isLeft = true;
                }
            }
        };

        //Setup listeners
        frameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isPanelShowed) {
                    isPanelShowed = false;
                    ObjectAnimator animTop = ObjectAnimator.ofFloat(topPanel, View.TRANSLATION_Y, 0);
                    animTop.setDuration(300);
                    animTop.start();
                } else {
                    isPanelShowed = true;
                    ObjectAnimator animTop = ObjectAnimator.ofFloat(topPanel, View.TRANSLATION_Y, -topPanel.getLayoutParams().height);
                    animTop.setDuration(300);
                    animTop.start();
                }
            }
        });

        ImageView ivSelectSavePath = (ImageView) findViewById(R.id.ivSelectSavePath);
        ivSelectSavePath.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Preferences.setSaveLocation("");
                finish();
                Intent intent = new Intent(CameraActivity.this, PicturesActivity.class);
                startActivity(intent);
            }
        });

        ImageView ivTakePicture = (ImageView) findViewById(R.id.ivTakePicture);
        ivTakePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isOccupied){
                    showStatus("Taking photo...");
                    camera.takePicture(null, null, camPictureCallback);
                }
                else{
                    Toast.makeText(CameraActivity.this, "App is currently busy", Toast.LENGTH_SHORT).show();
                }
            }
        });

        ImageView ivSavePictures = (ImageView) findViewById(R.id.ivSavePictures);
        ivSavePictures.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // update UI
                showStatus("Saving photos...");

                // create new Thread
                handler = new MyHandler(CameraActivity.this, Tasks.SAVEPHOTOS);
                Thread thread = new MyThread(CameraActivity.this, Tasks.SAVEPHOTOS, handler);
                thread.start();
            }
        });


        ImageView ivWhiteBalance = (ImageView) findViewById(R.id.ivWhiteBalance);
        ivWhiteBalance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<String> whiteBalanceList = camParams.getSupportedWhiteBalance();
                String[] whiteBalanceArray = new String[whiteBalanceList.size()];
                whiteBalanceArray = whiteBalanceList.toArray(whiteBalanceArray);
                listDialog("Set white balance level", whiteBalanceArray, 0);
            }
        });

        ImageView ivResolution = (ImageView) findViewById(R.id.ivResolution);
        ivResolution.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String[] resolutionArray = new String[resolutionList.size()];
                for(int i = 0; i < resolutionList.size(); i++){
                    String value = resolutionList.get(i).width + " x " + resolutionList.get(i).height;
                    resolutionArray[i] = value;
                }
                listDialog("Set desired resolution", resolutionArray, 1);
            }
        });

        ImageView ivcolorFilter = (ImageView) findViewById(R.id.ivFilter);
        ivcolorFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<String> colorFilterList = camParams.getSupportedColorEffects();
                String[] colorFilterArray = new String[colorFilterList.size()];
                colorFilterArray = colorFilterList.toArray(colorFilterArray);
                listDialog("Available color filters", colorFilterArray, 2);
            }
        });

        ImageView ivExposure = (ImageView) findViewById(R.id.ivExposure);
        ivExposure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int minExposure = camParams.getMinExposureCompensation();
                int maxExposure = camParams.getMaxExposureCompensation();

                if(minExposure == maxExposure && maxExposure == 0){ //exposure setting not supported
                    Toast.makeText(CameraActivity.this, "Exposure setting not supported on your device", Toast.LENGTH_SHORT).show();
                }
                else{
                    String[] exposureLevels = new String [maxExposure - minExposure + 1];
                    int index = 0;
                    for (int i = minExposure; i <= maxExposure; i++){
                        exposureLevels[index] = ""+i;
                        index++;
                    }
                    listDialog("Set exposure", exposureLevels, 3);
                }
            }
        });

        findViewById(R.id.ivClosePreview).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hidePreview();
            }
        });
        //===============
        buttonsToAnimate = new View[7];
        buttonsToAnimate[0] = ivSelectSavePath;
        buttonsToAnimate[1] = ivTakePicture;
        buttonsToAnimate[2] = ivSavePictures;
        buttonsToAnimate[3] = ivWhiteBalance;
        buttonsToAnimate[4] = ivResolution;
        buttonsToAnimate[5] = ivcolorFilter;
        buttonsToAnimate[6] = ivExposure;
    }

    @Override
    protected void onPause() {
        super.onPause();

        orientationEventListener.disable();

        if (camera != null) {
            camera.stopPreview();
            cameraPreview.getHolder().removeCallback(cameraPreview);
            camera.release();
            camera = null;
        }
    }

    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first

        if (orientationEventListener.canDetectOrientation())
            orientationEventListener.enable();

        if (camera == null) {
            initCamera();
            initPreview();
            drawCircle();
        }
    }

    private void initCamera() {
        boolean cam = getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);

        if (!cam) {
            Toast.makeText(CameraActivity.this, "Couldn't find any camera", Toast.LENGTH_SHORT).show();
            finish();
        }
        else {
            int cameraId = getCameraId();
            if (cameraId < 0) {
                Toast.makeText(CameraActivity.this, "No rear facing camera", Toast.LENGTH_SHORT).show();
                finish();
            } else if (cameraId >= 0) {
                camera = Camera.open(cameraId);

                camParams = camera.getParameters();
                try{
                    camParams.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
                    camParams.setSceneMode(Camera.Parameters.SCENE_MODE_AUTO);
                    camParams.setPictureFormat(ImageFormat.JPEG);
                    camParams.setJpegQuality(100);

                    resolutionList = camParams.getSupportedPictureSizes();
                    camParams.setPictureSize(resolutionList.get(0).width, resolutionList.get(0).height);

                    camera.setParameters(camParams);
                }
                catch(Exception ignored){}
            }
        }
    }

    private int getCameraId() {
        int cid = 0;
        int camerasCount = Camera.getNumberOfCameras();
        for (int i = 0; i < camerasCount; i++) {
            Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
            Camera.getCameraInfo(i, cameraInfo);
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                cid = i;
            }
        }
        return cid;
    }

    private void initPreview() {
        cameraPreview = new CameraPreview(CameraActivity.this, camera);
        frameLayout.addView(cameraPreview);
    }

    private Camera.PictureCallback camPictureCallback = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            camera.startPreview();
            hideStatus();

            ImagePreview preview = new ImagePreview(getApplicationContext(), data);
            relativeLayout.addView(preview);

            //setup longpress listener for every new ImagePreview object
            preview.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    lastLongClickedView = (ImagePreview) v;

                    String[] options = {
                            "show preview",
                            "save",
                            "save all",
                            "delete",
                            "delete all"
                    };
                    listDialog("What do you want to do?", options, 4);

                    return true;
                }
            });
            preview.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()){
                        case MotionEvent.ACTION_DOWN:
                            startx = event.getRawX();
                            break;
                        case MotionEvent.ACTION_MOVE:
                            if(event.getRawX()-startx > 200){
                                int id = ((ImagePreview) v).ID;
                                for (int i = 0; i < previewList.size(); i++){
                                    ImagePreview view = previewList.get(i);
                                    if(view.ID == id){
                                        ((ViewGroup)view.getParent()).removeView(view);
                                        previewList.remove(i);
                                        positionPreviews();
                                        break;
                                    }
                                }
                            }
                            else{
                                RelativeLayout.LayoutParams tempMargins = (RelativeLayout.LayoutParams) v.getLayoutParams();
                                tempMargins.setMargins( (int) event.getRawX() - Utils.getPreviewSize(CameraActivity.this)/2, tempMargins.topMargin, 0, 0);
                                v.setLayoutParams(tempMargins);
                            }
                            break;
                        case MotionEvent.ACTION_UP:
                            positionPreviews();
                            break;
                    }
                    return false;
                }
            });

            //limits max number of simultaneous previews
            if(previewList.size() == MAX_NUMBER_OF_PREVIEWS){
                ImagePreview ip = previewList.getLast();
                ((ViewGroup)ip.getParent()).removeView(ip);
                previewList.removeLast();
                System.gc();
            }

            //reposition previews after adding new ImagePreview
            previewList.addFirst(preview);
            positionPreviews();
        }
    };

    private void listDialog(String title, final String[] values, final int mode) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder
                .setTitle(title)
                .setItems(values, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int i) {
                        switch (mode) {
                            case 0:
                                camParams.setWhiteBalance(values[i]);
                                break;
                            case 1:
                                camParams.setPictureSize(resolutionList.get(i).width, resolutionList.get(i).height);
                                break;
                            case 2:
                                camParams.setColorEffect(values[i]);
                                break;
                            case 3:
                                camParams.setExposureCompensation(Integer.parseInt(values[i]));
                                break;

                            //Longpress listener Dialog
                            case 4:
                                Thread thread;
                                int count;
                                switch (i) {
                                    case 0:
                                        showStatus("Loading preview...");
                                        showPreview();
                                        break;
                                    case 1:
                                        showStatus("Saving photo...");
                                        handler = new MyHandler(CameraActivity.this, Tasks.SAVEPHOTO);
                                        thread = new MyThread(CameraActivity.this, Tasks.SAVEPHOTO, handler);
                                        thread.start();
                                        break;

                                    case 2:
                                        showStatus("Saving photos...");
                                        handler = new MyHandler(CameraActivity.this, Tasks.SAVEPHOTOS);
                                        thread = new MyThread(CameraActivity.this, Tasks.SAVEPHOTOS, handler);
                                        thread.start();
                                        break;
                                    case 3:
                                        discardLastClickedPreview();
                                        break;
                                    case 4:
                                        count = previewList.size();
                                        for(int j = 0; j < count; j++){
                                            ImagePreview view = previewList.getFirst();
                                            ((ViewGroup) view.getParent()).removeView(view);
                                            previewList.removeFirst();
                                        }
                                        break;
                                }
                                break;
                        }
                        camera.setParameters(camParams);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .show();
    }

    private void alertDialog(String message) {
        final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);

        builder
                .setTitle("Alert")
                .setMessage(message)
                .setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                })
                .show();
    }

    private void animButtons(int startAngle, int targetAngle) {
        // animate camera ui buttons
        for (View view: buttonsToAnimate){
            ObjectAnimator
                    .ofFloat(view, View.ROTATION, startAngle, targetAngle)
                    .setDuration(300)
                    .start();
        }
        // animate all existing previews
        for (int i = 0; i < previewList.size(); i++){
            View view = previewList.get(i);
            ObjectAnimator
                    .ofFloat(view, View.ROTATION, startAngle, targetAngle)
                    .setDuration(300)
                    .start();
        }
    }

    private void drawCircle(){
        Circle circle = new Circle(getApplicationContext());
        frameLayout.addView(circle);
    }

    public void positionPreviews(){
        LinkedList list = previewList;
        Point[] positions = calcPositions((double)list.size());
        for(int i = 0; i < list.size(); i++){
            ImagePreview ip = (ImagePreview) list.get(i);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ip.getLayoutParams());
            params.setMargins(positions[i].x, positions[i].y, 0, 0);
            ip.setLayoutParams(params);
        }
    }

    private Point[] calcPositions(double n){
        Point s = new Point(
                Utils.getScreenCenter(this).x - Utils.getPreviewSize(this)/2,
                Utils.getScreenCenter(this).y - Utils.getPreviewSize(this)/2
        );
        int r = Utils.getCameraCircleRadius(this);

        Point[] retArr = new Point[(int)n];
        for (double i = 0; i < n; i++) {
            retArr[(int)i] = new Point(
                    (int) (r*Math.cos(Math.toRadians(180 + 360 * ( (i+1)/ n) )) + s.x),
                    (int) (r*Math.sin(Math.toRadians(180 + 360 * ( (i+1)/ n) )) + s.y)
            );
        }
        return retArr;
    }

    public void showStatus(String message){
        isOccupied = true;
        TextView tvStatus = (TextView) findViewById(R.id.tvCameraStatus);
        tvStatus.setText(message);
        tvStatus.setVisibility(View.VISIBLE);
    }
    public void hideStatus(){
        isOccupied = false;
        TextView tvStatus = (TextView) findViewById(R.id.tvCameraStatus);
        tvStatus.setVisibility(View.INVISIBLE);
    }

    private void showPreview(){
        try{
            // create new Thread
            handler = new MyHandler(CameraActivity.this, Tasks.SHOWPREVIEW);
            Thread thread = new MyThread(CameraActivity.this, Tasks.SHOWPREVIEW, handler);
            thread.start();
        }
        catch(ClassCastException ex){
//            ex.printStackTrace();
            Log.e("showPreviewError", "View parameter isn't a instance of ImagePreview class");
        }
    }
    public void hidePreview(){
        relativeLayout.findViewById(R.id.rlPicturePreview).setVisibility(View.INVISIBLE);
        relativeLayout.findViewById(R.id.ivPicturePreview).setVisibility(View.INVISIBLE);
        relativeLayout.findViewById(R.id.ivClosePreview).setVisibility(View.INVISIBLE);
    }

    private void discardLastClickedPreview(){
        int count = previewList.size();
        for(int j = 0; j < count; j++){
            ImagePreview view = previewList.get(j);
            if(view.ID == lastLongClickedView.ID){
                ((ViewGroup) view.getParent()).removeView(view);
                previewList.remove(j);
                positionPreviews();
                break;
            }
        }
    }
}
