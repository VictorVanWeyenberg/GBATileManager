/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dev.cnpuvache.gba.tile_manager.domain;

import java.nio.ByteBuffer;

/**
 *
 * @author Reznov
 */
public abstract class Palette {
    protected String name;
    private boolean isDefault = false;
    
    // RGB15
    protected Palette(String name) {
        if (name.contains(" ")) {
            throw new IllegalArgumentException("Palette name may not contain spaces.");
        }
        this.name = name.toLowerCase();
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean isDefault) {
        this.isDefault = isDefault;
    }
    
    public abstract ByteBuffer toC();
    
}
