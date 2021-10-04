package com.example.ordertakerfrontend.BackEnd.Logic;



import com.example.ordertakerfrontend.BackEnd.Services.OrderDistribution.OrderDistributionRequest;
import com.example.ordertakerfrontend.BackEnd.Services.OrderDistribution.OrderDistributor;
import com.example.ordertakerfrontend.BackEnd.Services.Utils;


public class Waitress {


    private Restaurant restaurant;
    private String name;
    private boolean inited = false;


    /**
     *  Waitress class is responsible for the interaction
     *  with the restaurant tables
     *  the functionalities that you can ask a waitress in a restaurant should be here
     * **/
    public Waitress(String name, Restaurant restaurant){
        this.restaurant = restaurant;
        this.name = name;
        inited = true;

    }

    public Restaurant getRestaurant() {
        return restaurant;
    }

    public String getName() {
        return name;
    }


    /**
     * open the table {@param table}
     *
     * label the table as opened and notify rest of the observers
     * **/
    public void openTable(int table){
        Table theTable = restaurant.getTable(table);
        if (!theTable.isActive()){
            theTable.startOrder(getName());
            Utils.writeToLog("Opened Table " + table);
        }else {
            Utils.writeToLog("Tried to open Table " + table + " While it Was opened");
        }
    }

    /**
     * order the given item for table {@param table}
     *
     * add the given item to the table order
     * if the table is not active, then treat it as the first order and start and order
     *
     * returns the index of the item in the order
     * **/
    public int orderItem(int table, Product product, int quantity, String notes){
        if(!this.restaurant.getTable(table).isActive()){
            this.restaurant.getTable(table).startOrder(getName());
        }
        Utils.writeToLog("Table " + table + " Ordered Item : name : " + product.toString() + " quantity : " + quantity + " notes : " + notes);
        return this.restaurant.getTable(table).getCurrentOrder().addItem(this.getName(), product, quantity, notes);
    }


    /**
     * remove the item with index {@param orderItem} from the order of  {@param table}
     *
     * if the table is active remove the given item from the order*
     * **/
    public void removeItem(int table, int orderItem){
        Table theTable = restaurant.getTable(table);
        if(theTable.isActive()){
            theTable.getCurrentOrder().removeItem(orderItem);
        }
        Utils.writeToLog("Table " + table + " Removed Item number : " + orderItem);
    }

    /**
     * edit the given item index in the order of table {@param table}
     *
     * set the item with index {@param orderItem} to be
     * the item given in the 3-most right params
     * **/
    public void editOrder(int table, int orderItem, Product product, int quantity, String notes){
        restaurant.getTable(table).getCurrentOrder().editOrder(orderItem, product, quantity, notes);
        Utils.writeToLog("Table " + table + " Edited Item number : " + orderItem + " new name : " + product.toString() + " new quantity : " + quantity + " new notes : " + notes);
    }

    /**
     * submits order of table {@param table}
     *
     * notify rest of observers for table changes
     * update local config
     *
     * if the order is not distributed yet
     * distribute the order over OrderDistributor, mark as distributed and set the distribution version
     *
     * if the order is already distribute,
     * distribute an edit with the last and new versions
     *
     * returns the submitted order if table is active, otherwise null
     * **/
    public Order submitOrder(int table){
        if(restaurant.getTable(table).isActive()){
            Table theTable = restaurant.getTable(table);
            Order currentOrder = theTable.getCurrentOrder();
            if(currentOrder.getOrderItems().size() > 0){
                currentOrder.distributeItems();
                if(currentOrder.isDistributed()){
                    // the order already distributed
                    Order distributedVersion = currentOrder.getDistributeVersion();
                    OrderDistributor.distributeEdit(distributedVersion, new OrderDistributionRequest(table, currentOrder));
                    currentOrder.setDistributeVersion(currentOrder.clone());
                    Utils.writeToLog("Table " + table + " ReSubmitted an order");
                }else{
                    // this is the first order from this table
                    OrderDistributor.distribute(new OrderDistributionRequest(table, currentOrder));
                    currentOrder.setDistributed(true);
                    currentOrder.setDistributeVersion(currentOrder.clone());
                    Utils.writeToLog("Table " + table + " Submitted the order");
                }
                return currentOrder;
            }else{
                Utils.writeToLog("Order contains no item");
                return null;
            }
        }else{
            Utils.writeToLog("Table " + table + " Tried to submit a closed table !");
            return null;
        }
    }


    /**
     * close order of table {@param table}
     *
     * notify rest of observers for table changes
     *
     * close the order of the table
     * and add the order to the restaurant order history
     *
     * returns the closed order if table is active, otherwise null
     * **/
    public Order closeOrder(int table){
        Table theTable = restaurant.getTable(table);
        if(theTable.isActive()){
            if(theTable.getCurrentOrder().getOrderItems().size() > 0){
                boolean added = this.restaurant.getOrderHistory().addToHistory(table, theTable.getCurrentOrder());
                if(added) {
                    this.restaurant.getOrderHistory().write();
                }
            }
            Order closedOrder = theTable.closeOrder();
            Utils.writeToLog("Table " + table + " Closed the order");
            return closedOrder;
        }else{
            Utils.writeToLog("Table " + table + " Tried to close a closed table");
            return null;
        }
    }


    /**
     * cancel the order of table {@param table}
     *
     * notify rest of observers for table changes
     *
     * close the order of the table without saving it to history
     *
     * returns the canceled order if table is active, otherwise null
     * **/
    public Order cancelOrder(int table){
        Table theTable = restaurant.getTable(table);
        if(restaurant.getTable(table).isActive()){
            Order closedOrder = theTable.closeOrder();
            Utils.writeToLog("Table " + table + " Canceled the order");
            return closedOrder;
        }else{
            Utils.writeToLog("Table " + table + " Tried to cancel a closed table");
            return null;
        }
    }

}
