package com.example.ordertakerfrontend.FrontEnd.OrderHistory;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.example.ordertakerfrontend.BackEnd.Logic.Order;
import com.example.ordertakerfrontend.FrontEnd.Menus.Menu;
import com.example.ordertakerfrontend.FrontEnd.Menus.MenuProduct;
import com.example.ordertakerfrontend.FrontEnd.Menus.OrderProduct;


import java.time.LocalDateTime;
import java.time.Month;
import java.util.LinkedList;
import java.util.Random;

@RequiresApi(api = Build.VERSION_CODES.O)
public class DataMock {

    private static int random(int min, int max){
        Random rand = new Random();
        int randomNum = rand.nextInt((max - min)) + min;
        return randomNum;
    }

    private static LinkedList<Order> makeRandomOrders(Month month){

        LinkedList<Order> orders = new LinkedList<>();
        MenuProduct[] products = Menu.getInstance().getMenuProductList().toArray(new MenuProduct[]{});

        for (int day = 1; day <= 30; day++){
            int numOfOrders = random(1, 100);
            for(int n = 0 ; n < numOfOrders ; n ++){
                int table = random(1, 9);
                int hour = random(8, 23);
                int minutes = random(0, 60);
                LocalDateTime localDateTime = LocalDateTime.of(2021, month, day, hour, minutes);
                String startedBy = "B2b2";

                Order order = new Order(table, startedBy);
                order.setStartedAt(localDateTime);
                order.setDistributed(true);

                int numOfProducts = random(1, 9);

                for (int i = 0; i < numOfProducts; i ++){
                    int quantity = random(1, 5);
                    MenuProduct menuProduct = products[random(0, products.length)];
                    order.addItem(startedBy, new OrderProduct(menuProduct), quantity, "", false);
                }
                orders.add(order);
            }
        }

        return orders;

    }

    private static LinkedList<Order> createDataset2(Month month){
        MenuProduct[] products = Menu.getInstance().getMenuProductList().toArray(new MenuProduct[]{});
        LinkedList<Order> orders = new LinkedList<>();

        LocalDateTime localDateTime1 = LocalDateTime.of(2021, month, 1, 12, 0);
        Order order1 = new Order(1, "B2b2");
        order1.setDistributed(true);
        order1.setStartedAt(localDateTime1);
        order1.addItem("B2b2", new OrderProduct(products[0]), 2, "", false);
        order1.addItem("B2b2", new OrderProduct(products[10]), 1, "", false);
        order1.addItem("B2b2", new OrderProduct(products[20]), 1, "", false);

        LocalDateTime localDateTime2 = LocalDateTime.of(2021, month, 1, 12, 0);
        Order order2 = new Order(1, "B2b2");
        order2.setDistributed(true);
        order2.setStartedAt(localDateTime2);
        order2.addItem("B2b2", new OrderProduct(products[0]), 2, "", false);
        order2.addItem("B2b2", new OrderProduct(products[10]), 1, "", false);
        order2.addItem("B2b2", new OrderProduct(products[20]), 1, "", false);


        LocalDateTime localDateTime3 = LocalDateTime.of(2021, month, 1, 12, 0);
        Order order3 = new Order(1, "B2b2");
        order3.setDistributed(true);
        order3.setStartedAt(localDateTime3);
        order3.addItem("B2b2", new OrderProduct(products[0]), 2, "", false);
        order3.addItem("B2b2", new OrderProduct(products[10]), 1, "", false);
        order3.addItem("B2b2", new OrderProduct(products[20]), 1, "", false);

        orders.add(order1);
        orders.add(order2);
        orders.add(order3);

        return orders;
    }

    public static LinkedList<Order> data_set_1 = makeRandomOrders(Month.NOVEMBER);
    public static LinkedList<Order> data_set_2 = createDataset2(Month.NOVEMBER);

}
