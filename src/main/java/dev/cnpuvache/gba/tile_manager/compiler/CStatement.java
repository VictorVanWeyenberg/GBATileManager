/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dev.cnpuvache.gba.tile_manager.compiler;

/**
 *
 * @author Reznov
 */
public abstract class CStatement {
    
    public abstract String toC(int indentation);
    
    protected final String getTabs(int indentationLevel) {
        String tabs = "";
        for (int i = 0; i < indentationLevel; i++) {
            tabs += "\t";
        }
        return tabs;
    }
    
}
