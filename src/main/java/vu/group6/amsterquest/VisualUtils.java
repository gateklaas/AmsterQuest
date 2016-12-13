package vu.group6.amsterquest;


import android.app.Activity;
import android.content.res.Resources;
import android.os.Environment;
import android.util.Log;

import com.ibm.watson.developer_cloud.http.ServiceCallback;
import com.ibm.watson.developer_cloud.visual_recognition.v3.VisualRecognition;
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.ClassifierOptions;
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.ClassifyImagesOptions;
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.VisualClassification;
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.VisualClassifier;

import java.io.File;

public class VisualUtils {

    private static VisualRecognition getService(Resources resources) {
        VisualRecognition service = new VisualRecognition(VisualRecognition.VERSION_DATE_2016_05_20);
        service.setApiKey(resources.getString(R.string.ticket_recognition_api_key));
        return service;
    }

    private static String getClassifierId(VisualRecognition service) {
        return service.getClassifiers().execute().get(0).getId();
    }

    public static void train(final Resources resources) {
        new Thread(new Runnable() {
            @Override
            public void run() {

                VisualRecognition service = getService(resources);
                service.deleteClassifier(getClassifierId(service)).execute();

                String ext = Environment.getExternalStorageDirectory().getPath();
                ClassifierOptions options = new ClassifierOptions.Builder().classifierName("receipt")
                        .addClass("rijksmuseum_ticket", new File(ext, "/rijksmuseum_ticket.zip"))
                        .addClass("van_gogh_ticket", new File(ext, "/van_gogh_ticket.zip"))
                        .negativeExamples(new File(ext, "/negative.zip")).build();

                VisualClassifier receipt = service.createClassifier(options).execute();
                Log.e("test", "" + receipt);
            }
        }).start();
    }

    public static void test(final Activity activity, final File testImage, final ServiceCallback<VisualClassification> callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {

                VisualRecognition service = getService(activity.getResources());
                ClassifyImagesOptions options2 = new ClassifyImagesOptions.Builder()
                        .images(testImage)
                        .classifierIds(getClassifierId(service))
                        .threshold(0.1)
                        .build();

                service.classify(options2).enqueue(callback);
            }
        }).start();
    }
}
