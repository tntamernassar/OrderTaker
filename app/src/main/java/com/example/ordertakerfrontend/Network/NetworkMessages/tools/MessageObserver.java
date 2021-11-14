package com.example.ordertakerfrontend.Network.NetworkMessages.tools;

import com.example.ordertakerfrontend.Network.NetworkMessages.In.MenuEditNotification;
import com.example.ordertakerfrontend.Network.NetworkMessages.In.OrderHistoryContainer;
import com.example.ordertakerfrontend.Network.NetworkMessages.In.ServerImage;
import com.example.ordertakerfrontend.Network.NetworkMessages.In.Tables.CancelTableNotification;
import com.example.ordertakerfrontend.Network.NetworkMessages.In.Tables.CloseTableNotification;
import com.example.ordertakerfrontend.Network.NetworkMessages.In.Tables.OpenTableNotification;
import com.example.ordertakerfrontend.Network.NetworkMessages.In.Tables.SubmitTableNotification;
import com.example.ordertakerfrontend.Network.NetworkMessages.In.initResponse;

public interface MessageObserver {

    public void accept(NetworkMessage message);
    public void accept(initResponse message);
    public void accept(ServerImage message);

    public void accept(MenuEditNotification message);
    public void accept(OpenTableNotification message);
    public void accept(CloseTableNotification message);
    public void accept(CancelTableNotification message);
    public void accept(SubmitTableNotification message);
    public void accept(OrderHistoryContainer message);

}
