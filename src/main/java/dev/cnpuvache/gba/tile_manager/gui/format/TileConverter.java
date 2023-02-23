package dev.cnpuvache.gba.tile_manager.gui.format;

import dev.cnpuvache.gba.tile_manager.domain.Tile;
import javafx.scene.control.ListCell;
import javafx.util.StringConverter;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class TileConverter extends StringConverter<Tile> {

    private final ListCell<Tile> cell;
    private final Consumer<String> errorCallback;

    public TileConverter(ListCell<Tile> cell, Consumer<String> errorCallback) {
        this.cell = cell;
        this.errorCallback = errorCallback;
    }

    @Override
    public String toString(Tile tile) {
        return tile.getName();
    }

    @Override
    public Tile fromString(String name) {
        Tile tile = cell.getItem();
        if (name == null) {
            return tile;
        }
        boolean alreadyExists = cell.getListView().getItems().stream()
                .map(Tile::getName)
                .anyMatch(Predicate.isEqual(name));
        if (alreadyExists) {
            errorCallback.accept("Tile name " + name + " already exists.");
            return tile;
        }
        tile.setName(name);
        return tile;
    }
}
