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

    public PrinterService(String printerName){
        this.printerName = printerName;
        this.device = findDevice(printerName);
        if(this.device != null) {
            connect();
        }
    }


    public BluetoothPrinter getPrinter() {
        return printer;
    }

    public boolean isConnected(){
        return this.device != null && this.printer != null && this.printer.isConnected();
    }

    private void connect(){
        final BluetoothPrinter mPrinter = new BluetoothPrinter(this.device);
        mPrinter.connectPrinter(new BluetoothPrinter.PrinterConnectListener() {
            @Override
            public void onConnected() {
                System.out.println("connected to " + printerName);
                Utils.writeToLog("connected to " + printerName);
                printer = mPrinter;
            }

            @Override
            public void onFailed() {
                System.out.println("Failed to connected to " + printerName);
                Utils.writeToLog("Failed to connected to " + printerName);
            }
        });
    }

    private void reconnect(){
        this.device = findDevice(printerName);
        if(this.device != null) {
            connect();
        }
    }

    private BluetoothDevice findDevice(String deviceName){
        BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
        Iterator<BluetoothDevice> iterator = btAdapter.getBondedDevices().iterator();
        BluetoothDevice mBtDevice = iterator.next();
        while (iterator.hasNext()) {
            mBtDevice = iterator.next();
            if (mBtDevice.getName().equals(deviceName)){
                break;
            }
        }
        return mBtDevice;
    }


    private static void init(String printerName){
        PrinterService printerService = new PrinterService("MTP-II");
        instance = printerService;
    }


}
