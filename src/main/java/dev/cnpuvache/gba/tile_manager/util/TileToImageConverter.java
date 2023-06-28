package dev.cnpuvache.gba.tile_manager.util;

import dev.cnpuvache.gba.tile_manager.domain.Palette;
import dev.cnpuvache.gba.tile_manager.domain.RGB15;
import dev.cnpuvache.gba.tile_manager.domain.Tile;
import javafx.scene.image.Image;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class TileToImageConverter {

    private static final Map<TileToImageCachingKey, Image> cache = new HashMap<>();

    public static void clearCache() {
        cache.clear();
    }

    private static class TileToImageCachingKey {
        private final int tileNumber;
        private final int paletteNumber;
        private final boolean hFlip;
        private final boolean vFlip;
        TileToImageCachingKey(int tileNumber, int paletteNumber, boolean hFlip, boolean vFlip) {
            this.tileNumber = tileNumber;
            this.paletteNumber = paletteNumber;
            this.hFlip = hFlip;
            this.vFlip = vFlip;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            TileToImageCachingKey that = (TileToImageCachingKey) o;
            return tileNumber == that.tileNumber && paletteNumber == that.paletteNumber && hFlip == that.hFlip && vFlip == that.vFlip;
        }

        @Override
        public int hashCode() {
            return Objects.hash(tileNumber, paletteNumber, hFlip, vFlip);
        }
    }

    public synchronized static void reset() {
        cache.clear();
    }

    public synchronized static Image tileToImage(Tile tile, RGB15[] colors, boolean horizontalFlip, boolean verticalFlip, int paletteNumber, int tileNumber, RGB15 backgroundColor) {
        TileToImageCachingKey tileToImageCachingKey = new TileToImageCachingKey(tileNumber, paletteNumber, horizontalFlip, verticalFlip);
        if (cache.containsKey(tileToImageCachingKey)) {
            return cache.get(tileToImageCachingKey);
        }
        WritableImage image = new WritableImage(8, 8);
        cache.put(tileToImageCachingKey, image);
        PixelWriter writer = image.getPixelWriter();
        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                int paletteColor = tile.getTileData(x, y);
                RGB15 colorRGB15 = colors[paletteColor];
                Color color;
                if (colorRGB15.equals(backgroundColor)) {
                    color = Color.TRANSPARENT;
                } else {
                    color = colorRGB15.getColor();
                }
                int drawX = horizontalFlip ? 7 - x : x;
                int drawY = verticalFlip ? 7 - y : y;
                writer.setColor(drawX, drawY, color);
            }
        }
        return image;
    }

}
