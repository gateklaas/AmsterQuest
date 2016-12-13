package vu.group6.amsterquest;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;
import android.util.TypedValue;
import android.widget.ImageView;
import android.widget.Toast;

import org.osmdroid.util.GeoPoint;

import java.io.InputStream;

public class Utils {

    public static void setImageViewFromUrl(final ImageView imageView, String url) {

        AsyncTask<String, Void, Bitmap> task = new AsyncTask<String, Void, Bitmap>() {

            protected Bitmap doInBackground(String... urls) {
                String urldisplay = urls[0];
                Bitmap mIcon11 = null;
                try {
                    InputStream in = new java.net.URL(urldisplay).openStream();
                    mIcon11 = BitmapFactory.decodeStream(in);
                } catch (Exception e) {
                    Log.e("Error", e.getMessage());
                    e.printStackTrace();
                }
                return mIcon11;
            }

            protected void onPostExecute(Bitmap result) {
                imageView.setImageBitmap(result);
            }
        }.execute(url);
    }

    public static float getDistanceInKm(GeoPoint p1, GeoPoint p2) {
        float[] dist = new float[1];
        Location.distanceBetween(p1.getLatitude(), p1.getLongitude(), p2.getLatitude(), p2.getLongitude(), dist);
        return dist[0] * 0.0001f;
    }

    public static float pxToDp(Resources resources, float px) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, px, resources.getDisplayMetrics());
    }

    public static float dpToPx(Resources resources, float dp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, dp, resources.getDisplayMetrics());
    }

    public static void toast(final Activity activity, final String text) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(activity, text, Toast.LENGTH_LONG).show();
            }
        });
    }
}
