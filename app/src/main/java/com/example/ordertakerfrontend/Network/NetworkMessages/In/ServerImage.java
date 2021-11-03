package com.example.ordertakerfrontend.Network.NetworkMessages.In;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.example.ordertakerfrontend.BackEnd.Logic.Waitress;
import com.example.ordertakerfrontend.BackEnd.Services.ImagesManager;
import com.example.ordertakerfrontend.BackEnd.Services.Utils;
import com.example.ordertakerfrontend.Network.NetworkMessages.NetworkMessage;

import org.json.JSONObject;

public class ServerImage implements NetworkMessage {

    private String name;
    private String base64;
    private long chunks;
    private long chunkNumber;

    public ServerImage(String name, String base64, long chunks, long chunkNumber){
        this.name = name;
        this.base64 = base64;
        this.chunks = chunks;
        this.chunkNumber = chunkNumber;
    }

    @Override
    public JSONObject encode() {
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void visit(Waitress Waitress) {
        ImagesManager.addChunk(name, base64, (int)chunks, (int)chunkNumber);
        if(chunkNumber == chunks){
            String fullImageBase64 = ImagesManager.collectImage(name);
            boolean success = ImagesManager.saveImage(name, fullImageBase64);
            if(!success){
                Utils.writeToLog("Can't save image " + name);
            }
        }

    }
}
