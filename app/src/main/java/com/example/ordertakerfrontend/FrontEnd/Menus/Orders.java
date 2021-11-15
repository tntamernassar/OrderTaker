package com.example.ordertakerfrontend.FrontEnd.Menus;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.example.ordertakerfrontend.BackEnd.Logic.OrderItem;
import com.example.ordertakerfrontend.BackEnd.Services.Constants;
import com.example.ordertakerfrontend.BackEnd.Services.Utils;
import com.example.ordertakerfrontend.FrontEnd.Popups.OnePageOrderActivity;
import com.example.ordertakerfrontend.FrontEnd.Popups.YesNoCallbacks;
import com.example.ordertakerfrontend.R;

import java.util.LinkedList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;

public class Orders extends ArrayAdapter<OrderItem> {

    private OnePageOrderActivity orderActivity;
    private int tableId;
    private List<OrderItem> orders;
    private Context context;


    public Orders(OnePageOrderActivity orderActivity, int tableId, Context context, List<OrderItem> orders){
        super(context, R.layout.order_item, orders);
        this.orderActivity = orderActivity;
        this.tableId = tableId;
        this.orders = orders;
        this.context = context;
    }



    @RequiresApi(api = Build.VERSION_CODES.O)
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = layoutInflater.inflate(R.layout.order_item, parent, false);

        OrderItem orderItem = orders.get(position);
        OrderProduct orderedProduct = (OrderProduct)orderItem.getProduct();

        TextView order_name = (TextView) row.findViewById(R.id.order_name);
        TextView name_note = (TextView) row.findViewById(R.id.name_note);
        TextView order_notes = (TextView) row.findViewById(R.id.order_notes);
        TextView quantity = (TextView) row.findViewById(R.id.order_quantity);
        Button delete_item = (Button) row.findViewById(R.id.delete_item);


        LinkedList<String> all_addons = new LinkedList<>();
        for(String s: orderedProduct.getAddons().keySet()){
            LinkedList<String> addons = orderedProduct.getAddons().get(s);
            all_addons.addAll(addons);
        }
        String addons_string = String.join(" • ", all_addons);


        if(addons_string.equals("")){
            addons_string = "بدون اضافات";
        }


        order_name.setText(orderedProduct.getMenuProduct().getName());
        if(!orderedProduct.getMenuProduct().isAvailable()){
            name_note.setText("- غير متاح");
        }
        order_notes.setText(addons_string);
        quantity.setText(orderItem.getQuantity()+"");



        /**
         * Delete item
         * */
        delete_item.setOnClickListener((view)->{
            Utils.YesNoDialog(context, "are you sure you want to delete this item ?", new YesNoCallbacks() {
                @Override
                public void yes() {
                    Constants.WAITRESS.removeItem(tableId, orderItem.getIndex());
                    orderActivity.createOrdersList();
                    orderActivity.updateOrderPrice();
                }

                @Override
                public void no() {

                }
            });
        });

        /***
         * Edit Item
         * */
        row.setOnClickListener((v)->{
            orderActivity.makeItemPopUp(orderedProduct, convertView, orderItem.getQuantity(), orderItem.getNotes(), (AlertDialog alertDialog, OrderProduct newOrderProduct, int newQuantity, String newNotes)->{
                Constants.WAITRESS.editOrder(tableId, orderItem.getIndex(), newOrderProduct, newQuantity, newNotes);
                alertDialog.cancel();
                orderActivity.createOrdersList();
                orderActivity.updateOrderPrice();
            });
        });

        return row;
    }
}
