package fr.insa_lyon.bg4if.trollgame;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.FrameLayout;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static android.hardware.Camera.getCameraInfo;
import static android.hardware.Camera.getNumberOfCameras;
import static android.hardware.Camera.open;


public class MainActivity extends ActionBarActivity {

    public static final String TAG = MainActivity.class.getName();
    public static boolean safeToTakePicture = false;
    private Camera mCamera;
    private CameraPreview mPreview;
    private Context mContext;

    public static final int MEDIA_TYPE_IMAGE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public void onStart() {
        super.onStart();
        // Create an instance of Camera
        mCamera = getFrontFacingCamera();
        Camera.Parameters params = mCamera.getParameters();
        List<Camera.Size> sizes = params.getSupportedPictureSizes();
        int height = 0 ;
        int width = 0 ;
        // Choose the best size for the picture and server
        for(Camera.Size size : sizes){
            if(size.height>height && size.width<1000){
                height = size.height;
                width = size.width;
            }
        }
        params.setPictureSize(width, height);
        params.setRotation(270);
        mCamera.setParameters(params);


        // Create our Preview view and set it as the content of our activity.
        mPreview = new CameraPreview(this, mCamera);
        FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
        preview.addView(mPreview);

        // Add a listener to the Capture button
        Button captureButton = (Button) findViewById(R.id.button_capture);

        captureButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // get an image from the camera
                        if (safeToTakePicture) {
                            mCamera.takePicture(null, null, mPicture);
                            safeToTakePicture = false;
                        }
                    }
                }
        );
    }

    private Camera getFrontFacingCamera() {
        int cameraCount = 0;
        Camera cam = null;
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        cameraCount = getNumberOfCameras();
        for (int camIdx = 0; camIdx < cameraCount; camIdx++) {
            getCameraInfo(camIdx, cameraInfo);
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                try {
                    cam = open(camIdx);
                    Log.d("Main", "FrontCamera Opened");
                } catch (RuntimeException e) {
                    Log.e("Main", "Camera failed to open: " + e.getLocalizedMessage());
                }
            }
        }
        return cam;
    }

    private Camera.PictureCallback mPicture = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {

            File pictureFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);
            if (pictureFile == null){
                Log.d(TAG, "Error creating media file, check storage permissions: ");
                return;
            }

            try {
                FileOutputStream fos = new FileOutputStream(pictureFile);
                fos.write(data);
                fos.close();
                FileSyncService.startSync(MainActivity.this);
            } catch (FileNotFoundException e) {
                Log.d(TAG, "File not found: " + e.getMessage());
            } catch (IOException e) {
                Log.d(TAG, "Error accessing file: " + e.getMessage());
            }
        }
    };

    /** Create a File for saving an image */
    public File getOutputMediaFile(int type){
        File mediaStorageDir = new File(this.getApplicationContext().getFilesDir().getPath());

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                Log.d("MyCameraApp", "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_"+ timeStamp + ".jpg");
        try {
            mediaFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return mediaFile;
    }
}
