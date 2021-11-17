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
        MenuProduct[] products = Menu.getInstance().getMenuProductList().toArray(new MenuProduct[]{});

        for (int day = 1; day <= 5; day++){
            int numOfOrders = ThreadLocalRandom.current().nextInt(1, 3);
            for(int n = 0 ; n < numOfOrders ; n ++){
                int table = ThreadLocalRandom.current().nextInt(1, 9);
                int hour = ThreadLocalRandom.current().nextInt(8, 23);
                int minutes = ThreadLocalRandom.current().nextInt(0, 60);
                LocalDateTime localDateTime = LocalDateTime.of(2021, month, day, hour, minutes);
                String startedBy = "B2b2";

                Order order = new Order(table, startedBy);
                order.setStartedAt(localDateTime);
                order.setDistributed(true);

                int numOfProducts = ThreadLocalRandom.current().nextInt(1, 9);

                for (int i = 0; i < numOfProducts; i ++){
                    int quantity = ThreadLocalRandom.current().nextInt(1, 5);
                    MenuProduct menuProduct = products[ThreadLocalRandom.current().nextInt(0, products.length)];
                    order.addItem(startedBy, new OrderProduct(menuProduct), quantity, "", false);
                    System.out.println("product "  + menuProduct.getName());
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
        order1.addItem("B2b2", new OrderProduct(products[0]), 1, "", false);
        order1.addItem("B2b2", new OrderProduct(products[10]), 1, "", false);
        order1.addItem("B2b2", new OrderProduct(products[20]), 1, "", false);

        LocalDateTime localDateTime2 = LocalDateTime.of(2021, month, 1, 12, 0);
        Order order2 = new Order(1, "B2b2");
        order2.setDistributed(true);
        order2.setStartedAt(localDateTime2);
        order2.addItem("B2b2", new OrderProduct(products[0]), 1, "", false);
        order2.addItem("B2b2", new OrderProduct(products[10]), 1, "", false);
        order2.addItem("B2b2", new OrderProduct(products[20]), 1, "", false);


        LocalDateTime localDateTime3 = LocalDateTime.of(2021, month, 1, 12, 0);
        Order order3 = new Order(1, "B2b2");
        order3.setDistributed(true);
        order3.setStartedAt(localDateTime3);
        order3.addItem("B2b2", new OrderProduct(products[0]), 1, "", false);
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
