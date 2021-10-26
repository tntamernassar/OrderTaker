package com.example.ordertakerfrontend;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.ordertakerfrontend.BackEnd.Logic.OrderItem;
import com.example.ordertakerfrontend.BackEnd.Services.Constants;
import com.example.ordertakerfrontend.BackEnd.Services.Utils;
import com.example.ordertakerfrontend.FrontEnd.Menus.Menu;
import com.example.ordertakerfrontend.FrontEnd.Menus.MenuProduct;
import com.example.ordertakerfrontend.FrontEnd.Menus.MenuSection;
import com.example.ordertakerfrontend.FrontEnd.Menus.OrderProduct;
import com.example.ordertakerfrontend.FrontEnd.Menus.Orders;
import com.example.ordertakerfrontend.FrontEnd.Popups.ItemSubmitCallback;
import com.example.ordertakerfrontend.FrontEnd.Popups.PopupAddons;
import com.example.ordertakerfrontend.FrontEnd.Popups.YesNoCallbacks;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

public class OrderActivity extends AppCompatActivity implements com.example.ordertakerfrontend.FrontEnd.Popups.OrderActivity {

    private int tableId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        getSupportActionBar().hide();

        Intent intent = getIntent();
        this.tableId = intent.getExtras().getInt("table");

        TabLayout categories = (TabLayout) findViewById(R.id.categories);
        for (String category : Menu.getInstance().getCategories()) {
            TabLayout.Tab tab = categories.newTab();
            tab.setText(category);
            categories.addTab(categories.newTab().setText(category));
        }

        categories.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                createMenuList(tab.getText().toString());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        createOrdersList();

        String selected = categories.getTabAt(categories.getSelectedTabPosition()).getText().toString();
        createMenuList(selected);

        FloatingActionButton cancel = findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utils.YesNoDialog(OrderActivity.this, "لالغاء الطلب اضغط نعم", new YesNoCallbacks() {
                    @Override
                    public void yes() {
                        Constants.WAITRESS.cancelOrder(tableId);
                        Intent intent = new Intent(OrderActivity.this, MainActivity.class);
                        startActivity(intent);
                    }

                    @Override
                    public void no() { }
                });
            }
        });

        FloatingActionButton submit = findViewById(R.id.submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utils.YesNoDialog(OrderActivity.this, "لارسال الطلب اضغط نعم", new YesNoCallbacks() {
                    @Override
                    public void yes() {
                        Constants.WAITRESS.submitOrder(tableId);
                    }

                    @Override
                    public void no() { }
                });
            }
        });

        FloatingActionButton close = findViewById(R.id.close);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Utils.YesNoDialog(OrderActivity.this, "لالغاء الطلب اضغط نعم", new YesNoCallbacks() {
                    @Override
                    public void yes() {
                        Constants.WAITRESS.closeOrder(tableId);
                        Intent intent = new Intent(OrderActivity.this, MainActivity.class);
                        startActivity(intent);
                    }

                    @Override
                    public void no() { }
                });

            }
        });
    }


    private void createMenuList(String category) {
        ListView listView = findViewById(R.id.menu_holder);

        Menu menu = Menu.getInstance().getSubMenu(category);
        listView.setAdapter(menu);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                MenuProduct menuProduct = menu.getMenuProductList().get(i);
                makeItemPopUp(menuProduct, view, (AlertDialog alertDialog, OrderProduct orderProduct, int quantity, String notes)->{
                    Constants.WAITRESS.orderItem(tableId, orderProduct, quantity, notes);
                    alertDialog.cancel();
                    createOrdersList();
                });
            }
        });
    }

    public int increaseDecreaseQuantity(View view, char ch) {
        TextView quantity = view.findViewById(R.id.quantity);
        int oldQuantity = Integer.parseInt(quantity.getText().toString());
        int newQuantity;
        switch (ch) {
            case '-':
                if (oldQuantity == 1) {
                    return 1;
                }
                newQuantity = oldQuantity - 1;
                quantity.setText(newQuantity + "");
                return newQuantity;

            case '+':
                newQuantity = oldQuantity + 1;
                quantity.setText(newQuantity + "");
                return newQuantity;
            default:
                Log.d("xx", "Incorrect arguments, must provide - or + as char ");
                return -1;
        }
    }

    @Override
    public void createOrdersList() {
        ListView parentList = findViewById(R.id.order_holder);
        List<OrderItem> orderItems = new LinkedList<>();
        for (OrderItem o : Constants.WAITRESS.getRestaurant().getTable(tableId).getCurrentOrder().getOrderItems().values()) {
            orderItems.add(o);
        }

        Orders orders = new Orders(this, tableId,OrderActivity.this, orderItems);
        parentList.setAdapter(orders);
    }

    @Override
    public void makeItemPopUp(MenuProduct menuProduct, View view, ItemSubmitCallback submitCallback) {
        makeItemPopUp(new OrderProduct(menuProduct), view, 1, "", submitCallback);
    }

    @Override
    public void makeItemPopUp(OrderProduct orderProduct, View view, int previousQuantity, String previousNote, ItemSubmitCallback submitCallback) {
        MenuProduct menuProduct = orderProduct.getMenuProduct();
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        View menuPopup = this.getLayoutInflater().inflate(R.layout.menu_popup, (ViewGroup) view, false);
        dialogBuilder.setView(menuPopup);
        AlertDialog dialog = dialogBuilder.create();


        TextView nameTV = menuPopup.findViewById(R.id.name);
        TextView descTV = menuPopup.findViewById(R.id.description);
        ImageView image = menuPopup.findViewById(R.id.image);
        TextView quantity = menuPopup.findViewById(R.id.quantity);
        TextView notes = menuPopup.findViewById(R.id.notes);

        File imgFile = new File(getFilesDir().getAbsolutePath() + "/" + menuProduct.getImages()[0]);
        Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
        image.setImageBitmap(myBitmap);

        nameTV.setText(menuProduct.getName());
        descTV.setText(menuProduct.getDescription());
        quantity.setText(previousQuantity + "");
        notes.setText(previousNote);

        /** popupAddons have been created as an array of 1 item
         *  Because it will be used inside of a callback
         * */
        PopupAddons[] popupAddons = new PopupAddons[1];


        /**
         * Display Addons
         * */
        if (menuProduct.getSections() != null) {
            ListView AddonsHandler = menuPopup.findViewById(R.id.addons_handler);
            LinkedList<String[]> addons = new LinkedList<>();
            LinkedList<MenuSection> sections = menuProduct.getSections();
            for (MenuSection ms : sections) {
                addons.add(ms.getAddons());
            }
            popupAddons[0] = new PopupAddons(getApplicationContext(), R.layout.section_addons, sections, orderProduct.getAddons());
            AddonsHandler.setAdapter(popupAddons[0]);

            int totalHeight = 0;
            for (int index = 0; index < popupAddons[0].getCount(); index++) {
                View listItem = popupAddons[0].getView(index, null, AddonsHandler);
                listItem.measure(0, 0);
                totalHeight += listItem.getMeasuredHeight();
            }

            ViewGroup.LayoutParams par = AddonsHandler.getLayoutParams();
            par.height = totalHeight + (AddonsHandler.getDividerHeight() * (popupAddons[0].getCount() - 1));
            AddonsHandler.setLayoutParams(par);
            AddonsHandler.requestLayout();
        }

        /**
         * increase and decrease quantity buttons.
         * */
        FloatingActionButton f = menuPopup.findViewById(R.id.increase_quantity);
        f.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                increaseDecreaseQuantity(menuPopup, '+');
            }
        });

        menuPopup.findViewById(R.id.decrease_quantity).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                increaseDecreaseQuantity(menuPopup, '-');
            }
        });

        /**
         *  setup OrderItem confirmation listener.
         * */
        FloatingActionButton submit_order = (FloatingActionButton) menuPopup.findViewById(R.id.submit_order);
        submit_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OrderProduct ordered;
                if(popupAddons[0] == null){
                    ordered = new OrderProduct(menuProduct);
                }else{
                    ordered = new OrderProduct(menuProduct, popupAddons[0].getChoosed());
                }
                int quantity = Integer.parseInt(((TextView) menuPopup.findViewById(R.id.quantity)).getText().toString());
                String notes = ((EditText) menuPopup.findViewById(R.id.notes)).getText().toString();

                submitCallback.onItemSubmit(dialog, ordered, quantity, notes);
            }
        });


        /**
         * Display Popup
         * */
        dialog.show();
        dialog.getWindow().setLayout(1000, 1500);
    }
}