package com.example.ordertakerfrontend.Network.NetworkMessages.Out;

import com.example.ordertakerfrontend.BackEnd.Logic.Waitress;
import com.example.ordertakerfrontend.Network.NetworkMessages.tools.NetworkMessage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MenuEdit extends NetworkMessage {

    private JSONObject menu;
    private String[] newImages;
    private String[] shouldDeleteImages;

    public MenuEdit(JSONObject menu, String[] newImages, String[] shouldDeleteImages){
        this.menu = menu;
        this.newImages = newImages;
        this.shouldDeleteImages = shouldDeleteImages;
    }

    @Override
    public JSONObject encode() {
        try {
            JSONObject result = makeJSONMessage("MenuEdit");
            result.put("menu", menu);

            JSONArray newImagesArray = new JSONArray();
            for(String img: newImages){
                newImagesArray.put(img);
            }

            JSONArray shouldDeleteImagesArray = new JSONArray();
            for(String img: shouldDeleteImages){
                shouldDeleteImagesArray.put(img);
            }
            result.put("newImages", newImagesArray);
            result.put("shouldDeleteImages", shouldDeleteImagesArray);
            return result;
        }catch (JSONException e){
            e.printStackTrace();
            return null;
        }

    }

    @Override
    public void visit(Waitress waitress) {

    }
}
