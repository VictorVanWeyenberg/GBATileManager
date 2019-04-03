/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package compiler;

import domain.Project;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author Reznov
 */
public class CCompiler {
    
    private static final List<String> imports = Arrays.asList(new String[] {"io_control.h", "palette_control.h", "vram_control.h"});
    
    private ProjectCAdapter adapter;
    private MainFunction mainFunction;
    
    private static final String REG_DISPLAY = "REG_DISPLAY";
    private static final String MEM_BG_PAL = "MEM_BG_PAL";
    private static final String MEM_OBJ_PAL = "MEM_OBJ_PAL";
    
    public CCompiler(Project project) {
        this.adapter = new ProjectCAdapter(project);
        this.mainFunction = new MainFunction();
        this.mainFunction.assign(REG_DISPLAY, this.adapter.REG_DISPLAY());
        this.mainFunction.assign(MEM_BG_PAL, this.adapter.bgPalette());
        this.mainFunction.assign(MEM_OBJ_PAL, this.adapter.objPalette());
        ForLoop bgPalAssigner = new ForLoop(0, 256, true);
        bgPalAssigner.addStatement(new Assignment(String.format("%s[%s]", MEM_BG_PAL, bgPalAssigner.getIndex()), String.format("%s[%s]", this.adapter.bgPaletteName(), bgPalAssigner.getIndex())));
        this.mainFunction.addStatement(bgPalAssigner);
        ForLoop objPalAssigner = new ForLoop(0, 256, false);
        objPalAssigner.addStatement(new Assignment(String.format("%s[%s]", MEM_OBJ_PAL, objPalAssigner.getIndex()), String.format("%s[%s]", this.adapter.objPaletteName(), objPalAssigner.getIndex())));
        this.mainFunction.addStatement(objPalAssigner);
    }
    
    public String toC() {
        return new StringBuilder().append(compileImports()).append(mainFunction.toC(0)).toString();
    }
    
    private String compileImports() {
        StringBuilder sb = new StringBuilder();
        for (String importje : imports) {
            sb.append("#include ").append(String.format("\"%s\"", importje)).append("\n");
        }
        return sb.append("\n").toString();
    }
    
}
