package com.example.ordertakerfrontend.FrontEnd.Popups;

import android.view.View;

import com.example.ordertakerfrontend.FrontEnd.Menus.MenuProduct;
import com.example.ordertakerfrontend.FrontEnd.Menus.OrderProduct;

public interface OnePageOrderActivity {

    /** Create new Item popup **/
    void makeItemPopUp(MenuProduct menuProduct, View view, ItemSubmitCallback submitCallback);

    /** Create Item popup from ordered item **/
    void makeItemPopUp(OrderProduct orderProduct, View view, int previousQuantity, String previousNote, ItemSubmitCallback submitCallback);

    void createOrdersList();

}
