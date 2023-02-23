package dev.cnpuvache.gba.tile_manager.gui.format;

import javafx.util.StringConverter;

import java.util.function.Consumer;

public class IntRangeStringConverter extends StringConverter<Integer> {

    private final int min;
    private final int max;
    private final Consumer<String> errorCallback;

    public IntRangeStringConverter(int min, int max, Consumer<String> errorCallback) {
        this.min = min;
        this.max = max;
        this.errorCallback = errorCallback;
    }

    @Override
    public String toString(Integer object) {
        if (object == null) {
            object = 0;
        }
        return String.format("%d", object);
    }

    @Override
    public Integer fromString(String string) {
        try {
            int integer = Integer.parseInt(string);
            if (integer > max || integer < min) {
                String error = String.format("Intensity must be in range [%d:%d].", min, max);
                errorCallback.accept(error);
                throw new IllegalArgumentException(error);
            }
            return integer;
        } catch (NumberFormatException e) {
            errorCallback.accept("Intensity must be a number.");
            throw e;
        }
    }

}
