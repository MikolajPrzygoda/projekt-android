package przygoda.com.projektkoncowy_przygoda;

import android.animation.ObjectAnimator;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.net.LinkAddress;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class CameraActivity extends AppCompatActivity {

    private Camera camera;
    private int cameraId = -1;
    private CameraPreview cameraPreview;
    private boolean pictureTaken = false;
    public byte[] fdata;
    private String saveLocation;
    private Boolean isPanelShowed = false;
    private LinearLayout bottomPanel;
    private LinearLayout topPanel;
    private FrameLayout frameLayout;
    private Camera.Parameters camParams;
    private List<Camera.Size> resolutionList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

//        Bundle bundle = getIntent().getExtras();
//        TextView textView = (TextView) findViewById(R.id.tvCamera);
//        textView.setText(bundle.getString("saveLocation"));

        topPanel = (LinearLayout) findViewById(R.id.llCameraTopPanel);
        bottomPanel = (LinearLayout) findViewById(R.id.llCameraBottomPanel);
        frameLayout = (FrameLayout) findViewById(R.id.cameraFrame);

        initCamera();
        initPreview();

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
                    animTop.setDuration(150);
                    animTop.start();
                } else {
                    isPanelShowed = true;
                    ObjectAnimator animTop = ObjectAnimator.ofFloat(topPanel, View.TRANSLATION_Y, -topPanel.getLayoutParams().height);
                    animTop.setDuration(150);
                    animTop.start();
                }
            }
        });

        ImageView ivSelectSavePath = (ImageView) findViewById(R.id.ivSelectSavePath);
        ivSelectSavePath.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!pictureTaken) {
                    finish();
                    Intent intent = new Intent(CameraActivity.this, PicturesActivity.class);
                    startActivity(intent);
                } else {
                    cancelPhoto();
                }
            }
        });

        ImageView ivTakePicture = (ImageView) findViewById(R.id.ivTakePicture);
        ivTakePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!pictureTaken) {
                    camera.takePicture(null, null, camPictureCallback);
                } else {
                    SimpleDateFormat dFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
                    String d = dFormat.format(new Date());

                    File picturesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                    File destDir = new File(picturesDir, "MikolajPrzygoda/" + saveLocation);
                    File myFoto = new File(destDir, d + ".jpg");

                    FileOutputStream fs = null;
                    try {
                        fs = new FileOutputStream(myFoto);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    try {
                        assert fs != null;
                        fs.write(fdata);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        fs.close();
                        cancelPhoto();
                        Toast.makeText(CameraActivity.this, "Photo saved", Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
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
                    alertDialog("Exposure setting not supported on your device");
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
    }

    @Override
    protected void onPause() {
        super.onPause();

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

        if (camera == null) {
            initCamera();
            initPreview();
        }
    }

    private void initCamera() {
        boolean cam = getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);

        if (!cam) { // uwaga - brak kamery
            Toast.makeText(CameraActivity.this, "Couldn't find any camera", Toast.LENGTH_SHORT).show();
            finish();
        }
        else {
            cameraId = getCameraId();
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
        int camerasCount = Camera.getNumberOfCameras(); // gdy więcej niż jedna kamera
        for (int i = 0; i < camerasCount; i++) {
            Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
            Camera.getCameraInfo(i, cameraInfo);
//          if (cameraInfo.facing == CameraInfo.CAMERA_FACING_FRONT)
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
            fdata = data;
            pictureTaken = true;

            ImageView ivSelectSavePath = (ImageView) findViewById(R.id.ivSelectSavePath);
            ImageView ivTakePicture = (ImageView) findViewById(R.id.ivTakePicture);
            ivSelectSavePath.setImageResource(R.drawable.ic_close_black_24dp);
            ivTakePicture.setImageResource(R.drawable.ic_check_black_24dp1);
        }
    };

    private void cancelPhoto() {
        camera.startPreview();
        pictureTaken = false;

        ImageView ivSelectSavePath = (ImageView) findViewById(R.id.ivSelectSavePath);
        ImageView ivTakePicture = (ImageView) findViewById(R.id.ivTakePicture);
        ivSelectSavePath.setImageResource(R.drawable.ic_menu_black_24dp);
        ivTakePicture.setImageResource(R.drawable.ic_camera_alt_black_24dp1);
    }

    private void listDialog(String title, final String[] values, final int mode) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        Log.e("tag", "" + values.length);
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
                    public void onClick(DialogInterface dialog,int id) {
                        dialog.cancel();
                    }
                })
                .show();
    }

    private void rangeDialog(String title, final int min, int max){
        LayoutInflater li = LayoutInflater.from(this);
        View dialogView = li.inflate(R.layout.range_dialog_layout, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setView(dialogView);

        final NumberPicker exposureRangeInput = (NumberPicker) dialogView.findViewById(R.id.npExposureRange);
        //block soft keyboard
        exposureRangeInput.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        exposureRangeInput.setMinValue(0);
        exposureRangeInput.setMaxValue(max - min);
        exposureRangeInput.setWrapSelectorWheel(false);
        exposureRangeInput.setFormatter(new NumberPicker.Formatter() {
            @Override
            public String format(int index) {
                return Integer.toString(index + min);
            }
        });
        Log.e("LOG", "min: "+0+", max: "+(max-min)+", set: "+(max+min));
        exposureRangeInput.setValue(max + min);

        alertDialogBuilder
            .setTitle(title)
            .setPositiveButton("Set",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        Log.e("TAG", ""+(exposureRangeInput.getValue()+min));
                    }
                })
            .setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

    }

}
