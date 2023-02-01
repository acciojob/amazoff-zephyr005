package com.driver;

import org.springframework.stereotype.Repository;
import org.w3c.dom.stylesheets.LinkStyle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Repository
public class OrderRepository {
    private HashMap<String,Order> orderDB;

    private HashMap<String,DeliveryPartner> partnerDB;

    private HashMap<String, List<String>> orderPartnerPairDB;
    //Partner --> List<Order>  pair

    private List<String> orders;

    public OrderRepository() {
        this.orderDB = new HashMap<>();
        this.partnerDB = new HashMap<>();
        this.orderPartnerPairDB = new HashMap<>();
        this.orders = new ArrayList<>();
    }

    public void saveOrder(Order order) {
        orders.add(order.getId());
        orderDB.put(order.getId(), order);
    }



    public void savePartner(String partnerId) {
        partnerDB.put(partnerId, new DeliveryPartner(partnerId));
    }


    public void saveOrderPartnerPair(String orderId, String partnerId) {
        if(partnerDB.containsKey(partnerId) && orderDB.containsKey(orderId)){
            List<String> orderList = new ArrayList<>();
            if(orderPartnerPairDB.containsKey(partnerId)){
                orderList = orderPartnerPairDB.get(partnerId);
            }

            orderList.add(orderId);

            orderPartnerPairDB.put(partnerId, orderList);
        }
    }

    public Order findOrderById(String orderId) {
        if(orderDB.containsKey(orderId)){
            return orderDB.get(orderId);
        }
        return null;
    }

    public DeliveryPartner findPartnerBy(String partnerId) {
        if(partnerDB.containsKey(partnerId)){
            return partnerDB.get(partnerId);
        }
        return null;
    }

    public Integer findOrderCountByPartnerId(String partnerId) {
        if(orderPartnerPairDB.containsKey(partnerId)){
            return orderPartnerPairDB.get(partnerId).size();
        }
        return 0;
    }

    public List<String> findOrdersByPartnerId(String partnerId) {
        if(orderPartnerPairDB.containsKey(partnerId)){
            List<String> orders = orderPartnerPairDB.get(partnerId);
            return orders;
        }
        else{
            return null;
        }
    }

    public List<String> findAllOrders() {
        List<String> allOrders = orders;
        return allOrders;
    }

    public Integer findCountOfUnassignedOrders() {
        int countOfAssignedOrders = 0;
        for(List<String> orders1 : orderPartnerPairDB.values()){
            countOfAssignedOrders += orders1.size();
        }

        int countOfAllOrders = orders.size();

        int countOfUnassignedOrders = countOfAllOrders - countOfAssignedOrders;

        return countOfUnassignedOrders;
    }

    public Integer findOrdersLeftAfterGivenTimeByPartnerId(String time, String partnerId) {
        int hour = Integer.parseInt(time.substring(0,2));
        int min = Integer.parseInt(time.substring(3,5));
        int givenTime = hour*60 + min;
        int leftOrders = 0;
        for(List<String> orders : orderPartnerPairDB.values()){
            for(String order1 : orders){
                int orderTime = orderDB.get(order1).getDeliveryTime();
                if(orderTime > givenTime){
                    leftOrders++;
                }
            }
        }

        return leftOrders;
    }

    public String findLastDeliveryTimeByPartnerId(String partnerId) {
        int lastTime = 0;
        int maxTime = Integer.MIN_VALUE;
        if(orderPartnerPairDB.containsKey(partnerId)){
            for(String order : orderPartnerPairDB.get(partnerId)){
                lastTime = Math.max(maxTime, orderDB.get(order).getDeliveryTime());
            }
        }
        int hour = lastTime/60;
        int min = lastTime%60;

        String lastDeliveredTime = null;

        if(hour/10 == 0){
            lastDeliveredTime += "0" + hour + ":";
        }
        else{
            lastDeliveredTime += hour+ ":";
        }

        if(min/10 == 0){
            lastDeliveredTime += "0" + min;
        }
        else{
            lastDeliveredTime += min;
        }

        return lastDeliveredTime;
    }

    public void deletePartnerById(String partnerId) {
        if(orderPartnerPairDB.containsKey(partnerId)){
            orderPartnerPairDB.remove(partnerId);
        }

        if(partnerDB.containsKey(partnerId)){
            partnerDB.remove(partnerId);
        }
    }

    public void deleteOrderById(String orderId) {
        for(List<String> orders : orderPartnerPairDB.values()){
            for(String order : orders){
                if(order.equals(orderId)){
                    orders.remove(orderId);
                }
            }
        }

        if(orderDB.containsKey(orderId)){
            orderDB.remove(orderId);
        }

        orders.remove(orderId);
    }
}
