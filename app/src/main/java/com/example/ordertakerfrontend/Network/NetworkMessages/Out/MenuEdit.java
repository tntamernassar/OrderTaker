package com.example.ordertakerfrontend.Network.NetworkMessages.Out;

import com.example.ordertakerfrontend.BackEnd.Logic.Waitress;
import com.example.ordertakerfrontend.Network.NetworkMessages.NetworkMessage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MenuEdit implements NetworkMessage {

    private JSONObject menu;
    private String[] images;

    public MenuEdit(JSONObject menu, String[] images){
        this.menu = menu;
        this.images = images;
    }

    @Override
    public JSONObject encode() {
        try {
            JSONObject result = makeJSONMessage("MenuEdit");
            result.put("menu", menu);
            JSONArray jsonArray = new JSONArray();
            for(String img: images){
                jsonArray.put(img);
            }
            result.put("images", jsonArray);
            return result;
        }catch (JSONException e){
            e.printStackTrace();
            return null;
        }

    }

    @Override
    public void visit(Waitress Waitress) {

    }
}
