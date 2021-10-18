package com.example.ordertakerfrontend.Network.NetworkManager;

import android.os.AsyncTask;
import android.util.Log;

import com.example.ordertakerfrontend.BackEnd.Services.Utils;
import com.example.ordertakerfrontend.Network.NetworkMessages.NetworkMessage;

import java.io.IOException;
import java.net.Socket;

public abstract class NetworkAdapter extends Thread {

    private Socket socket;
    private NetworkReceiver receiver;


    public NetworkAdapter(){

    }

    public void receive(){
        this.receiver.start();
    }

    public void send(NetworkMessage message){
        NetworkSender networkSender = new NetworkSender(socket, message);
        networkSender.start();
    }


    @Override
    public void run() {
        boolean connected = false;
        while (!connected){
            try {
                this.socket = new Socket("192.168.84.135",2222);
                this.receiver = new NetworkReceiver(this.socket);
                connected = true;
                onConnection(this);
            } catch (IOException e) {

                try {
                    Log.d("NetworkAdapter", "Trying to connect to server ...");
                    Utils.writeToLog("NetworkAdapter Trying to connect to server ...");
                    Thread.sleep(5000);
                }catch (Exception e1){
                    e1.printStackTrace();
                }
            }
        }


    }

    public abstract void onConnection(NetworkAdapter adapter);

}
