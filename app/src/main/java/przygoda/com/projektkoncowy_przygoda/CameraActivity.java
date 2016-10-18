package przygoda.com.projektkoncowy_przygoda;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Point;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.OrientationEventListener;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Queue;

public class CameraActivity extends AppCompatActivity {

    private Camera camera;
    private CameraPreview cameraPreview;
    private String saveLocation;
    private Boolean isPanelShowed = false;
    private LinearLayout topPanel;
    private FrameLayout frameLayout;
    private RelativeLayout relativeLayout;
    private Camera.Parameters camParams;
    private List<Camera.Size> resolutionList;
    private DisplayMetrics screen;
    private OrientationEventListener orientationEventListener;
    private boolean isLeft = false;
    private boolean isRight = false;
    private boolean isUp = false;
    private boolean isDown = false;
    private View[] buttonsToAnimate;

    public LinkedList<ImagePreview> previewList= new LinkedList<>();
    private boolean takingPhoto;


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


        //get saveLocation
        if (!Objects.equals(Preferences.getSaveLocation(), ""))
            saveLocation = Preferences.getSaveLocation();
        else {
            saveLocation = getIntent().getExtras().getString("path");
        }

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
                finish();
                Intent intent = new Intent(CameraActivity.this, PicturesActivity.class);
                startActivity(intent);
            }
        });

        ImageView ivTakePicture = (ImageView) findViewById(R.id.ivTakePicture);
        ivTakePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!takingPhoto){
                    takingPhoto = true;
                    if(previewList.size() == 3){
                        ImagePreview ip = previewList.getLast();
                        ((ViewGroup)ip.getParent()).removeView(ip);
                        previewList.removeLast();
                        System.gc();
                    }
                    camera.takePicture(null, null, camPictureCallback);
                }
                else{
                    Toast.makeText(CameraActivity.this, "Wait between taking photos", Toast.LENGTH_SHORT).show();
                }
            }
        });

        ImageView ivSavePictures = (ImageView) findViewById(R.id.ivSavePictures);
//      TODO: Save bitmaps from the LIST!
        ivSavePictures.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                SimpleDateFormat dFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
//                String d = dFormat.format(new Date());
//
//                File picturesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
//                File destDir = new File(picturesDir, "MikolajPrzygoda/" + saveLocation);
//                File myFoto = new File(destDir, d + ".jpg");
//
//                FileOutputStream fs = null;
//                try {
//                    fs = new FileOutputStream(myFoto);
//                } catch (FileNotFoundException e) {
//                    e.printStackTrace();
//                }
//                try {
//                    assert fs != null;
//                    fs.write(buffer);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                try {
//                    fs.close();
//                    cancelPhoto();
//                    Toast.makeText(CameraActivity.this, "Photo saved", Toast.LENGTH_SHORT).show();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
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
//                    rangeDialog("Set exposure", minExposure, maxExposure);
                }
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
            takingPhoto = false;

            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
            ImagePreview preview = new ImagePreview(getApplicationContext(), bitmap, screen);
            relativeLayout.addView(preview);

            //reposition previews after adding new ImageViews
            previewList.addFirst(preview);
            positionPreviews(previewList);
        }
    };

    private void cancelPhoto() {
        camera.startPreview();
    }

    private void listDialog(String title, final String[] values, final int mode) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder
                .setTitle(title)
                .setItems(values, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int i) {
                        switch (mode){
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

//    private void rangeDialog(String title, final int min, int max){
//        LayoutInflater li = LayoutInflater.from(this);
//        View dialogView = li.inflate(R.layout.range_dialog_layout, null);
//
//        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
//        alertDialogBuilder.setView(dialogView);
//
//        final NumberPicker exposureRangeInput = (NumberPicker) dialogView.findViewById(R.id.npExposureRange);
//        //block soft keyboard
//        exposureRangeInput.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
//        exposureRangeInput.setMinValue(0);
//        exposureRangeInput.setMaxValue(max - min);
//        exposureRangeInput.setWrapSelectorWheel(false);
//        exposureRangeInput.setFormatter(new NumberPicker.Formatter() {
//            @Override
//            public String format(int index) {
//                return Integer.toString(index + min);
//            }
//        });
//        Log.e("LOG", "min: "+0+", max: "+(max-min)+", set: "+(max+min));
//        exposureRangeInput.setValue(max + min);
//
//        alertDialogBuilder
//            .setTitle(title)
//            .setPositiveButton("Set",
//                new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog,int id) {
//                        Log.e("TAG", ""+(exposureRangeInput.getValue()+min));
//                    }
//                })
//            .setNegativeButton("Cancel",
//                new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog,int id) {
//                        dialog.cancel();
//                    }
//                });
//
//        AlertDialog alertDialog = alertDialogBuilder.create();
//        alertDialog.show();
//
//    }

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
        screen = getResources().getDisplayMetrics();
        Circle circle = new Circle(getApplicationContext(), screen);
        frameLayout.addView(circle);
    }

    private void positionPreviews(LinkedList list){
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
                    (int) (r*Math.cos(Math.toRadians(360 * ( (i+1)/ n) )) + s.x),
                    (int) (r*Math.sin(Math.toRadians(360 * ( (i+1)/ n) )) + s.y)
            );
            Log.e("tag", retArr[(int)i].x+"-"+retArr[(int)i].y);
        }
        return retArr;
    }
}
