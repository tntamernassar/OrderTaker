package com.example.ordertakerfrontend;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ordertakerfrontend.BackEnd.Services.ImagesManager;
import com.example.ordertakerfrontend.BackEnd.Services.Utils;
import com.example.ordertakerfrontend.FrontEnd.Menus.DiskMenu;
import com.example.ordertakerfrontend.FrontEnd.Menus.EditableAddons;
import com.example.ordertakerfrontend.FrontEnd.Menus.Menu;
import com.example.ordertakerfrontend.FrontEnd.Menus.MenuProduct;
import com.example.ordertakerfrontend.FrontEnd.Menus.MenuSection;
import com.example.ordertakerfrontend.FrontEnd.Popups.AddProductCallback;
import com.example.ordertakerfrontend.FrontEnd.Popups.YesNoCallbacks;
import com.example.ordertakerfrontend.Network.NetworkManager.NetworkAdapter;
import com.example.ordertakerfrontend.Network.NetworkMessages.Out.MenuEdit;
import com.example.ordertakerfrontend.Network.NetworkMessages.Out.TabletImage;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import java.io.InputStream;
import java.util.LinkedList;

public class MenuEditActivity extends AppCompatActivity {

    private Bitmap productBitmap;
    private ImageView productImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_edit);
        getSupportActionBar().hide();

        Menu menu = Menu.getInstance();
        BuildTabLayout(menu);
        setAddProductListener(menu);

    }

    private boolean havingPendingImage(){
        return productImage != null && productBitmap != null;
    }

    private void writeMenu(Menu menu){
        DiskMenu diskMenu = new DiskMenu(menu.getMenuProductList());
        diskMenu.execute();
    }

    private void AddNewCategory(Menu menu, String category){
        menu.addEmptyCategory(category);
        TabLayout categories = (TabLayout) findViewById(R.id.categories);
        categories.removeAllTabs();
        BuildTabLayout(menu);
        categories.selectTab(categories.getTabAt(menu.getCategories().length - 1));
    }

    private void BuildTabLayout(Menu menu){
        TabLayout categories = (TabLayout) findViewById(R.id.categories);
        FloatingActionButton add_section = findViewById(R.id.add_section);

        for (String category : menu.getCategories()) {
            TabLayout.Tab tab = categories.newTab();
            tab.setTag("category");
            categories.addTab(tab.setText(category));
        }

        Activity that = this;

        add_section.setOnClickListener(view ->{
            Utils.AcquireInputDialog(that, "ادخل اسم السكشن الجديد", (input)->{
                if (input.length() > 0) {
                    AddNewCategory(menu, input);
                } else {
                    Toast.makeText(getApplicationContext(), "PLease enter valid section name", Toast.LENGTH_LONG).show();
                }
            });
        });

        categories.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                BuildMenuList(menu, tab.getText().toString());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        String selected = categories.getTabAt(categories.getSelectedTabPosition()).getText().toString();
        BuildMenuList(menu, selected);
    }

    private void BuildMenuList(Menu menu, String category) {
        ListView listView = findViewById(R.id.menu_holder);
        Menu subMenu = menu.getSubMenu(category);
        listView.setAdapter(subMenu);

        Activity that = this;

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                MenuProduct menuProduct = subMenu.getMenuProductList().get(i);
                setWorkingArea(menuProduct.getName(), menuProduct.getDescription(), menuProduct.getPrice(), menuProduct.getSections() != null ? menuProduct.getSections() : new LinkedList<>() , new AddProductCallback() {
                    @Override
                    public void callback(MenuProduct newMenuProduct) {
                        Utils.YesNoDialog(that, "Are you sure you want to save " + menuProduct.getName() + " ? ", new YesNoCallbacks() {
                            @Override
                            public void yes() {
                                menuProduct.setName(newMenuProduct.getName());
                                menuProduct.setDescription(newMenuProduct.getDescription());
                                menuProduct.setPrice(newMenuProduct.getPrice());
                                menuProduct.setImages(newMenuProduct.getImages());
                                menuProduct.setSections(newMenuProduct.getSections());
                                resetWorkingArea();
                                BuildMenuList(menu, category);
                                writeMenu(menu);
                            }

                            @Override
                            public void no() {

                            }
                        });
                    }
                }, new AddProductCallback() {
                    @Override
                    public void callback(MenuProduct m) {
                        Utils.YesNoDialog(that, "Are you sure you want to delete " + menuProduct.getName() + " ? ", new YesNoCallbacks() {
                            @Override
                            public void yes() {
                                menu.removeProduct(menuProduct);
                                resetWorkingArea();
                                BuildMenuList(menu, category);
                                writeMenu(menu);
                            }

                            @Override
                            public void no() {

                            }
                        });
                    }
                });
            }
        });
    }

    private void BuildMenuSection(ListView view, LinkedList<MenuSection> menuSections){
        EditableAddons editableAddons = new EditableAddons(this, view.getId(), menuSections);
        view.setAdapter(editableAddons);
    }

    private void resetWorkingArea(){
        this.productBitmap = null;
        this.productImage = null;
        ScrollView working_area = findViewById(R.id.working_area);
        working_area.removeAllViews();
    }

    private void setWorkingArea(String productName, String productDescription, double productPrice, LinkedList<MenuSection> productMenuSections, AddProductCallback submitCallback, AddProductCallback cancelCallback) {
        TabLayout categories = findViewById(R.id.categories);
        ScrollView working_area = findViewById(R.id.working_area);

        String selectedCategory = categories.getTabAt(categories.getSelectedTabPosition()).getText().toString();

        working_area.removeAllViews();
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View inflated = inflater.inflate(R.layout.new_product, null);
        working_area.addView(inflated);

        ImageView product_image = inflated.findViewById(R.id.product_image);
        FloatingActionButton upload_image = inflated.findViewById(R.id.upload_image);
        TextView header_title = inflated.findViewById(R.id.header_title);
        FloatingActionButton add_section = inflated.findViewById(R.id.add_section);
        LinearLayout linearLayout = inflated.findViewById(R.id.sections_holder);
        FloatingActionButton submit_product = inflated.findViewById(R.id.submit_product);
        FloatingActionButton cancel_product = inflated.findViewById(R.id.cancel_product);
        ListView listView = linearLayout.findViewById(R.id.sections_list_view);

        TextView product_name = inflated.findViewById(R.id.product_name);
        TextView product_price = inflated.findViewById(R.id.product_price);
        TextView product_description = inflated.findViewById(R.id.product_description);

        header_title.setText(selectedCategory);
        product_name.setText(productName);
        product_price.setText(productPrice + "");
        product_description.setText(productDescription);
        this.productImage = product_image;

        upload_image.setOnClickListener(v -> {
            ImagePicker.Companion.with(MenuEditActivity.this).start();
        });

        LinkedList<MenuSection> menuSections = new LinkedList<>(productMenuSections);

        add_section.setOnClickListener(add_section_view -> {
            Utils.AcquireInputDialog(this, "Enter Section name", input -> {
                menuSections.add(new MenuSection(input, new String[]{}, false));
                BuildMenuSection(listView, menuSections);
            });
        });

        BuildMenuSection(listView, menuSections);

        submit_product.setOnClickListener(submit_product_view -> {
            String name = product_name.getText().toString();
            double price = Double.parseDouble(product_price.getText().toString());
            String description = product_description.getText().toString();

            if(name.length() == 0){
                Toast.makeText(getApplicationContext(), "Please Enter product name", Toast.LENGTH_LONG).show();
                return;
            }else if(description.length() == 0){
                Toast.makeText(getApplicationContext(), "Please Enter product Description", Toast.LENGTH_LONG).show();
                return;
            }

            LinkedList<MenuSection> newMenuSection = new LinkedList<>();
            for (MenuSection menuSection: menuSections){
                if(menuSection.getAddons().length > 0){
                    newMenuSection.add(menuSection);
                }
            }
            MenuProduct newMenuProduct = new MenuProduct(selectedCategory, name, description, price, menuSections, new String[]{"ham.png"});
            submitCallback.callback(newMenuProduct);
        });

        cancel_product.setOnClickListener(cancel_product_view -> {
            cancelCallback.callback(null);
        });

    }

    private void setAddProductListener(Menu menu){
        FloatingActionButton add_product = findViewById(R.id.add_product);

        add_product.setOnClickListener(add_product_view -> {
            setWorkingArea("", "", 0, new LinkedList<>(), new AddProductCallback() {
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void callback(MenuProduct menuProduct) {
                    boolean havingPendingImage = havingPendingImage();
                    Bitmap chosenProductBitmap = productBitmap;
                    resetWorkingArea();

                    AsyncTask t = new AsyncTask() {
                        @Override
                        protected void onPreExecute() {

                            Toast.makeText(getApplicationContext(),"started ,,,", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        protected Object doInBackground(Object[] objects) {
                            if (havingPendingImage){
                                String imageName = ImagesManager.makeFileName();
                                String imageBase64 = ImagesManager.imageToBase64(chosenProductBitmap);
                                ImagesManager.saveImage(imageName, imageBase64);
                                menuProduct.setImages(new String[]{imageName});
                                ImagesManager.sendImageInChucks(imageName, imageBase64);
                            }

                            return null;
                        }

                        @Override
                        protected void onPostExecute(Object o) {
                            Toast.makeText(getApplicationContext(),"finished ,,,", Toast.LENGTH_SHORT).show();
                            menu.addProduct(menuProduct);
                            BuildMenuList(menu, menuProduct.getCategory());
                            setAddProductListener(menu);
                            writeMenu(menu);
                            NetworkAdapter.getInstance().send(new MenuEdit(menu.toJSON(), ImagesManager.listImages()));
                        }
                    };

                    t.execute();
                }
            }, new AddProductCallback() {
                @Override
                public void callback(MenuProduct menuProduct) {
                    resetWorkingArea();
                }
            });

        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            try {
                final Uri imageUri = data.getData();
                final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
                this.productBitmap = selectedImage;
                this.productImage.setImageBitmap(selectedImage);
            }catch (Exception e){
                e.printStackTrace();
            }
        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show();
        }

    }
}