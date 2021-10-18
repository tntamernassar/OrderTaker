package com.example.ordertakerfrontend.Network.NetworkMessages;


import android.util.Log;

import com.example.ordertakerfrontend.BackEnd.Logic.Waitress;
import com.example.ordertakerfrontend.FrontEnd.Menus.MenuProduct;

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

    @Override
    public void visit(Waitress Waitress) {
        try {
            JSONArray menuProductsJson = (JSONArray) menu.get("menuProductList");
            for(int i = 0; i < menuProductsJson.length(); i++){
                JSONObject menuProductJson = (JSONObject)menuProductsJson.get(i);

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
