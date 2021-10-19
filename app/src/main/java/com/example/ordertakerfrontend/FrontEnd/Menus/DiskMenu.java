package com.example.ordertakerfrontend.FrontEnd.Menus;

import android.content.Context;

import com.example.ordertakerfrontend.BackEnd.Services.FileManager;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class DiskMenu implements Serializable {

    List<MenuProduct> menuProducts;

    public DiskMenu(List<MenuProduct> menuProductList){
        this.menuProducts = menuProductList;
    }

    public void writeToDisk(){
        FileManager.writeObject(this, "menu");
    }


    public void makeMenu(Context context){
        Menu.init(context, menuProducts);
    }


}
