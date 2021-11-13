package com.example.ordertakerfrontend.Network.NetworkMessages.Out.Tables;

import com.example.ordertakerfrontend.BackEnd.Logic.Waitress;
import com.example.ordertakerfrontend.Network.NetworkMessages.tools.NetworkMessage;

import org.json.JSONException;
import org.json.JSONObject;

public class CancelTable extends NetworkMessage {
    private int table;

    public CancelTable(int table){
        this.table = table;
    }
    @Override
    public JSONObject encode() {
        try {
            JSONObject result = makeJSONMessage("CancelTable");
            result.put("table", table);
            return result;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void visit(Waitress waitress) {

    }
}
