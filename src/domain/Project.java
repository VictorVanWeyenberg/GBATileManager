/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package domain;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Reznov
 */
public class Project implements Serializable {
    
    public enum OBJMapping {
        ONE_DIMENSIONAL,
        TWO_DIMENSIONAL
    }
    
    public enum PaletteType {
        PALETTE16,
        PALETTE256
    }
    
    private String name;
    private OBJMapping objMapping;
    private PaletteType paletteType;
    
    private final int BG_MODE = 0;
    private boolean displayOBJ;
    private boolean displayBG0;
    private boolean displayBG1;
    private boolean displayBG2;
    private boolean displayBG3;
    
    private Map<String, Palette> objectPalettes;
    private Map<String, Palette> backgroundPalettes;
    
    public Project(String name, OBJMapping objMapping, PaletteType paletteType, boolean displayOBJ, boolean displayBG0, boolean displayBG1, boolean displayBG2, boolean displayBG3) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Project name is required.");
        } else if (name.length() < 4) {
            throw new IllegalArgumentException("Project name must at least be 4 characters long.");
        }
        this.name = name;
        this.objMapping = objMapping;
        this.paletteType = paletteType;
        this.displayOBJ = displayOBJ;
        this.displayBG0 = displayBG0;
        this.displayBG1 = displayBG1;
        this.displayBG2 = displayBG2;
        this.displayBG3 = displayBG3;
        
        objectPalettes = new HashMap<String, Palette>();
        backgroundPalettes = new HashMap<String, Palette>();
        switch (paletteType) {
            case PALETTE16:
                objectPalettes.put("Default Palette", new Palette16("default_obj_palette", true));
                backgroundPalettes.put("Default Palette", new Palette16("default_bg_palette", true));
                break;
        }
    }
    
    public String getName() {
        return name;
    }

    public Map<String, Palette> getObjectPalettes() {
        return objectPalettes;
    }
    
    public Palette getObjectPalette(String paletteName) {
        return objectPalettes.get(paletteName);
    }
    
    public Map<String, Palette> getBackgroundPalettes() {
        return backgroundPalettes;
    }
    
    public Palette getBackgroundPalette(String paletteName) {
        return backgroundPalettes.get(paletteName);
    }
    
    @Override
    public String toString() {
        Iterator<String> titles = Arrays.asList(new String[] {"Project", "OBJ Mapping", "Palette Type", "Display OBJ", "Display BG0", "Display BG1", "Display BG2", "Display BG3"}).iterator();
        Iterator<String> values = Arrays.asList(new String[] {this.name, this.objMapping.toString(), this.paletteType.toString(), Boolean.toString(displayOBJ), Boolean.toString(displayBG0), Boolean.toString(displayBG1), Boolean.toString(displayBG2), Boolean.toString(displayBG3)}).iterator();
        StringBuilder sb = new StringBuilder();
        while(titles.hasNext() && values.hasNext()) {
            sb.append(String.format("%-20s:%20s%n", titles.next(), values.next()));
        }
        return sb.toString();
    }

    public OBJMapping getObjMapping() {
        return objMapping;
    }

    public PaletteType getPaletteType() {
        return paletteType;
    }

    public boolean isDisplayOBJ() {
        return displayOBJ;
    }

    public boolean isDisplayBG0() {
        return displayBG0;
    }

    public boolean isDisplayBG1() {
        return displayBG1;
    }

    public boolean isDisplayBG2() {
        return displayBG2;
    }

    public boolean isDisplayBG3() {
        return displayBG3;
    }

    public int getBgMode() {
        return BG_MODE;
    }
    
}
