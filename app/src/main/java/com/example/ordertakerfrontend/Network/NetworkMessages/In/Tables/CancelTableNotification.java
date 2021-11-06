package com.example.ordertakerfrontend.Network.NetworkMessages.In.Tables;

import com.example.ordertakerfrontend.BackEnd.Logic.Waitress;
import com.example.ordertakerfrontend.Network.NetworkMessages.tools.MessageObserver;
import com.example.ordertakerfrontend.Network.NetworkMessages.tools.NetworkMessage;


public class CancelTableNotification extends NetworkMessage {

    private int table;

    public CancelTableNotification(int table){
        this.table = table;
    }

    public int getTable() {
        return table;
    }

    @Override
    public void visit(Waitress waitress) {
        waitress.cancelOrder(table);
    }

    @Override
    public void visitMessageObserver(MessageObserver messageObserver) {
        messageObserver.accept(this);
    }
}
