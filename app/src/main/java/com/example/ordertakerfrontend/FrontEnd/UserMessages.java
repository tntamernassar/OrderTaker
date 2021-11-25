package com.example.ordertakerfrontend.FrontEnd;

import java.util.HashMap;

public class UserMessages {

    private static HashMap<String, String> messages = new HashMap<>();


    public static String get(String label, Object ... args){
        return String.format(messages.get(label), args);
    }
    public static void init(){
        /** Main Activity **/
        messages.put("ask_open_table", "فتح الطاولة رقم %d ؟");

        /** Order Activity **/
        messages.put("table_i", "طاولة %d");
        messages.put("number_of_people_label", "عدد الاشخاص : ");
        messages.put("close_table", "اغلاق الطاولة");
        messages.put("ask_table_close", "هل تريد اغلاق الطاولة ؟");
        messages.put("ask_enter_num_of_people", "الرجاء ادخال عدد الاشخاص ");
        messages.put("ask_if_send_to_kitchen", "ارسال الطلبية الى المطعم ؟");
        messages.put("send_to_kitchen_success", "تم ارسال الطلبيه الى المطبخ !");

        /** Menu Edit Activity **/
        messages.put("edit_menu_header", "اعداد قائمة الطعام");
        messages.put("ask_enter_category_name", "أدخل اسم الفئة الجديدة");
        messages.put("ask_enter_valid_name", "الرجاء ادخال الاسم");
        messages.put("ask_if_delete", "هل أنت متأكد أنك تريد حذف '%s' ?");
        messages.put("ask_if_save", "هل أنت متأكد أنك تريد حفظ '%s' ?");
        messages.put("save_success", "تم حفظ المنتج بنجاح !");
        messages.put("ask_enter_section_name", "أدخل اسم القسم الجديد");
        messages.put("ask_enter_product_name", "أدخل اسم المنتج الجديد");
        messages.put("add_product_success", "تم اضافة المنتج !");
        messages.put("available", "متاح");
        messages.put("product_name", "اسم المنتج");
        messages.put("product_description", "وصف المنتج");
        messages.put("add_new_section_label", "اضافة قسم جديد");
        messages.put("ask_enter_addon_name", "ادخل اسم الاضافة الجديدة");

    }

}
