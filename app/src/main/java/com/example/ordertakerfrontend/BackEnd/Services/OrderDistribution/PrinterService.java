package com.example.ordertakerfrontend.BackEnd.Services.OrderDistribution;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;

import com.example.ordertakerfrontend.BackEnd.Services.Utils;

import java.util.Iterator;

public class PrinterService {

    private static PrinterService instance = null;
    public static PrinterService getInstance() {
        return instance;
    }


    private String printerName;
    private BluetoothDevice device;
    private BluetoothPrinter printer;
    private boolean inConnectingState;

    public PrinterService(String printerName){
        this.inConnectingState = true;
        this.printerName = printerName;
        this.device = findDevice(printerName);
        if(this.device != null) {
            connect();
        }else {
            inConnectingState = false;
        }
    }


    public String getPrinterName() {
        return printerName;
    }

    public BluetoothPrinter getPrinter() {
        return printer;
    }

    public boolean isConnected(){
        return this.device != null && this.printer != null && this.printer.isConnected();
    }

    public BluetoothDevice getDevice() {
        return device;
    }

    private void connect(){
        final BluetoothPrinter mPrinter = new BluetoothPrinter(this.device);
        mPrinter.connectPrinter(new BluetoothPrinter.PrinterConnectListener() {
            @Override
            public void onConnected() {
                System.out.println("connected to " + printerName);
                Utils.writeToLog("connected to " + printerName);
                printer = mPrinter;
                inConnectingState = false;
            }

            @Override
            public void onFailed() {
                System.out.println("Failed to connected to " + printerName);
                Utils.writeToLog("Failed to connected to " + printerName);
            }
        });
    }

    public void reconnect(){
        this.inConnectingState = true;
        this.device = findDevice(printerName);
        if(this.device != null) {
            connect();
        }else{
            inConnectingState = false;
        }
    }

    public boolean isInConnectingState() {
        return inConnectingState;
    }

    private BluetoothDevice findDevice(String deviceName){
        BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
        try {
            for (BluetoothDevice device : btAdapter.getBondedDevices()) {
                if (device.getName().equals(deviceName)) {
                    return device;
                }
            }
        }catch(Exception e){
            return null;
        }
        return null;
    }


    public static void init(String printerName){
        PrinterService printerService = new PrinterService(printerName);
        instance = printerService;
    }


}
