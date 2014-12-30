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
    Integer quantity;
    Float price;

    ParcelableMenu() {}

    ParcelableMenu(Parcel in) {
        this.menuId = in.readLong();
        this.serverId = in.readLong();
        this.name = in.readString();
        this.description = in.readString();
        this.quantity = in.readInt();
        this.price = in.readFloat();
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(menuId);
        dest.writeLong(serverId);
        dest.writeString(name);
        dest.writeString(description);
        dest.writeInt(quantity);
        dest.writeFloat(price);
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