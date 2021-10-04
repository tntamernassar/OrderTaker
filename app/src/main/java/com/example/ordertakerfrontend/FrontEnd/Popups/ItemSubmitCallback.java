package com.example.ordertakerfrontend.FrontEnd.Popups;

import androidx.appcompat.app.AlertDialog;

import com.example.ordertakerfrontend.FrontEnd.Menus.OrderProduct;

public interface ItemSubmitCallback {

    void onItemSubmit(AlertDialog alertDialog, OrderProduct orderProduct, int quantity, String notes);

}
