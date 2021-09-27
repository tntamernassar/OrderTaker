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
import com.example.ordertakerfrontend.FrontEnd.Menu;
import com.example.ordertakerfrontend.FrontEnd.MenuProduct;

import java.net.InetAddress;
import java.util.HashMap;
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

        HashMap<String, String[]> addons_example_1 = new HashMap<>();
        addons_example_1.put("מידת עשייה", new String[]{"MW", "WD", "M", "D"});
        addons_example_1.put("نوع اللحم", new String[]{"عجل", "خروف", "دجاج"});
        addons_example_1.put("نوع الخبز", new String[]{"ابيض", "اسود"});
        addons_example_1.put("نوع الخبز", new String[]{"ابيض", "اسود"});
        addons_example_1.put("اضافات", new String[]{"خس", "بندورة" ,"خيار", "بصل", "فقع", "جبنه"});

        /**
         * Set up Menu
         * **/
        Menu.init(getApplicationContext(), new LinkedList<>());
        Menu.getInstance().addProduct(new MenuProduct("لحوم","همبرجر", "لحمه مفرومه   وبعض الوصف  وبعض الوصف", 15, addons_example_1, new String[]{"ham.png"}));
        Menu.getInstance().addProduct(new MenuProduct("لحوم","شنيتسل", "لحمه مفرومه وبعض الوصف لحمه مفرومه وبعض الوصف", 30, null, new String[]{"ham.png"}));
        Menu.getInstance().addProduct(new MenuProduct("لحوم","كباب", "لحمه مفرومه وبعض الوصف لحمه مفرومه وبعض الوصف", 30, null, new String[]{"ham.png"}));
        Menu.getInstance().addProduct(new MenuProduct("لحوم","تورتيا", "لحمه مفرومه وبعض الوصف لحمه مفرومه وبعض الوصف", 30, null, new String[]{"ham.png"}));
        Menu.getInstance().addProduct(new MenuProduct("لحوم","شوارما", "لحمه مفرومه وبعض الوصف لحمه مفرومه وبعض الوصف", 30, null, new String[]{"ham.png"}));
        Menu.getInstance().addProduct(new MenuProduct("لحوم","برجيت", "لحمه مفرومه وبعض الوصف لحمه مفرومه وبعض الوصف", 30, null, new String[]{"ham.png"}));
        Menu.getInstance().addProduct(new MenuProduct("لحوم","صدر دجاج", "لحمه مفرومه وبعض الوصف لحمه مفرومه وبعض الوصف", 30, null, new String[]{"ham.png"}));
        Menu.getInstance().addProduct(new MenuProduct("لحوم","عروسه", "لحمه مفرومه وبعض الوصف لحمه مفرومه وبعض الوصف", 30, null, new String[]{"ham.png"}));
        Menu.getInstance().addProduct(new MenuProduct("لحوم","خروف", "لحمه مفرومه وبعض الوصف لحمه مفرومه وبعض الوصف", 30, null, new String[]{"ham.png"}));

        Menu.getInstance().addProduct(new MenuProduct("اسماك","سلمون", "لحمه مفرومه وبعض الوصف لحمه مفرومه وبعض الوصف", 100, null, new String[]{"ham.png"}));
        Menu.getInstance().addProduct(new MenuProduct("اسماك","شرمبس", "لحمه مفرومه وبعض الوصف لحمه مفرومه وبعض الوصف", 100, null, new String[]{"ham.png"}));
        Menu.getInstance().addProduct(new MenuProduct("اسماك","دينيس", "لحمه مفرومه وبعض الوصف لحمه مفرومه وبعض الوصف", 100, null, new String[]{"ham.png"}));
        Menu.getInstance().addProduct(new MenuProduct("اسماك","لفز", "لحمه مفرومه وبعض الوصف لحمه مفرومه وبعض الوصف", 100, null, new String[]{"ham.png"}));
        Menu.getInstance().addProduct(new MenuProduct("اسماك","سرغوس", "لحمه مفرومه وبعض الوصف لحمه مفرومه وبعض الوصف", 100, null, new String[]{"ham.png"}));
        Menu.getInstance().addProduct(new MenuProduct("اسماك","فريده", "لحمه مفرومه وبعض الوصف لحمه مفرومه وبعض الوصف", 100, null, new String[]{"ham.png"}));
        Menu.getInstance().addProduct(new MenuProduct("اسماك","طرخون", "لحمه مفرومه وبعض الوصف لحمه مفرومه وبعض الوصف", 100, null, new String[]{"ham.png"}));
        Menu.getInstance().addProduct(new MenuProduct("اسماك","لفراك", "لحمه مفرومه وبعض الوصف لحمه مفرومه وبعض الوصف", 100, null, new String[]{"ham.png"}));
        Menu.getInstance().addProduct(new MenuProduct("اسماك","مشط", "لحمه مفرومه وبعض الوصف لحمه مفرومه وبعض الوصف", 100, null, new String[]{"ham.png"}));
        Menu.getInstance().addProduct(new MenuProduct("اسماك","بوري", "لحمه مفرومه وبعض الوصف لحمه مفرومه وبعض الوصف", 100, null, new String[]{"ham.png"}));
        Menu.getInstance().addProduct(new MenuProduct("اسماك","سمك", "لحمه مفرومه وبعض الوصف لحمه مفرومه وبعض الوصف", 100, null, new String[]{"ham.png"}));

        Menu.getInstance().addProduct(new MenuProduct("ايطالي","رفيولي", "لحمه مفرومه وبعض الوصف لحمه مفرومه وبعض الوصف", 100, null, new String[]{"ham.png"}));
        Menu.getInstance().addProduct(new MenuProduct("ايطالي","بيتسا", "لحمه مفرومه وبعض الوصف لحمه مفرومه وبعض الوصف", 100, null, new String[]{"ham.png"}));
        Menu.getInstance().addProduct(new MenuProduct("ايطالي","باستا", "لحمه مفرومه وبعض الوصف لحمه مفرومه وبعض الوصف", 100, null, new String[]{"ham.png"}));
        Menu.getInstance().addProduct(new MenuProduct("ايطالي","طوسط", "لحمه مفرومه وبعض الوصف لحمه مفرومه وبعض الوصف", 100, null, new String[]{"ham.png"}));
        Menu.getInstance().addProduct(new MenuProduct("ايطالي","لزانيا", "لحمه مفرومه وبعض الوصف لحمه مفرومه وبعض الوصف", 100, null, new String[]{"ham.png"}));

        Menu.getInstance().addProduct(new MenuProduct("سلطات","عربيه", "لحمه مفرومه وبعض الوصف لحمه مفرومه وبعض الوصف", 20, null, new String[]{"ham.png"}));
        Menu.getInstance().addProduct(new MenuProduct("سلطات","فتوش", "لحمه مفرومه وبعض الوصف لحمه مفرومه وبعض الوصف", 20, null, new String[]{"ham.png"}));
        Menu.getInstance().addProduct(new MenuProduct("سلطات","ملفوف", "لحمه مفرومه وبعض الوصف لحمه مفرومه وبعض الوصف", 20, null, new String[]{"ham.png"}));
        Menu.getInstance().addProduct(new MenuProduct("سلطات","ذره", "لحمه مفرومه وبعض الوصف لحمه مفرومه وبعض الوصف", 20, null, new String[]{"ham.png"}));
        Menu.getInstance().addProduct(new MenuProduct("سلطات","يونانيه", "لحمه مفرومه وبعض الوصف لحمه مفرومه وبعض الوصف", 20, null, new String[]{"ham.png"}));
        Menu.getInstance().addProduct(new MenuProduct("سلطات","حمص", "لحمه مفرومه وبعض الوصف لحمه مفرومه وبعض الوصف", 20, null, new String[]{"ham.png"}));
        Menu.getInstance().addProduct(new MenuProduct("سلطات","باذنجان", "لحمه مفرومه وبعض الوصف لحمه مفرومه وبعض الوصف", 20, null, new String[]{"ham.png"}));
        Menu.getInstance().addProduct(new MenuProduct("سلطات","بندوره", "لحمه مفرومه وبعض الوصف لحمه مفرومه وبعض الوصف", 20, null, new String[]{"ham.png"}));
        Menu.getInstance().addProduct(new MenuProduct("سلطات","ذره", "لحمه مفرومه وبعض الوصف لحمه مفرومه وبعض الوصف", 20, null, new String[]{"ham.png"}));
        Menu.getInstance().addProduct(new MenuProduct("سلطات","جزر", "لحمه مفرومه وبعض الوصف لحمه مفرومه وبعض الوصف", 20, null, new String[]{"ham.png"}));

        Menu.getInstance().addProduct(new MenuProduct("مشروبات","ماء", "لحمه مفرومه وبعض الوصف لحمه مفرومه وبعض الوصف", 10, null, new String[]{"ham.png"}));
        Menu.getInstance().addProduct(new MenuProduct("مشروبات","كولا", "لحمه مفرومه وبعض الوصف لحمه مفرومه وبعض الوصف", 10, null, new String[]{"ham.png"}));
        Menu.getInstance().addProduct(new MenuProduct("مشروبات","فانتا", "لحمه مفرومه وبعض الوصف لحمه مفرومه وبعض الوصف", 10, null, new String[]{"ham.png"}));
        Menu.getInstance().addProduct(new MenuProduct("مشروبات","سبرايت", "لحمه مفرومه وبعض الوصف لحمه مفرومه وبعض الوصف", 10, null, new String[]{"ham.png"}));
        Menu.getInstance().addProduct(new MenuProduct("مشروبات","توت موز", "لحمه مفرومه وبعض الوصف لحمه مفرومه وبعض الوصف", 10, null, new String[]{"ham.png"}));
        Menu.getInstance().addProduct(new MenuProduct("مشروبات","عنب", "لحمه مفرومه وبعض الوصف لحمه مفرومه وبعض الوصف", 10, null, new String[]{"ham.png"}));
        Menu.getInstance().addProduct(new MenuProduct("مشروبات","برتقال", "لحمه مفرومه وبعض الوصف لحمه مفرومه وبعض الوصف", 10, null, new String[]{"ham.png"}));

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