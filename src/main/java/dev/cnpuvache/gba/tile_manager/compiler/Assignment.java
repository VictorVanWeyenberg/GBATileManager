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
public class Assignment extends CStatement {
    
    private String variable;
    private String value;
    
    public Assignment(String variable, String value) {
        this.variable = variable;
        this.value = value;
    }

    @Override
    public String toC(int indentationLevel) {
        return new StringBuilder().append(getTabs(indentationLevel)).append(variable).append(" = ").append(value).append(";\n").toString();
    }
    
}
