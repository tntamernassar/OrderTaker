package com.example.ordertakerfrontend.Network.NetworkMessages.Out;


import com.example.ordertakerfrontend.BackEnd.Logic.Waitress;
import com.example.ordertakerfrontend.Network.NetworkMessages.tools.NetworkMessage;

import org.json.JSONObject;

public class GetOrderHistory extends NetworkMessage {

    @Override
    public JSONObject encode() {
        JSONObject res = makeJSONMessage("GetOrderHistory");
        return res;
    }

    @Override
    public void visit(Waitress waitress) {

    }
}
