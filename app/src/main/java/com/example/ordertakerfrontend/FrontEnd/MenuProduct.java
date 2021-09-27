package com.example.ordertakerfrontend.FrontEnd;


import com.example.ordertakerfrontend.BackEnd.Logic.Product;

import java.util.HashMap;

public class MenuProduct implements Product {

    private String category;
    private String name;
    private String description;
    private double price;
    private String[] images;
    private HashMap<String, String[]> addons;


    public MenuProduct(String category, String name, String description, double price, HashMap<String, String[]> addons, String[] images){
        this.category = category;
        this.name = name;
        this.description = description;
        this.price = price;
        this.addons = addons;
        this.images = images;
    }

    public double getPrice() {
        return price;
    }

    public String getDescription() {
        return description;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public HashMap<String, String[]> getAddons() {
        return addons;
    }

    public String[] getImages() {
        return images;
    }
}
