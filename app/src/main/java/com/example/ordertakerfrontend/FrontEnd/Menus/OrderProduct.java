package com.example.ordertakerfrontend.FrontEnd.Menus;

import com.example.ordertakerfrontend.BackEnd.Logic.Order;
import com.example.ordertakerfrontend.BackEnd.Logic.Product;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class OrderProduct implements Product{

    private MenuProduct menuProduct;
    private HashMap<String, LinkedList<String>> addons;

    public OrderProduct(MenuProduct mProduct){
        this.menuProduct = mProduct;
        this.addons = new HashMap<String, LinkedList<String>>();
    }

    public OrderProduct(MenuProduct mProduct, HashMap<String, LinkedList<String>> addons){
        this.menuProduct = mProduct;
        this.addons = addons;
    }

    public OrderProduct(JSONObject orderProduct) throws JSONException {

        this.menuProduct = new MenuProduct((JSONObject) orderProduct.get("menuProduct"));
        this.addons = new HashMap<>();

        JSONObject addons = (JSONObject) orderProduct.get("addons");
        JSONArray sections = (JSONArray) addons.get("sections");
        for (int i = 0; i < sections.length(); i++) {
            String section = (String) sections.get(i);
            JSONArray sectionAddons = (JSONArray) addons.get(section);
            LinkedList<String> sectionAddonsStr = new LinkedList<>();
            for (int j = 0; j < sectionAddons.length(); j++) {
                sectionAddonsStr.add((String) sectionAddons.getString(j));
            }
            this.addons.put(section, sectionAddonsStr);
        }

    }

    public MenuProduct getMenuProduct() {
        return menuProduct;
    }

    public HashMap<String, LinkedList<String>> getAddons() {
        return addons;
    }

    @Override
    public JSONObject toJSON() {
        try{
            JSONObject res = new JSONObject();
            res.put("menuProduct", menuProduct.toJSON());

            JSONObject addonsObject = new JSONObject();
            JSONArray sections = new JSONArray();
            for (String section: addons.keySet()){
                sections.put(section);
                JSONArray addonsList = new JSONArray(addons.get(section));
                addonsObject.put(section, addonsList);
            }
            addonsObject.put("sections", sections);
            res.put("addons", addonsObject);

            return res;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Product clone() {
        HashMap<String, LinkedList<String>> hashMap = new HashMap<>();
        for (String key : addons.keySet()){
            LinkedList<String> clonedAddons = new LinkedList<>();
            clonedAddons.addAll(addons.get(key));
            hashMap.put(key, clonedAddons);
        }
        OrderProduct orderProduct = new OrderProduct((MenuProduct) menuProduct.clone(), hashMap);
        return orderProduct;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderProduct that = (OrderProduct) o;
        if (!Objects.equals(addons.keySet(), that.addons.keySet())){
            return false;
        }

        for (String k : addons.keySet()){
            if (!addons.get(k).equals(that.addons.get(k))){
                return false;
            }
        }

        return Objects.equals(menuProduct, that.menuProduct);
    }

    @Override
    public int hashCode() {
        return Objects.hash(menuProduct);
    }
}
