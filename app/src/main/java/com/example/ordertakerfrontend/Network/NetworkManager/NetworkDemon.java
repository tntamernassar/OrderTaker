package com.example.ordertakerfrontend.Network.NetworkManager;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.example.ordertakerfrontend.BackEnd.Services.Constants;
import com.example.ordertakerfrontend.BackEnd.Services.Utils;
import com.example.ordertakerfrontend.Network.NetworkMessages.Out.HealthMessage;
import com.example.ordertakerfrontend.Network.NetworkMessages.Out.initRequest;
import com.example.ordertakerfrontend.Network.NetworkMessages.tools.TestMessage;

public class NetworkDemon{

    private int reconnectionAttempt;
    private NetworkAdapter networkAdapter;
    private static NetworkDemon instance;

    public static NetworkDemon init(NetworkAdapter networkAdapter) {
        instance = new NetworkDemon(networkAdapter);
        return instance;
    }

    public static NetworkDemon getInstance() {
        return instance;
    }

    public NetworkDemon(NetworkAdapter networkAdapter){
        this.networkAdapter = networkAdapter;
        this.reconnectionAttempt = 0;
    }

    private void restartReconnectionAttempt(){
        reconnectionAttempt = 0;
    }

    private synchronized int nextReconnectionAttempt(){
        int r = reconnectionAttempt;
        reconnectionAttempt += 1;
        return r;
    }


    public void start() {
        System.out.println("Network demon started");
        new Thread(()->{
            while (true){

                if(networkAdapter != null) {
                    networkAdapter.send(new HealthMessage());
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                boolean connectedToServer = networkAdapter != null && !networkAdapter.isConnecting() && networkAdapter.getReceiver().isConnected() && networkAdapter.getSender().isConnected();
                if (!connectedToServer){
                    final int a = nextReconnectionAttempt();
                    System.out.println("Reconnecting attempt " + a);
                    Utils.writeToLog("NetworkDemon is trying to reconnect to server");
                    NetworkAdapter.init(new NetworkAdapter() {
                        @Override
                        public void onConnection(NetworkAdapter adapter) {
                            System.out.println("NetworkDemon reconnected to server successfully in attempt " + a);

                            if (networkAdapter != null){
                                System.out.println("Copying "+ networkAdapter.getObservers().size() + " observers");
                                adapter.setObservers(networkAdapter.getObservers());
                            }
                            networkAdapter = adapter;
                            networkAdapter.receive();
                            networkAdapter.send(new HealthMessage());
                            restartReconnectionAttempt();
                        }

                        @Override
                        public void onError(Exception e) {
                            System.out.println("NetworkDemon failed to reconnect to server in attempt " + a);
                            Utils.writeToLog("NetworkDemon failed to reconnect to server in attempt " + a);
                        }
                    });
                    NetworkAdapter.getInstance().start();
                }
                try {
                    Thread.sleep(Constants.NETWORK_DEMON_INTERVAL);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }
}
