package com.example.ordertakerfrontend.FrontEnd.OrderHistory;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.ordertakerfrontend.BackEnd.Logic.Order;
import com.example.ordertakerfrontend.BackEnd.Services.Utils;
import com.example.ordertakerfrontend.FrontEnd.Menus.MenuProduct;
import com.example.ordertakerfrontend.R;

import java.time.format.DateTimeFormatter;
import java.util.LinkedList;

public class OrderHistoryTable extends ArrayAdapter<Order> {

    private LinkedList<Order> orders;

    public OrderHistoryTable(@NonNull Context context, int resource, LinkedList<Order> orders) {
        super(context, resource, orders);
        this.orders = orders;
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = (LayoutInflater) getContext().getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = layoutInflater.inflate(R.layout.order_history_item, parent, false);
        Order order = this.orders.get(position);

        TextView table = row.findViewById(R.id.table);
        TextView date = row.findViewById(R.id.date);
        TextView price = row.findViewById(R.id.price);

        table.setText(order.getTable() + "");
        date.setText(Utils.dateToString(order.getStartedAt()));
        price.setText(order.calculatePrice() + "₪");

        return row;
    }
}
