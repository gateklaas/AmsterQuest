package vu.group6.amsterquest;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.ibm.watson.developer_cloud.android.library.camera.CameraHelper;

public class MainActivity extends AppCompatActivity {

    private CameraHelper cameraHelper;
    private Button cameraButton;
    private Button mapButton;
    private ImageView loadedImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topic);

        loadedImage = (ImageView) findViewById(R.id.loaded_image);
        cameraButton = (Button) findViewById(R.id.camera_button);
        mapButton = (Button) findViewById(R.id.map_button);

        cameraHelper = new CameraHelper(this);

        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraHelper.dispatchTakePictureIntent();
            }
        });

        mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, MapActivity.class));
            }
        });
    }

    private boolean checkPermissions() {
        String permissions[] = {android.Manifest.permission.CAMERA, android.Manifest.permission.WRITE_EXTERNAL_STORAGE};
        boolean grantCamera = ContextCompat.checkSelfPermission(this, permissions[0]) == PackageManager.PERMISSION_GRANTED;
        boolean grantExternal = ContextCompat.checkSelfPermission(this, permissions[1]) == PackageManager.PERMISSION_GRANTED;

        if (!grantCamera && !grantExternal) {
            ActivityCompat.requestPermissions(this, permissions, cameraHelper.REQUEST_PERMISSION);
        } else if (!grantCamera) {
            ActivityCompat.requestPermissions(this, new String[]{permissions[0]}, cameraHelper.REQUEST_PERMISSION);
        } else if (!grantExternal) {
            ActivityCompat.requestPermissions(this, new String[]{permissions[1]}, cameraHelper.REQUEST_PERMISSION);
        }

        return grantCamera && grantExternal;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case CameraHelper.REQUEST_PERMISSION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    cameraHelper.dispatchTakePictureIntent();
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CameraHelper.REQUEST_IMAGE_CAPTURE) {
            loadedImage.setImageBitmap(cameraHelper.getBitmap(resultCode));
        }
    }
}
