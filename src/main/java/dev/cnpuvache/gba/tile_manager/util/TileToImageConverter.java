package dev.cnpuvache.gba.tile_manager.util;

import dev.cnpuvache.gba.tile_manager.domain.Palette;
import dev.cnpuvache.gba.tile_manager.domain.RGB15;
import dev.cnpuvache.gba.tile_manager.domain.Tile;
import javafx.scene.image.Image;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;

public class TileToImageConverter {

    public static Image tileToImage(Tile tile, Palette palette, boolean horizontalFlip, boolean verticalFlip) {
        WritableImage image = new WritableImage(8, 8);
        PixelWriter writer = image.getPixelWriter();
        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                int paletteColor = tile.getTileData(x, y);
                RGB15 color = palette.getColor(paletteColor);
                int drawX = verticalFlip ? 7 - x : x;
                int drawY = horizontalFlip ? 7 - y : y;
                writer.setColor(drawX, drawY, color.getColor());
            }
        }
        return image;
    }

}
