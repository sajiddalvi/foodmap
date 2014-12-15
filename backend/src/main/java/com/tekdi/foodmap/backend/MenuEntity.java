package com.tekdi.foodmap.backend;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

@Entity
public class MenuEntity {
    @Id
    Long id;
    Long serverId;
    String name;
    String description;
    Integer quantity;
    Float price;

    public Long getId() {
        return id;
    }

    public Long getServerId() { return serverId; }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public Float getPrice() {
        return price;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setServerId(Long serverId) { this.serverId = serverId; }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public void setPrice(Float price) {
        this.price = price;
    }
}