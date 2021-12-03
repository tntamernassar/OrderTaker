package com.example.ordertakerfrontend.Network.NetworkManager;

import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.example.ordertakerfrontend.BackEnd.Services.Utils;
import com.example.ordertakerfrontend.Network.NetworkMessages.tools.NetworkMessage;
import com.example.ordertakerfrontend.Network.NetworkMessages.tools.MessageObserver;

import java.io.IOException;
import java.net.Socket;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

public abstract class NetworkAdapter extends Thread {

    private Socket socket;
    private NetworkReceiver receiver;
    private NetworkSender sender;
    private ConcurrentHashMap<String, MessageObserver> observers;
    private boolean connecting;
    public static NetworkAdapter instance;


    public NetworkAdapter(){
        this.observers = new ConcurrentHashMap<>();
        this.connecting = false;
    }

    public boolean isConnecting() {
        return connecting;
    }

    public static void init(NetworkAdapter networkAdapter){
        instance = networkAdapter;
    }

    public static NetworkAdapter getInstance(){
        return instance;
    }

    public NetworkReceiver getReceiver() {
        return receiver;
    }

    public NetworkSender getSender() {
        return sender;
    }

    public ConcurrentHashMap<String, MessageObserver> getObservers() {
        return observers;
    }

    public void setObservers(ConcurrentHashMap<String, MessageObserver> observers) {
        this.observers = observers;
    }

    public void register(String id, MessageObserver observer){
        this.observers.put(id, observer);
    }

    public synchronized void unregister(String id){
        if(this.observers.containsKey(id)){
            this.observers.remove(id);
        }
    }

    public synchronized void notifyObservers(NetworkMessage message){
        for(MessageObserver observer: observers.values()){
            if(observer != null) {
                message.visitMessageObserver(observer);
            }
        }
    }

    public void receive(){
        this.receiver.start();
    }

    public synchronized void send(NetworkMessage message){
        if (this.sender != null) {
            this.sender.send(message);
        }
    }


    @Override
    public void run() {
        try {
            this.connecting = true;
            this.socket = new Socket("193.123.85.54", 443);
//            this.socket = new Socket("10.0.0.5", 2222);
            this.receiver = new NetworkReceiver(this.socket, this);
            this.sender = new NetworkSender(socket);
            this.connecting = false;
            onConnection(this);
        } catch (IOException e) {
            this.connecting = false;
            onError(e);
        }
    }



    public abstract void onConnection(NetworkAdapter adapter);
    public abstract void onError(Exception e);

}
