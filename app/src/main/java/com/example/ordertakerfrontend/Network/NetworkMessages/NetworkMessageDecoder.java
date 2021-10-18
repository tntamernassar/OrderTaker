package com.example.ordertakerfrontend.Network.NetworkMessages;


import org.json.JSONException;
import org.json.JSONObject;

public class NetworkMessageDecoder {

    public static NetworkMessage decode(JSONObject JSONMessage) throws JSONException{

        String type = (String)JSONMessage.get("type");

        if(type.equals("TestMessage")){
            return new TestMessage();
        }else if(type.equals("initResponse")){
            return new initResponse((JSONObject)JSONMessage.get("menu"));
        }

        return null;
    }


}
