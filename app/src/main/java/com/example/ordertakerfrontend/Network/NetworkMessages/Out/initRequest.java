package com.example.ordertakerfrontend.Network.NetworkMessages.Out;

import com.example.ordertakerfrontend.BackEnd.Logic.Waitress;
import com.example.ordertakerfrontend.BackEnd.Services.FileManager;
import com.example.ordertakerfrontend.BackEnd.Services.ImagesManager;
import com.example.ordertakerfrontend.Network.NetworkMessages.NetworkMessage;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Arrays;

public class initRequest implements NetworkMessage {


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
    public void visit(Waitress Waitress) {

    }
}
