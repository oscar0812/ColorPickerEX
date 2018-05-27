package com.bittle.colorpicker.realm;

import com.bittle.colorpicker.utils.ColorUtil;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

/**
 * Scribbled by oscartorres on 5/18/18.
 */
public class ColorModel extends RealmObject {
    private long timestamp;
    @Required
    @PrimaryKey
    private String hex;
    private String name = null;

    public ColorModel(){}

    public ColorModel(String name, int r, int g, int b){
        this.name = name;
        this.hex = ColorUtil.rgbToHex(r, g, b);
    }

    public ColorModel(String hex) {
        this.hex = hex;
        this.timestamp = System.currentTimeMillis() / 1000;
    }

    public ColorModel updateTime() {
        this.timestamp = System.currentTimeMillis() / 1000;
        return this;
    }

    public String getHex() {
        return hex;
    }

    // methods to display on list
    public int getColor() {
        return ColorUtil.hexToColor(hex);
    }

    public String getName() {
        if(name == null){
            String new_name = ColorUtil.getClosestColor(this);
            try {
                name = new_name;
            }catch (Exception e){
                return new_name;
            }
        }
        return name;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof ColorModel && hex.equals(((ColorModel) o).getHex());
    }
}
