package com.example.ordertakerfrontend.BackEnd.Logic;



import com.example.ordertakerfrontend.BackEnd.Services.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicLong;

public class Table implements Serializable {

    private int number;
    private boolean isActive;
    private Order currentOrder;


    public Table(int number){
        this.number = number;
        this.isActive = false;
        this.currentOrder = null;
    }


    public Table(JSONObject table) throws JSONException {

        int number = (int) table.get("number");
        boolean isActive = (boolean) table.get("isActive");
        Order currentOrder = null;
        if (!table.isNull("currentOrder")) {
            currentOrder = new Order((JSONObject) table.get("currentOrder"));
        }
        this.number = (int) number;
        this.isActive = isActive;
        this.currentOrder = currentOrder;
    }

    public void setTable(Table table){
        this.number = table.getNumber();
        this.isActive = table.isActive();
        this.currentOrder = table.getCurrentOrder();
    }

    public void mergeTable(Table table, String tabletWaitressName){
        currentOrder.mergerOrder(table.getCurrentOrder(), tabletWaitressName);
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



    public Order startOrder(String startedBy){
        return this.startOrder(new Order(startedBy));
    }

    public Order startOrder(Order order){
        this.isActive = true;
        this.currentOrder = order;
        return this.currentOrder;
    }

    public Order closeOrder(){
        Order o = this.currentOrder;
        this.currentOrder = null;
        this.isActive = false;
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

    public JSONObject toJSON(){
        try {
            JSONObject res = new JSONObject();
            res.put("number", number);
            res.put("isActive", isActive);
            res.put("currentOrder", currentOrder != null ? currentOrder.toJSON() : null);
            return res;
        }catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
