package dev.cnpuvache.gba.tile_manager.util;

import dev.cnpuvache.gba.tile_manager.domain.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ComponentResolver {

    private ComponentResolver() {
    }

    private static double distance(Component one, Component two) {
        double oneCenterX = (double) (one.getEndX() + one.getBeginX()) / 2;
        double oneCenterY = (double) (one.getEndY() + one.getBeginY()) / 2;
        double twoCenterX = (double) (two.getEndX() + two.getBeginX()) / 2;
        double twoCenterY = (double) (two.getEndY() + two.getBeginY()) / 2;
        return Math.sqrt(Math.pow(twoCenterX - oneCenterX, 2) + Math.pow(twoCenterY - oneCenterY, 2));
    }

    private static double drift(Component one, Component two, boolean horizontal) {
        if (horizontal) {
            return driftY(one, two);
        } else {
            return driftX(one, two);
        }
    }

    private static int driftX(Component one, Component two) {
        double oneCenterX = (double) (one.getEndX() + one.getBeginX()) / 2;
        double twoCenterX = (double) (two.getEndX() + two.getBeginX()) / 2;
        return (int) (Math.abs(twoCenterX - oneCenterX) * 100);
    }

    private static int driftY(Component one, Component two) {
        double oneCenterY = (double) (one.getEndY() + one.getBeginY()) / 2;
        double twoCenterY = (double) (two.getEndY() + two.getBeginY()) / 2;
        return (int) (Math.abs(twoCenterY - oneCenterY) * 100);
    }

    private static Component mostPreferred(Component reference, List<Component> gladiators, boolean horizontal) {
            Comparator<Object> componentComparator = Comparator.comparingDouble(g -> drift(reference, (Component) g, horizontal))
                            .thenComparingDouble(g -> distance(reference, (Component) g));
            return gladiators.stream().min(componentComparator).orElse(null);
    }

    public static void resolve(List<Component> components) {
        components.forEach(Component::reset);
        for (Component reference : components) {
            List<Component> norths = new ArrayList<>();
            List<Component> easts = new ArrayList<>();
            List<Component> souths = new ArrayList<>();
            List<Component> wests = new ArrayList<>();
            for (Component gladiator : components) {
                if (reference.equals(gladiator)) {
                    continue;
                }
                if (gladiator.getBeginY() < reference.getBeginY()) {
                    norths.add(gladiator);
                } else if (gladiator.getBeginY() > reference.getBeginY()) {
                    souths.add(gladiator);
                }

                if (gladiator.getBeginX() > reference.getEndX()) {
                    easts.add(gladiator);
                } else if (gladiator.getEndX() < reference.getBeginX()) {
                    wests.add(gladiator);
                }
            }
            reference.setNorth(mostPreferred(reference, norths, false));
            reference.setEast(mostPreferred(reference, easts, true));
            reference.setSouth(mostPreferred(reference, souths, false));
            reference.setWest(mostPreferred(reference, wests, true));
        }
    }

}
