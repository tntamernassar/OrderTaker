package com.example.ordertakerfrontend.Network.NetworkMessages.Out;

import com.example.ordertakerfrontend.BackEnd.Logic.Waitress;
import com.example.ordertakerfrontend.BackEnd.Services.ImagesManager;
import com.example.ordertakerfrontend.Network.NetworkMessages.tools.NetworkMessage;

import org.json.JSONArray;
import org.json.JSONObject;

public class initRequest extends NetworkMessage {


    @Override
    public JSONObject encode() {
        // add current images
        try {
            String[] images = ImagesManager.listImages();
            JSONObject jsonObject = makeJSONMessage("initRequest");
            JSONArray jsonArray = new JSONArray(images);
            jsonObject.put("images", jsonArray);
            return jsonObject;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void visit(Waitress waitress) {

    }
}
