/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package compiler;

import collectors.SingletonCollector;
import domain.Palette;
import domain.Project;

/**
 *
 * @author Reznov
 */
public class ProjectCAdapter {
    
    private Project project;
    
    public ProjectCAdapter(Project project) {
        this.project = project;
    }
    
    public String REG_DISPLAY() {
        int regDisplay = project.getBgMode() + 
                (1 << 5) + 
                ((project.getObjMapping() == Project.OBJMapping.TWO_DIMENSIONAL ? 0 : 1) << 6) + 
                (1 << 7) + 
                (binToInt(project.isDisplayBG0(), project.isDisplayBG1(), project.isDisplayBG2(), project.isDisplayBG3(), project.isDisplayOBJ()) << 8);
        System.out.println(regDisplay);
        return String.format("0x%s", Integer.toHexString(regDisplay));
    }
    
    private int binToInt(boolean... booleans) {
        int result = 0;
        for (int i = 0; i < booleans.length; i++) {
            result += (booleans[i] ? 1 : 0) << i;
        }
        return result;
    }
    
    public String bgPalette() {
        return project.getBackgroundPalettes().values().stream().filter(Palette::isDefault).collect(SingletonCollector.toSingleton()).toC();
    }
    
    public String objPalette() {
        return project.getObjectPalettes().values().stream().filter(Palette::isDefault).collect(SingletonCollector.toSingleton()).toC();
    }
    
    public String bgPaletteName() {
        return project.getBackgroundPalettes().entrySet().stream().filter(entry -> entry.getValue().isDefault()).collect(SingletonCollector.toSingleton()).getKey();
    }
    
    public String objPaletteName() {
        return project.getObjectPalettes().entrySet().stream().filter(entry -> entry.getValue().isDefault()).collect(SingletonCollector.toSingleton()).getKey();
    }
    
}
