package com.example.ordertakerfrontend.BackEnd.Services.Network;

import com.example.ordertakerfrontend.BackEnd.Logic.NetworkNotification.ConnectMeNotification;
import com.example.ordertakerfrontend.BackEnd.Logic.NetworkNotification.HealthCheck;
import com.example.ordertakerfrontend.BackEnd.Logic.NetworkNotification.SyncMe;
import com.example.ordertakerfrontend.BackEnd.Logic.NetworkNotification.SyncTablesNotification;
import com.example.ordertakerfrontend.BackEnd.Logic.Table;
import com.example.ordertakerfrontend.BackEnd.Logic.Waitress;
import com.example.ordertakerfrontend.BackEnd.Services.Constants;
import com.example.ordertakerfrontend.BackEnd.Services.Utils;

import java.util.LinkedList;

public class NetworkDemon extends Thread{


    private NetworkAdapter networkAdapter;
    private Waitress waitress;

    public NetworkDemon(Waitress waitress){
        this.networkAdapter = NetworkAdapter.getInstance();
        this.waitress = waitress;
    }


    private boolean networkHealthy(){
        return this.networkAdapter.multicast(new HealthCheck());
    }

    /**
     * Delete all closed connections
     * Send connect me request
     * Sync with others if still in syncing mode
     * Sync Changed Tables
     * **/
    @Override
    public void run() {
        Utils.writeToLog("Running Demon");
        while (true){
            try {
                /** Delete all closed connections **/
                waitress.getNetworkAdapter().cleanConnections();

                System.out.println("Connections : " + waitress.getNetworkAdapter().getConnections().keySet());

                /** Send connect me request **/
                waitress.getNetworkAdapter().multicast(new ConnectMeNotification(waitress.getName(), Constants.TCP_PORT));

                /** Sync with others if still in syncing mode **/
                if(waitress.isSyncing()){
                    Utils.writeToLog("Syncing with others ....");
                    waitress.getNetworkAdapter().sendTCPToAll(new SyncMe(waitress.getName()));
                }


                /** Sync Changed Tables **/
                LinkedList<Table> tables = new LinkedList<>();
                for (Integer i : waitress.getChangedTables()) {
                    tables.add(waitress.getRestaurant().getTable(i));
                }
                if (tables.size() > 0) {
                    boolean success = waitress.getNetworkAdapter().sendTCPToAll(new SyncTablesNotification(waitress.getName(), tables));
                    if(success){
                        waitress.setChangedTables(new LinkedList<>());
                    }
                }

            }catch (Exception e){
                e.printStackTrace();
                Utils.writeToLog("[ERROR] in Demon.run " + e.getMessage());
            }

            try {
                sleep(Constants.DEMON_DELAY);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
