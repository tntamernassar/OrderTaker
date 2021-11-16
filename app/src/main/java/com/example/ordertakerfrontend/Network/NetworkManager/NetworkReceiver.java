package com.example.ordertakerfrontend.Network.NetworkManager;
import com.example.ordertakerfrontend.BackEnd.Services.Constants;
import com.example.ordertakerfrontend.BackEnd.Services.Utils;
import com.example.ordertakerfrontend.Network.NetworkMessages.tools.NetworkMessage;
import com.example.ordertakerfrontend.Network.NetworkMessages.tools.NetworkMessageDecoder;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

public class NetworkReceiver extends Thread {

    private Socket socket;
    private boolean running;
    private boolean connected;
    private NetworkAdapter adapter;

    public NetworkReceiver(Socket socket, NetworkAdapter adapter) {
        this.socket = socket;
        this.running = true;
        this.connected = true;
        this.adapter = adapter;
    }


    public boolean isConnected() {
        return connected;
    }

    @Override
    public void run() {
        try {
            DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
            while (this.running) {
                String message = dataInputStream.readUTF();
                JSONObject JSONMessage = new JSONObject(message);
                NetworkMessage networkMessage = NetworkMessageDecoder.decode(JSONMessage);
                networkMessage.visit(Constants.WAITRESS);
                Constants.activity.runOnUiThread(()->{
                    adapter.notifyObservers(networkMessage);
                });
            }
        } catch (IOException e) {
            this.connected = false;
            this.running = false;
            Utils.writeToLog("NetworkReceiver Lost the connection with the server");
        } catch (JSONException e){
            this.connected = false;
            this.running = false;
        }
    }
}
