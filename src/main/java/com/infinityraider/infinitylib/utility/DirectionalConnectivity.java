package com.infinityraider.infinitylib.utility;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.datafixers.util.Either;
import net.minecraft.core.Direction;
import net.minecraft.util.Tuple;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class DirectionalConnectivity implements Comparable<DirectionalConnectivity> {
    public static final List<DirectionalConnectivity>[] MAP = init();
    public static final DirectionalConnectivity NONE = MAP[0].get(0);   // Per definition the first list (combination of 0 out of n) always contains only one object)
    public static final Set<DirectionalConnectivity> ALL = Arrays.stream(MAP).flatMap(Collection::stream).collect(Collectors.toSet());

    public static Optional<DirectionalConnectivity> fromString(String string) {
        String[] split = string.split("_");
        int connections = split.length;
        int counter = 0;
        for(String text : split) {
            Direction dir = Direction.byName(text);
            if(dir == null) {
                connections--;
            } else {
                counter += dir.get3DDataValue();
            }
        }
        final int order = counter;
        return MAP[connections].stream().filter(c -> c.order == order).findFirst();
    }

    private final Set<Direction> connections;
    private final Function<Direction, DirectionalConnectivity> adds;
    private final Function<Direction, DirectionalConnectivity> removes;

    private final int order;
    private final String id;

    private DirectionalConnectivity(Set<Direction> connections, Function<Direction, DirectionalConnectivity> adds, Function<Direction, DirectionalConnectivity> removes) {
        this.connections = ImmutableSet.copyOf(connections);
        this.adds = adds;
        this.removes = removes;
        int order = 0;
        StringBuilder builder = new StringBuilder();
        for(Direction dir : this.connections) {
            order += dir.get3DDataValue();
            builder.append(dir.name() + "_");
        }
        builder.deleteCharAt(builder.length() - 1);
        this.order = order;
        this.id = builder.toString();
    }

    @Override
    public int compareTo(DirectionalConnectivity o) {
        int d = this.connections() - o.connections();
        return d == 0 ? this.order - o.order : d;
    }

    @Override
    public String toString() {
        return this.id;
    }

    public boolean isConnected(Direction direction) {
        return this.connections.contains(direction);
    }

    public int connections() {
        return this.connections.size();
    }

    public DirectionalConnectivity toggleConnection(Direction dir) {
        if(this.isConnected(dir)) {
            return this.addConnection(dir);
        } else {
            return this.removeConnection(dir);
        }
    }

    public DirectionalConnectivity addConnection(Direction dir) {
        return this.isConnected(dir) ? this : this.adds.apply(dir);
    }

    public DirectionalConnectivity removeConnection(Direction dir) {
        return this.isConnected(dir) ? this.removes.apply(dir) : this;
    }

    @SuppressWarnings("unchecked")
    private static List<DirectionalConnectivity>[] init() {
        // Initialize all possible combinations
        List<Builder>[] nodes = Combinatorics.combineAsObject(Direction.values().length, (array) -> new Builder().addDirections(array));
        // Add links
        for(int i = 0; i < nodes.length - 1; i++) {
            List<Builder> forwards = nodes[i + 1];
            nodes[0].forEach(builder -> linkForwards(builder, forwards));
        }
        // Build the network
        List<DirectionalConnectivity>[] connections = new List[nodes.length];
        for(int i = 0; i < nodes.length; i++) {
            connections[i] = nodes[i].stream().map(Builder::build).collect(Collectors.toList());
        }
        // Clean the network
        for(int i = 0; i < nodes.length; i++) {
            nodes[i].stream().forEach(Builder::clean);
        }
        return connections;
    }

    private static void linkForwards(Builder builder, List<Builder> forwards) {
        for(Direction direction : Direction.values()) {
            if(builder.hasConnection(direction)) {
                continue;
            }
            for(Builder forward : forwards) {
                if(forward.hasConnection(direction)) {
                    builder.linkForwards(forward, direction);
                }
            }
        }
    }
    private static class Builder {
        private final Set<Direction> connections;
        private final Map<Direction, Either<Builder, DirectionalConnectivity>> adds;
        private final Map<Direction, Either<Builder, DirectionalConnectivity>> removes;

        private DirectionalConnectivity built;

        private Builder() {
            this.connections = Sets.newEnumSet(Collections.emptyList(), Direction.class);
            this.adds = Maps.newEnumMap(Direction.class);
            this.removes = Maps.newEnumMap(Direction.class);
            Arrays.stream(Direction.values()).forEach(dir -> {
                this.adds.put(dir, Either.left(this));
                this.removes.put(dir, Either.left(this));
            });
        }

        private DirectionalConnectivity build() {
            if(this.isBuilt()) {
                this.built = new DirectionalConnectivity(
                        this.connections,
                        dir -> this.adds.get(dir).map(Builder::build, d -> d),
                        dir -> this.removes.get(dir).map(Builder::build, d -> d)
                );
            }
            return this.built;
        }


        private boolean isBuilt() {
            return this.built != null;
        }

        private boolean hasConnection(Direction direction) {
            return this.connections.contains(direction);
        }

        private Builder addDirections(int[] ordinals) {
            Arrays.stream(ordinals).forEach(this::addDirection);
            return this;
        }

        private Builder addDirection(int ordinal) {
            return this.addDirection(Direction.from3DDataValue(ordinal));
        }

        private Builder addDirection(Direction connection) {
            this.connections.add(connection);
            this.adds.put(connection, Either.left(this));
            return this;
        }

        private Builder linkForwards(Builder other, Direction direction) {
            this.adds.put(direction, Either.left(other));
            other.removes.put(direction, Either.left(this));
            return this;
        }

        private void clean() {
            cleanMap(this.adds);
            cleanMap(this.removes);
        }

        private static void cleanMap(Map<Direction, Either<Builder, DirectionalConnectivity>> map) {
            map.entrySet().stream().filter(e -> e.getValue().left().map(Builder::isBuilt).orElse(false))
                    .map(e -> e.getValue().left().map(b -> new Tuple<>(e, b)))
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .forEach(t -> t.getA().setValue(Either.right(t.getB().build())));
        }
    }
}
