/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dev.cnpuvache.gba.tile_manager.compiler;

import dev.cnpuvache.gba.tile_manager.collectors.SingletonCollector;
import dev.cnpuvache.gba.tile_manager.domain.Palette;
import dev.cnpuvache.gba.tile_manager.domain.Project;

import java.util.ArrayList;
import java.util.Map;
import java.util.stream.Collectors;

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
        return new ArrayList<>(project.getBackgroundPalettes().values()).get(0).toC();
    }
    
    public String objPalette() {
        return new ArrayList<>(project.getObjectPalettes().values()).get(0).toC();
    }
    
    public String bgPaletteName() {
        return new ArrayList<>(project.getBackgroundPalettes().entrySet()).get(0).getKey();
    }
    
    public String objPaletteName() {
        return new ArrayList<>(project.getObjectPalettes().entrySet()).get(0).getKey();
    }
    
}
