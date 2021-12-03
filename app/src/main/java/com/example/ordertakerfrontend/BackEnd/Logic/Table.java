package com.example.ordertakerfrontend.BackEnd.Logic;

import org.json.JSONException;
import org.json.JSONObject;
import java.io.Serializable;
import java.time.LocalDateTime;

public class Table implements Serializable {

    private int number;
    private boolean isActive;
    private LocalDateTime closedAt;
    private Order currentOrder;

    public Table(int number){
        this.number = number;
        this.isActive = false;
        this.closedAt = null;
        this.currentOrder = null;
    }


    public Table(JSONObject table) throws JSONException {
        int number = (int) table.get("number");
        boolean isActive = (boolean) table.get("isActive");
        String closedAt = null;
        if (!table.isNull("closedAt")) {
            closedAt = (String) table.get("closedAt");
        }

        Order currentOrder = null;
        if (!table.isNull("currentOrder")) {
            currentOrder = new Order((JSONObject) table.get("currentOrder"));
        }
        this.number = number;
        this.isActive = isActive;
        this.closedAt = closedAt != null ? LocalDateTime.parse(closedAt) : null;
        this.currentOrder = currentOrder;
    }

    public LocalDateTime getClosedAt() {
        return closedAt;
    }

    public void setClosedAt(LocalDateTime closedAt) {
        this.closedAt = closedAt;
    }

    public void setTable(Table table){
        this.number = table.getNumber();
        this.isActive = table.isActive();
        this.closedAt = table.getClosedAt();
        this.currentOrder = table.getCurrentOrder();
    }

    public void mergeTable(Table table){
        currentOrder.mergerOrder(table.getCurrentOrder());
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



    public Order startOrder(int table, String startedBy){
        return this.startOrder(new Order(table, startedBy));
    }

    public Order startOrder(Order order){
        this.isActive = true;
        this.currentOrder = order;
        this.closedAt = null;
        return this.currentOrder;
    }

    public Order closeOrder(){
        Order o = this.currentOrder;
        this.currentOrder = null;
        this.isActive = false;
        this.closedAt = LocalDateTime.now();
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
        return toJSON().toString();
    }

    public JSONObject toJSON(){
        try {
            JSONObject res = new JSONObject();
            res.put("number", number);
            res.put("isActive", isActive);
            res.put("closedAt", closedAt);
            res.put("currentOrder", currentOrder != null ? currentOrder.toJSON() : null);
            return res;
        }catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
