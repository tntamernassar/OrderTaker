package com.example.ordertakerfrontend;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
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
import com.anychart.charts.Cartesian;
import com.anychart.charts.Pie;
import com.anychart.core.cartesian.series.Line;
import com.anychart.data.Mapping;
import com.anychart.data.Set;
import com.anychart.enums.Align;
import com.anychart.enums.Anchor;
import com.anychart.enums.LegendLayout;
import com.anychart.enums.MarkerType;
import com.example.ordertakerfrontend.BackEnd.Logic.Order;
import com.example.ordertakerfrontend.BackEnd.Logic.OrderHistory;
import com.example.ordertakerfrontend.BackEnd.Services.Constants;
import com.example.ordertakerfrontend.FrontEnd.OrderHistory.OrderHistoryCharts;
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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class OrderHistoryActivity extends AppCompatActivity implements MessageObserver {

    private String id = "order_history";
    private boolean syncing;
    private OrderHistory orderHistory;
    private LinearLayout container;

    @RequiresApi(api = Build.VERSION_CODES.O)
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

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void charts(){
        if(!syncing) {
            System.out.println("Charts !");
            this.container.removeAllViews();
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View inflated = inflater.inflate(R.layout.report_layout, null);
            this.container.addView(inflated);
            OrderHistoryCharts orderHistoryCharts = new OrderHistoryCharts(this.orderHistory.getOrders());

            // chart for quantity X days.
            orderHistoryCharts.createTrafficChart(inflated);
            orderHistoryCharts.createTrafficChartForPrice(inflated);

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
        System.out.println("accepted...***********************");
        this.syncing = false;
        this.orderHistory = Constants.WAITRESS.getRestaurant().getOrderHistory();
        table();
    }

    private class CustomDataEntry extends ValueDataEntry{
        CustomDataEntry(String x, Number value) {
            super(x, value);
        }
    }
}