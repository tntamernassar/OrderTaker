package com.example.ordertakerfrontend;


import com.example.ordertakerfrontend.Network.NetworkMessages.TestMessage;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class TestClient {
    public static void main(String[] args){
        try{
//            Socket socket = new Socket("193.123.85.54",443);
            Socket socket = new Socket("localhost",2222);
            ByteArrayOutputStream baos = new ByteArrayOutputStream(8024);
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            TestMessage ts = new TestMessage();
            oos.writeObject(ts.encode());
            byte[] data = baos.toByteArray();
            socket.getOutputStream().write(data);
        }catch(Exception e){e.printStackTrace();}
    }

}
