package com.example.ordertakerfrontend.FrontEnd.Menus;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.ordertakerfrontend.R;

import java.util.LinkedList;

public class EditableAddons extends ArrayAdapter<MenuSection> {

    private LinkedList<MenuSection> menuSections;

    public EditableAddons(@NonNull Context context, int resource, LinkedList<MenuSection> menuSections) {
        super(context, resource, menuSections);
        this.menuSections = menuSections;
    }



    private Button createAddonButton(String section, String addon){
        Button button = new Button(getContext());
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );

        layoutParams.setMargins(10, 10, 10, 10);

        button.setBackground(getContext().getResources().getDrawable(R.drawable.addon_button_off));

        button.setText(addon + " X ");
        button.setLayoutParams(layoutParams);

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

        sectionName.setText(section);

        for (String addon : addons) {
            Button button = createAddonButton(section, addon);
            addonsList.addView(button);
        }

        return row;
    }
}
