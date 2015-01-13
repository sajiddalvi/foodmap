package com.tekdi.foodmap;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by fsd017 on 1/11/15.
 */
public class ParcelableFinderOrder implements  Parcelable{

    Long serverId;
    String serverName;
    String serverAddress;
    String serverPhone;
    Long menuId;
    String menuName;
    Integer quantity;
    Float price;
    Integer orderState;

    ParcelableFinderOrder() {
    }

    ParcelableFinderOrder(Parcel in) {
        this.serverId = in.readLong();
        this.serverName = in.readString();
        this.serverAddress = in.readString();
        this.serverPhone = in.readString();
        this.menuId = in.readLong();
        this.menuName = in.readString();
        this.quantity = in.readInt();
        this.price = in.readFloat();
        this.orderState = in.readInt();
    }
    public void writeToParcel(Parcel out, int flags) {
        out.writeLong(serverId);
        out.writeString(serverName);
        out.writeString(serverAddress);
        out.writeString(serverPhone);
        out.writeLong(menuId);
        out.writeString(menuName);
        out.writeInt(quantity);
        out.writeFloat(price);
        out.writeInt(orderState);
    }

    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator CREATOR
            = new Parcelable.Creator() {
        public ParcelableFinderOrder createFromParcel(Parcel in) {
            return new ParcelableFinderOrder(in);
        }

        public ParcelableFinderOrder[] newArray(int size) {
            return new ParcelableFinderOrder[size];
        }
    };
}