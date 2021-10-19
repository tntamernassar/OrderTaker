package com.example.ordertakerfrontend.Network.NetworkMessages;

import com.example.ordertakerfrontend.BackEnd.Logic.Waitress;
import com.example.ordertakerfrontend.FrontEnd.Menus.DiskMenu;
import com.example.ordertakerfrontend.FrontEnd.Menus.Menu;
import com.example.ordertakerfrontend.FrontEnd.Menus.MenuProduct;
import com.example.ordertakerfrontend.FrontEnd.Menus.MenuSection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;


public class initResponse implements NetworkMessage {

    private JSONObject menu;

    public initResponse(JSONObject menu){
        this.menu = menu;
    }

    @Override
    public JSONObject encode() {
        return null;
    }


    private LinkedList<MenuProduct> getMenuProducts(){
        try {
            LinkedList<MenuProduct> menuProducts = new LinkedList<>();
            JSONArray menuProductsJson = (JSONArray) menu.get("menuProductList");
            for(int i = 0; i < menuProductsJson.length(); i++){
                JSONObject menuProductJson = (JSONObject)menuProductsJson.get(i);

                String category, name, description;
                int price;
                String[] images = null;
                LinkedList<MenuSection> menuSections = null;

                category = menuProductJson.getString("category");
                name = menuProductJson.getString("name");
                description = menuProductJson.getString("description");
                price = menuProductJson.getInt("price");

                JSONArray imagesArray = (JSONArray) menuProductJson.get("images");
                if (imagesArray.length() != 0){
                    images = new String[imagesArray.length()];
                    for (int j = 0; j < imagesArray.length(); j++){
                        images[j] = imagesArray.getString(j);
                    }
                }


                JSONArray sectionArray = menuProductJson.getJSONArray("sections");
                if (sectionArray.length() != 0){
                    menuSections = new LinkedList<>();
                    for (int j = 0; j < sectionArray.length(); j++){
                        JSONObject menuSectionJson = sectionArray.getJSONObject(j);
                        String section = menuSectionJson.getString("section");
                        boolean maxOne = menuSectionJson.getBoolean("maxOne");

                        JSONArray addonsArray = menuSectionJson.getJSONArray("addons");
                        String[] addons = new String[addonsArray.length()];
                        for (int k = 0; k < addonsArray.length(); k++){
                            addons[k] = addonsArray.getString(k);
                        }

                        MenuSection menuSection = new MenuSection(section, addons, maxOne);
                        menuSections.add(menuSection);
                    }
                }

                menuProducts.add(new MenuProduct(category,name, description, price, menuSections, images));
            }
            return menuProducts;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void visit(Waitress waitress) {
        LinkedList<MenuProduct> serverMenuProducts = getMenuProducts();
        Menu.getInstance().setMenuProductList(serverMenuProducts);
        DiskMenu diskMenu = new DiskMenu(serverMenuProducts);
        diskMenu.writeToDisk();
    }

}
