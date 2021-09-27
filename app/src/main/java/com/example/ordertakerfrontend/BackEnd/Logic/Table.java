package com.example.ordertakerfrontend.BackEnd.Logic;



import com.example.ordertakerfrontend.BackEnd.Services.Utils;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicLong;

public class Table implements Serializable {

    private int number;
    private boolean isActive;
    private Order currentOrder;
    private String startedAt;
    private String closedAt;


    public Table(int number){
        this.number = number;
        this.isActive = false;
        this.currentOrder = null;
    }

    public Table(int number,int version){
        this.number = number;
        this.isActive = false;
        this.currentOrder = null;
    }

    public int getNumber() {
        return number;
    }

    public void setCurrentOrder(Order currentOrder) {
        this.currentOrder = currentOrder;
    }

    public boolean isActive() {
        return isActive;
    }

    public Order getCurrentOrder() {
        return currentOrder;
    }

    public String getStartedAt() {
        return startedAt;
    }

    public String getClosedAt() {
        return closedAt;
    }

    public Order startOrder(String startedBy){
        return this.startOrder(new Order(startedBy));
    }

    public Order startOrder(Order order){
        this.isActive = true;
        this.currentOrder = order;
        this.startedAt = Utils.getTimeStamp();
        return this.currentOrder;
    }

    public Order closeOrder(){
        Order o = this.currentOrder;
        this.currentOrder = null;
        this.isActive = false;
        this.closedAt = Utils.getTimeStamp();
        return o;
    }

    public boolean containsOrder(String waiterName, String timestamp){
        return getOrderItem(waiterName, timestamp) != null;
    }

    public OrderItem getOrderItem(String waiterName, String timestamp){
        for(Integer i : this.currentOrder.getOrderItems().keySet()){
            OrderItem orderItem = this.currentOrder.getOrderItems().get(i);
            if(orderItem.getWaiterName().equals(waiterName) && orderItem.getTimestamp().equals(timestamp)){
                return orderItem;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return "Table {" + "\n\t" +
                "number=" + number + ",\n\t" +
                "isActive=" + isActive + ",\n\t" +
                "currentOrder=" + currentOrder + "\n" +
                "}";
    }
}
