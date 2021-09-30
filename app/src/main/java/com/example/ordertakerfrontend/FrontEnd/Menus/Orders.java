package com.example.ordertakerfrontend.FrontEnd.Menus;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ordertakerfrontend.BackEnd.Logic.OrderItem;
import com.example.ordertakerfrontend.R;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class Orders extends ArrayAdapter<OrderItem> {
    private List<OrderItem> orders;
    private Context context;
    public Orders(Context context, List<OrderItem> orders){
        super(context, R.layout.order_item, orders);

        this.orders = orders;
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.order_item, parent, false);
        }

        OrderItem order = orders.get(position);
        TextView order_name = (TextView) convertView.findViewById(R.id.order_name);
        TextView order_notes = (TextView) convertView.findViewById(R.id.order_notes);
        ImageView order_image = (ImageView) convertView.findViewById(R.id.order_image);


        /**
         * add here order history
         * */
        String notes = "";
        for(String s: ((OrderProduct)order.getProduct()).getAddons().keySet()){
            for(String s1: ((OrderProduct)order.getProduct()).getAddons().get(s)){
                notes += s1 + "/";
            }
        }
        order_name.setText(((OrderProduct)order.getProduct()).getmProduct().getName());
        order_notes.setText(notes);


        return convertView;
    }
}
