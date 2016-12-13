package vu.group6.amsterquest;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.ibm.watson.developer_cloud.android.library.camera.CameraHelper;
import com.ibm.watson.developer_cloud.http.ServiceCallback;
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.VisualClassification;
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.VisualClassifier;

import java.io.FileNotFoundException;

public class MainActivity extends AppCompatActivity {

    private ImageView loadedImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loadedImage = (ImageView) findViewById(R.id.loaded_image);
        Button cameraButton = (Button) findViewById(R.id.camera_button);
        Button mapButton = (Button) findViewById(R.id.map_button);
        Button chatButton = (Button) findViewById(R.id.chat_button);

        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CameraUtils.dispatchTakePictureIntent(MainActivity.this);
            }
        });

        mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, QuestMapActivity.class));
            }
        });

        chatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, ChatActivity.class));
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CameraHelper.REQUEST_IMAGE_CAPTURE) {
            try {
                loadedImage.setImageBitmap(CameraUtils.getBitmap(MainActivity.this, resultCode));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            //VisualUtils.train();

            VisualUtils.test(MainActivity.this, CameraUtils.getFile(resultCode), new ServiceCallback<VisualClassification>() {
                @Override
                public void onResponse(VisualClassification response) {
                    if (response.getImages().isEmpty() || response.getImages().get(0).getClassifiers().isEmpty() || response.getImages().get(0).getClassifiers().get(0).getClasses().isEmpty()) {
                        Utils.toast(MainActivity.this, "Ticket not valid");
                    } else {
                        final VisualClassifier.VisualClass firstClass = response.getImages().get(0).getClassifiers().get(0).getClasses().get(0);
                        Utils.toast(MainActivity.this, "Ticket valid! " + firstClass.getName() + ", " + firstClass.getScore());
                    }
                }

                @Override
                public void onFailure(Exception e) {
                    e.printStackTrace();
                }
            });
        }
    }
}
