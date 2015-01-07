package com.tekdi.foodmap;

import com.tekdi.foodmap.backend.orderEntityApi.model.OrderEntity;

import java.util.Comparator;

/**
 * Created by fsd017 on 1/7/15.
 */
public class ComparatorOrderEntity implements Comparator<OrderEntity> {

        public int compare(OrderEntity left, OrderEntity right) {
            return left.getFinderDevRegId().compareTo(right.getFinderDevRegId());
        }

}
