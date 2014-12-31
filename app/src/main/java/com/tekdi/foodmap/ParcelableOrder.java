package com.tekdi.foodmap;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by fsd017 on 12/19/14.
 */
public class ParcelableOrder implements Parcelable {
    Long menuId;
    Long serverId;
    String finderDevRegId;
    String serverName;
    Integer orderState;
    String name;
    String description;
    Integer quantity;
    Float price;

    ParcelableOrder() {}

    ParcelableOrder(Parcel in) {
        this.menuId = in.readLong();
        this.serverId = in.readLong();
        this.finderDevRegId = in.readString();
        this.serverName = in.readString();
        this.orderState = in.readInt();
        this.name = in.readString();
        this.description = in.readString();
        this.quantity = in.readInt();
        this.price = in.readFloat();
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(menuId);
        dest.writeLong(serverId);
        dest.writeString(finderDevRegId);
        dest.writeString(serverName);
        dest.writeInt(orderState);
        dest.writeString(name);
        dest.writeString(description);
        dest.writeInt(quantity);
        dest.writeFloat(price);
    }

    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator CREATOR
            = new Parcelable.Creator() {
        public ParcelableOrder createFromParcel(Parcel in) {
            return new ParcelableOrder(in);
        }

        public ParcelableOrder[] newArray(int size) {
            return new ParcelableOrder[size];
        }
    };
}
