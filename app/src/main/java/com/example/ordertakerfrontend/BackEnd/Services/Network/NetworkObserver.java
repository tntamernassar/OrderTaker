package com.example.ordertakerfrontend.BackEnd.Services.Network;

import com.example.ordertakerfrontend.BackEnd.Logic.NetworkNotification.NetworkNotification;

import java.net.InetAddress;

public interface NetworkObserver {

    void UDPNotification(InetAddress address, NetworkNotification notification);
    void TCPNotification(ConnectionHandler handler, NetworkNotification notification);

}
