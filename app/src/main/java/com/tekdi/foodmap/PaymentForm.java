package com.tekdi.foodmap;

        /**
  * Created by fsd017 on 2/1/15.
  */
        public interface PaymentForm {
        public String getCardNumber();
        public String getCvc();
        public Integer getExpMonth();
        public Integer getExpYear();
    }