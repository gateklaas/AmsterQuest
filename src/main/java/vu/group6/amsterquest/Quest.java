package vu.group6.amsterquest;

import android.content.res.AssetManager;
import android.os.Parcel;
import android.os.Parcelable;

import com.ibm.watson.developer_cloud.alchemy.v1.model.Document;

import org.apache.solr.common.SolrDocument;
import org.osmdroid.util.GeoPoint;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class Quest implements Parcelable {

    public static final String[] QUEST_CSVS = new String[]{"EtenDrinken.csv", "MuseaGalleries.csv", "UitInAmsterdam.csv"};
    private static final String VALUE_SEPARATOR = "\";\"";
    public static final GeoPoint AMSTERDAM_CITY_CENTER = new GeoPoint(52.36666666666667, 4.9);

    public String[] values;

    public Quest(String[] values) {
        this.values = values;
    }

    public Quest(SolrDocument doc) {
        values = new String[25];
        values[0] = (String) doc.get("Trcid");
        values[1] = (String) doc.get("Title");
        values[2] = (String) doc.get("Shortdescription");
        values[3] = (String) doc.get("Longdescription");
        values[4] = (String) doc.get("Calendarsummary");
        values[5] = (String) doc.get("TitleEN");
        values[6] = (String) doc.get("ShortdescriptionEN");
        values[7] = (String) doc.get("LongdescriptionEN");
        values[8] = (String) doc.get("CalendarsummaryEN");
        values[9] = (String) doc.get("Types");
        values[10] = (String) doc.get("Ids");
        values[11] = (String) doc.get("Locatienaam");
        values[12] = (String) doc.get("City");
        values[13] = (String) doc.get("Adres");
        values[14] = (String) doc.get("Zipcode");
        values[15] = (String) doc.get("Latitude");
        values[16] = (String) doc.get("Longitude");
        values[17] = (String) doc.get("Urls");
        values[18] = (String) doc.get("Media");
        values[19] = (String) doc.get("Thumbnail");
        values[20] = (String) doc.get("Datepattern_startdate");
        values[21] = (String) doc.get("Datepattern_enddate");
        values[22] = (String) doc.get("Singledates");
        values[23] = (String) doc.get("Type1");
        values[24] = (String) doc.get("Lastupdated");
    }

    public GeoPoint getGeoPoint() {
        double latitude = Double.parseDouble(getLatitude().replace(',', '.'));
        double longitude = Double.parseDouble(getLongitude().replace(',', '.'));
        return new GeoPoint(latitude, longitude);
    }

    public int getReward() {
        float distance = Utils.getDistanceInKm(AMSTERDAM_CITY_CENTER, getGeoPoint());
        return (int) Math.max(distance * 10 + 1, 10);
    }

    public static List<Quest> fromDocuments(AssetManager assets, final List<Document> docs) {
        final List<Quest> quests = new ArrayList<Quest>();

        for (String csvName : QUEST_CSVS) {
            String line;
            BufferedReader bin = null;
            try {
                bin = new BufferedReader(new InputStreamReader(assets.open(csvName)));
                bin.readLine();

                while ((line = bin.readLine()) != null) {
                    for (Document doc : docs) {
                        String[] values = line.split(VALUE_SEPARATOR);
                        if (doc.getId().equals(values[0])) {
                            quests.add(new Quest(values));
                        }
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (bin != null) {
                    try {
                        bin.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return quests;
    }

    public String getTrcid() {
        return values[0];
    }

    public String getTitle() {
        return values[1];
    }

    public String getShortdescription() {
        return values[2];
    }

    public String getLongdescription() {
        return values[3];
    }

    public String getCalendarsummary() {
        return values[4];
    }

    public String getTitleEN() {
        return values[5];
    }

    public String getShortdescriptionEN() {
        return values[6];
    }

    public String getLongdescriptionEN() {
        return values[7];
    }

    public String getCalendarsummaryEN() {
        return values[8];
    }

    public String getTypes() {
        return values[9];
    }

    public String getIds() {
        return values[10];
    }

    public String getLocatienaam() {
        return values[11];
    }

    public String getCity() {
        return values[12];
    }

    public String getAdres() {
        return values[13];
    }

    public String getZipcode() {
        return values[14];
    }

    public String getLatitude() {
        return values[15];
    }

    public String getLongitude() {
        return values[16];
    }

    public String getUrls() {
        return values[17];
    }

    public String getMedia() {
        return values[18];
    }

    public String getThumbnail() {
        return values[19];
    }

    public String getDatepattern_startdate() {
        return values[20];
    }

    public String getDatepattern_enddate() {
        return values[21];
    }

    public String getSingledates() {
        return values[22];
    }

    public String getType1() {
        return values[23];
    }

    public String getLastupdated() {
        return values[24];
    }

    public Quest(Parcel in) {
        values = new String[25];
        in.readStringArray(values);
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(values);
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Quest createFromParcel(Parcel in) {
            return new Quest(in);
        }

        public Quest[] newArray(int size) {
            return new Quest[size];
        }
    };
}