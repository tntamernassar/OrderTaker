package com.example.ordertakerfrontend.FrontEnd;


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

import java.util.HashMap;
import java.util.LinkedList;



public class PopupAddons extends ArrayAdapter<String[]> {

    Context context;
    String[] sections;
    LinkedList<String[]> addons;
    HashMap<String, LinkedList<String>> choosed;
    public PopupAddons(@NonNull Context context, int resource, String[] sections, LinkedList<String[]> addons) {
        super(context, resource, addons);
        this.context = context;
        this.addons = addons;
        this.sections = sections;
        this.choosed = new HashMap<>();
    }

    private Button createAddonButton(String section, String addon){
        Button button = new Button(this.context);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );

        layoutParams.setMargins(10, 10, 10, 10);
        button.setBackground(this.context.getResources().getDrawable(R.drawable.addon_button_off));
        button.setText(addon);
        button.setLayoutParams(layoutParams);

        button.setOnClickListener((View view)->{
            if(this.choosed.containsKey(section) && this.choosed.get(section).contains(addon)){
                this.choosed.get(section).remove(addon);
                button.setBackground(this.context.getResources().getDrawable(R.drawable.addon_button_off));
            }else{
                if(this.choosed.containsKey(section)){
                    this.choosed.get(section).add(addon);
                }else{
                    LinkedList<String> addons = new LinkedList<>();
                    addons.add(addon);
                    this.choosed.put(section, addons);
                }
                button.setBackground(this.context.getResources().getDrawable(R.drawable.addon_button_on));
            }
        });

        return button;
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = layoutInflater.inflate(R.layout.section_addons, parent, false);
        String section = this.sections[position];
        String[] addons = this.addons.get(position);

        TextView sectionName = row.findViewById(R.id.section_name);
        LinearLayout addonsList = row.findViewById(R.id.addons_list);

        sectionName.setText(section);

        for (String addon: addons){
            Button button = createAddonButton(section, addon);
            addonsList.addView(button);
        }

        return row;
    }
}



















