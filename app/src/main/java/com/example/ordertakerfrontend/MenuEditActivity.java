package com.example.ordertakerfrontend;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.ordertakerfrontend.BackEnd.Services.Utils;
import com.example.ordertakerfrontend.FrontEnd.Menus.EditableAddons;
import com.example.ordertakerfrontend.FrontEnd.Menus.Menu;
import com.example.ordertakerfrontend.FrontEnd.Menus.MenuProduct;
import com.example.ordertakerfrontend.FrontEnd.Menus.MenuSection;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import java.util.LinkedList;

public class MenuEditActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_edit);
        getSupportActionBar().hide();

        Menu menu = Menu.getInstance();
        BuildTabLayout(menu);
        setAddProductListener(menu);

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
        for (String category : menu.getCategories()) {
            TabLayout.Tab tab = categories.newTab();
            tab.setTag("category");
            categories.addTab(tab.setText(category));
        }
        TabLayout.Tab newTab = categories.newTab();
        newTab.setTag("newTab");
        newTab.setIcon(R.drawable.ic_baseline_control_point_24);
        categories.addTab(newTab);

        Activity that = this;

        categories.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                String tag = tab.getTag().toString();
                if(tag.equals("newTab")){
                    Utils.AcquireInputDialog(that, "ادخل اسم السكشن الجديد", (input)->{
                        AddNewCategory(menu, input);
                    });
                }else {
                    BuildMenuList(menu, tab.getText().toString());
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                String tag = tab.getTag().toString();
                if(tag.equals("newTab")){
                    Utils.AcquireInputDialog(that, "ادخل اسم السكشن الجديد", (input)->{
                        AddNewCategory(menu, input);
                    });
                }else {
                    BuildMenuList(menu, tab.getText().toString());
                }
            }
        });

        String selected = categories.getTabAt(categories.getSelectedTabPosition()).getText().toString();
        BuildMenuList(menu, selected);
    }

    private void BuildMenuList(Menu menu, String category) {
        ListView listView = findViewById(R.id.menu_holder);
        Menu subMenu = menu.getSubMenu(category);
        listView.setAdapter(subMenu);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                MenuProduct menuProduct = subMenu.getMenuProductList().get(i);

            }
        });
    }

    private void BuildMenuSection(ListView view, LinkedList<MenuSection> menuSections){
        EditableAddons editableAddons = new EditableAddons(getApplicationContext(), view.getId(), menuSections);
        view.setAdapter(editableAddons);

    }

    private void setAddProductListener(Menu menu){
        FloatingActionButton add_product = findViewById(R.id.add_product);
        TabLayout categories = findViewById(R.id.categories);
        ScrollView working_area = findViewById(R.id.working_area);

        add_product.setOnClickListener(add_product_view -> {
            String selectedCategory  = categories.getTabAt(categories.getSelectedTabPosition()).getText().toString();

            working_area.removeAllViews();
            LayoutInflater inflater =(LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View inflated = inflater.inflate(R.layout.new_product, null);
            working_area.addView(inflated);

            TextView header_title = inflated.findViewById(R.id.header_title);
            FloatingActionButton add_section = inflated.findViewById(R.id.add_section);
            LinearLayout linearLayout = inflated.findViewById(R.id.sections_holder);

            header_title.setText(selectedCategory);

            LinkedList<MenuSection> menuSections = new LinkedList<>();
            menuSections.add(new MenuSection("test",new String[]{"a1", "a2", "a3", "a4"}, false));
            menuSections.add(new MenuSection("test",new String[]{"a1", "a2", "a3", "a4"}, false));
            menuSections.add(new MenuSection("test",new String[]{"a1", "a2", "a3", "a4"}, false));

            add_section.setOnClickListener(add_section_view -> {
                linearLayout.removeAllViews();
                ListView listView = new ListView(linearLayout.getContext());
                linearLayout.addView(listView);
                BuildMenuSection(listView, menuSections);
            });

        });
    }
}