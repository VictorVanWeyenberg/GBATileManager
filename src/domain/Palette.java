/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package domain;

import compiler.CStatement;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.paint.Color;

/**
 *
 * @author Reznov
 */
public abstract class Palette implements Serializable {
    
    protected boolean isDefault;
    protected String name;
    
    // RGB15
    protected Palette(String name, boolean isDefault) {
        this.isDefault = isDefault;
        if (name.contains(" ")) {
            throw new IllegalArgumentException("Palette name may not contain spaces.");
        }
        this.name = name.toLowerCase();
    }
    
    public void setDefault(boolean isDefault) {
        this.isDefault = isDefault;
    }
    
    public boolean isDefault() {
        return isDefault;
    }
    
    public abstract String toC();
    
}
