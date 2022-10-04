/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dev.cnpuvache.gba.tile_manager.collectors;

import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 *
 * @author Reznov
 */
public class SingletonCollector {
    
    public static <T> Collector<T, ?, T> toSingleton() {
    return Collectors.collectingAndThen(
            Collectors.toList(),
            list -> {
                if (list.size() != 1) {
                    throw new IllegalStateException();
                }
                return list.get(0);
            }
    );
}
    
}
