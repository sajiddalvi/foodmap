package com.tekdi.foodmap;

import com.tekdi.foodmap.backend.orderEntityApi.model.OrderEntity;

import java.util.Comparator;

public class ComparatorFinderOrderEntity implements Comparator<OrderEntity> {

    public int compare(OrderEntity left, OrderEntity right) {
        return left.getServerId().compareTo(right.getServerId());
    }

}
