package vu.group6.amsterquest;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;

import org.osmdroid.api.IMapController;
import org.osmdroid.tileprovider.constants.OpenStreetMapTileProviderConstants;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;

import java.util.ArrayList;
import java.util.List;

public class QuestMapActivity extends AppCompatActivity {

    private static final String RESTAURANTS_FILENAME = "EtenDrinken.csv";
    private static final String MUSEUMS_FILENAME = "MuseaGalleries.csv";
    private static final String CLUBS_FILENAME = "UitInAmsterdam.csv";

    private MapView mapView;
    private IMapController mapController;
    private List<QuestMarker> questMarkers;
    private ItemizedIconOverlay<QuestMarker> QuestMarkersOverlay;
    private ArrayList<Parcelable> quests;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        // Setup map
        OpenStreetMapTileProviderConstants.setUserAgentValue(BuildConfig.APPLICATION_ID);

        mapView = (MapView) findViewById(R.id.quest_map);
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setBuiltInZoomControls(true);
        mapView.setMultiTouchControls(true);

        mapController = mapView.getController();
        mapController.setZoom(10);
        mapController.setCenter(Quest.AMSTERDAM_CITY_CENTER);

        questMarkers = new ArrayList<QuestMarker>();

        // get quests
        quests = getIntent().getParcelableArrayListExtra("quests");
        if (quests == null)
            quests = savedInstanceState.getParcelableArrayList("quests");
        for (Parcelable quest : getIntent().getParcelableArrayListExtra("quests")) {
            questMarkers.add(new QuestMarker((Quest) quest));
        }

        // add markers to map
        QuestMarkersOverlay = new ItemizedIconOverlay<QuestMarker>(questMarkers,
                new ItemizedIconOverlay.OnItemGestureListener<QuestMarker>() {

                    public boolean onItemSingleTapUp(int index, QuestMarker marker) {
                        Intent intent = new Intent(QuestMapActivity.this, QuestDetailsActivity.class);
                        intent.putExtra("quest", marker.quest);
                        startActivityForResult(intent, ChatActivity.REQUEST_QUEST);
                        return true;
                    }

                    public boolean onItemLongPress(int index, QuestMarker marker) {
                        return onItemSingleTapUp(index, marker);
                    }
                }, this);

        mapView.getOverlays().add(QuestMarkersOverlay);
        mapView.invalidate();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ChatActivity.REQUEST_QUEST && data != null) {
            setResult(ChatActivity.REQUEST_QUEST, data);
            finish();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList("quests", quests);
        super.onSaveInstanceState(outState);
    }
}
