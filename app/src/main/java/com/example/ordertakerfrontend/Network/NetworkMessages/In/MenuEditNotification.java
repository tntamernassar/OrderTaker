package com.example.ordertakerfrontend.Network.NetworkMessages.In;

import com.example.ordertakerfrontend.BackEnd.Logic.Waitress;
import com.example.ordertakerfrontend.BackEnd.Services.FileManager;
import com.example.ordertakerfrontend.BackEnd.Services.ImagesManager;
import com.example.ordertakerfrontend.FrontEnd.Menus.DiskMenu;
import com.example.ordertakerfrontend.FrontEnd.Menus.Menu;
import com.example.ordertakerfrontend.FrontEnd.Menus.MenuProduct;
import com.example.ordertakerfrontend.Network.NetworkMessages.tools.MessageObserver;
import com.example.ordertakerfrontend.Network.NetworkMessages.tools.NetworkMessage;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.LinkedList;

public class MenuEditNotification extends NetworkMessage {

    private JSONObject menu;
    private JSONArray newImages;
    private JSONArray shouldDelete;

    public MenuEditNotification(JSONObject menu, JSONArray newImages, JSONArray shouldDelete){
        this.menu = menu;
        this.newImages = newImages;
        this.shouldDelete = shouldDelete;
    }


    @Override
    public void visit(Waitress waitress) {
        System.out.println("New Images : " + newImages.toString());
        System.out.println("Should delete Images : " + shouldDelete.toString());

        /** Save menu **/
        initResponse initResponse = new initResponse(this.menu, null, null);
        LinkedList<MenuProduct> serverMenuProducts = initResponse.getMenuProducts();
        Menu.getInstance().setMenuProductList(serverMenuProducts);
        DiskMenu diskMenu = new DiskMenu(serverMenuProducts);
        diskMenu.execute();

        try {
            for (int i = 0; i < shouldDelete.length(); i++) {
                String img = shouldDelete.getString(i);
                ImagesManager.deleteImage(img);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void visitMessageObserver(MessageObserver messageObserver) {
        messageObserver.accept(this);
    }
}
