package dev.cnpuvache.gba.tile_manager.gui.format;

import dev.cnpuvache.gba.tile_manager.domain.Screen;
import dev.cnpuvache.gba.tile_manager.domain.Tile;
import javafx.scene.control.ListCell;
import javafx.util.StringConverter;

import java.util.function.Consumer;
import java.util.function.Predicate;

public class ScreenConverter extends StringConverter<Screen> {

    private final ListCell<Screen> cell;
    private final Consumer<String> errorCallback;

    public ScreenConverter(ListCell<Screen> cell, Consumer<String> errorCallback) {
        this.cell = cell;
        this.errorCallback = errorCallback;
    }

    @Override
    public String toString(Screen screen) {
        return screen.getName();
    }

    @Override
    public Screen fromString(String name) {
        Screen screen = cell.getItem();
        if (name == null) {
            return screen;
        }
        boolean alreadyExists = cell.getListView().getItems().stream()
                .map(Screen::getName)
                .anyMatch(Predicate.isEqual(name));
        if (alreadyExists) {
            errorCallback.accept("Tile name " + name + " already exists.");
            return screen;
        }
        screen.setName(name);
        return screen;
    }
}
