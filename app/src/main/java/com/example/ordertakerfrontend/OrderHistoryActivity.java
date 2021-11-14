package com.example.ordertakerfrontend;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.chart.common.listener.Event;
import com.anychart.chart.common.listener.ListenersInterface;
import com.anychart.charts.Pie;
import com.anychart.enums.Align;
import com.anychart.enums.LegendLayout;
import com.example.ordertakerfrontend.BackEnd.Logic.OrderHistory;
import com.example.ordertakerfrontend.BackEnd.Services.Constants;
import com.example.ordertakerfrontend.FrontEnd.OrderHistory.OrderHistoryTable;
import com.example.ordertakerfrontend.Network.NetworkManager.NetworkAdapter;
import com.example.ordertakerfrontend.Network.NetworkMessages.In.MenuEditNotification;
import com.example.ordertakerfrontend.Network.NetworkMessages.In.OrderHistoryContainer;
import com.example.ordertakerfrontend.Network.NetworkMessages.In.ServerImage;
import com.example.ordertakerfrontend.Network.NetworkMessages.In.Tables.CancelTableNotification;
import com.example.ordertakerfrontend.Network.NetworkMessages.In.Tables.CloseTableNotification;
import com.example.ordertakerfrontend.Network.NetworkMessages.In.Tables.OpenTableNotification;
import com.example.ordertakerfrontend.Network.NetworkMessages.In.Tables.SubmitTableNotification;
import com.example.ordertakerfrontend.Network.NetworkMessages.In.initResponse;
import com.example.ordertakerfrontend.Network.NetworkMessages.Out.GetOrderHistory;
import com.example.ordertakerfrontend.Network.NetworkMessages.tools.MessageObserver;
import com.example.ordertakerfrontend.Network.NetworkMessages.tools.NetworkMessage;
import com.google.android.material.button.MaterialButtonToggleGroup;

import java.util.ArrayList;
import java.util.List;

public class OrderHistoryActivity extends AppCompatActivity implements MessageObserver {

    private String id = "order_history";
    private boolean syncing;
    private OrderHistory orderHistory;
    private LinearLayout container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_history);
        getSupportActionBar().hide();

        this.container = findViewById(R.id.container);
        this.syncing = true;
        NetworkAdapter.getInstance().register(id, this);
        NetworkAdapter.getInstance().send(new GetOrderHistory());

        MaterialButtonToggleGroup materialButtonToggleGroup = findViewById(R.id.toggle);
        materialButtonToggleGroup.addOnButtonCheckedListener((toggleButtonGroup, checkedId, isChecked)->{

            if (checkedId == R.id.charts){
                if(isChecked) {
                    charts();
                }
            }else if (checkedId == R.id.table){
                if(isChecked) {
                    table();
                }
            }

        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        NetworkAdapter.getInstance().unregister(id);
    }

    private void charts(){
        if(!syncing) {
            System.out.println("Charts !");
            this.container.removeAllViews();
        }
    }

    private void table(){
        if(!syncing) {
            System.out.println("Tables");
            this.container.removeAllViews();

            ListView table = new ListView(getApplicationContext());
            this.container.addView(table);
            table.setAdapter(new OrderHistoryTable(getApplicationContext(), table.getId(), this.orderHistory.getOrders()));

            ViewGroup.LayoutParams params = table.getLayoutParams();
            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            params.height = ViewGroup.LayoutParams.MATCH_PARENT;
            table.setLayoutParams(params);
            table.requestLayout();
        }
    }

    @Override
    public void accept(NetworkMessage message) {

    }

    @Override
    public void accept(initResponse message) {

    }

    @Override
    public void accept(ServerImage message) {

    }

    @Override
    public void accept(MenuEditNotification message) {

    }

    @Override
    public void accept(OpenTableNotification message) {

    }

    @Override
    public void accept(CloseTableNotification message) {

    }

    @Override
    public void accept(CancelTableNotification message) {

    }

    @Override
    public void accept(SubmitTableNotification message) {

    }

    @Override
    public void accept(OrderHistoryContainer message) {
        this.syncing = false;
        this.orderHistory = Constants.WAITRESS.getRestaurant().getOrderHistory();
        table();
    }
}