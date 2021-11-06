package com.example.ordertakerfrontend.Network.NetworkMessages.Out.Tables;

import com.example.ordertakerfrontend.BackEnd.Logic.Waitress;
import com.example.ordertakerfrontend.Network.NetworkMessages.tools.NetworkMessage;

import org.json.JSONException;
import org.json.JSONObject;

public class OpenTable extends NetworkMessage {
    private int table;

    public OpenTable(int table){
        this.table = table;
    }
    @Override
    public JSONObject encode() {
        try {
            JSONObject result = makeJSONMessage("OpenTable");
            result.put("table", table);
            return result;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void visit(Waitress Waitress) {

    }
}
