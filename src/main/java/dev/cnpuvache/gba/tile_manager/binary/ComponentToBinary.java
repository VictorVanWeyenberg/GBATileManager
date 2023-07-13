package dev.cnpuvache.gba.tile_manager.binary;

import dev.cnpuvache.gba.tile_manager.domain.Component;
import dev.cnpuvache.gba.tile_manager.util.ComponentResolver;

import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.List;

public class ComponentToBinary {

    private static final int SIZE_OF_ONE_COMPONENT_BYTES = 8;

    public static byte[] convert(List<Component> components) {
        ComponentResolver.resolve(components);
        ByteBuffer buffer = ByteBuffer.allocate(SIZE_OF_ONE_COMPONENT_BYTES * components.size());
        for (Component component : components) {
            ByteBuffer componentBuffer = ByteBuffer.allocate(SIZE_OF_ONE_COMPONENT_BYTES);
            componentBuffer.put((byte) component.getBeginX());
            componentBuffer.put((byte) component.getBeginY());
            componentBuffer.put((byte) component.getEndX());
            componentBuffer.put((byte) component.getEndY());
            componentBuffer.put((byte) components.indexOf(component.getNorth()));
            componentBuffer.put((byte) components.indexOf(component.getEast()));
            componentBuffer.put((byte) components.indexOf(component.getSouth()));
            componentBuffer.put((byte) components.indexOf(component.getWest()));
            buffer.put(componentBuffer.array());
        }
        return buffer.array();
    }

}
