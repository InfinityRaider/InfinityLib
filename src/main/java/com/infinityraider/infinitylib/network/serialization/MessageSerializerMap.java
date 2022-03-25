package com.infinityraider.infinitylib.network.serialization;

import com.google.common.collect.Maps;
import com.infinityraider.infinitylib.InfinityLib;
import net.minecraft.network.FriendlyByteBuf;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ConcurrentMap;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class MessageSerializerMap<K, V> implements IMessageSerializer<Map<K, V>> {
    public static final IMessageSerializer INSTANCE = new MessageSerializerMap();

    @Override
    public boolean accepts(Class<Map<K, V>> clazz) {
        return Type.accepts(clazz);
    }

    @Override
    public IMessageWriter<Map<K, V>> getWriter(Class<Map<K, V>> clazz) {
        return ((buf, data) -> {
            int count = data.size();
            buf.writeInt(count);
            Type.identifyMap(clazz, buf, data);
            if(count > 0) {
                int written = 0;
                for(Map.Entry<K,V> entry : data.entrySet()) {
                    Optional<IMessageSerializer<K>> keySerializerOpt = Util.getSerializer(entry.getKey());
                    Optional<IMessageSerializer<V>> valueSerializerOpt = Util.getSerializer(entry.getValue());
                    if(Util.write(keySerializerOpt, buf, entry.getKey()) && Util.write(valueSerializerOpt, buf, entry.getValue())) {
                        written = written + 1;
                    } else {
                        InfinityLib.instance.getLogger().error("Failed serialization of a map entry: ");
                        InfinityLib.instance.getLogger().error(entry.toString());
                    }
                }
                buf.writeInt(written);
            }
        });
    }

    @Override
    public IMessageReader<Map<K, V>> getReader(Class<Map<K, V>> clazz) {
        return (buf) -> {
            int count = buf.readInt();
            Map<K,V> map = Type.createMap(clazz, buf).orElseThrow();
            if(count > 0) {
                int written = buf.readInt();
                int read = 0;
                for(int i = 0; i < written; i++) {
                    Optional<K> key = Util.read(buf);
                    Optional<V> value = Util.read(buf);
                    if(key.isPresent() && value.isPresent()) {
                        map.put(key.get(), value.get());
                        read = read + 1;
                    } else {
                        InfinityLib.instance.getLogger().error("Failed deserialization of a map entry");
                    }
                }
            }
            return map;
        };
    }

    private enum Type {
        HASH(HashMap.class, (buf, map) -> {}, buf -> Maps.newHashMap()),
        IDENTITY(IdentityHashMap.class, (buf, map) -> {}, buf -> Maps.newIdentityHashMap()),
        CONCURRENT(ConcurrentMap.class, (buf, map) -> {}, buf -> Maps.newConcurrentMap()),
        ENUM(EnumMap.class, (buf, map) -> buf.writeUtf(Util.readKeyType(map)), buf -> Util.createEnumMap(buf.readUtf()));

        private final Class<? extends Map<?,?>> clazz;
        private final BiConsumer<FriendlyByteBuf, ? extends Map<?,?>> identifier;
        private final Function<FriendlyByteBuf, ? extends Map<?,?>> factory;

        <M extends Map<?,?>> Type(Class<M> clazz, BiConsumer<FriendlyByteBuf, M> identifier, Function<FriendlyByteBuf, M> factory) {
            this.clazz = clazz;
            this.identifier = identifier;
            this.factory = factory;
        }

        public Class<? extends Map<?,?>> getMapClass() {
            return this.clazz;
        }

        public boolean matches(Class<? extends Map> clazz) {
            boolean match = clazz == this.getMapClass();
            if(match && this == ENUM) {
                match = Util.field != null;
                if(!match) {
                    InfinityLib.instance.getLogger().error("Could not (de)serialize an EnumMap on a Message due to Reflection failure (com.infinityraider.infinitylib.network.serialization)");
                }
            }
            return match;
        }

        @SuppressWarnings({"unchecked","unused"})
        private <K,V> BiConsumer<FriendlyByteBuf, Map<K,V>> getIdentifier(Map<K,V> map) {
            return (BiConsumer<FriendlyByteBuf, Map<K,V>>) this.identifier;
        }

        @SuppressWarnings("unchecked")
        private <K,V> Function<FriendlyByteBuf, Map<K,V>> getFactory() {
            return (Function<FriendlyByteBuf, Map<K,V>>) this.factory;
        }

        public <K,V> void identifyMap(FriendlyByteBuf buf, Map<K,V> map) {
            this.getIdentifier(map).accept(buf, map);
        }

        @SuppressWarnings("unchecked")
        public <K,V> Map<K,V> createMap(FriendlyByteBuf buf) {
            return (Map<K,V>) this.getFactory().apply(buf);
        }

        public static boolean accepts(Class<? extends Map<?,?>> clazz) {
            return Arrays.stream(values()).anyMatch(type -> type.matches(clazz));
        }

        public static <K,V> void identifyMap(Class<? extends Map<K,V>> clazz, FriendlyByteBuf buf, Map<K,V> map) {
            Arrays.stream(values())
                    .sorted()
                    .filter(type -> type.matches(clazz))
                    .findFirst()
                    .ifPresent(type -> type.identifyMap(buf, map));
        }

        public static <K,V> Optional<Map<K,V>> createMap(Class<? extends Map<K,V>> clazz, FriendlyByteBuf buf) {
            return Arrays.stream(values())
                    .sorted()
                    .filter(type -> type.matches(clazz))
                    .findFirst()
                    .map(type -> type.createMap(buf));
        }
    }

    private static class Util {
        private static final Field field;

        static {
            Field temp;
            try {
                temp = EnumMap.class.getDeclaredField("keyType");
            } catch (NoSuchFieldException e) {
                temp = null;
                e.printStackTrace();
            }
            field = temp;
            field.setAccessible(true);
        }

        private static <T> Optional<IMessageSerializer<T>> getSerializer(Class<T> clazz) {
            return MessageSerializerStore.getMessageSerializer(clazz);
        }

        @SuppressWarnings("unchecked")
        private static <T> Class<T> getClass(T object) {
            return (Class<T>) object.getClass();
        }

        private static <T> Optional<IMessageSerializer<T>> getSerializer(T object) {
            return getSerializer(getClass(object));
        }

        private static <T> boolean write(Optional<IMessageSerializer<T>> serializer, FriendlyByteBuf buf, T object) {
            Class<T> clazz = getClass(object);
            if(serializer.isPresent() && serializer.get().accepts(clazz)) {
                buf.writeUtf(clazz.toString());
                serializer.get().getWriter(clazz).writeData(buf, object);
                return true;
            }
            return false;
        }

        @SuppressWarnings("Unchecked")
        private static <T> Optional<T> read(FriendlyByteBuf buf) {
            try {
                Class<T> clazz = (Class<T>) Class.forName(buf.readUtf());
                return getSerializer(clazz).map(ser -> ser.getReader(clazz).readData(buf));
            } catch (Exception e) {
                InfinityLib.instance.getLogger().printStackTrace(e);
                return Optional.empty();
            }
        }

        @SuppressWarnings("Unchecked")
        private static EnumMap createEnumMap(String name) {
            try {
                Class clazz = Class.forName(name);
                return Maps.newEnumMap(clazz);
            } catch (ClassNotFoundException e) {
                InfinityLib.instance.getLogger().printStackTrace(e);
                throw new IllegalStateException("Invalid enum");
            }
        }

        @SuppressWarnings("Unchecked")
        private static String readKeyType(EnumMap map) {
            try {
                return (String) field.get(map);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                InfinityLib.instance.getLogger().printStackTrace(e);
                throw new IllegalStateException("Invalid enum");
            }
        }

    }
}
