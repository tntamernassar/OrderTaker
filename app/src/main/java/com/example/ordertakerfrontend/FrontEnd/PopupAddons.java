package com.example.ordertakerfrontend.FrontEnd;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.ContentView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.ordertakerfrontend.BackEnd.Services.Constants;
import com.example.ordertakerfrontend.R;

import java.util.HashMap;
import java.util.LinkedList;



public class PopupAddons extends ArrayAdapter<MenuSection> {

    Context context;
    LinkedList<MenuSection> addons;
    HashMap<String, LinkedList<String>> choosed;
    HashMap<String, MenuSection> sections;

    HashMap<String, Button> section_buttons;


    public PopupAddons(@NonNull Context context, int resource, LinkedList<MenuSection> addons) {
        super(context, resource, addons);
        this.context = context;
        this.addons = addons;
        this.choosed = new HashMap<>();
        this.sections = new HashMap<>();
        for (MenuSection s : addons) {
            sections.put(s.getSection(), s);
        }

        this.section_buttons = new HashMap<>();
    }

    private Button createAddonButton(String section, String addon) {
        Button button = new Button(this.context);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );

        layoutParams.setMargins(10, 10, 10, 10);
        button.setBackground(this.context.getResources().getDrawable(R.drawable.addon_button_off));
        button.setText(addon);
        button.setLayoutParams(layoutParams);

        button.setOnClickListener((View view) -> {
            if (this.choosed.containsKey(section) && this.choosed.get(section).contains(addon)) {
                this.choosed.get(section).remove(addon);
                button.setBackground(this.context.getResources().getDrawable(R.drawable.addon_button_off));

            } else {
                if (this.choosed.get(section) == null) {
                    LinkedList<String> addons = new LinkedList<>();
                    this.choosed.put(section, addons);
                    this.section_buttons.put(section, button);
                }
                if (this.sections.get(section).isMaxOne() && this.choosed.get(section).size() >= 1) {
                    Button tmp = this.section_buttons.get(section);
                    tmp.setBackground(this.context.getResources().getDrawable(R.drawable.addon_button_off));
                    this.section_buttons.remove(section);
                    this.choosed.get(section).removeLast();

                    this.choosed.get(section).add(addon);
                    button.setBackground(this.context.getResources().getDrawable(R.drawable.addon_button_on));
                    this.section_buttons.put(section, button);

                } else {
                    this.choosed.get(section).add(addon);
                    this.section_buttons.put(section, button);
                    button.setBackground(this.context.getResources().getDrawable(R.drawable.addon_button_on));
                }

            }
        });


        return button;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = layoutInflater.inflate(R.layout.section_addons, parent, false);
        String section = this.addons.get(position).getSection();
        String[] addons = this.addons.get(position).getAddons();

        TextView sectionName = row.findViewById(R.id.section_name);
        LinearLayout addonsList = row.findViewById(R.id.addons_list);

        sectionName.setText(section);

        for (String addon : addons) {
            Button button = createAddonButton(section, addon);
            addonsList.addView(button);
        }


        return row;
    }
}





















