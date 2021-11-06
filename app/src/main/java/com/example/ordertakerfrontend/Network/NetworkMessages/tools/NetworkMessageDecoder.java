package com.example.ordertakerfrontend.Network.NetworkMessages.tools;


import com.example.ordertakerfrontend.Network.NetworkMessages.In.MenuEditNotification;
import com.example.ordertakerfrontend.Network.NetworkMessages.In.ServerImage;
import com.example.ordertakerfrontend.Network.NetworkMessages.In.Tables.CancelTableNotification;
import com.example.ordertakerfrontend.Network.NetworkMessages.In.Tables.CloseTableNotification;
import com.example.ordertakerfrontend.Network.NetworkMessages.In.Tables.OpenTableNotification;
import com.example.ordertakerfrontend.Network.NetworkMessages.In.initResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class NetworkMessageDecoder {

    public static NetworkMessage decode(JSONObject JSONMessage) throws JSONException{

        String type = (String)JSONMessage.get("type");

        if(type.equals("TestMessage")){
            return new TestMessage();
        }else if(type.equals("initResponse")){
            JSONObject menuObject = (JSONObject) JSONMessage.get("menu");
            JSONArray serverImages = JSONMessage.getJSONArray("serverImages");
            return new initResponse(menuObject, serverImages);
        }else if(type.equals("ServerImage")){
            String name = JSONMessage.getString("name");
            String base64 = JSONMessage.getString("base64");
            long chunks = JSONMessage.getLong("chunks");
            long chunkNumber = JSONMessage.getLong("chunkNumber");
            return new ServerImage(name, base64, chunks, chunkNumber);
        }else if(type.equals("MenuEditNotification")){
            JSONObject menuObject = (JSONObject) JSONMessage.get("menu");
            JSONArray newImages = JSONMessage.getJSONArray("newImages");
            JSONArray shouldDelete = JSONMessage.getJSONArray("shouldDelete");
            return new MenuEditNotification(menuObject, newImages, shouldDelete);
        }else if(type.equals("OpenTableNotification")){
            int table = (int)JSONMessage.get("table");
            return new OpenTableNotification(table);
        }else if(type.equals("CloseTableNotification")){
            int table = (int)JSONMessage.get("table");
            return new CloseTableNotification(table);
        }else if(type.equals("CancelTableNotification")){
            int table = (int)JSONMessage.get("table");
            return new CancelTableNotification(table);
        }

        return null;
    }


}
