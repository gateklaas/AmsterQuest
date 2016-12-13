package vu.group6.amsterquest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import org.osmdroid.api.IMapController;
import org.osmdroid.tileprovider.constants.OpenStreetMapTileProviderConstants;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.io.IOException;

public class QuestMapActivity extends AppCompatActivity {

    private static final String RESTAURANTS_FILENAME = "EtenDrinken.csv";
    private static final String MUSEUMS_FILENAME = "MuseaGalleries.csv";
    private static final String CLUBS_FILENAME = "UitInAmsterdam.csv";
    private static final GeoPoint AMSTERDAM_GEOPOINT = new GeoPoint(52.36666666666667, 4.9);

    private MapView map;
    private IMapController mapController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        // Setup map
        OpenStreetMapTileProviderConstants.setUserAgentValue(BuildConfig.APPLICATION_ID);

        map = (MapView) findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setBuiltInZoomControls(true);
        map.setMultiTouchControls(true);
        mapController = map.getController();
        mapController.setZoom(10);
        mapController.setCenter(AMSTERDAM_GEOPOINT);

        // read csv
        try {
            CsvReader.read(getAssets().open(RESTAURANTS_FILENAME), new CsvReader.RowListener()
            {
                public void row(String values[]){
                    String title = values[1];
                    double latitude = Double.parseDouble(values[15].replace(',','.'));
                    double longitude = Double.parseDouble(values[16].replace(',','.'));
                    GeoPoint geoPoint = new GeoPoint(latitude, longitude);
                    Log.w("asdf", title+","+latitude+","+longitude);
                    mapController.animateTo(geoPoint);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
