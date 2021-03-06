package com.example.ordertakerfrontend.FrontEnd.Menus;


import com.example.ordertakerfrontend.BackEnd.Logic.Product;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Objects;

public class MenuProduct implements Product, Serializable {

    private String category;
    private String name;
    private String description;
    private double price;
    private boolean available;
    private String[] images;
    private LinkedList<MenuSection> sections;


    public MenuProduct(String category, String name, String description, double price, boolean available, LinkedList<MenuSection> sections, String[] images){
        this.category = category;
        this.name = name;
        this.description = description;
        this.price = price;
        this.sections = sections;
        this.images = images;
        this.available = available;
    }

    public MenuProduct(JSONObject menuProduct) throws JSONException {
        String category = (String) menuProduct.get("category");
        String name = (String) menuProduct.get("name");
        String description = (String) menuProduct.get("description");
        double price = (double) menuProduct.get("price");
        boolean available = (boolean) menuProduct.get("available");
        JSONArray images = (JSONArray) menuProduct.get("images");
        JSONArray sections = (JSONArray) menuProduct.get("sections");

        this.images = new String[images.length()];
        for (int i = 0; i < images.length(); i++) {
            this.images[i] = (String) images.get(i);
        }

        this.sections = new LinkedList<>();
        for (int i = 0; i < sections.length(); i++) {
            this.sections.add(new MenuSection((JSONObject) sections.get(i)));
        }

        this.category = category;
        this.name = name;
        this.description = description;
        this.price = price;
        this.available = available;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
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

    public void setCategory(String category) {
        this.category = category;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setImages(String[] images) {
        this.images = images;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setSections(LinkedList<MenuSection> sections) {
        this.sections = sections;
    }


    public JSONObject toJSON(){
        try {
            JSONObject o = new JSONObject();
            o.put("category", category);
            o.put("name", name);
            o.put("description", description);
            o.put("price", price);
            o.put("available", available);
            JSONArray objects = new JSONArray();
            if(sections != null) {
                for (MenuSection menuSection : sections) {
                    objects.put(menuSection.toJSON());
                }
            }
            o.put("sections", objects);


            JSONArray imagesArray = new JSONArray();
            if(images != null){
                for (int i = 0; i < images.length; i ++){
                    imagesArray.put(images[i]);
                }
            }
            o.put("images", imagesArray);
            return o;
        }catch (JSONException e){
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Product clone() {
        MenuProduct menuProduct = new MenuProduct(category, name, description, price, available, sections != null ? (LinkedList<MenuSection>) sections.clone(): null, images.clone());
        return menuProduct;
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
