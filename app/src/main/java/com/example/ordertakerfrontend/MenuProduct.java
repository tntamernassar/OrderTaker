package com.example.ordertakerfrontend;


public class MenuProduct{

    private String category;
    private String name;
    private String description;
    private double price;
    private String[] addons;
    private String[] images;


    public MenuProduct(String category, String name, String description, double price, String[] addons, String[] images){
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

    public String[] getAddons() {
        return addons;
    }

    public String[] getImages() {
        return images;
    }
}
