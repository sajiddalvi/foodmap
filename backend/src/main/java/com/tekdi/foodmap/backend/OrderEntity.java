package com.tekdi.foodmap.backend;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

/**
 * Created by fsd017 on 12/19/14.
 */

@Entity
public class OrderEntity {

    @Id
    Long id;
    @Index
    Long menuId;
    @Index
    Long serverId;
    @Index
    String finderDevRegId;
    Integer orderState;
    String menuName;
    Integer quantity;
    Float price;

    public Long getId() {
        return id;
    }

    public Long getMenuId() {
        return menuId;
    }

    public Long getServerId() {
        return serverId;
    }

    public String getFinderDevRegId() {
        return finderDevRegId;
    }

    public Integer getOrderState() {
        return orderState;
    }

    public String getMenuName() { return menuName; }

    public Integer getQuantity() { return quantity; }

    public Float getPrice() { return price; }

    public void setId(Long id) {
        this.id = id;
    }

    public void setMenuId(Long menuId) {
        this.menuId = menuId;
    }

    public void setServerDevRegId(Long serverId) {
        this.serverId = serverId;
    }

    public void setFinderDevRegId(String finderDevRegId) {
        this.finderDevRegId = finderDevRegId;
    }

    public void setOrderState(Integer orderState) {
        this.orderState = orderState;
    }

    public void setMenuName(String menuName) { this.menuName = menuName; }

    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public void setPrice(Float price) { this.price = price; }
}
