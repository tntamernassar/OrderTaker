package com.example.ordertakerfrontend.Network.NetworkMessages.tools;

import com.example.ordertakerfrontend.BackEnd.Logic.Waitress;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public abstract class NetworkMessage implements Serializable {


    public abstract void visit(Waitress Waitress);

    public JSONObject encode() {
        return null;
    }

    public JSONObject makeJSONMessage(String type){
        JSONObject j = new JSONObject();
        try {
            j.put("type", type);
            return j;
        } catch (JSONException e) {
            return null;
        }
    }

    public void visitMessageObserver(MessageObserver messageObserver){
        messageObserver.accept(this);
    }
}
