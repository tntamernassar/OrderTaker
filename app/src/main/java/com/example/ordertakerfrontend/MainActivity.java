package com.example.ordertakerfrontend;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import com.example.ordertakerfrontend.BackEnd.Logic.OrderHistory;
import com.example.ordertakerfrontend.BackEnd.Logic.Restaurant;
import com.example.ordertakerfrontend.BackEnd.Logic.Table;
import com.example.ordertakerfrontend.BackEnd.Logic.Waitress;
import com.example.ordertakerfrontend.BackEnd.Services.Constants;
import com.example.ordertakerfrontend.BackEnd.Services.FileManager;
import com.example.ordertakerfrontend.BackEnd.Services.Utils;
import com.example.ordertakerfrontend.FrontEnd.Menus.DiskMenu;
import com.example.ordertakerfrontend.FrontEnd.Menus.Menu;
import com.example.ordertakerfrontend.FrontEnd.Menus.MenuProduct;
import com.example.ordertakerfrontend.FrontEnd.Menus.MenuSection;
import com.example.ordertakerfrontend.FrontEnd.Popups.YesNoCallbacks;
import com.example.ordertakerfrontend.Network.NetworkManager.NetworkAdapter;
import com.example.ordertakerfrontend.Network.NetworkMessages.TestMessage;
import com.example.ordertakerfrontend.Network.NetworkMessages.initRequest;

import java.util.LinkedList;


public class MainActivity extends AppCompatActivity {


    @Override
    protected void onStart() {
        super.onStart();
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




        Waitress waitress;
        if(Constants.WAITRESS == null) {
            waitress = initSystem();
        } else {
            waitress = Constants.WAITRESS;
        }


        Resources resources = getResources();
        for(Integer table: waitress.getRestaurant().getTables()){
            int table_id = resources.getIdentifier("table_"+table, "id", getApplicationContext().getPackageName());
            Button table_btn = findViewById(table_id);

            table_btn.setOnClickListener(view ->{
                Intent intent = new Intent(this, OrderActivity.class);
                if(!waitress.getRestaurant().getTable(table).isActive()) {
                    Log.d("OnCreat: MainActivity", "Table is not active");
                    Utils.YesNoDialog(MainActivity.this, "فتح طاوله رقم 1 ؟", new YesNoCallbacks() {
                        @Override
                        public void yes() {
                            waitress.openTable(table);
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


            });
        }



    }

    private Waitress initSystem(){
        Constants.CONTEXT = getApplicationContext();



        LinkedList<MenuSection> sections_example_1 = new LinkedList<>();
        sections_example_1.add(new MenuSection("מידת עשייה", new String[]{"ميديوم", "ويل دان", "دان", "محروق"}, true));
        sections_example_1.add(new MenuSection("نوع اللحم", new String[]{"عجل", "خروف", "دجاج"}, true));
        sections_example_1.add(new MenuSection("نوع الخبز", new String[]{"خبز ابيض", "خبز اسود"}, true));
        sections_example_1.add(new MenuSection("اضافات", new String[]{"خس", "بندورة" ,"خيار", "بصل", "فقع", "جبنه"}, false));



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
        });
        NetworkAdapter.getInstance().start();
        return waitress;
    }

}