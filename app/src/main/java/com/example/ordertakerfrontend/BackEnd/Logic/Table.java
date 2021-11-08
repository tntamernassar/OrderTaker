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
    private String startedAt;
    private String closedAt;


    public Table(int number){
        this.number = number;
        this.isActive = false;
        this.currentOrder = null;
        this.startedAt = "";
        this.closedAt = "";
    }


    public Table(JSONObject table) throws JSONException {

        int number = (int) table.get("number");
        boolean isActive = (boolean) table.get("isActive");
        String startedAt = (String) table.get("startedAt");
        String closedAt = (String) table.get("closedAt");
        Order currentOrder = null;
        if (table.get("currentOrder") != null) {
            currentOrder = new Order((JSONObject) table.get("currentOrder"));
        }
        this.number = (int) number;
        this.isActive = isActive;
        this.startedAt = startedAt;
        this.closedAt = closedAt;
        this.currentOrder = currentOrder;
    }

    public void setTable(Table table){
        this.number = table.getNumber();
        this.isActive = table.isActive();
        this.startedAt = table.getStartedAt();
        this.closedAt = table.getClosedAt();
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

    public String getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(String startedAt) {
        this.startedAt = startedAt;
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

    public JSONObject toJSON(){
        try {
            JSONObject res = new JSONObject();
            res.put("number", number);
            res.put("isActive", isActive);
            res.put("currentOrder", currentOrder != null ? currentOrder.toJSON() : null);
            res.put("startedAt", startedAt);
            res.put("closedAt", closedAt);
            return res;
        }catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
