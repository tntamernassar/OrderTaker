package com.example.ordertakerfrontend.FrontEnd.Menus;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.ordertakerfrontend.BackEnd.Logic.OrderItem;
import com.example.ordertakerfrontend.BackEnd.Logic.Waitress;
import com.example.ordertakerfrontend.BackEnd.Services.Constants;
import com.example.ordertakerfrontend.BackEnd.Services.Utils;
import com.example.ordertakerfrontend.FrontEnd.Popups.OrderActivity;
import com.example.ordertakerfrontend.FrontEnd.Popups.YesNoCallbacks;
import com.example.ordertakerfrontend.R;
import com.example.ordertakerfrontend.ScrollingActivity;

import java.util.LinkedList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

public class Orders extends ArrayAdapter<OrderItem> {

    private OrderActivity orderActivity;
    private int tableId;
    private List<OrderItem> orders;
    private Context context;


    public Orders(OrderActivity orderActivity, int tableId, Context context, List<OrderItem> orders){
        super(context, R.layout.order_item, orders);
        this.orderActivity = orderActivity;
        this.tableId = tableId;
        this.orders = orders;
        this.context = context;
    }



    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = layoutInflater.inflate(R.layout.order_item, parent, false);

        OrderItem orderItem = orders.get(position);

        TextView order_name = (TextView) row.findViewById(R.id.order_name);
        TextView order_notes = (TextView) row.findViewById(R.id.order_notes);
        TextView quantity = (TextView) row.findViewById(R.id.order_quantity);
        Button delete_item = (Button) row.findViewById(R.id.delete_item);
        Button edit_item = (Button) row.findViewById(R.id.edit_item);


        OrderProduct orderedProduct = (OrderProduct)orderItem.getProduct();


        String notes = "";
        if(orderedProduct.getAddons().keySet().size() > 0){
            for(String s: orderedProduct.getAddons().keySet()){
                for(String s1: orderedProduct.getAddons().get(s)){
                    notes += s1 + " • ";
                }
            }
        }else{
            notes = "بدون اضافات";
        }

        order_name.setText(orderedProduct.getMenuProduct().getName());
        order_notes.setText(notes);
        quantity.setText(orderItem.getQuantity() + "");

        /**
         * Delete item
         * */
        delete_item.setOnClickListener((view)->{
            Utils.YesNoDialog(context, "are you sure you want to delete this item ?", new YesNoCallbacks() {
                @Override
                public void yes() {
                    Constants.WAITRESS.removeItem(tableId, orderItem.getIndex());
                    orderActivity.createOrdersList();
                }

                @Override
                public void no() {

                }
            });
        });

        /***
         * Edit Item
         * */

        edit_item.setOnClickListener((v)->{
            orderActivity.makeItemPopUp(orderedProduct, convertView, orderItem.getQuantity(), orderItem.getNotes(), (AlertDialog alertDialog, OrderProduct newOrderProduct, int newQuantity, String newNotes)->{
                Constants.WAITRESS.editOrder(tableId, orderItem.getIndex(), newOrderProduct, newQuantity, newNotes);
                alertDialog.cancel();
                orderActivity.createOrdersList();
            });
        });

        return row;
    }
}
