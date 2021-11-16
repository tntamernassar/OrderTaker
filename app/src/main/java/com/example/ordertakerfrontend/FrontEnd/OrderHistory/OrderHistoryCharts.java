package com.example.ordertakerfrontend.FrontEnd.OrderHistory;

import android.os.Build;
import android.view.View;
import android.view.ViewGroup;

import com.anychart.APIlib;
import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Cartesian;
import com.anychart.core.cartesian.series.Line;
import com.anychart.enums.Anchor;
import com.anychart.enums.MarkerType;
import com.example.ordertakerfrontend.BackEnd.Logic.Order;
import com.example.ordertakerfrontend.R;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import androidx.annotation.RequiresApi;

public class OrderHistoryCharts {
    public static HashMap<Integer, HashMap<Integer, ArrayList<Order>>> ordersPerMonthPerDays;
    public static HashMap<Integer, Integer> quantityPerDays;
    static int currentMonth;

    @RequiresApi(api = Build.VERSION_CODES.O)
    public OrderHistoryCharts(LinkedList<Order> orders){
        ordersPerMonthPerDays = new HashMap<>();
        quantityPerDays = new HashMap<>();
        currentMonth = (int) LocalDateTime.now().getMonth().getValue();
        init(orders);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void init(LinkedList<Order> orders) {
        for(Order o: orders){
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

    }

    /**
     * Line Charts
     **/
    public static void createTrafficChart(View parent){

        AnyChartView lineChart = (AnyChartView) parent.findViewById(R.id.trafficChartForQuantity);
//        lineChart.setProgressBar(parent.findViewById(R.id.progressBar));
        APIlib.getInstance().setActiveAnyChartView(lineChart);

        // building the line chart:
        List<DataEntry> seriesData = new ArrayList<>();
        for(Integer day: quantityPerDays.keySet())
            seriesData.add(new ValueDataEntry(day + "/" + currentMonth, quantityPerDays.get(day)));


        initializeLineChart(lineChart, seriesData, "كمية المبيعات", "כמות המוצרים", "", "blue");

    }
    public static void createTrafficChartForPrice(View parent){
        AnyChartView priceChart = (AnyChartView) parent.findViewById(R.id.trafficChartForPrice);
        APIlib.getInstance().setActiveAnyChartView(priceChart);

        HashMap<Integer, Integer> pricePerDay = new HashMap<>();

        for(Integer day: ordersPerMonthPerDays.get(currentMonth).keySet()){
            int totalPerDay = 0;
            for(Order o: ordersPerMonthPerDays.get(currentMonth).get(day)){
                totalPerDay += o.calculatePrice();
            }

            pricePerDay.put(day, totalPerDay);
        }


        // building the line chart:
        List<DataEntry> seriesData = new ArrayList<>();
        for(Integer day: pricePerDay.keySet())
            seriesData.add(new ValueDataEntry(day + "/" + currentMonth, pricePerDay.get(day)));

        initializeLineChart(priceChart, seriesData, "كمية المبيعات", "מחיר ₪", "", "red");
    }

    /**
     * Pie Chart
     **/
    public static void createPieChart(View parent){

    }





    public static void initializeLineChart(AnyChartView someChart, List<DataEntry> seriesData, String chartName, String yTitle, String xTitle, String LineColor){
        Cartesian cartesian = AnyChart.line();
        cartesian.yAxis(0).title(yTitle);
        cartesian.xAxis(0).title(xTitle);
        Line series = cartesian.line(seriesData);
        series.name(chartName);
        series.hovered().markers().enabled(true);
        series.hovered().markers()
                .type(MarkerType.CIRCLE)
                .size(4d);
        series.tooltip()
                .position("right")
                .anchor(Anchor.LEFT_CENTER)
                .offsetX(5d)
                .offsetY(5d);

        series.stroke(LineColor);

        cartesian.legend().enabled(true);
        cartesian.legend().fontSize(13d);
        cartesian.legend().padding(0d, 0d, 10d, 0d);
        someChart.setChart(cartesian);
    }
}
