package com.example.ordertakerfrontend.FrontEnd.OrderHistory;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.example.ordertakerfrontend.BackEnd.Logic.Order;
import com.example.ordertakerfrontend.FrontEnd.Menus.Menu;
import com.example.ordertakerfrontend.FrontEnd.Menus.MenuProduct;
import com.example.ordertakerfrontend.FrontEnd.Menus.OrderProduct;

import org.json.JSONObject;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

@RequiresApi(api = Build.VERSION_CODES.O)
public class DataMock {

    private static LinkedList<Order> makeRandomOrders(Month month){

        LinkedList<Order> orders = new LinkedList<>();
        List<String> allCategories = List.of(Menu.getInstance().getCategories());

        for (int day = 1; day <= 30; day++){
            int numOfOrders = ThreadLocalRandom.current().nextInt(0, 100);
            for(int n = 0 ; n < numOfOrders ; n ++){
                int table = ThreadLocalRandom.current().nextInt(1, 9);
                int hour = ThreadLocalRandom.current().nextInt(8, 23);
                int minutes = ThreadLocalRandom.current().nextInt(0, 60);
                int sectionIndex = ThreadLocalRandom.current().nextInt(0, allCategories.size());
                LocalDateTime localDateTime = LocalDateTime.of(2021, month, day, hour, minutes);
                String startedBy = "B2b2";

                Order order = new Order(table, startedBy);
                order.setStartedAt(localDateTime);
                order.setDistributed(true);

                int numOfProducts = ThreadLocalRandom.current().nextInt(1, 9);

                for (int i = 0; i < numOfProducts; i ++){
                    int price = ThreadLocalRandom.current().nextInt(10, 100);
                    int quantity = ThreadLocalRandom.current().nextInt(1, 5);
                    order.addItem(startedBy, new OrderProduct(new MenuProduct(allCategories.get(sectionIndex), "p1", "desc", price, true, new LinkedList<>(), new String[]{})), quantity, "", false);
                }
                orders.add(order);
            }
        }

        return orders;

    }

    public static LinkedList<Order> data_set_1 = makeRandomOrders(Month.NOVEMBER);

}
