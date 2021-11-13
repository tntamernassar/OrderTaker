package com.example.ordertakerfrontend.Network.NetworkMessages.Out.Tables;

import com.example.ordertakerfrontend.BackEnd.Logic.Table;
import com.example.ordertakerfrontend.BackEnd.Logic.Waitress;
import com.example.ordertakerfrontend.Network.NetworkMessages.tools.NetworkMessage;

import org.json.JSONException;
import org.json.JSONObject;

public class SubmitTable extends NetworkMessage {
    private Table table;

    public SubmitTable(Table table){
        this.table = table;
    }
    @Override
    public JSONObject encode() {
        try {
            JSONObject result = makeJSONMessage("SubmitTable");
            result.put("table", table.toJSON());
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
