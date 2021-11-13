package com.example.ordertakerfrontend.Network.NetworkMessages.In;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.example.ordertakerfrontend.BackEnd.Logic.Waitress;
import com.example.ordertakerfrontend.BackEnd.Services.ImagesManager;
import com.example.ordertakerfrontend.BackEnd.Services.Utils;
import com.example.ordertakerfrontend.Network.NetworkMessages.tools.MessageObserver;
import com.example.ordertakerfrontend.Network.NetworkMessages.tools.NetworkMessage;

import org.json.JSONObject;

public class ServerImage extends NetworkMessage {

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

    public String getName() {
        return name;
    }

    public long getChunkNumber() {
        return chunkNumber;
    }

    public long getChunks() {
        return chunks;
    }

    @Override
    public JSONObject encode() {
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void visit(Waitress waitress) {
        ImagesManager.addChunk(name, base64, (int)chunks, (int)chunkNumber);
        if(chunkNumber == chunks){
            String fullImageBase64 = ImagesManager.collectImage(name);
            boolean success = ImagesManager.saveImage(name, fullImageBase64);
            if(!success){
                Utils.writeToLog("Can't save image " + name);
            }
        }
    }

    @Override
    public void visitMessageObserver(MessageObserver messageObserver) {
        messageObserver.accept(this);
    }
}
