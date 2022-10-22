package dev.cnpuvache.gba.tile_manager;

import dev.cnpuvache.gba.tile_manager.domain.*;
import dev.cnpuvache.gba.tile_manager.io.ProjectJsonConverter;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;

public class Test {

    public static void main(String[] args) throws IOException {
        Palette16 palette = new Palette16("TEST");
        palette.setColor(0, 0, new RGB15(0, 0, 0));
        for (int i = 1; i < 16; i++) {
            palette.setColor(0, i, new RGB15(31 - 2*i, 0, 2*i));
        }
        ByteBuffer buffer = palette.toC();
        System.out.println(String.format("Writing (%d):", buffer.array().length));
        File outputFile = new File("/home/cnpuvache/Desktop/C Playground/linking/palette.bin");
        try (FileOutputStream os = new FileOutputStream(outputFile)) {
            os.write(buffer.array());
        }

        CharacterData characterData = new CharacterData(false);
        characterData.addTile(new Tile(false, "BG_TILE"));
        Tile tile = new Tile(false, "Ooohhh pretty");
        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                tile.setTileData(x, y, x + y);
            }
        }
        System.out.println(tile.toCCode());
        characterData.addTile(tile);
        buffer = characterData.toC();
        System.out.println(String.format("Writing (%d):", buffer.array().length));
        outputFile = new File("/home/cnpuvache/Desktop/C Playground/linking/tiles.bin");
        try (FileOutputStream os = new FileOutputStream(outputFile)) {
            os.write(buffer.array());
        }
    }

}
