package com.tekdi.foodmap.backend;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import java.util.Date;

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
    String finderPhone;
    String serverName;
    String serverAddress;
    String serverPhone;
    Integer orderState;
    Integer prevOrderState;
    String menuName;
    Integer quantity;
    Float price;
    Date timestamp;

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

    public String getServerName() { return serverName; }

    public Integer getOrderState() {
        return orderState;
    }

    public Integer getPrevOrderState() {
        return prevOrderState;
    }

    public String getMenuName() { return menuName; }

    public Integer getQuantity() { return quantity; }

    public Float getPrice() { return price; }

    public Date getTimestamp() { return timestamp; }

    public String getServerAddress() { return serverAddress; }

    public String getServerPhone() { return serverPhone; }

    public String getFinderPhone() { return finderPhone; }

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

    public void setServerName(String serverName) { this.serverName = serverName; }

    public void setOrderState(Integer orderState) {
        this.orderState = orderState;
    }

    public void setPrevOrderState(Integer orderState) {
        this.prevOrderState = orderState;
    }

    public void setMenuName(String menuName) { this.menuName = menuName; }

    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public void setPrice(Float price) { this.price = price; }

    public void setTimestamp(Date timestamp) { this.timestamp = timestamp; }

    public void setServerAddress(String address) { this.serverAddress = address; }

    public void setServerPhone(String phone) { this.serverPhone = phone; }

    public void setFinderPhone(String phone) { this.finderPhone = phone; }
}
