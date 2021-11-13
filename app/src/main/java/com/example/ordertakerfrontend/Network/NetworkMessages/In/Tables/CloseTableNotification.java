package com.example.ordertakerfrontend.Network.NetworkMessages.In.Tables;

import com.example.ordertakerfrontend.BackEnd.Logic.Waitress;
import com.example.ordertakerfrontend.Network.NetworkMessages.tools.MessageObserver;
import com.example.ordertakerfrontend.Network.NetworkMessages.tools.NetworkMessage;


public class CloseTableNotification extends NetworkMessage {

    private int table;

    public CloseTableNotification(int table){
        this.table = table;
    }

    public int getTable() {
        return table;
    }

    @Override
    public void visit(Waitress waitress) {
        waitress.closeOrder(table);
    }

    @Override
    public void visitMessageObserver(MessageObserver messageObserver) {
        messageObserver.accept(this);
    }
}
