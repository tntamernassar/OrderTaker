package com.example.ordertakerfrontend.Network.NetworkMessages.Out;

import com.example.ordertakerfrontend.BackEnd.Logic.Waitress;
import com.example.ordertakerfrontend.Network.NetworkMessages.tools.NetworkMessage;

import org.json.JSONException;
import org.json.JSONObject;

public class TabletImage extends NetworkMessage {

    private String name;
    private String base64;
    private int chunks;
    private int chunkNumber;

    public TabletImage(String name, String base64, int chunks, int chunkNumber){
        this.name = name;
        this.base64 = base64;
        this.chunks = chunks;
        this.chunkNumber = chunkNumber;
    }

    @Override
    public JSONObject encode() {
        try {
            JSONObject result = makeJSONMessage("TabletImage");
            result.put("name", name);
            result.put("base64", base64);
            result.put("chunks", chunks);
            result.put("chunkNumber", chunkNumber);
            return result;
        }catch (JSONException jsonException){
            jsonException.printStackTrace();
            return null;
        }
    }

    @Override
    public void visit(Waitress Waitress) {

    }
}
