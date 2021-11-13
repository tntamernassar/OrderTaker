package com.example.ordertakerfrontend.Network.NetworkMessages.Out.Tables;

import com.example.ordertakerfrontend.BackEnd.Logic.Waitress;
import com.example.ordertakerfrontend.Network.NetworkMessages.tools.NetworkMessage;

import org.json.JSONException;
import org.json.JSONObject;

public class CloseTable extends NetworkMessage {
    private int table;

    public CloseTable(int table){
        this.table = table;
    }
    @Override
    public JSONObject encode() {
        try {
            JSONObject result = makeJSONMessage("CloseTable");
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
