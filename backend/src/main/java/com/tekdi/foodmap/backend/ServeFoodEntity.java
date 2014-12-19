package com.tekdi.foodmap.backend;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

/**
 * Created by fsd017 on 12/7/14.
 */

@Entity
public class ServeFoodEntity {
    @Id
    Long id;
    private String serverRegId;
    private String name;
    private String phone;
    private String address;
    private String cuisine;
    private String latitude;
    private String longitude;

    public void setId(Long id) {
        this.id = id;
    }

    public void setServerRegId(String serverRegId) {
        this.serverRegId = serverRegId;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setCuisine(String cuisine) {
        this.cuisine = cuisine;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setLatitude(String latitude) {this.latitude = latitude; }

    public void setLongitude(String longitude) {this.longitude = longitude; }

    public Long getId() {
        return id;
    }

    public String getServerRegId() {
        return serverRegId;
    }

    public String getAddress() {
        return address;
    }

    public String getCuisine() {
        return cuisine;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public String getLatitude() { return latitude; }

    public String getLongitude() { return  longitude; }
}
