package com.example.ordertakerfrontend.Network.NetworkMessages;


import android.util.Log;

import com.example.ordertakerfrontend.BackEnd.Logic.Waitress;
import com.example.ordertakerfrontend.BackEnd.Services.Constants;

import org.json.JSONException;
import org.json.JSONObject;

public class TestMessage implements NetworkMessage{

    @Override
    public JSONObject encode() {
        return makeJSONMessage("TestMessage");
    }

    @Override
    public void visit(Waitress waitress) {
        Log.d("From network :", "Visiting res");
    }
}
