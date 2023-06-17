package dev.cnpuvache.gba.tile_manager.binary;

import dev.cnpuvache.gba.tile_manager.domain.CharacterData;
import dev.cnpuvache.gba.tile_manager.domain.Tile;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.List;

public class CharacterDataToBinary {

    private static final int BYTES_PER_TILE = 32;

    public static byte[] convert(CharacterData characterData) {
        List<Tile> tiles = characterData.getTiles();
        if (tiles == null) {
            return new byte[0];
        }
        int byteBufferAllocationSize = tiles.size() * BYTES_PER_TILE;
        ByteBuffer characterDataBuffer = ByteBuffer.allocate(byteBufferAllocationSize).order(ByteOrder.LITTLE_ENDIAN);
        for (Tile tile : characterData.getTiles()) {
            characterDataBuffer.put(tileToBinary(tile).array());
        }
        return characterDataBuffer.array();
    }

    private static ByteBuffer tileToBinary(Tile tile) {
        ByteBuffer tileBuffer = ByteBuffer.allocate(BYTES_PER_TILE).order(ByteOrder.LITTLE_ENDIAN);
        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x+= 2) {
                byte lowerNibble = (byte) (tile.getTileData(x, y) & 0xf);
                byte higherNibble = (byte) (tile.getTileData(x + 1, y) & 0xf);
                byte piPixel = (byte) (lowerNibble | (higherNibble << 4));
                tileBuffer.put(piPixel);
            }
        }
        return tileBuffer;
    }

}
