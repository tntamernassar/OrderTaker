package com.example.ordertakerfrontend;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.widget.TextView;

import com.example.ordertakerfrontend.BackEnd.Logic.Restaurant;
import com.example.ordertakerfrontend.BackEnd.Logic.Table;
import com.example.ordertakerfrontend.BackEnd.Logic.Waitress;
import com.example.ordertakerfrontend.BackEnd.Services.Constants;
import com.example.ordertakerfrontend.BackEnd.Services.FileManager;
import com.example.ordertakerfrontend.BackEnd.Services.ImagesManager;
import com.example.ordertakerfrontend.BackEnd.Services.OrderDistribution.PrinterService;
import com.example.ordertakerfrontend.BackEnd.Services.Utils;
import com.example.ordertakerfrontend.FrontEnd.Menus.DiskMenu;
import com.example.ordertakerfrontend.Network.NetworkManager.NetworkAdapter;
import com.example.ordertakerfrontend.Network.NetworkManager.NetworkDemon;
import com.example.ordertakerfrontend.Network.NetworkMessages.In.MenuEditNotification;
import com.example.ordertakerfrontend.Network.NetworkMessages.In.OrderHistoryContainer;
import com.example.ordertakerfrontend.Network.NetworkMessages.In.ServerImage;
import com.example.ordertakerfrontend.Network.NetworkMessages.In.Tables.CancelTableNotification;
import com.example.ordertakerfrontend.Network.NetworkMessages.In.Tables.CloseTableNotification;
import com.example.ordertakerfrontend.Network.NetworkMessages.In.Tables.OpenTableNotification;
import com.example.ordertakerfrontend.Network.NetworkMessages.In.Tables.SubmitTableNotification;
import com.example.ordertakerfrontend.Network.NetworkMessages.In.initResponse;
import com.example.ordertakerfrontend.Network.NetworkMessages.Out.initRequest;
import com.example.ordertakerfrontend.Network.NetworkMessages.tools.MessageObserver;
import com.example.ordertakerfrontend.Network.NetworkMessages.tools.NetworkMessage;

import org.json.JSONArray;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class InitializeActivity extends AppCompatActivity implements MessageObserver {

    private String id="initActivity";
    private int shouldDownload;
    private LinkedList<String> missingImages;

    private TextView label;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initialize);
        getSupportActionBar().hide();

        Constants.activity = this;
        Constants.CONTEXT = getApplicationContext();
        this.label = findViewById(R.id.label);

        SharedPreferences prefs = this.getSharedPreferences("orderTaker", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        if(prefs.contains("name")){
            initSystem(prefs.getString("name", "unknown"));
            initNetworkAdapter();

        }else {
            Utils.AcquireInputDialog(this, "Enter Waitress Name", input -> {
                editor.putString("name", input);
                editor.commit();
                initSystem(input);
                initNetworkAdapter();
            });
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onStop() {
        super.onStop();
        NetworkAdapter.getInstance().unregister(id);
    }

    private void initNetworkAdapter(){
        final Activity thatActivity = this;
        /** Network init **/
        NetworkAdapter.init(new NetworkAdapter() {
            @Override
            public void onConnection(NetworkAdapter adapter) {
                adapter.receive();
                adapter.send(new initRequest());
                NetworkDemon.init(adapter).start();
            }

            @Override
            public void onError(Exception e) {
                NetworkAdapter.getInstance().unregister(id);
                NetworkDemon.init(null).start();
                Intent mainActivity = new Intent(thatActivity, MainActivity.class);
                startActivity(mainActivity);
            }
        });

        NetworkAdapter.getInstance().register(id, this);
        NetworkAdapter.getInstance().start();
    }

    private Waitress initSystem(String waitressName){
        Constants.CONTEXT = getApplicationContext();

        PrinterService.getInstance().init("MTP-II");

        /**
         * Set up Menu
         *
         * Read from 'menu' file, and build the menu accordingly.
         * if menu doesn't exist, create empty menu and wait for the server
         * **/

        DiskMenu diskMenu = (DiskMenu) FileManager.readObject("menu");
        if(diskMenu == null){
            diskMenu = new DiskMenu(new LinkedList<>());
        }
        diskMenu.makeMenu(this);


        /**
         * Set up Restaurant, check if there is cached version in memory
         * **/
        Restaurant lastState;
        try{
            lastState = (Restaurant) FileManager.readObject(Constants.RESTAURANT_STATE_FILE);
            if(lastState == null){
                lastState = new Restaurant();
                lastState.addTable(new Table(1));
                lastState.addTable(new Table(2));
                lastState.addTable(new Table(3));
                lastState.addTable(new Table(4));
                lastState.addTable(new Table(5));
                lastState.addTable(new Table(6));
                lastState.addTable(new Table(7));
                lastState.addTable(new Table(8));
            }
        }catch (Exception e){
            lastState = new Restaurant();
            lastState.addTable(new Table(1));
            lastState.addTable(new Table(2));
            lastState.addTable(new Table(3));
            lastState.addTable(new Table(4));
            lastState.addTable(new Table(5));
            lastState.addTable(new Table(6));
            lastState.addTable(new Table(7));
            lastState.addTable(new Table(8));
        }

        final Restaurant restaurant = lastState;



        /**
         * Set up Waitress Listeners
         * **/

        Waitress waitress = new Waitress(waitressName, restaurant);
        Constants.WAITRESS = waitress;

        /**
         * Check for images dir, if not existed create it
         * **/
        FileManager.mkdir("images");

        /**
         * Cache this run in log
         * **/
        Utils.writeToLog(waitress.getName() + " Started OrderTaker");

        return waitress;
    }


    private void checkIfDone() {
        boolean done = missingImages != null && missingImages.size() == 0;
        if (done) {
            NetworkAdapter.getInstance().unregister(id);
            Intent mainActivity = new Intent(this, MainActivity.class);
            startActivity(mainActivity);
        }
    }

    private void updateProgress(){
        String txt;
        double remains = this.missingImages.size();
        if(remains > 0) {
            double done = shouldDownload - remains;
            int progress = (int)((done / shouldDownload) * 100);
            System.out.println(done );
            txt = progress + "%";
        }else {
            txt = "Done";
        }
        label.setText(txt);
    }



    @Override
    public synchronized void accept(initResponse message) {
        try {
            JSONArray serverImages = message.getServerImages();
            String[] tabletImages = ImagesManager.listImages();
            List<String> tabletImagesList = Arrays.asList(tabletImages);
            LinkedList<String> missingImages = new LinkedList<>();
            for (int i = 0; i < serverImages.length(); i++) {
                String serverImage = serverImages.getString(i);
                if (!tabletImagesList.contains(serverImage)) {
                    missingImages.add(serverImage);
                }
            }
            this.missingImages = missingImages;
            this.shouldDownload = this.missingImages.size();
            this.checkIfDone();
            this.updateProgress();
        }catch (Exception e){
            e.printStackTrace();
        }

    }



    @Override
    public synchronized void accept(ServerImage message) {
        if(this.missingImages != null){
            boolean doneThisImage = message.getChunks() == message.getChunkNumber();
            if(doneThisImage){
                this.missingImages.remove(message.getName());
                checkIfDone();
                updateProgress();
            }
        }
    }

    @Override
    public void accept(NetworkMessage message) { }

    @Override
    public void accept(MenuEditNotification message) { }

    @Override
    public void accept(OpenTableNotification message) { }

    @Override
    public void accept(CloseTableNotification message) {

    }

    @Override
    public void accept(CancelTableNotification message) {

    }

    @Override
    public void accept(SubmitTableNotification message) {

    }

    @Override
    public void accept(OrderHistoryContainer message) {

    }


}