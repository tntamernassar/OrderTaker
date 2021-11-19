package com.example.ordertakerfrontend.BackEnd.Services.OrderDistribution;

import android.graphics.Bitmap;

import android.util.Pair;

import com.example.ordertakerfrontend.BackEnd.Logic.Order;
import com.example.ordertakerfrontend.BackEnd.Logic.OrderItem;
import com.example.ordertakerfrontend.BackEnd.Services.Utils;
import com.example.ordertakerfrontend.FrontEnd.Menus.OrderProduct;

import java.util.HashMap;
import java.util.LinkedList;

public class OrderDistributor {

    private static int TEXT_SIZE = 40;
    private static int ADDON_TEXT_SIZE = 30;



    private static void printToPrinter(StringBuilder stringBuilder, int textSize){
        Bitmap headerBitmap = Utils.textAsBitmap(stringBuilder.toString(), textSize);
        PrinterService.getInstance().getPrinter().printImage(headerBitmap);
    }

    private static void printMultiLine(StringBuilder stringBuilder, int textSize){
        int line_width = 20; //character
        LinkedList<String> lines = new LinkedList<>();
        String str = stringBuilder.toString();
        int i = 0;
        while ((i*line_width) < str.length()){
            int from = i * line_width;
            int to = Math.min(line_width*i + line_width, str.length() );
            System.out.println(from  +" to " + to);
            String sub = str.substring(from, to);
            lines.add(sub);
            i += 1;
        }

        for (String line : lines){
            printToPrinter(new StringBuilder(line), textSize);
        }
    }

    public static void distribute(OrderDistributionRequest orderDistributionRequest){
        StringBuilder stringBuilder = new StringBuilder("");
        try {
            if(PrinterService.getInstance().isConnected()){
                PrinterService.getInstance().getPrinter().setAlign(BluetoothPrinter.ALIGN_CENTER);
                PrinterService.getInstance().getPrinter().setBold(true);
                PrinterService.getInstance().getPrinter().printText("***************************************");

                stringBuilder.append("طاولة ");
                stringBuilder.append(orderDistributionRequest.getTableNumber());
                stringBuilder.append(" - ");
                stringBuilder.append("طلبية جديدة");
                printToPrinter(stringBuilder, TEXT_SIZE);


                for(OrderItem orderItem : orderDistributionRequest.getOrder().getOrderItems().values()){
                    if(orderItem.isDeleted()){
                        continue;
                    }
                    OrderProduct orderProduct = (OrderProduct)orderItem.getProduct();
                    HashMap<String, LinkedList<String>> sections = orderProduct.getAddons();
                    stringBuilder = new StringBuilder("");
                    stringBuilder.append(" - ");
                    stringBuilder.append(orderItem.getQuantity());
                    stringBuilder.append(" ");
                    stringBuilder.append(orderProduct.getMenuProduct().getName());
                    printToPrinter(stringBuilder, ADDON_TEXT_SIZE);


                    for (String section: sections.keySet()){
                        LinkedList<String> addons = sections.get(section);
                        stringBuilder = new StringBuilder("");
                        stringBuilder.append("       •");
                        stringBuilder.append(section);
                        stringBuilder.append(": ");
                        if (addons.size() == 1){
                            stringBuilder.append(addons.get(0));
                            printToPrinter(stringBuilder, ADDON_TEXT_SIZE);
                        }else{
                            int i = 1;
                            printToPrinter(stringBuilder, ADDON_TEXT_SIZE);
                            stringBuilder = new StringBuilder("              •");
                            for(String addon: addons){
                                stringBuilder.append(addon);
                                stringBuilder.append(", ");
                                if(i%3 == 0){
                                    printToPrinter(stringBuilder, ADDON_TEXT_SIZE);
                                    stringBuilder = new StringBuilder("              •");
                                }

                                i++;
                            }
                        }
                    }

                    printMultiLine(new StringBuilder(orderItem.getNotes()), ADDON_TEXT_SIZE);
                }

                PrinterService.getInstance().getPrinter().setAlign(BluetoothPrinter.ALIGN_CENTER);
                PrinterService.getInstance().getPrinter().setBold(true);
                PrinterService.getInstance().getPrinter().addNewLine();
                PrinterService.getInstance().getPrinter().printText("*******************************");
                PrinterService.getInstance().getPrinter().addNewLines(2);

                Utils.writeToLog("Sent an order distribution to the kitchen for table " + orderDistributionRequest.getTableNumber() );
                System.out.println("Sent an order distribution to the kitchen for table " + orderDistributionRequest.getTableNumber() );
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    /**
     * Re-send an order to the kitchen
     * should group the new order by:
     *  - deleted items
     *  - edited items
     *  - new items
     * **/
    public static void distributeEdit(Order oldOrder, OrderDistributionRequest newOrderDistributionRequest) {

        System.out.println(oldOrder.toJSON());
        System.out.println(newOrderDistributionRequest.getOrder().toJSON());

        LinkedList<OrderItem> deletedItems = new LinkedList<>();
        LinkedList<OrderItem> newItems = new LinkedList<>();
        LinkedList<Pair<OrderItem, OrderItem>> editedItems = new LinkedList<>();

        for (OrderItem orderItem : newOrderDistributionRequest.getOrder().getOrderItems().values()) {
            OrderItem previousOrderItem = oldOrder.getOrderItem(orderItem);
            if (previousOrderItem == null) {
                newItems.add(orderItem);
            } else {
                if (orderItem.isDeleted() && !previousOrderItem.isDeleted()) {
                    deletedItems.add(orderItem);
                } else {
                    if (!orderItem.sameItem(previousOrderItem)) {
                        editedItems.add(new Pair<>(previousOrderItem, orderItem));
                    }
                }
            }
        }

        System.out.println("deleted : " + deletedItems.size());
        System.out.println("new : " + newItems.size());
        System.out.println("edited : " + editedItems.size());

        StringBuilder stringBuilder = new StringBuilder("");
        if (PrinterService.getInstance().isConnected()) {
            PrinterService.getInstance().getPrinter().setAlign(BluetoothPrinter.ALIGN_CENTER);
            PrinterService.getInstance().getPrinter().setBold(true);
            PrinterService.getInstance().getPrinter().printText("***************************************");

            stringBuilder.append("طاولة ");
            stringBuilder.append(newOrderDistributionRequest.getTableNumber());
            stringBuilder.append(" - ");
            stringBuilder.append("تعديل الطلبية");
            printToPrinter(stringBuilder, TEXT_SIZE);


            if (!deletedItems.isEmpty()) {
                stringBuilder = new StringBuilder("");
                stringBuilder.append("منتجات تم ازالتها :");
                printToPrinter(stringBuilder, TEXT_SIZE);

                for (OrderItem orderItem : deletedItems) {
                    OrderProduct orderProduct = (OrderProduct) orderItem.getProduct();
                    stringBuilder = new StringBuilder("");
                    stringBuilder.append("     - ");
                    stringBuilder.append(orderProduct.getMenuProduct().getName());
                    printToPrinter(stringBuilder, TEXT_SIZE);
                }

                PrinterService.getInstance().getPrinter().addNewLines(2);
            }

            if (!editedItems.isEmpty()){
                stringBuilder = new StringBuilder("");
                stringBuilder.append("منتجات تم تعديلها :");
                printToPrinter(stringBuilder, TEXT_SIZE);

                for (Pair<OrderItem, OrderItem> orderItemChange : editedItems) {

                    StringBuilder changeReason = new StringBuilder("تغيير");
                    if (orderItemChange.first.getQuantity() != orderItemChange.second.getQuantity()){
                        changeReason.append("الكميه");
                        changeReason.append(" ");
                    }
                    if(!orderItemChange.first.getProduct().equals(orderItemChange.second.getProduct())){
                        changeReason.append("الاضافات");
                        changeReason.append(" ");
                    }

                    OrderProduct orderProduct = (OrderProduct) orderItemChange.second.getProduct();
                    HashMap<String, LinkedList<String>> sections = orderProduct.getAddons();
                    stringBuilder = new StringBuilder("");
                    stringBuilder.append("     - ");
                    stringBuilder.append(orderItemChange.second.getQuantity());
                    stringBuilder.append(" ");
                    stringBuilder.append(orderProduct.getMenuProduct().getName());
                    printToPrinter(stringBuilder, ADDON_TEXT_SIZE);
                    stringBuilder = new StringBuilder("");
                    stringBuilder.append("     - ");
                    stringBuilder.append(changeReason.toString());
                    printToPrinter(stringBuilder, ADDON_TEXT_SIZE);

                    for (String section: sections.keySet()){
                        LinkedList<String> addons = sections.get(section);
                        stringBuilder = new StringBuilder("");
                        stringBuilder.append("       •");
                        stringBuilder.append(section);
                        stringBuilder.append(": ");
                        if (addons.size() == 1){
                            stringBuilder.append(addons.get(0));
                            printToPrinter(stringBuilder, ADDON_TEXT_SIZE);
                        }else{
                            int i = 1;
                            printToPrinter(stringBuilder, ADDON_TEXT_SIZE);
                            stringBuilder = new StringBuilder("              •");
                            for(String addon: addons){
                                stringBuilder.append(addon);
                                stringBuilder.append(", ");
                                if(i%3 == 0){
                                    printToPrinter(stringBuilder, ADDON_TEXT_SIZE);
                                    stringBuilder = new StringBuilder("              •");
                                }

                                i++;
                            }
                        }
                    }
                    printMultiLine(new StringBuilder(orderItemChange.second.getNotes()), ADDON_TEXT_SIZE);
                }

            }

            if (!newItems.isEmpty()){
                stringBuilder = new StringBuilder("");
                stringBuilder.append("منتجات جديده :");
                printToPrinter(stringBuilder, TEXT_SIZE);

                for (OrderItem orderItem : newItems) {
                    OrderProduct orderProduct = (OrderProduct) orderItem.getProduct();
                    HashMap<String, LinkedList<String>> sections = orderProduct.getAddons();
                    stringBuilder = new StringBuilder("");
                    stringBuilder.append("     - ");
                    stringBuilder.append(orderItem.getQuantity());
                    stringBuilder.append(" ");
                    stringBuilder.append(orderProduct.getMenuProduct().getName());
                    printToPrinter(stringBuilder, ADDON_TEXT_SIZE);

                    for (String section: sections.keySet()){
                        LinkedList<String> addons = sections.get(section);
                        stringBuilder = new StringBuilder("");
                        stringBuilder.append("       •");
                        stringBuilder.append(section);
                        stringBuilder.append(": ");
                        if (addons.size() == 1){
                            stringBuilder.append(addons.get(0));
                            printToPrinter(stringBuilder, ADDON_TEXT_SIZE);
                        }else{
                            int i = 1;
                            printToPrinter(stringBuilder, ADDON_TEXT_SIZE);
                            stringBuilder = new StringBuilder("              •");
                            for(String addon: addons){
                                stringBuilder.append(addon);
                                stringBuilder.append(", ");
                                if(i%3 == 0){
                                    printToPrinter(stringBuilder, ADDON_TEXT_SIZE);
                                    stringBuilder = new StringBuilder("              •");
                                }

                                i++;
                            }
                        }
                    }
                    printMultiLine(new StringBuilder("* " + orderItem.getNotes()), ADDON_TEXT_SIZE);
                }
            }

            PrinterService.getInstance().getPrinter().setAlign(BluetoothPrinter.ALIGN_CENTER);
            PrinterService.getInstance().getPrinter().setBold(true);
            PrinterService.getInstance().getPrinter().addNewLine();
            PrinterService.getInstance().getPrinter().printText("*******************************");
            PrinterService.getInstance().getPrinter().addNewLines(2);

            Utils.writeToLog("Sent an order distribution edit to the kitchen for table " + newOrderDistributionRequest.getTableNumber());
            System.out.println("Sent an order distribution edit to the kitchen for table " + newOrderDistributionRequest.getTableNumber());

        }
    }


}
