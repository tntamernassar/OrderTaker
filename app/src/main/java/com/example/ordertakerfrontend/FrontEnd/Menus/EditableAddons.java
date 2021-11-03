package com.example.ordertakerfrontend.FrontEnd.Menus;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.ordertakerfrontend.BackEnd.Services.Utils;
import com.example.ordertakerfrontend.FrontEnd.Popups.YesNoCallbacks;
import com.example.ordertakerfrontend.MainActivity;
import com.example.ordertakerfrontend.R;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.LinkedList;

public class EditableAddons extends ArrayAdapter<MenuSection> {

    private LinkedList<MenuSection> menuSections;

    public EditableAddons(@NonNull Context context, int resource, LinkedList<MenuSection> menuSections) {
        super(context, resource, menuSections);
        this.menuSections = menuSections;
    }


    public LinkedList<MenuSection> getMenuSections() {
        return menuSections;
    }

    private Button createAddonButton(LinearLayout addonsList, MenuSection menuSection, String addon){
        Button button = new Button(getContext());
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );

        layoutParams.setMargins(10, 10, 10, 10);

        button.setBackground(getContext().getResources().getDrawable(R.drawable.addon_button_off));

        button.setText(addon);
        button.setLayoutParams(layoutParams);

        button.setOnClickListener(view->{
            Utils.YesNoDialog(getContext(), "Are you sure you want to delete " + addon + " ?", new YesNoCallbacks() {
                @Override
                public void yes() {
                    menuSection.removeAddon(addon);
                    addonsList.removeView(button);
                }

                @Override
                public void no() {

                }
            });
        });

        return button;
    }



    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = layoutInflater.inflate(R.layout.editable_section_addons, parent, false);

        MenuSection menuSection = this.menuSections.get(position);
        String section = menuSection.getSection();
        String[] addons = menuSection.getAddons();

        TextView sectionName = row.findViewById(R.id.section_name);
        LinearLayout addonsList = row.findViewById(R.id.addons_list);
        FloatingActionButton add_addon = row.findViewById(R.id.add_addon);
        CheckBox only_one = row.findViewById(R.id.only_one);
        ImageView delete_section = row.findViewById(R.id.delete_section);


        sectionName.setText(section);
        only_one.setChecked(menuSection.isMaxOne());

        only_one.setOnClickListener(view -> {
            menuSection.setMaxOne(only_one.isChecked());
        });

        for (String addon : addons) {
            Button button = createAddonButton(addonsList, menuSection, addon);
            addonsList.addView(button);
        }

        add_addon.setOnClickListener(view -> {
            Utils.AcquireInputDialog(getContext(), "Enter category name ", input -> {
                menuSection.addAddon(input);
                Button button = createAddonButton(addonsList, menuSection, input);
                addonsList.addView(button);
            });
        });

        delete_section.setOnClickListener(view ->{
            Utils.YesNoDialog(getContext(), "Are you sure you want to delete " + section + " ?", new YesNoCallbacks() {
                @Override
                public void yes() {
                    menuSections.remove(position);
                    notifyDataSetChanged();
                    Toast.makeText(getContext(), "Section Removed", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void no() {

                }
            });
        });

        return row;
    }
}
