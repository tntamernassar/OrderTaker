package com.example.ordertakerfrontend.Network.NetworkMessages;

import com.example.ordertakerfrontend.BackEnd.Logic.Waitress;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public interface NetworkMessage extends Serializable {

    JSONObject encode();

    void visit(Waitress Waitress);


    default JSONObject makeJSONMessage(String type){
        JSONObject j = new JSONObject();
        try {
            j.put("type", type);
            return j;
        } catch (JSONException e) {
            return null;
        }
    }
}
