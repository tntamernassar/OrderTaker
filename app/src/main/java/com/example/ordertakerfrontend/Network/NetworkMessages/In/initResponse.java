package com.example.ordertakerfrontend.Network.NetworkMessages.In;

import com.example.ordertakerfrontend.BackEnd.Logic.Table;
import com.example.ordertakerfrontend.BackEnd.Logic.Waitress;
import com.example.ordertakerfrontend.BackEnd.Services.ImagesManager;
import com.example.ordertakerfrontend.BackEnd.Services.Utils;
import com.example.ordertakerfrontend.FrontEnd.Menus.DiskMenu;
import com.example.ordertakerfrontend.FrontEnd.Menus.Menu;
import com.example.ordertakerfrontend.FrontEnd.Menus.MenuProduct;
import com.example.ordertakerfrontend.FrontEnd.Menus.MenuSection;
import com.example.ordertakerfrontend.Network.NetworkMessages.tools.MessageObserver;
import com.example.ordertakerfrontend.Network.NetworkMessages.tools.NetworkMessage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.LinkedList;


public class initResponse extends NetworkMessage {

    private JSONObject menu;
    private JSONArray serverImages;
    private JSONArray tables;

    public initResponse(JSONObject menu, JSONArray serverImages, JSONArray tables){
        this.menu = menu;
        this.serverImages = serverImages;
        this.tables = tables;
    }

    public JSONArray getServerImages() {
        return serverImages;
    }

    public void deleteExtraImages(){
        try{
            String[] tabletImages = ImagesManager.listImages();
            LinkedList<String> serverImagesList = new LinkedList<>();
            for(int i = 0; i < serverImages.length(); i++){
                serverImagesList.add(serverImages.getString(i));
            }

            System.out.println("Tablet images : " + Arrays.toString(tabletImages));
            System.out.println("Server images : " + Arrays.toString(serverImagesList.toArray()));

            for(String tabletImage: tabletImages){
                if (!serverImagesList.contains(tabletImage)){
                    ImagesManager.deleteImage(tabletImage);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public LinkedList<MenuProduct> getMenuProducts(){
        try {
            LinkedList<MenuProduct> menuProducts = new LinkedList<>();
            JSONArray menuProductsJson = (JSONArray) menu.get("menuProductList");
            for(int i = 0; i < menuProductsJson.length(); i++){
                JSONObject menuProductJson = (JSONObject)menuProductsJson.get(i);

                String category, name, description;
                int price;
                boolean available;
                String[] images = null;
                LinkedList<MenuSection> menuSections = null;

                category = menuProductJson.getString("category");
                name = menuProductJson.getString("name");
                description = menuProductJson.getString("description");
                price = menuProductJson.getInt("price");
                available = menuProductJson.getBoolean("available");

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

                menuProducts.add(new MenuProduct(category,name, description, price, available, menuSections, images));
            }
            return menuProducts;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void mergeTables(Waitress waitress){
        try {
            for(int i = 0 ; i < tables.length(); i++){
                JSONObject tablesJSONObject = tables.getJSONObject(i);
                Table serverTable = new Table(tablesJSONObject);
                Table clientTable = waitress.getRestaurant().getTable(serverTable.getNumber());
                System.out.println(serverTable.toJSON());
                System.out.println(clientTable.toJSON());

                if (clientTable.isActive() && !serverTable.isActive()){
                    // if serverTable closed after client table order is opened, close client table
                    if (serverTable.getClosedAt() != null && Utils.after(serverTable.getClosedAt(), clientTable.getCurrentOrder().getStartedAt())){
                        waitress.closeOrder(clientTable.getNumber());
                    }
                }else if (!clientTable.isActive() && serverTable.isActive()){
                    // if serverTable order opened after clientTable closed, open client table
                    if ( (clientTable.getClosedAt() == null) ||
                            (clientTable.getClosedAt() != null) && Utils.after(serverTable.getCurrentOrder().getStartedAt(), clientTable.getClosedAt())){
                        waitress.openTable(clientTable.getNumber());
                        clientTable.mergeTable(serverTable);
                    }
                }else if(clientTable.isActive() && serverTable.isActive()){
                    // if clientTable opened the same is server table, merge the two tables
                    if (clientTable.getCurrentOrder().getStartedAt().equals(serverTable.getCurrentOrder().getStartedAt())){
                        clientTable.mergeTable(serverTable);
                    }
                }else {
                    clientTable.setTable(serverTable);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void visit(Waitress waitress) {
        LinkedList<MenuProduct> serverMenuProducts = getMenuProducts();
        Menu.getInstance().setMenuProductList(serverMenuProducts);
        DiskMenu diskMenu = new DiskMenu(serverMenuProducts);
        diskMenu.execute();
        deleteExtraImages();
        mergeTables(waitress);
    }

    @Override
    public void visitMessageObserver(MessageObserver messageObserver) {
        messageObserver.accept(this);
    }
}
