package com.example.ordertakerfrontend.FrontEnd.Menus;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Objects;

public class MenuSection implements Serializable {
    private String section;
    private String[] addons;
    private boolean maxOne;

    public MenuSection(String section, String[] addons, boolean maxOne){
        this.section = section;
        this.addons = addons;
        this.maxOne = maxOne;
    }
    //this is just a test for writing some text on tis stand
    //
    public void setAddons(String[] addons) {
        this.addons = addons;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public void setMaxOne(boolean maxOne) {
        this.maxOne = maxOne;
    }

    public String getSection() {
        return section;
    }

    public String[] getAddons() {
        return addons;
    }

    public boolean isMaxOne() {
        return maxOne;
    }

    public void removeAddon(String addon){

        String[] newAddons = new String[this.addons.length - 1];
        int i = 0;
        int j = 0;
        while (j < newAddons.length){
            if(!this.addons[i].equals(addon)){
                newAddons[j] = this.addons[i];
                j++;
            }
            i++;
        }

        this.addons = newAddons;

    }

    public void addAddon(String addon){
        String[] newAddon = new String[this.addons.length + 1];
        for(int i = 0; i < this.addons.length; i++){
            newAddon[i] = this.addons[i];
        }
        newAddon[newAddon.length - 1] = addon;
        this.addons = newAddon;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MenuSection that = (MenuSection) o;
        return maxOne == that.maxOne && section.equals(that.section) && Arrays.equals(addons, that.addons);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(section, maxOne);
        result = 31 * result + Arrays.hashCode(addons);
        return result;
    }
}
