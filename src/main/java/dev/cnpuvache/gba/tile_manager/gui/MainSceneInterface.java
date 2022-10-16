/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dev.cnpuvache.gba.tile_manager.gui;

import dev.cnpuvache.gba.tile_manager.domain.Project;

import java.util.Collection;

/**
 *
 * @author Reznov
 */
public interface MainSceneInterface {
    
    void setProject(Project project);

    void addBackgroundPalette(String paletteName, boolean makeDefault);

    void addObjectPalette(String paletteName, boolean makeDefault);

    Collection<String> getBackgroundPaletteNames();

    Collection<String> getObjectPaletteNames();
}
