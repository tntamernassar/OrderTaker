package com.example.ordertakerfrontend;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import com.dantsu.escposprinter.EscPosPrinter;
import com.dantsu.escposprinter.connection.bluetooth.BluetoothConnection;
import com.dantsu.escposprinter.connection.bluetooth.BluetoothPrintersConnections;
import com.dantsu.escposprinter.textparser.PrinterTextParserImg;
import com.example.ordertakerfrontend.BackEnd.Logic.OrderHistory;
import com.example.ordertakerfrontend.BackEnd.Logic.Restaurant;
import com.example.ordertakerfrontend.BackEnd.Logic.Table;
import com.example.ordertakerfrontend.BackEnd.Logic.Waitress;
import com.example.ordertakerfrontend.BackEnd.Services.Constants;
import com.example.ordertakerfrontend.BackEnd.Services.FileManager;
import com.example.ordertakerfrontend.BackEnd.Services.ImagesManager;
import com.example.ordertakerfrontend.BackEnd.Services.OrderDistribution.BluetoothPrinter;
import com.example.ordertakerfrontend.BackEnd.Services.Utils;
import com.example.ordertakerfrontend.FrontEnd.Menus.DiskMenu;
import com.example.ordertakerfrontend.FrontEnd.Popups.YesNoCallbacks;
import com.example.ordertakerfrontend.Network.NetworkManager.NetworkAdapter;
import com.example.ordertakerfrontend.Network.NetworkMessages.In.MenuEditNotification;
import com.example.ordertakerfrontend.Network.NetworkMessages.In.ServerImage;
import com.example.ordertakerfrontend.Network.NetworkMessages.In.Tables.CancelTableNotification;
import com.example.ordertakerfrontend.Network.NetworkMessages.In.Tables.CloseTableNotification;
import com.example.ordertakerfrontend.Network.NetworkMessages.In.Tables.OpenTableNotification;
import com.example.ordertakerfrontend.Network.NetworkMessages.In.Tables.SubmitTableNotification;
import com.example.ordertakerfrontend.Network.NetworkMessages.In.initResponse;
import com.example.ordertakerfrontend.Network.NetworkMessages.Out.Tables.OpenTable;
import com.example.ordertakerfrontend.Network.NetworkMessages.Out.initRequest;
import com.example.ordertakerfrontend.Network.NetworkMessages.tools.MessageObserver;
import com.example.ordertakerfrontend.Network.NetworkMessages.tools.NetworkMessage;
import com.google.android.material.floatingactionbutton.FloatingActionButton;


import java.util.Iterator;
import java.util.LinkedList;
import java.util.Objects;


public class MainActivity extends AppCompatActivity implements MessageObserver {

    private String id="mainActivity";
    private Waitress waitress;

    @Override
    protected void onStart() {
        super.onStart();
        NetworkAdapter.getInstance().register(id, this);
        if (Constants.WAITRESS != null) {
            Resources resources = getResources();
            for (Integer table : Constants.WAITRESS.getRestaurant().getTables()) {
                int table_id = resources.getIdentifier("table_"+table, "id", getApplicationContext().getPackageName());
                Button table_btn = findViewById(table_id);
                if(Constants.WAITRESS.getRestaurant().getTable(table).isActive()){
                    table_btn.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.taken_table));
                    table_btn.setTextColor(this.getResources().getColor(R.color.white));
                }else{
                    table_btn.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.available_table));
                    table_btn.setTextColor(this.getResources().getColor(R.color.gold2));
                }
            }
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

        if(Constants.WAITRESS == null) {
            this.waitress = initSystem();
        } else {
            this.waitress = Constants.WAITRESS;
        }

        NetworkAdapter.getInstance().register(id, this);


        FloatingActionButton edit_menu = findViewById(R.id.edit_menu);
        edit_menu.setOnClickListener(v->{
            Intent menuEditActivity = new Intent(this, MenuEditActivity.class);
            startActivity(menuEditActivity);
        });

        Resources resources = getResources();
        for(Integer table: waitress.getRestaurant().getTables()){
            int table_id = resources.getIdentifier("table_"+table, "id", getApplicationContext().getPackageName());
            Button table_btn = findViewById(table_id);

            table_btn.setOnClickListener(view ->{
                openTable(table);
            });
        }


    }

    @Override
    protected void onStop() {
        super.onStop();
        NetworkAdapter.getInstance().unregister(id);
    }

    private Waitress initSystem(){
        Constants.CONTEXT = getApplicationContext();

        /**
         * Set up Menu
         *
         * Read from 'menu' file, and build the menu accordingly.
         * if menu doesn't exist, create empty menu and wait for the server
         * **/

        DiskMenu diskMenu = (DiskMenu) FileManager.readObject("menu");
        if(diskMenu == null){
            diskMenu = new DiskMenu(new LinkedList<>());
        }
        diskMenu.makeMenu(this);


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
         * Set up Waitress Listeners
         * **/
        Waitress waitress = new Waitress("John", restaurant);

        Constants.WAITRESS = waitress;

        /** Cache this run in log **/
        Utils.writeToLog(waitress.getName() + " Started OrderTaker");


        /** Network init **/
        NetworkAdapter.init(new NetworkAdapter() {
            @Override
            public void onConnection(NetworkAdapter adapter) {
                adapter.receive();
                adapter.send(new initRequest());
            }

            @Override
            public void onError(Exception e) {
                System.out.println("Not connected");
            }
        });
        NetworkAdapter.getInstance().start();
        return waitress;
    }

    private void openTable(int table){
        MessageObserver that = this;
        Intent intent = new Intent(this, OrderActivity.class);
        if(!waitress.getRestaurant().getTable(table).isActive()) {
            Utils.YesNoDialog(MainActivity.this, "فتح طاوله رقم 1 ؟", new YesNoCallbacks() {
                @Override
                public void yes() {
                    waitress.openTable(table);
                    NetworkAdapter.getInstance().send(new OpenTable(table));
                    NetworkAdapter.getInstance().unregister(id);
                    intent.putExtra("table", table);
                    startActivity(intent);
                }

                @Override
                public void no() { }
            });
        }else{
            intent.putExtra("table", table);
            startActivity(intent);
        }
    }

    @Override
    public void accept(OpenTableNotification message) {
        int table = message.getTable();
        Resources resources = getResources();
        int table_id = resources.getIdentifier("table_"+table, "id", getApplicationContext().getPackageName());
        Button table_btn = findViewById(table_id);
        table_btn.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.taken_table));
        table_btn.setTextColor(this.getResources().getColor(R.color.white));
    }

    @Override
    public void accept(CloseTableNotification message) {
        int table = message.getTable();
        Resources resources = getResources();
        int table_id = resources.getIdentifier("table_"+table, "id", getApplicationContext().getPackageName());
        Button table_btn = findViewById(table_id);
        table_btn.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.available_table));
        table_btn.setTextColor(this.getResources().getColor(R.color.gold2));
    }

    @Override
    public void accept(CancelTableNotification message) {
        int table = message.getTable();
        Resources resources = getResources();
        int table_id = resources.getIdentifier("table_"+table, "id", getApplicationContext().getPackageName());
        Button table_btn = findViewById(table_id);
        table_btn.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.available_table));
        table_btn.setTextColor(this.getResources().getColor(R.color.gold2));
    }

    @Override
    public void accept(SubmitTableNotification message) {

    }

    @Override
    public void accept(NetworkMessage message) { }

    @Override
    public void accept(initResponse message) { }

    @Override
    public void accept(ServerImage message) { }

    @Override
    public void accept(MenuEditNotification message) { }


}