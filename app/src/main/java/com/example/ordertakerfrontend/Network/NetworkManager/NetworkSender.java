package com.example.ordertakerfrontend.Network.NetworkManager;

import com.example.ordertakerfrontend.BackEnd.Services.Constants;
import com.example.ordertakerfrontend.Network.NetworkMessages.tools.NetworkMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class NetworkSender {

    private Socket socket;
    private ThreadPoolExecutor threadPool;
    private boolean connected;

    public NetworkSender(Socket socket) {
        this.socket = socket;
        this.connected = true;
        this.threadPool =  (ThreadPoolExecutor) Executors.newFixedThreadPool(1);
    }

    public boolean isConnected() {
        return connected;
    }

    public void send(NetworkMessage message){
        threadPool.execute(()->{
            try {
                JSONObject jsonObject = message.encode();
                jsonObject.put("SerialNumber", Constants.WAITRESS.getName());
                String msg = new String(jsonObject.toString().getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8);
                if(socket != null) {
                    DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
                    dos.writeUTF(msg);
                }
            } catch (IOException e) {
                this.connected = false;
            } catch (JSONException e){
                this.connected = false;
            } catch (Exception e){
                this.connected = false;
            }
        });
    }


}
