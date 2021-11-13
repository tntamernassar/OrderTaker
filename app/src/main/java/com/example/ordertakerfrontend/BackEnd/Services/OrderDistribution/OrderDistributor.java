package com.example.ordertakerfrontend.BackEnd.Services.OrderDistribution;

import android.graphics.Bitmap;
import android.graphics.Color;

import com.example.ordertakerfrontend.BackEnd.Logic.Order;
import com.example.ordertakerfrontend.BackEnd.Logic.OrderItem;
import com.example.ordertakerfrontend.BackEnd.Services.Utils;
import com.example.ordertakerfrontend.FrontEnd.Menus.OrderProduct;

import java.util.HashMap;
import java.util.LinkedList;

public class OrderDistributor {

    private static int TEXT_SIZE = 40;
    private static int ADDON_TEXT_SIZE = 30;


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
                Bitmap headerBitmap = Utils.textAsBitmap(stringBuilder.toString(), TEXT_SIZE);
                PrinterService.getInstance().getPrinter().printImage(headerBitmap);


                for(OrderItem orderItem : orderDistributionRequest.getOrder().getOrderItems().values()){
                    OrderProduct orderProduct = (OrderProduct)orderItem.getProduct();
                    HashMap<String, LinkedList<String>> sections = orderProduct.getAddons();
                    stringBuilder = new StringBuilder("");
                    stringBuilder.append(" - ");
                    stringBuilder.append(orderItem.getQuantity());
                    stringBuilder.append(" ");
                    stringBuilder.append(orderProduct.getMenuProduct().getName());
                    Bitmap productBitmap = Utils.textAsBitmap(stringBuilder.toString(), TEXT_SIZE);
                    PrinterService.getInstance().getPrinter().printImage(productBitmap);

                    for (String section: sections.keySet()){
                        LinkedList<String> addons = sections.get(section);
                        stringBuilder = new StringBuilder("");
                        stringBuilder.append("       •");
                        stringBuilder.append(section);
                        stringBuilder.append(": ");
                        if (addons.size() == 1){
                            stringBuilder.append(addons.get(0));
                            Bitmap sectionBitmap = Utils.textAsBitmap(stringBuilder.toString(), ADDON_TEXT_SIZE);
                            PrinterService.getInstance().getPrinter().printImage(sectionBitmap);
                        }else{
                            int i = 1;
                            Bitmap bitmap = Utils.textAsBitmap(stringBuilder.toString(), ADDON_TEXT_SIZE);
                            PrinterService.getInstance().getPrinter().printImage(bitmap);
                            stringBuilder = new StringBuilder("          •");
                            for(String addon: addons){
                                stringBuilder.append(addon);
                                stringBuilder.append(", ");
                                if(i%3 == 0){
                                    bitmap = Utils.textAsBitmap(stringBuilder.toString(), ADDON_TEXT_SIZE);
                                    PrinterService.getInstance().getPrinter().printImage(bitmap);
                                    stringBuilder = new StringBuilder("          •");
                                }

                                i++;
                            }
                        }
                    }
                }

                PrinterService.getInstance().getPrinter().setAlign(BluetoothPrinter.ALIGN_CENTER);
                PrinterService.getInstance().getPrinter().setBold(true);
                PrinterService.getInstance().getPrinter().addNewLine();
                PrinterService.getInstance().getPrinter().printText("*******************************");
                PrinterService.getInstance().getPrinter().addNewLines(5);

                Utils.writeToLog("Sent an order distribution to the kitchen for table " + orderDistributionRequest.getTableNumber() );
                System.out.println("Sent an order distribution to the kitchen for table " + orderDistributionRequest.getTableNumber() );
            }
        }catch (Exception e){
            e.printStackTrace();
        }


    }

    public static void distributeEdit(Order oldOrder, OrderDistributionRequest newOrderDistributionRequest){
        System.out.println("************************KITCHEN - EDIT************************");
        System.out.println("Table : " + newOrderDistributionRequest.getTableNumber());
        System.out.println("WAS : " + Utils.MapToString(oldOrder.getOrderItems()));
        System.out.println("NOW : " + Utils.MapToString(newOrderDistributionRequest.getOrder().getOrderItems()));
        System.out.println("************************KITCHEN - EDIT************************\n\n\n");

        Utils.writeToLog("Sent an edit order distribution to the kitchen  for table " + newOrderDistributionRequest.getTableNumber());

    }

}
