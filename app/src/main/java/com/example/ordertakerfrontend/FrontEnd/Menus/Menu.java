package com.example.ordertakerfrontend.FrontEnd.Menus;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.ordertakerfrontend.R;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

public class Menu extends ArrayAdapter<MenuProduct> {

    private static Menu instance = null;

    /** Used for Displaying on FE**/
    private List<MenuProduct> menuProductList;
    /** Used for creating an instant instance of sub menu **/
    private HashMap<String, LinkedList<MenuProduct>> categoriesProducts;

    private Context context;

    private Menu(Context context, List<MenuProduct> menuProductList){
        super(context, R.layout.menu_item, menuProductList);
        this.menuProductList = menuProductList;
        this.context = context;
        this.categoriesProducts = new LinkedHashMap<>();

    }

    public static void init(Context context, List<MenuProduct> menuProductList){
        instance = new Menu(context, menuProductList);
    }

    public String[] getCategories() {
        return this.categoriesProducts.keySet().toArray(new String[0]);
    }

    public static Menu getInstance() {
        return instance;
    }

    public List<MenuProduct> getMenuProductList() {
        return menuProductList;
    }

    public void addProduct(MenuProduct product){
        this.menuProductList.add(product);
        if(this.categoriesProducts.containsKey(product.getCategory())){
            this.categoriesProducts.get(product.getCategory()).add(product);
        }else{
            LinkedList t = new LinkedList<>();
            t.add(product);
            this.categoriesProducts.put(product.getCategory(), t);
        }
    }


    public Menu getSubMenu(String category){
        return new Menu(this.context, this.categoriesProducts.get(category));
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = layoutInflater.inflate(R.layout.menu_item, parent, false);
        MenuProduct menuProduct = menuProductList.get(position);

        TextView nameTV = row.findViewById(R.id.name);
        TextView descTV = row.findViewById(R.id.description);
        ImageView image = row.findViewById(R.id.image);
        TextView price = row.findViewById(R.id.price);

        File imgFile = new  File(this.context.getFilesDir().getAbsolutePath()+"/"+menuProduct.getImages()[0]);
        Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
        image.setImageBitmap(myBitmap);

        nameTV.setText(menuProduct.getName());
        descTV.setText(menuProduct.getDescription());
        price.setText((menuProduct.getPrice() + "₪").replace(".0", ""));

        return row;
    }
}
