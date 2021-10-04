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
import com.example.ordertakerfrontend.FrontEnd.Popups.YesNoCallbacks;
import com.example.ordertakerfrontend.R;
import com.example.ordertakerfrontend.ScrollingActivity;

import java.util.LinkedList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class Orders extends ArrayAdapter<OrderItem> {

    private ListView parentList;
    private int tableId;
    private List<OrderItem> orders;
    private Context context;


    public Orders(ListView parentList, int tableId, Context context, List<OrderItem> orders){
        super(context, R.layout.order_item, orders);
        this.parentList = parentList;
        this.tableId = tableId;
        this.orders = orders;
        this.context = context;
    }

    /**
     * Create the order list that contains the waitress notes + menuProduct
     */
    public static void createOrdersList(ListView parentList, int tableId, Context context) {
        List<OrderItem> orders = new LinkedList<>();
        for (OrderItem o : Constants.WAITRESS.getRestaurant().getTable(tableId).getCurrentOrder().getOrderItems().values()) {
            orders.add(o);
        }
        if (orders != null) {
            Orders orders1 = new Orders(parentList, tableId, context, orders);
            parentList.setAdapter(orders1);
        }
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = layoutInflater.inflate(R.layout.order_item, parent, false);

        OrderItem order = orders.get(position);

        TextView order_name = (TextView) row.findViewById(R.id.order_name);
        TextView order_notes = (TextView) row.findViewById(R.id.order_notes);
        TextView quantity = (TextView) row.findViewById(R.id.order_quantity);
        Button delete_item = (Button) row.findViewById(R.id.delete_item);
        Button edit_item = (Button) row.findViewById(R.id.edit_item);


        OrderProduct orderedProduct = (OrderProduct)order.getProduct();


        /**
         * add here order history
         * */
        String notes = "";
        for(String s: orderedProduct.getAddons().keySet()){
            for(String s1: ((OrderProduct)order.getProduct()).getAddons().get(s)){
                notes += s1 + " • ";
            }
        }

        order_name.setText(orderedProduct.getMenuProduct().getName());
        order_notes.setText(notes);
        quantity.setText(order.getQuantity() + "");

        /**
         * Delete item
         * */

        delete_item.setOnClickListener((view)->{
            Utils.YesNoDialog(context, "are you sure you want to delete this item ?", new YesNoCallbacks() {
                @Override
                public void yes() {
                    Constants.WAITRESS.removeItem(tableId, order.getIndex());
                    createOrdersList(parentList, tableId, context);
                }

                @Override
                public void no() {

                }
            });
        });

        return row;
    }
}
