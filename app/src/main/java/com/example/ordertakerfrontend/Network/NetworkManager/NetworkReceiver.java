package com.example.ordertakerfrontend.Network.NetworkManager;
import android.util.Log;

import com.example.ordertakerfrontend.BackEnd.Services.FileManager;
import com.example.ordertakerfrontend.BackEnd.Services.Utils;
import com.example.ordertakerfrontend.Network.NetworkMessages.NetworkMessage;
import com.example.ordertakerfrontend.Network.NetworkMessages.NetworkMessageDecoder;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class NetworkReceiver extends Thread {

    private Socket socket;
    private boolean running;
    private boolean connected;

    public NetworkReceiver(Socket socket) {
        this.socket = socket;
        this.running = true;
        this.connected = true;
    }


    @Override
    public void run() {
        try {
            DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
            while (this.running) {
                String message = dataInputStream.readUTF();
                JSONObject JSONMessage = new JSONObject(message);
                NetworkMessage networkMessage = NetworkMessageDecoder.decode(JSONMessage);
                networkMessage.visit(null);
            }
        } catch (IOException e) {
            e.printStackTrace();
            this.connected = false;
            this.running = false;
            Utils.writeToLog("NetworkReceiver Lost the connection with the server");
        } catch (JSONException e){
            e.printStackTrace();
            this.connected = false;
            this.running = false;
        }
    }
}
