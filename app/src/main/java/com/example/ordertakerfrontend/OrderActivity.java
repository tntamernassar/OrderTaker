package com.example.ordertakerfrontend;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.ordertakerfrontend.BackEnd.Logic.OrderItem;
import com.example.ordertakerfrontend.BackEnd.Services.Constants;
import com.example.ordertakerfrontend.BackEnd.Services.ImagesManager;
import com.example.ordertakerfrontend.BackEnd.Services.Utils;
import com.example.ordertakerfrontend.FrontEnd.Menus.Menu;
import com.example.ordertakerfrontend.FrontEnd.Menus.MenuProduct;
import com.example.ordertakerfrontend.FrontEnd.Menus.MenuSection;
import com.example.ordertakerfrontend.FrontEnd.Menus.OrderProduct;
import com.example.ordertakerfrontend.FrontEnd.Menus.Orders;
import com.example.ordertakerfrontend.FrontEnd.Popups.ItemSubmitCallback;
import com.example.ordertakerfrontend.FrontEnd.Popups.OnePageOrderActivity;
import com.example.ordertakerfrontend.FrontEnd.Popups.PopupAddons;
import com.example.ordertakerfrontend.FrontEnd.Popups.YesNoCallbacks;
import com.example.ordertakerfrontend.Network.NetworkManager.NetworkAdapter;
import com.example.ordertakerfrontend.Network.NetworkMessages.In.MenuEditNotification;
import com.example.ordertakerfrontend.Network.NetworkMessages.In.OrderHistoryContainer;
import com.example.ordertakerfrontend.Network.NetworkMessages.In.ServerImage;
import com.example.ordertakerfrontend.Network.NetworkMessages.In.Tables.CancelTableNotification;
import com.example.ordertakerfrontend.Network.NetworkMessages.In.Tables.CloseTableNotification;
import com.example.ordertakerfrontend.Network.NetworkMessages.In.Tables.OpenTableNotification;
import com.example.ordertakerfrontend.Network.NetworkMessages.In.Tables.SubmitTableNotification;
import com.example.ordertakerfrontend.Network.NetworkMessages.In.initResponse;
import com.example.ordertakerfrontend.Network.NetworkMessages.Out.Tables.CancelTable;
import com.example.ordertakerfrontend.Network.NetworkMessages.Out.Tables.CloseTable;
import com.example.ordertakerfrontend.Network.NetworkMessages.Out.Tables.SubmitTable;
import com.example.ordertakerfrontend.Network.NetworkMessages.tools.MessageObserver;
import com.example.ordertakerfrontend.Network.NetworkMessages.tools.NetworkMessage;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import java.util.LinkedList;
import java.util.List;

public class OrderActivity extends AppCompatActivity implements OnePageOrderActivity, MessageObserver {

    private String id="orderActivity";
    private int tableId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        getSupportActionBar().hide();

        NetworkAdapter.getInstance().register(id, this);
        Intent intent = getIntent();
        this.tableId = intent.getExtras().getInt("table");

        buildTabs();

        createOrdersList();
        updateOrderPrice();

        TabLayout categories = (TabLayout) findViewById(R.id.categories);
        String selected = categories.getTabAt(categories.getSelectedTabPosition()).getText().toString();
        createMenuList(selected);

        Button cancel = findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utils.YesNoDialog(OrderActivity.this, "لالغاء الطلب اضغط نعم", new YesNoCallbacks() {
                    @Override
                    public void yes() {
                        Constants.WAITRESS.cancelOrder(tableId);
                        NetworkAdapter.getInstance().send(new CancelTable(tableId));
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
                        NetworkAdapter.getInstance().send(new SubmitTable(Constants.WAITRESS.getRestaurant().getTable(tableId)));
                    }

                    @Override
                    public void no() { }
                });
            }
        });

        Button close = findViewById(R.id.close);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Utils.YesNoDialog(OrderActivity.this, "لاغلاق الطلب اضغط نعم", new YesNoCallbacks() {
                    @Override
                    public void yes() {
                        Constants.WAITRESS.closeOrder(tableId);
                        NetworkAdapter.getInstance().send(new CloseTable(tableId));
                        Intent intent = new Intent(OrderActivity.this, MainActivity.class);
                        startActivity(intent);
                    }

                    @Override
                    public void no() { }
                });

            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        NetworkAdapter.getInstance().unregister(id);
    }

    public void buildTabs(){
        TabLayout categories = (TabLayout) findViewById(R.id.categories);

        int currentTabsCount = categories.getTabCount();
        if (currentTabsCount == 0){
            for (String category : Menu.getInstance().getCategories()) {
                TabLayout.Tab tab = categories.newTab();
                tab.setText(category);
                categories.addTab(categories.newTab().setText(category));
            }
        }else{
            String[] menuCategories = Menu.getInstance().getCategories();
            if (currentTabsCount != menuCategories.length){
                String selected = categories.getTabAt(categories.getSelectedTabPosition()).getText().toString();

                categories.removeAllTabs();
                int new_index = -1;
                int i = 0;
                for (String category : Menu.getInstance().getCategories()) {
                    categories.addTab(categories.newTab().setText(category));
                    if (category.equals(selected)){
                        new_index = i;
                    }
                    i++;
                }

                if(!List.of(menuCategories).contains(selected)){
                    categories.selectTab(categories.getTabAt(0));
                }else{
                    categories.selectTab(categories.getTabAt(new_index));
                }
            }
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
    }

    @Override
    public void updateOrderPrice(){
        double price = Constants.WAITRESS.getRestaurant().getTable(tableId).getCurrentOrder().calculatePrice();
        TextView total_price = findViewById(R.id.total_price);
        total_price.setText(price + "₪");
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


    private void createMenuList(String category) {
        ListView listView = findViewById(R.id.menu_holder);

        Menu menu = Menu.getInstance().getSubMenu(category);
        listView.setAdapter(menu);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                MenuProduct menuProduct = menu.getMenuProductList().get(i);
                if(menuProduct.isAvailable()) {
                    makeItemPopUp(menuProduct, view, (AlertDialog alertDialog, OrderProduct orderProduct, int quantity, String notes) -> {
                        Constants.WAITRESS.orderItem(tableId, orderProduct, quantity, notes);
                        alertDialog.cancel();
                        createOrdersList();
                        updateOrderPrice();
                    });
                }
            }
        });
    }

    @Override
    public void createOrdersList() {
        ListView parentList = findViewById(R.id.order_holder);
        List<OrderItem> orderItems = new LinkedList<>();
        for (OrderItem o : Constants.WAITRESS.getRestaurant().getTable(tableId).getCurrentOrder().getOrderItems().values()) {
            System.out.println(o.toJSON());
            if(!o.isDeleted()) {
                orderItems.add(o);
            }
        }

        Orders orders = new Orders(this, tableId,OrderActivity.this, orderItems);
        parentList.setAdapter(orders);
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

        Bitmap bitmap = ImagesManager.readImage(menuProduct.getImages()[0]);
        image.setImageBitmap(bitmap);

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

    @Override
    public void makeItemPopUp(MenuProduct menuProduct, View view, ItemSubmitCallback submitCallback) {
        makeItemPopUp(new OrderProduct(menuProduct), view, 1, "", submitCallback);
    }

    @Override
    public void accept(initResponse message) {
        runOnUiThread(()->{
            TabLayout categories = (TabLayout) findViewById(R.id.categories);
            String selected = categories.getTabAt(categories.getSelectedTabPosition()).getText().toString();
            createMenuList(selected);
            createOrdersList();
        });
    }

    @Override
    public void accept(MenuEditNotification message) {
        runOnUiThread(()->{
            buildTabs();
            TabLayout categories = (TabLayout) findViewById(R.id.categories);
            String selected = categories.getTabAt(categories.getSelectedTabPosition()).getText().toString();
            createMenuList(selected);
        });
    }

    @Override
    public void accept(CloseTableNotification message) {
        runOnUiThread(()->{
            if (message.getTable() == tableId){
                Intent mainActivity = new Intent(this, MainActivity.class);
                startActivity(mainActivity);
            }
        });
    }

    @Override
    public void accept(CancelTableNotification message) {
        runOnUiThread(()->{
            if (message.getTable() == tableId){
                Intent mainActivity = new Intent(this, MainActivity.class);
                startActivity(mainActivity);
            }
        });
    }

    @Override
    public void accept(SubmitTableNotification message) {
        runOnUiThread(()->{
            createOrdersList();
            updateOrderPrice();
        });
    }

    @Override
    public void accept(NetworkMessage message) { }

    @Override
    public void accept(OpenTableNotification message) { }

    @Override
    public void accept(ServerImage message) { }

    @Override
    public void accept(OrderHistoryContainer message) {

    }

}