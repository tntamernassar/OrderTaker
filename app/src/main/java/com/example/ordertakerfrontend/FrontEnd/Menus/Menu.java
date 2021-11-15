package com.example.ordertakerfrontend.FrontEnd.Menus;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.ordertakerfrontend.BackEnd.Services.ImagesManager;
import com.example.ordertakerfrontend.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

public class Menu extends ArrayAdapter<MenuProduct> implements Serializable {

    private static Menu instance = null;

    /** Used for Displaying on FE**/
    private List<MenuProduct> menuProductList;
    /** Used for creating an instant instance of sub menu **/
    private HashMap<String, LinkedList<MenuProduct>> categoriesProducts;

    private Context context;

    private Menu(Context context, List<MenuProduct> menuProductList){
        super(context, R.layout.menu_item, menuProductList);
        setMenuProductList(menuProductList);
        this.context = context;
    }


    public static void init(Context context, List<MenuProduct> menuProductList){
        instance = new Menu(context, menuProductList);
    }

    public String[] getCategories() {
        return this.categoriesProducts.keySet().toArray(new String[0]);
    }

    public void addEmptyCategory(String category){
        this.categoriesProducts.put(category, new LinkedList<>());
    }

    public int removeCategory(String category){
        int n = this.categoriesProducts.get(category).size();
        this.menuProductList.removeIf(menuProduct -> menuProduct.getCategory().equals(category));
        this.categoriesProducts.remove(category);
        return n;
    }

    public static Menu getInstance() {
        return instance;
    }

    public synchronized List<MenuProduct> getMenuProductList() {
        return menuProductList;
    }

    public synchronized void setMenuProductList(List<MenuProduct> menuProductList) {
        this.menuProductList = new LinkedList<>();
        this.categoriesProducts = new LinkedHashMap<>();
        for (MenuProduct menuProduct : menuProductList){
            addProduct(menuProduct);
        }
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

    public void removeProduct(MenuProduct menuProduct){
        this.menuProductList.remove(menuProduct);
        this.categoriesProducts.get(menuProduct.getCategory()).remove(menuProduct);
    }

    public boolean containsProduct(MenuProduct menuProduct){
        for(MenuProduct mp: this.menuProductList){
            if (mp.equals(menuProduct)){
                return true;
            }
        }
        return false;
    }

    public Menu getSubMenu(String category){
        return new Menu(this.context, this.categoriesProducts.get(category));
    }

    public JSONObject toJSON(){
        try{
            JSONObject o = new JSONObject();
            JSONArray objects = new JSONArray();
            for (MenuProduct menuProduct : menuProductList){
                objects.put(menuProduct.toJSON());
            }
            o.put("menuProductList", objects);
            return o;
        }catch (JSONException e){
            e.printStackTrace();
            return null;
        }

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = layoutInflater.inflate(R.layout.menu_item, parent, false);
        MenuProduct menuProduct = menuProductList.get(position);

        TextView nameTV = row.findViewById(R.id.name);
        TextView name_noteTV = row.findViewById(R.id.name_note);
        TextView descTV = row.findViewById(R.id.description);
        ImageView image = row.findViewById(R.id.image);
        TextView price = row.findViewById(R.id.price);

        Bitmap bitmap = ImagesManager.Base64ToImage(menuProduct.getImages()[0]);
        image.setImageBitmap(bitmap);

        nameTV.setText(menuProduct.getName());
        descTV.setText(menuProduct.getDescription());
        price.setText((menuProduct.getPrice() + "₪").replace(".0", ""));

        if(!menuProduct.isAvailable()){
            name_noteTV.setText("- غير متاح");
        }
        return row;
    }



}
