package com.example.ordertakerfrontend.Network.NetworkMessages;


import com.example.ordertakerfrontend.Network.NetworkMessages.In.ServerImage;
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
            JSONArray serverImages = JSONMessage.getJSONArray("serverImages");
            return new initResponse((JSONObject)JSONMessage.get("menu"), serverImages);
        }else if(type.equals("ServerImage")){
            String name = JSONMessage.getString("name");
            String base64 = JSONMessage.getString("base64");
            long chunks = JSONMessage.getLong("chunks");
            long chunkNumber = JSONMessage.getLong("chunkNumber");
            return new ServerImage(name, base64, chunks, chunkNumber);
        }

        return null;
    }


}
