package com.example.ordertakerfrontend.Network.NetworkManager;

import android.os.AsyncTask;

import com.example.ordertakerfrontend.BackEnd.Services.Constants;
import com.example.ordertakerfrontend.Network.NetworkMessages.NetworkMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class NetworkSender extends Thread {

    private Socket socket;
    private NetworkMessage networkMessage;

    public NetworkSender(Socket socket, NetworkMessage networkMessage){
        this.socket = socket;
        this.networkMessage = networkMessage;
    }

    @Override
    public void run() {
        try {
            JSONObject jsonObject = networkMessage.encode();
            jsonObject.put("SerialNumber", Constants.WAITRESS.getName());
            String msg = new String(jsonObject.toString().getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8);
            socket.getOutputStream().write(msg.length());
            socket.getOutputStream().write(msg.getBytes(StandardCharsets.UTF_8));
            socket.getOutputStream().flush();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e){
            e.printStackTrace();
        }
    }
}
