package com.tekdi.foodmap;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by fsd017 on 12/29/14.
 */


public class ParcelableMenu implements Parcelable {
    Long menuId;
    Long serverId;
    String name;
    String description;
    Float price;
    String thumbnail;

    ParcelableMenu() {}

    ParcelableMenu(Parcel in) {
        this.menuId = in.readLong();
        this.serverId = in.readLong();
        this.name = in.readString();
        this.description = in.readString();
        this.price = in.readFloat();
        this.thumbnail = in.readString();
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(menuId);
        dest.writeLong(serverId);
        dest.writeString(name);
        dest.writeString(description);
        dest.writeFloat(price);
        dest.writeString(thumbnail);
    }

    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator<ParcelableMenu> CREATOR
            = new Parcelable.Creator<ParcelableMenu>() {
        public ParcelableMenu createFromParcel(Parcel in)
        {
            return new ParcelableMenu(in);
        }

        public ParcelableMenu[] newArray(int size) {
            return new ParcelableMenu[size];
        }
    };
}