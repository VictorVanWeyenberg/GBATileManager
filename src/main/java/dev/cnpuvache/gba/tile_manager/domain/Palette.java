/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dev.cnpuvache.gba.tile_manager.domain;

import java.io.Serializable;

/**
 *
 * @author Reznov
 */
public abstract class Palette {
    protected String name;
    
    // RGB15
    protected Palette(String name) {
        if (name.contains(" ")) {
            throw new IllegalArgumentException("Palette name may not contain spaces.");
        }
        this.name = name.toLowerCase();
    }
    
    public abstract String toC();
    
}
