package com.example.ordertakerfrontend.FrontEnd;


import com.example.ordertakerfrontend.BackEnd.Logic.Product;

import java.util.HashMap;
import java.util.LinkedList;

public class MenuProduct implements Product {

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

//    public String toString(){
//        return "{\n\t\"category\":\""+this.category+"\", \n" +
//                "\t\"name\":\""+this.name+"\", \n" +
//                "\t\"description\":\""+this.description+"\", \n" +
//                "\t\"price\":"+this.price+", \n" +
//                "\t\"images\":"+concatenate_(this.images)+", \n" +
//                "\t\"addons\":"+concatenate_hash()+" \n";
//
//    }
//
//    public String concatenate_(String[] e){
//        String res = "";
//        for(String s: e){
//            res = res + "\"" + s + "\"" + ",";
//        }
//        res = res.substring(0, res.length()-1);
//        res = "[" + res + "]";
//        return res;
//    }
//
//    public String concatenate_hash(){
//        String res = "";
//
//        if(addons==null){
//            return "null";
//        }
//        for(String s : addons.keySet()){
//            res += "\"" + s + "\":" + concatenate_(addons.get(s)) + ",\n";
//        }
//
//        res = res.substring(0, res.length()-2);
//        res = "{\n" + res + "\n}";
//        return res;
//    }
}
