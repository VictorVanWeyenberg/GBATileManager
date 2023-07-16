package dev.cnpuvache.gba.tile_manager.binary;

import dev.cnpuvache.gba.tile_manager.domain.Component;
import dev.cnpuvache.gba.tile_manager.util.ComponentResolver;

import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ComponentToBinary {

    private static final int SIZE_OF_ONE_COMPONENT_BYTES = 11;

    public static byte[] convert(List<Component> components) {
        ByteBuffer buffer = ByteBuffer.allocate(SIZE_OF_ONE_COMPONENT_BYTES * components.size());
        int argsIndex = 0;
        ComponentResolver.resolve(components);
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
            componentBuffer.put((byte) component.getCallbackIndex());
            componentBuffer.put((byte) argsIndex);
            componentBuffer.put((byte) component.getArgs().size());
            buffer.put(componentBuffer.array());
            argsIndex += component.getArgs().size();
        }
        return buffer.array();
    }

    public static byte[] convertArgs(List<Component> components) {
        int length = components.stream().map(c -> c.getArgs().size()).reduce(Integer::sum).orElse(0);
        ByteBuffer buffer = ByteBuffer.allocate(length);
        components.forEach(c -> c.getArgs().forEach(a -> buffer.put(a.byteValue())));
        return buffer.array();
    }
}
