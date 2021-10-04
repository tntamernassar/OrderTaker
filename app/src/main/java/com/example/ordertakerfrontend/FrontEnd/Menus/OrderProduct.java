package com.example.ordertakerfrontend.FrontEnd.Menus;

import com.example.ordertakerfrontend.BackEnd.Logic.Product;

import java.util.HashMap;
import java.util.LinkedList;

public class OrderProduct implements Product{

    private MenuProduct menuProduct;
    private HashMap<String, LinkedList<String>> addons;

    public OrderProduct(MenuProduct mProduct, HashMap<String, LinkedList<String>> addons){
        this.menuProduct = mProduct;
        this.addons = addons;
    }

    public MenuProduct getMenuProduct() {
        return menuProduct;
    }

    public HashMap<String, LinkedList<String>> getAddons() {
        return addons;
    }
}
