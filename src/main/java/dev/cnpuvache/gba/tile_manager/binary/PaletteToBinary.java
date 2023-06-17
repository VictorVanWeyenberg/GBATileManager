package dev.cnpuvache.gba.tile_manager.binary;

import dev.cnpuvache.gba.tile_manager.domain.Palette;
import dev.cnpuvache.gba.tile_manager.domain.RGB15;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class PaletteToBinary {

    public static byte[] convert(Palette palette) {
        ByteBuffer paletteBinaryBuffer = ByteBuffer.allocate(512).order(ByteOrder.LITTLE_ENDIAN);
        for (int paletteNumber = 0; paletteNumber < 16; paletteNumber++) {
            RGB15[] colors = palette.getPalette(paletteNumber);
            for (RGB15 color : colors) {
                ByteBuffer intensities = colorToBinary(color);
                paletteBinaryBuffer.put(intensities.array());
            }
        }
        return paletteBinaryBuffer.array();
    }

    private static ByteBuffer colorToBinary(RGB15 color) {
        ByteBuffer colorBuffer = ByteBuffer.allocate(2).order(ByteOrder.LITTLE_ENDIAN);
        short r = (short) (color.getR() & 0x1f);
        short g = (short) (color.getG() & 0x1f);
        short b = (short) (color.getB() & 0x1f);
        short colorShort = (short) (r | (g << 5) | (b << 10));
        colorBuffer.putShort(colorShort);
        return colorBuffer;
    }

}
