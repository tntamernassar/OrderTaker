package com.example.ordertakerfrontend.Network.NetworkMessages.In;

import com.example.ordertakerfrontend.BackEnd.Logic.Order;
import com.example.ordertakerfrontend.BackEnd.Logic.OrderHistory;
import com.example.ordertakerfrontend.BackEnd.Logic.Waitress;
import com.example.ordertakerfrontend.Network.NetworkMessages.tools.NetworkMessage;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.LinkedList;

public class OrderHistoryContainer extends NetworkMessage {

    public JSONArray history;

    public OrderHistoryContainer(JSONArray history){
        this.history = history;
    }

    @Override
    public void visit(Waitress waitress) {
        try {
            LinkedList<Order> orders = new LinkedList<>();
            for (int i = 0; i < history.length(); i++){
                Order order = new Order((JSONObject) history.get(i));
                orders.add(order);
            }
            waitress.getRestaurant().setOrderHistory(new OrderHistory(orders));
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
