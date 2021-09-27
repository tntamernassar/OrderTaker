package com.example.ordertakerfrontend;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import com.example.ordertakerfrontend.BackEnd.Services.Constants;
import com.example.ordertakerfrontend.FrontEnd.MenuProduct;
import com.example.ordertakerfrontend.FrontEnd.MenuSection;
import com.example.ordertakerfrontend.FrontEnd.PopupAddons;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.ordertakerfrontend.databinding.ActivityScrollingBinding;
import com.google.android.material.tabs.TabLayout;

import java.io.File;
import java.util.LinkedList;


public class ScrollingActivity extends AppCompatActivity {

    private ActivityScrollingBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityScrollingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent intent = getIntent();
        int table = intent.getExtras().getInt("table");


        Toolbar toolbar = binding.toolbar;
        setSupportActionBar(toolbar);
        CollapsingToolbarLayout toolBarLayout = binding.toolbarLayout;
        toolBarLayout.setTitle("طاوله " + table);

        TabLayout categories = (TabLayout) findViewById(R.id.categories);
        for(String category: com.example.ordertakerfrontend.FrontEnd.Menu.getInstance().getCategories()){
            TabLayout.Tab tab = categories.newTab();
            tab.setText(category);
            categories.addTab(categories.newTab().setText(category));
        }


        categories.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                createMenuList(tab.getText().toString());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        String selected = categories.getTabAt(categories.getSelectedTabPosition()).getText().toString();
        createMenuList(selected);

        FloatingActionButton cancel = binding.cancel;
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Constants.WAITRESS.cancelOrder(table);

                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        FloatingActionButton submit = binding.submit;
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Constants.WAITRESS.submitOrder(table);
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_scrolling, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void createMenuList(String category){
        ListView listView = findViewById(R.id.menu_holder);


        com.example.ordertakerfrontend.FrontEnd.Menu menu = com.example.ordertakerfrontend.FrontEnd.Menu.getInstance().getSubMenu(category);
        listView.setAdapter(menu);

        Activity that = this;

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(that);
                View menuPopup = that.getLayoutInflater().inflate(R.layout.menu_popup, (ViewGroup) view,false);

                MenuProduct menuProduct = menu.getMenuProductList().get(i);

                TextView nameTV = menuPopup.findViewById(R.id.name);
                TextView descTV = menuPopup.findViewById(R.id.description);
                ImageView image = menuPopup.findViewById(R.id.image);

                File imgFile = new  File(getFilesDir().getAbsolutePath()+"/"+menuProduct.getImages()[0]);
                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                image.setImageBitmap(myBitmap);

                nameTV.setText(menuProduct.getName());
                descTV.setText(menuProduct.getDescription());

                /***
                 * Display Addons
                 * **/
                if(menuProduct.getSections() != null){
                    ListView AddonsHandler = menuPopup.findViewById(R.id.addons_handler);
                    LinkedList<String[]> addons = new LinkedList<>();
                    LinkedList<MenuSection> sections = menuProduct.getSections();
                    for(MenuSection ms: sections){
                        addons.add(ms.getAddons());
                    }
                    PopupAddons popupAddons = new PopupAddons(getApplicationContext(), R.layout.section_addons, sections);
                    AddonsHandler.setAdapter(popupAddons);

                    int totalHeight = 0;
                    for (int index = 0; index < popupAddons.getCount(); index++) {
                        View listItem = popupAddons.getView(index, null, AddonsHandler);
                        listItem.measure(0, 0);
                        totalHeight += listItem.getMeasuredHeight();
                    }

                    ViewGroup.LayoutParams par = AddonsHandler.getLayoutParams();
                    par.height = totalHeight + (AddonsHandler.getDividerHeight() * (popupAddons.getCount() - 1));
                    AddonsHandler.setLayoutParams(par);
                    AddonsHandler.requestLayout();
                }else{
                    // no addons
                }

                dialogBuilder.setView(menuPopup);
                AlertDialog dialog = dialogBuilder.create();
                dialog.show();
                dialog.getWindow().setLayout(1000, 1500);
            }
        });
    }
}