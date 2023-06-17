package dev.cnpuvache.gba.tile_manager.binary;

import dev.cnpuvache.gba.tile_manager.domain.Screen;
import dev.cnpuvache.gba.tile_manager.domain.ScreenEntry;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class ScreenDataToBinary {

    public static byte[] convert(Screen screen, int backgroundNumber) {
        ByteBuffer screenDataBuffer = ByteBuffer.allocate((int) Math.pow(2,14)).order(ByteOrder.LITTLE_ENDIAN);
	    int width = screen.getWidth(backgroundNumber);
        int height = screen.getHeight(backgroundNumber);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                ScreenEntry entry = screen.getEntry(backgroundNumber, x, y);
                ByteBuffer entryBuffer = screenEntryToBinary(entry);
                screenDataBuffer.put(entryBuffer.array());
            }
        }
        return screenDataBuffer.array();
    }

    private static ByteBuffer screenEntryToBinary(ScreenEntry entry) {
        ByteBuffer entryBuffer = ByteBuffer.allocate(2).order(ByteOrder.LITTLE_ENDIAN);
        if (entry == null) {
            return entryBuffer;
        }
        int tileNumber = entry.getTileNumber();
        boolean horizontalFlip = entry.isHorizontalFlip();
        boolean verticalFlip = entry.isVerticalFlip();
        int paletteNumber = entry.getPaletteNumber();
        int tile = tileNumber & 0x3ff;
        tile = tile | (horizontalFlip ? 0x1 << 10 : 0);
        tile = tile | (verticalFlip ? 0x1 << 11 : 0);
        tile = tile | ((paletteNumber & 0xf) << 12);
        entryBuffer.putShort((short) tile);
        return entryBuffer;
    }
    

}
