package com.example.ordertakerfrontend;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import com.example.ordertakerfrontend.BackEnd.Logic.NetworkNotification.NetworkNotification;
import com.example.ordertakerfrontend.BackEnd.Logic.OrderHistory;
import com.example.ordertakerfrontend.BackEnd.Logic.Restaurant;
import com.example.ordertakerfrontend.BackEnd.Logic.Table;
import com.example.ordertakerfrontend.BackEnd.Logic.Waitress;
import com.example.ordertakerfrontend.BackEnd.Services.Constants;
import com.example.ordertakerfrontend.BackEnd.Services.FileManager;
import com.example.ordertakerfrontend.BackEnd.Services.Network.ConnectionHandler;
import com.example.ordertakerfrontend.BackEnd.Services.Utils;

import java.net.InetAddress;
import java.util.LinkedList;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

        Log.d("dir", "DIR : " + getFilesDir().getAbsolutePath());

        Waitress waitress = initSystem();

        Resources resources = getResources();
        for(Integer table: waitress.getRestaurant().getTables()){
            int table_id = resources.getIdentifier("table_"+table, "id", getApplicationContext().getPackageName());
            Button table_btn = findViewById(table_id);

            table_btn.setOnClickListener(view ->{
                Intent intent = new Intent(this, ScrollingActivity.class);
                intent.putExtra("table", table);
                startActivity(intent);
            });
        }

    }

    private Waitress initSystem(){
        Constants.CONTEXT = getApplicationContext();

        /**
         * Set up Menu
         * **/
        LinkedList<MenuProduct> menuProducts = new LinkedList<>();
        menuProducts.add(new MenuProduct("c1","p1", "desc", 1, null, new String[]{"ham.png"}));
        menuProducts.add(new MenuProduct("c1","p2", "desc", 2, null, new String[]{"ham.png"}));
        menuProducts.add(new MenuProduct("c1","p3", "desc", 3, null, new String[]{"ham.png"}));
        menuProducts.add(new MenuProduct("c1","p3", "desc", 3, null, new String[]{"ham.png"}));
        menuProducts.add(new MenuProduct("c1","p3", "desc", 3, null, new String[]{"ham.png"}));
        menuProducts.add(new MenuProduct("c1","p3", "desc", 3, null, new String[]{"ham.png"}));
        menuProducts.add(new MenuProduct("c1","p3", "desc", 3, null, new String[]{"ham.png"}));
        menuProducts.add(new MenuProduct("c1","p3", "desc", 3, null, new String[]{"ham.png"}));
        menuProducts.add(new MenuProduct("c1","p3", "desc", 3, null, new String[]{"ham.png"}));
        Menu.init(getApplicationContext(), menuProducts);

        /**
         * Set up Restaurant, check if there is cached version in memory
         * **/
        Restaurant lastState;
        try{
            lastState = (Restaurant) FileManager.readObject(Constants.RESTAURANT_STATE_FILE);
            if(lastState == null){
                lastState = new Restaurant();
                lastState.addTable(new Table(1));
                lastState.addTable(new Table(2));
                lastState.addTable(new Table(3));
                lastState.addTable(new Table(4));
                lastState.addTable(new Table(5));
                lastState.addTable(new Table(6));
                lastState.addTable(new Table(7));
                lastState.addTable(new Table(8));
            }
        }catch (Exception e){
            lastState = new Restaurant();
            lastState.addTable(new Table(1));
            lastState.addTable(new Table(2));
            lastState.addTable(new Table(3));
            lastState.addTable(new Table(4));
            lastState.addTable(new Table(5));
            lastState.addTable(new Table(6));
            lastState.addTable(new Table(7));
            lastState.addTable(new Table(8));
        }

        final Restaurant restaurant = lastState;


        /**
         * Set up Order History, check if there is cached version in memory
         * **/
        OrderHistory orderHistory = (OrderHistory) FileManager.readObject(Constants.ORDER_HISTORY_FILE);
        if(orderHistory == null){
            orderHistory = new OrderHistory();
            FileManager.writeObject(orderHistory, Constants.ORDER_HISTORY_FILE);
        }
        restaurant.setOrderHistory(orderHistory);

        /**
         * Set up Waitress Listeners
         * **/
        Waitress waitress = new Waitress("John", restaurant) {
            @Override
            public void onUDPNotification(InetAddress address, NetworkNotification notification) {}
            @Override
            public void onTCPNotification(ConnectionHandler handler, NetworkNotification notification) {}
        };

        Constants.WAITRESS = waitress;

        /** Cache this run in log **/
        Utils.writeToLog(waitress.getName() + " Started OrderTaker");

        return waitress;
    }

}