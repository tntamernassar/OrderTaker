package com.example.ordertakerfrontend.BackEnd.Services.OrderDistribution;

import com.example.ordertakerfrontend.BackEnd.Logic.Order;

public class OrderDistributionRequest {

    private int tableNumber;
    private Order order;

    public OrderDistributionRequest(int tableNumber, Order order){
        this.tableNumber = tableNumber;
        this.order = order;
    }

    public int getTableNumber() {
        return tableNumber;
    }

    public Order getOrder() {
        return order;
    }

}
