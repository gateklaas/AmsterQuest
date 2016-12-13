package vu.group6.amsterquest;

import org.osmdroid.api.IGeoPoint;
import org.osmdroid.views.overlay.OverlayItem;

public class QuestMarker extends OverlayItem {

    public Quest quest;

    public QuestMarker(Quest quest) {
        super(quest.getTrcid(), quest.getTitleEN(), quest.getShortdescriptionEN(), quest.getGeoPoint());
        this.quest = quest;
    }
}
