package com.example.ordertakerfrontend.Network.NetworkMessages.tools;


import android.util.Log;

import com.example.ordertakerfrontend.BackEnd.Logic.Waitress;
import com.example.ordertakerfrontend.Network.NetworkMessages.tools.NetworkMessage;

import org.json.JSONObject;

public class TestMessage extends NetworkMessage {

    @Override
    public JSONObject encode() {
        return makeJSONMessage("TestMessage");
    }

    @Override
    public void visit(Waitress waitress) {
        Log.d("From network :", "Visiting res");
    }
}
