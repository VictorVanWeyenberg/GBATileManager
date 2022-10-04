/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dev.cnpuvache.gba.tile_manager.compiler;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Reznov
 */
public class ForLoop extends CStatement {
    
    private int start;
    private int end;
    private String index;
    private List<CStatement> statements;
    private boolean doInitialize;
    
    public ForLoop(int start, int end, boolean doInitialize) {
        this(start, end, doInitialize, "i");
    }
    
    public ForLoop(int start, int end, boolean doInitialize, String index) {
        this.start = start;
        this.end = end;
        this.index = index;
        this.doInitialize = doInitialize;
        this.statements = new ArrayList<CStatement>();
    }
    
    public void addStatement(CStatement statement) {
        this.statements.add(statement);
    }
    
    public String getIndex() {
        return this.index;
    }

    @Override
    public String toC(int indentationLevel) {
        StringBuilder sb = new StringBuilder();
        if (doInitialize) {
            sb.append(getTabs(indentationLevel));
            sb.append(String.format("int %s;%n", index));
        }
        sb.append(getTabs(indentationLevel));
        sb.append(String.format("for (%s = %d; %s < %d; %s++) {%n", index, start, index, end, index));
        statements.forEach(statement -> sb.append(statement.toC(indentationLevel + 1)));
        sb.append(getTabs(indentationLevel)).append("}\n");
        return sb.toString();
    }
    
    
    
}
