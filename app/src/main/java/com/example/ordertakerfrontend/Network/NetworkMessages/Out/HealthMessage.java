package com.example.ordertakerfrontend.Network.NetworkMessages.Out;


import com.example.ordertakerfrontend.BackEnd.Logic.Waitress;
import com.example.ordertakerfrontend.Network.NetworkMessages.tools.NetworkMessage;

import org.json.JSONObject;

public class HealthMessage extends NetworkMessage {

    @Override
    public JSONObject encode() {
        return makeJSONMessage("HealthMessage");
    }

    @Override
    public void visit(Waitress waitress) { }
}
