package com.example.ordertakerfrontend.Network.NetworkMessages.In.Tables;

import com.example.ordertakerfrontend.BackEnd.Logic.Table;
import com.example.ordertakerfrontend.BackEnd.Logic.Waitress;
import com.example.ordertakerfrontend.Network.NetworkMessages.tools.MessageObserver;
import com.example.ordertakerfrontend.Network.NetworkMessages.tools.NetworkMessage;

import org.json.JSONObject;


public class SubmitTableNotification extends NetworkMessage {

    private JSONObject table;

    public SubmitTableNotification(JSONObject table){
        this.table = table;
    }

    public JSONObject getTable() {
        return table;
    }

    @Override
    public void visit(Waitress waitress) {
        try{
            Table serverTable = new Table(table);
            Table tabletTable = waitress.getRestaurant().getTable(serverTable.getNumber());
            if (tabletTable.isActive()){
                tabletTable.getCurrentOrder().setDistributed(tabletTable.getCurrentOrder().isDistributed() || serverTable.getCurrentOrder().isDistributed());
                tabletTable.mergeTable(serverTable);
            }else{
                tabletTable.setTable(serverTable);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void visitMessageObserver(MessageObserver messageObserver) {
        messageObserver.accept(this);
    }
}
