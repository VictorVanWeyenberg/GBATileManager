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
public class MainFunction extends CStatement {
    private List<CStatement> statements;
    
    public MainFunction() {
        super();
        this.statements = new ArrayList<CStatement>();
    }

    @Override
    public String toC(int indentationLevel) {
        StringBuilder sb = new StringBuilder();
        sb.append("int main(void) {\n");
        statements.forEach(statement -> sb.append(statement.toC(indentationLevel + 1)));
        sb.append("}%n");
        return sb.toString();
    }
    
    public void addStatement(CStatement statement) {
        this.statements.add(statement);
    }

    void assign(String name, String value) {
       statements.add(new Assignment(name, value));
    }
    
}
