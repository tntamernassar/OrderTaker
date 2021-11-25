package com.example.ordertakerfrontend;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;


import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.Button;


import com.example.ordertakerfrontend.BackEnd.Logic.Restaurant;
import com.example.ordertakerfrontend.BackEnd.Logic.Table;
import com.example.ordertakerfrontend.BackEnd.Logic.Waitress;
import com.example.ordertakerfrontend.BackEnd.Services.Constants;
import com.example.ordertakerfrontend.BackEnd.Services.FileManager;
import com.example.ordertakerfrontend.BackEnd.Services.Utils;
import com.example.ordertakerfrontend.FrontEnd.Menus.DiskMenu;
import com.example.ordertakerfrontend.FrontEnd.Popups.YesNoCallbacks;
import com.example.ordertakerfrontend.FrontEnd.UserMessages;
import com.example.ordertakerfrontend.Network.NetworkManager.NetworkAdapter;
import com.example.ordertakerfrontend.Network.NetworkMessages.In.MenuEditNotification;
import com.example.ordertakerfrontend.Network.NetworkMessages.In.OrderHistoryContainer;
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


import java.util.LinkedList;



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

        this.waitress = Constants.WAITRESS;

        NetworkAdapter.getInstance().register(id, this);

        FloatingActionButton edit_menu = findViewById(R.id.edit_menu);
        edit_menu.setOnClickListener(v->{
            Intent menuEditActivity = new Intent(this, MenuEditActivity.class);
            startActivity(menuEditActivity);
        });

        FloatingActionButton order_history = findViewById(R.id.order_history);
        order_history.setOnClickListener(v->{
            Intent orderHistoryActivity = new Intent(this, OrderHistoryActivity.class);
            startActivity(orderHistoryActivity);
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

    @Override
    public void onBackPressed() { }


    private void openTable(int table){
        MessageObserver that = this;
        Intent intent = new Intent(this, OrderActivity.class);
        if(!waitress.getRestaurant().getTable(table).isActive()) {
            Utils.YesNoDialog(MainActivity.this, UserMessages.get("ask_open_table", table), new YesNoCallbacks() {
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
        runOnUiThread(()->{
            int table = message.getTable();
            Resources resources = getResources();
            int table_id = resources.getIdentifier("table_"+table, "id", getApplicationContext().getPackageName());
            Button table_btn = findViewById(table_id);
            table_btn.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.taken_table));
            table_btn.setTextColor(this.getResources().getColor(R.color.white));
        });
    }

    @Override
    public void accept(CloseTableNotification message) {
        runOnUiThread(()->{
            int table = message.getTable();
            Resources resources = getResources();
            int table_id = resources.getIdentifier("table_"+table, "id", getApplicationContext().getPackageName());
            Button table_btn = findViewById(table_id);
            table_btn.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.available_table));
            table_btn.setTextColor(this.getResources().getColor(R.color.gold2));
        });
    }

    @Override
    public void accept(CancelTableNotification message) {
        runOnUiThread(()->{
            int table = message.getTable();
            Resources resources = getResources();
            int table_id = resources.getIdentifier("table_"+table, "id", getApplicationContext().getPackageName());
            Button table_btn = findViewById(table_id);
            table_btn.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.available_table));
            table_btn.setTextColor(this.getResources().getColor(R.color.gold2));
        });
    }

    @Override
    public void accept(SubmitTableNotification message) { }

    @Override
    public void accept(OrderHistoryContainer message) { }

    @Override
    public void accept(NetworkMessage message) { }

    @Override
    public void accept(initResponse message) { }

    @Override
    public void accept(ServerImage message) { }

    @Override
    public void accept(MenuEditNotification message) { }


}