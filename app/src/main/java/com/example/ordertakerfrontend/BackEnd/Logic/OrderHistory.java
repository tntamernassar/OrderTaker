package com.example.ordertakerfrontend.BackEnd.Logic;


import com.example.ordertakerfrontend.BackEnd.Services.FileManager;

import java.io.Serializable;
import java.util.LinkedList;

public class OrderHistory implements Serializable {

    private LinkedList<Order> orders;

    public OrderHistory(LinkedList<Order> orders){
        this.orders = orders;
    }


    public LinkedList<Order> getOrders() {
        return orders;
    }

    public boolean write(String filename){
        return FileManager.writeObject(this, filename);
    }
}
