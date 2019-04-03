/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package domain;

import compiler.CStatement;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import javafx.scene.paint.Color;

/**
 *
 * @author Reznov
 */
public class RGB15 implements Serializable {
    
    private int r, g, b;
    private transient Color color;
    
    public RGB15(int r, int g, int b) {
        super();
        setColor(r, g, b);
    }
    
    public Color getColor() {
        return color;
    }
    
    public int getR() {
        return r;
    }
    
    public int getG() {
        return g;
    }
    
    public int getB() {
        return b;
    }

    private void setColor() {
        double r = this.r / 15.0;
        double g = this.g / 15.0;
        double b = this.b / 15.0;
        this.color = new Color(r, g, b, 1);
    }
    
    public void setColor(int r, int g, int b) {
        setR(r);
        setG(g);
        setB(b);
        setColor();
    }
    
    private void setR(int r) {
        if (r < 0 || r > 15) {
            rangeException("red");
        } else {
            this.r = r;
        }
    }
    
    private void setG(int g) {
        if (g < 0 || g > 15) {
            rangeException("green");
        } else {
            this.g = g;
        }
    }
    
    private void setB(int b) {
        if (b < 0 || b > 15) {
            rangeException("blue");
        } else {
            this.b = b;
        }
    }
    
    private void rangeException(String component) {
        throw new IllegalArgumentException(String.format("The value of the %s component must be in range of (0, 15).", component));
    }
    
    @Override
    public String toString() {
        return String.format("RGB15(%d, %d, %d)", r, g, b);
    }
    
    private void readObject(ObjectInputStream input) throws IOException, ClassNotFoundException {
        input.defaultReadObject();
        setColor();
    }

    public String toC() {
        int color = (b << 10) + (g << 5) + r;
        String hexString = String.format("0x%04x", color);
        return hexString;
    }
    
}
