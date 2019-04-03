/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.paint.Color;

/**
 *
 * @author Reznov
 */
public class Palette16 extends Palette implements Serializable {
    
    private List<List<RGB15>> paletteData;
    private static final int PALETTE_SIZE = 16;
    
    public Palette16(String name, boolean defaultPalette) {
        super(name, defaultPalette);
        paletteData = new ArrayList<List<RGB15>>();
        for (int i = 0; i < PALETTE_SIZE; i++) {
            paletteData.add(new ArrayList<RGB15>());
            for (int j = 0; j < PALETTE_SIZE; j++) {
                paletteData.get(i).add(new RGB15(0, 0, 0));
            }
        }
    }
    
    // RGB15
    public List<RGB15> getPalette(int index) {
        return ((List<List<RGB15>>) this.paletteData).get(index);
    }
    
    public void setColor(int palette, int index, RGB15 color) {
        paletteData.get(palette).set(index, color);
    }

    @Override
    public String toC() {
        StringBuilder sb = new StringBuilder();
        this.paletteData.forEach(palette -> palette.forEach(color -> {
            sb.append(color.toC());
            if (paletteData.indexOf(palette) < PALETTE_SIZE - 1 || palette.indexOf(color) < PALETTE_SIZE - 1) {
                sb.append(", ");
            }
        }));
        String paletteString = String.format("{ %s }", sb.toString());
        return paletteString;
    }
    
}
