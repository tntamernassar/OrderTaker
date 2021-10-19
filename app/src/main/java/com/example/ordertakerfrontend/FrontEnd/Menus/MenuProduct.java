package com.example.ordertakerfrontend.FrontEnd.Menus;


import com.example.ordertakerfrontend.BackEnd.Logic.Product;

import java.io.Serializable;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Objects;

public class MenuProduct implements Product, Serializable {

    private String category;
    private String name;
    private String description;
    private double price;
    private String[] images;
    private LinkedList<MenuSection> sections;


    public MenuProduct(String category, String name, String description, double price, LinkedList<MenuSection> sections, String[] images){
        this.category = category;
        this.name = name;
        this.description = description;
        this.price = price;
        this.sections = sections;
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

    public LinkedList<MenuSection> getSections() {
        return sections;
    }

    public String[] getImages() {
        return images;
    }

    public String[] getSectionsName(){
        LinkedList<String> res = new LinkedList<>();
        for(MenuSection ms : sections){
            res.add(ms.getSection());
        }
        return (String[]) res.toArray();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MenuProduct that = (MenuProduct) o;
        return Double.compare(that.price, price) == 0 && category.equals(that.category) && name.equals(that.name) && description.equals(that.description) && Arrays.equals(images, that.images) && Objects.equals(sections, that.sections);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(category, name, description, price, sections);
        result = 31 * result + Arrays.hashCode(images);
        return result;
    }
}
