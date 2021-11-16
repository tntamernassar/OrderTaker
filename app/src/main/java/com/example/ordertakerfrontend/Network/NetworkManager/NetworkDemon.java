package com.example.ordertakerfrontend.Network.NetworkManager;

import com.example.ordertakerfrontend.BackEnd.Services.Constants;
import com.example.ordertakerfrontend.BackEnd.Services.Utils;

public class NetworkDemon{

    private static NetworkDemon instance;
    public static NetworkDemon init(NetworkAdapter networkAdapter) {
        instance = new NetworkDemon(networkAdapter);
        return instance;
    }
    public static NetworkDemon getInstance() {
        return instance;
    }

    private NetworkAdapter networkAdapter;

    public NetworkDemon(NetworkAdapter networkAdapter){
        this.networkAdapter = networkAdapter;
    }


    public void start() {
        new Thread(()->{
            while (true){
                boolean connectedToServer = networkAdapter != null && networkAdapter.getReceiver().isConnected();
                if (!connectedToServer){
                    Utils.writeToLog("NetworkDemon is trying to reconnect to server");
                    NetworkAdapter.init(new NetworkAdapter() {
                        @Override
                        public void onConnection(NetworkAdapter adapter) {
                            Utils.writeToLog("NetworkDemon reconnected to server successfully");
                            networkAdapter = adapter;
                        }

                        @Override
                        public void onError(Exception e) {
                            Utils.writeToLog("NetworkDemon failed to reconnect to server");
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
