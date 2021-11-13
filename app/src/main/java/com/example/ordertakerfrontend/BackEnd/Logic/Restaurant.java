package com.example.ordertakerfrontend.BackEnd.Logic;

import java.io.Serializable;
import java.util.HashMap;

public class Restaurant implements Serializable {

    private HashMap<Integer, Table> tables;
    private OrderHistory orderHistory;

    public Restaurant(){
        this.tables = new HashMap<>();
    }

    public int[] getTables() {
        int [] res = new int[tables.size()];
        int index = 0;
        for (Integer i : tables.keySet()){
            res[index++] = i;
        }
        return res;
    }

    public Table getTable(int number) {
        return tables.get(number);
    }


    public OrderHistory getOrderHistory() {
        return orderHistory;
    }

    public void setOrderHistory(OrderHistory orderHistory) {
        this.orderHistory = orderHistory;
    }

    public void addTable(Table table) {
        this.tables.put(table.getNumber(), table);
    }

    @Override
    public String toString() {
        return "Restaurant{" +
                "tables=" + tables +
                ", orderHistory=" + orderHistory +
                '}';
    }
}
