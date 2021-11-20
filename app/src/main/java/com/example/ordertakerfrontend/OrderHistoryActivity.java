package com.example.ordertakerfrontend;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.example.ordertakerfrontend.BackEnd.Logic.Order;
import com.example.ordertakerfrontend.BackEnd.Logic.OrderHistory;
import com.example.ordertakerfrontend.BackEnd.Logic.OrderItem;
import com.example.ordertakerfrontend.BackEnd.Services.Constants;
import com.example.ordertakerfrontend.BackEnd.Services.Utils;
import com.example.ordertakerfrontend.FrontEnd.Menus.OrderProduct;
import com.example.ordertakerfrontend.FrontEnd.OrderHistory.DataMock;
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
import java.time.Month;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

public class OrderHistoryActivity extends AppCompatActivity implements MessageObserver {

    private String id = "order_history";
    private boolean syncing;
    private OrderHistory orderHistory;
    private LinearLayout container;
    private boolean inCharts;
    private String[] months = new String[]{"January", "February", "March", "April", "May", "June", "July", "Aguste", "September", "October", "November", "December"};
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_history);
        getSupportActionBar().hide();



        this.container = findViewById(R.id.container);
        this.syncing = true;
        NumberPicker numberPicker = findViewById(R.id.month);
        numberPicker.setDisplayedValues(months);
        numberPicker.setMinValue(0);
        numberPicker.setMaxValue(months.length-1);
        numberPicker.setValue(LocalDateTime.now().getMonthValue() - 1);
        numberPicker.setOnScrollListener(new NumberPicker.OnScrollListener() {
            @Override
            public void onScrollStateChange(NumberPicker numberPicker, int i) {
                if(i == 0) {
                    AsyncTask task = new AsyncTask() {
                        @Override
                        protected Object doInBackground(Object[] objects) {
                            if (inCharts) {
                                orderHistory = new OrderHistory(DataMock.makeRandomOrders(getMonth()));
                                runOnUiThread(()->{
                                    charts();
                                });
                            } else {
                                orderHistory = new OrderHistory(DataMock.makeRandomOrders(getMonth()));
                                runOnUiThread(()->{
                                    table();
                                });
                            }
                            return null;
                        }
                    };
                    task.execute();
                }
            }
        });

        NetworkAdapter.getInstance().register(id, this);
//        NetworkAdapter.getInstance().send(new GetOrderHistory());
        syncing = false;
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... voids) {
                runOnUiThread(()->{
                    orderHistory = new OrderHistory(DataMock.makeRandomOrders(getMonth()));
                    charts();
                });
                return null;
            }
        }.execute();


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

    private Month getMonth(){
        NumberPicker numberPicker = findViewById(R.id.month);
        return Month.of(numberPicker.getValue() + 1);
    }

    @Override
    protected void onStop() {
        super.onStop();
        NetworkAdapter.getInstance().unregister(id);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void charts(){
        this.inCharts = true;
        this.container.removeAllViews();
        if (this.orderHistory != null && this.orderHistory.getOrders().isEmpty()){
            Utils.ShowWarningAlert(this, "قائمة الطلبيات فارغة");
        }else{
            if(!syncing) {
                System.out.println("Charts !");
                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View inflated = inflater.inflate(R.layout.report_layout, null);
                this.container.addView(inflated);

                Collections.sort(this.orderHistory.getOrders(), new Comparator<Order>() {
                    @Override
                    public int compare(Order order, Order t1) {
                        return -1 * order.getStartedAt().compareTo(t1.getStartedAt());
                    }
                });

                OrderHistoryCharts orderHistoryCharts = new OrderHistoryCharts(this.orderHistory.getOrders(), getMonth());

                // chart for quantity X days.
                orderHistoryCharts.createIncomeLineChart(inflated);
                orderHistoryCharts.createTrafficPeopleChart(inflated);
                orderHistoryCharts.createTrafficLineChart(inflated);
                orderHistoryCharts.createSectionsPieChart(inflated);
                orderHistoryCharts.createTopNBarChart(inflated, 5);

            }
        }
    }

    private void table(){
        this.inCharts = false;
        this.container.removeAllViews();
        if (this.orderHistory != null && this.orderHistory.getOrders().isEmpty()){
            Utils.ShowWarningAlert(this, "قائمة الطلبيات فارغة");
        }else{
            if(!syncing) {
                System.out.println("Tables");
                Activity that = this;
                ListView table = new ListView(getApplicationContext());
                this.container.addView(table);
                table.setAdapter(new OrderHistoryTable(getApplicationContext(), table.getId(), this.orderHistory.getOrders()));
                table.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        Order o = (Order) adapterView.getItemAtPosition(i);
                        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(that);
                        View popUp = that.getLayoutInflater().inflate(R.layout.order_record_popup, (ViewGroup) view, false);
                        dialogBuilder.setView(popUp);
                        AlertDialog dialog = dialogBuilder.create();

                        TextView table_num = popUp.findViewById(R.id.table_num);
                        TextView number_of_people = popUp.findViewById(R.id.number_of_people);
                        TextView order_date = popUp.findViewById(R.id.order_date);
                        TextView price = popUp.findViewById(R.id.price);
                        LinearLayout order_items = popUp.findViewById(R.id.order_items);

                        table_num.setText(o.getTable() + "");
                        number_of_people.setText(o.getNumberOfPeople() + "");
                        order_date.setText(Utils.dateToString(o.getStartedAt()) + "");
                        price.setText(o.calculatePrice() + "");

                        HashMap<String, Integer> productsQuantities = new HashMap<>();
                        for (OrderItem orderItem: o.getOrderItems().values()){
                            OrderProduct orderProduct = (OrderProduct)orderItem.getProduct();
                            String productName = orderProduct.getMenuProduct().getName();
                            if (!productsQuantities.containsKey(productName)){
                                productsQuantities.put(productName, 0);
                            }

                            productsQuantities.put(productName, productsQuantities.get(productName) + orderItem.getQuantity());
                        }

                        for (String name: productsQuantities.keySet()) {
                            TextView textView = new TextView(getApplicationContext());
                            textView.setTextSize(30);
                            textView.setTextDirection(View.TEXT_DIRECTION_RTL);
                            StringBuilder stringBuilder = new StringBuilder("");
                            stringBuilder.append("x");
                            stringBuilder.append(productsQuantities.get(name));
                            stringBuilder.append(" ");
                            stringBuilder.append(name);
                            textView.setText(stringBuilder.toString());
                            order_items.addView(textView);
                        }

                        dialog.show();
                        dialog.getWindow().setLayout(1000, ViewGroup.LayoutParams.WRAP_CONTENT);
                    }
                });
                ViewGroup.LayoutParams params = table.getLayoutParams();
                params.width = ViewGroup.LayoutParams.MATCH_PARENT;
                params.height = ViewGroup.LayoutParams.MATCH_PARENT;
                table.setLayoutParams(params);
                table.requestLayout();
            }
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void accept(OrderHistoryContainer message) {
        runOnUiThread(()->{
            this.syncing = false;
//            this.orderHistory = Constants.WAITRESS.getRestaurant().getOrderHistory();
            this.orderHistory = new OrderHistory((DataMock.data_set_1));
            charts();
        });
    };

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

}