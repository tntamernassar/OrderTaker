package com.example.ordertakerfrontend.Network.NetworkMessages;

import com.example.ordertakerfrontend.BackEnd.Logic.Waitress;

import org.json.JSONObject;

public class initRequest implements NetworkMessage {


    @Override
    public JSONObject encode() {
        return makeJSONMessage("initRequest");
    }

    @Override
    public void visit(Waitress Waitress) {

    }
}
