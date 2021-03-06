package com.example.ordertakerfrontend.BackEnd.Logic;

import com.example.ordertakerfrontend.BackEnd.Services.Utils;
import com.example.ordertakerfrontend.FrontEnd.Menus.OrderProduct;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class Order implements Serializable {

    private int table;
    private int numberOfPeople;
    private AtomicInteger itemsCounter;
    private LocalDateTime startedAt;
    private String startedBy;
    private boolean distributed;
    private Order distributeVersion;
    private HashMap<Integer, OrderItem> orderItems;



    public Order(int table, String startedBy){
        this.table = table;
        this.startedBy = startedBy;
        this.itemsCounter = new AtomicInteger(0);
        this.distributed = false;
        this.distributeVersion = null;
        this.orderItems = new HashMap<>();
        this.startedAt = LocalDateTime.now();
        this.numberOfPeople = -1;
    }

    public Order(JSONObject order) throws JSONException {
        this.orderItems = new HashMap<>();

        int table = (int) order.get("table");
        int itemsCounter = (int) order.get("itemsCounter");
        String startedAt = (String) order.get("startedAt");
        String startedBy = (String) order.get("startedBy");
        boolean distributed = (boolean) order.get("distributed");
        int numberOfPeople = (int) order.get("numberOfPeople");

        Order distributeVersion = null;
        if (!order.isNull("distributeVersion")) {
            distributeVersion = new Order((JSONObject) order.get("distributeVersion"));
        }
        JSONObject orderItems = (JSONObject) order.get("orderItems");
        JSONArray indexes = (JSONArray) orderItems.get("indexes");
        for (int i = 0; i < indexes.length(); i++) {
            String indexStr = (String) indexes.get(i);
            int index = Integer.parseInt(indexStr);
            JSONObject orderItemObject = (JSONObject) orderItems.get(indexStr);
            OrderItem orderItem = new OrderItem(orderItemObject, new OrderProduct((JSONObject) orderItemObject.get("product")));
            this.orderItems.put(index, orderItem);
        }

        this.table = table;
        this.itemsCounter = new AtomicInteger((int) itemsCounter);
        this.startedAt = LocalDateTime.parse(startedAt);
        this.startedBy = startedBy;
        this.distributed = distributed;
        this.distributeVersion = distributeVersion;
        this.numberOfPeople = numberOfPeople;

    }

    public OrderItem getOrderItem(OrderItem orderItem){
        for (OrderItem o : orderItems.values()){
            if (o.equals(orderItem)){
                return o;
            }
        }
        return null;
    }

    public synchronized void mergerOrder(Order order){
        HashMap<Integer, OrderItem> otherOrderItems = order.getOrderItems();

        for (Integer index : otherOrderItems.keySet()){
            OrderItem otherOrderItem = otherOrderItems.get(index);
            OrderItem tabletOrderItem = getOrderItem(otherOrderItem);

            if (tabletOrderItem == null){ // new order item
                this.addItem(otherOrderItem.getWaiterName(), otherOrderItem.getTimestamp(), otherOrderItem.getProduct(), otherOrderItem.getQuantity(), otherOrderItem.getNotes(), otherOrderItem.isDistributed(), otherOrderItem.isDeleted());
            } else {
                tabletOrderItem.setProduct(otherOrderItem.getProduct());
                tabletOrderItem.setQuantity(otherOrderItem.getQuantity());
                tabletOrderItem.setDistributed(otherOrderItem.isDistributed());
                tabletOrderItem.setNotes(otherOrderItem.getNotes());
                tabletOrderItem.setDeleted(tabletOrderItem.isDeleted() || otherOrderItem.isDeleted());
            }
        }
        setDistributeVersion(order.getDistributeVersion());
        setDistributed(order.isDistributed());
    }

    public void distributeItems(){
        for(OrderItem orderItem : orderItems.values()){
            orderItem.setDistributed(true);
        }
    }

    public int getTable() {
        return table;
    }

    public int getNumberOfPeople() {
        return numberOfPeople;
    }

    public boolean isDistributed() {
        return distributed;
    }

    public void setDistributed(boolean distributed) {
        this.distributed = distributed;
    }

    public void setNumberOfPeople(int numberOfPeople) {
        this.numberOfPeople = numberOfPeople;
    }

    public Order getDistributeVersion() {
        return distributeVersion;
    }

    public void setDistributeVersion(Order distributeVersion) {
        this.distributeVersion = distributeVersion;
    }


    public void setStartedAt(LocalDateTime startedAt) {
        this.startedAt = startedAt;
    }

    public LocalDateTime getStartedAt() {
        return startedAt;
    }

    public String getStartedBy() {
        return startedBy;
    }

    public HashMap<Integer, OrderItem> getOrderItems() {
        return orderItems;
    }

    public int addItem(String waiterName, Product product, int quantity, String notes, boolean deleted){
        int newItemIndex = itemsCounter.incrementAndGet();
        this.orderItems.put(newItemIndex, new OrderItem(newItemIndex, waiterName, product, quantity, notes, deleted));
        return newItemIndex;
    }

    public int addItem(String waiterName, String timestamp, Product product, int quantity, String notes, boolean distributed, boolean deleted){
        int newItemIndex = itemsCounter.incrementAndGet();
        this.orderItems.put(newItemIndex, new OrderItem(newItemIndex, timestamp, waiterName, product, quantity, notes, distributed, deleted));
        return newItemIndex;
    }

    public void removeItem(int itemIndex){
        this.orderItems.get(itemIndex).setDeleted(true);
    }

    public void editOrder(int itemIndex, Product newProduct, int newQuantity, String newNotes){
        if(this.orderItems.containsKey(itemIndex)){
            this.orderItems.get(itemIndex).setProduct(newProduct);
            this.orderItems.get(itemIndex).setQuantity(newQuantity);
            this.orderItems.get(itemIndex).setNotes(newNotes);
        }
    }

    public double calculatePrice(){
        double price = 0;
        for (OrderItem orderItem : getOrderItems().values()) {
            if( !orderItem.isDeleted() ) {
                double orderItemPrice = orderItem.getQuantity() * ((OrderProduct) orderItem.getProduct()).getMenuProduct().getPrice();
                price += orderItemPrice;
            }
        }
        return price;
    }

    public Order clone() {
        Order o = new Order(table, getStartedBy());
        for(Integer oi: this.orderItems.keySet()){
            OrderItem orderItem = this.orderItems.get(oi);
            o.orderItems.put(oi, new OrderItem(orderItem.getIndex(), orderItem.getTimestamp(), orderItem.getWaiterName(), orderItem.getProduct().clone(), orderItem.getQuantity(), orderItem.getNotes(), orderItem.isDistributed(), orderItem.isDeleted()));
        }
        return o;
    }

    @Override
    public String toString() {
        return "Order {" + "\n\t" +
                "startedAt = " + Utils.dateToString(this.startedAt) + "\n\t" +
                "orderItems = " + orderItems + "\n" +
                '}';
    }

    public JSONObject toJSON(){
        try{
            JSONObject res = new JSONObject();
            res.put("table", table);
            res.put("itemsCounter", itemsCounter.get());
            res.put("startedAt", startedAt.toString());
            res.put("startedBy", startedBy);
            res.put("distributed", distributed);
            res.put("distributeVersion", distributeVersion != null ? distributeVersion.toJSON() : null);
            res.put("numberOfPeople", numberOfPeople);

            JSONObject orderItemsObject = new JSONObject();
            JSONArray indexes = new JSONArray();
            for (Integer index : orderItems.keySet()){
                indexes.put(index.toString());
                orderItemsObject.put(String.valueOf(index), orderItems.get(index).toJSON());
            }
            orderItemsObject.put("indexes", indexes);
            res.put("orderItems", orderItemsObject);
            return res;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }
}
