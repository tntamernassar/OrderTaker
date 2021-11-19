package com.example.ordertakerfrontend.Network.NetworkMessages.Out.Tables;

import com.example.ordertakerfrontend.BackEnd.Logic.Waitress;
import com.example.ordertakerfrontend.Network.NetworkMessages.tools.NetworkMessage;

import org.json.JSONException;
import org.json.JSONObject;

public class CloseTable extends NetworkMessage {
    private int table;
    private int numberOfPeople;

    public CloseTable(int table, int numberOfPeople){
        this.table = table;
        this.numberOfPeople = numberOfPeople;
    }
    @Override
    public JSONObject encode() {
        try {
            JSONObject result = makeJSONMessage("CloseTable");
            result.put("table", table);
            result.put("numberOfPeople", numberOfPeople);
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
