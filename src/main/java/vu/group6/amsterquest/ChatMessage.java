package vu.group6.amsterquest;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

public class ChatMessage implements Parcelable {

    public boolean mine;
    public String content;
    public Uri image;

    public ChatMessage(boolean mine, String content) {
        this.mine = mine;
        this.content = content;
        this.image = null;
    }

    public ChatMessage(boolean mine, Uri image) {
        this.mine = mine;
        this.content = null;
        this.image = image;
    }

    public ChatMessage(Parcel in) {
        mine = in.readInt() == 1;
        content = in.readString();
        image = in.readParcelable(Uri.class.getClassLoader());
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mine ? 1 : 0);
        dest.writeString(content);
        dest.writeParcelable(image, flags);
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public ChatMessage createFromParcel(Parcel in) {
            return new ChatMessage(in);
        }

        public ChatMessage[] newArray(int size) {
            return new ChatMessage[size];
        }
    };
}