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

            AnyChartView lineChart = (AnyChartView) inflated.findViewById(R.id.lineChart);
            lineChart.setProgressBar(inflated.findViewById(R.id.progressBar));


            // adding line chart
            Cartesian cartesian = AnyChart.line();
//            cartesian.animation(true);

            ArrayList<DataEntry> data = new ArrayList<>();

//            LinkedList<Pair<Integer, Integer>> quantityPerDays = new LinkedList<>();
            /**
             *  the below HashMap act like:
             * month(12) -> day(30) -> [o1, o2,..., on].size = quantity
             * */
            HashMap<Integer, HashMap<Integer, ArrayList<Order>>> ordersPerMonthPerDays = new HashMap<>();
            HashMap<Integer, Integer> quantityPerDays = new HashMap<>();
            int currentMonth = (int) LocalDateTime.now().getMonth().getValue();

            for(Order o: this.orderHistory.getOrders()){
                LocalDateTime startedAt = o.getStartedAt();
                int month = (int) startedAt.getMonth().getValue();
                int day = (int) startedAt.getDayOfMonth();

                if(!ordersPerMonthPerDays.containsKey(month))
                    ordersPerMonthPerDays.put(month, new HashMap<>());
                if(!ordersPerMonthPerDays.get(month).containsKey(day))
                    ordersPerMonthPerDays.get(month).put(day, new ArrayList<>());

                ordersPerMonthPerDays.get(month).get(day).add(o);
            }

            // building quantity X day, [day: 14] -> y (quantity of this day)
            for (Integer day: ordersPerMonthPerDays.get(currentMonth).keySet()) {
                    if (!quantityPerDays.containsKey(day))
                        quantityPerDays.put(day, ordersPerMonthPerDays.get(currentMonth).get(day).size());
            }

            // building the line chart:
            List<DataEntry> seriesData = new ArrayList<>();

            // for testing
            for(int i = 16; i < 31; i++){
                Random r = new Random();
                int random = ThreadLocalRandom.current().nextInt(13, 100);
                quantityPerDays.put(i, random);
            }
            //

            for(Integer day: quantityPerDays.keySet())
                seriesData.add(new ValueDataEntry(day + "", quantityPerDays.get(day)));


            Set set = Set.instantiate();
            set.data(seriesData);
            Mapping seriesMapping = set.mapAs("{x: 'x', value: 'value'}");

            Line series = cartesian.line(seriesData);
            series.name("TESTINGTESTINGTESTING");
            series.hovered().markers().enabled(true);

            series.tooltip()
                    .position("right")
                    .anchor(Anchor.LEFT_CENTER)
                    .offsetX(5d)
                    .offsetY(5d);

            cartesian.legend().enabled(true);
            cartesian.legend().fontSize(13d);
            cartesian.legend().padding(0d, 0d, 10d, 0d);



            lineChart.setChart(cartesian);


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