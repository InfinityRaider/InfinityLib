package com.infinityraider.infinitylib.utility;

import com.infinityraider.infinitylib.InfinityLib;
import sun.misc.Unsafe;

import java.lang.reflect.Field;

public abstract class UnsafeUtil {
    private static final UnsafeUtil INSTANCE = init();

    public static UnsafeUtil getInstance() {
        return INSTANCE;
    }

    private UnsafeUtil() {}

    public abstract boolean replaceField(Field field, Object target, Object value);

    public abstract boolean replaceStaticField(Field field, Object value);

    public abstract boolean replaceField(Field field, Object target, boolean value);

    public abstract boolean replaceStaticField(Field field, boolean value);

    public abstract boolean replaceField(Field field, Object target, byte value);

    public abstract boolean replaceStaticField(Field field, byte value);

    public abstract boolean replaceField(Field field, Object target, short value);

    public abstract boolean replaceStaticField(Field field, short value);

    public abstract boolean replaceField(Field field, Object target, int value);

    public abstract boolean replaceStaticField(Field field, int value);

    public abstract boolean replaceField(Field field, Object target, long value);

    public abstract boolean replaceStaticField(Field field, long value);

    public abstract boolean replaceField(Field field, Object target, float value);

    public abstract boolean replaceStaticField(Field field, float value);

    public abstract boolean replaceField(Field field, Object target, double value);

    public abstract boolean replaceStaticField(Field field, double value);

    public abstract boolean replaceField(Field field, Object target, char value);

    public abstract boolean replaceStaticField(Field field, char value);

    public abstract <T> T instantiateObject(Class<T> clazz) throws InstantiationException;

    private static UnsafeUtil init() {
        try {
            Field f = Unsafe.class.getDeclaredField("theUnsafe");
            f.setAccessible(true);
            return new Instance((Unsafe) f.get(null));
        } catch(Exception e) {
            InfinityLib.instance.getLogger().error("Failed to initialize UnsafeUtil");
            InfinityLib.instance.getLogger().printStackTrace(e);
        }
        return new Dummy();
    }

    private static class Instance extends UnsafeUtil {
        private final Unsafe unsafe;

        private Instance(Unsafe unsafe) {
            this.unsafe = unsafe;
        }

        @Override
        public boolean replaceField(Field field, Object target, Object value) {
            this.unsafe.putObject(target, this.unsafe.objectFieldOffset(field), value);
            return true;
        }

        @Override
        public boolean replaceStaticField(Field field, Object value) {
            this.unsafe.putObject(this.unsafe.staticFieldBase(field), this.unsafe.staticFieldOffset(field), value);
            return true;
        }

        @Override
        public boolean replaceField(Field field, Object target, boolean value) {
            this.unsafe.putBoolean(target, unsafe.objectFieldOffset(field), value);
            return true;
        }

        @Override
        public boolean replaceStaticField(Field field, boolean value) {
            this.unsafe.putBoolean(this.unsafe.staticFieldBase(field), unsafe.staticFieldOffset(field), value);
            return true;
        }

        @Override
        public boolean replaceField(Field field, Object target, byte value) {
            this.unsafe.putByte(target, unsafe.objectFieldOffset(field), value);
            return true;
        }

        @Override
        public boolean replaceStaticField(Field field, byte value) {
            this.unsafe.putByte(this.unsafe.staticFieldBase(field), unsafe.staticFieldOffset(field), value);
            return true;
        }

        @Override
        public boolean replaceField(Field field, Object target, short value) {
            this.unsafe.putShort(target, unsafe.objectFieldOffset(field), value);
            return true;
        }

        @Override
        public boolean replaceStaticField(Field field, short value) {
            this.unsafe.putShort(this.unsafe.staticFieldBase(field), unsafe.staticFieldOffset(field), value);
            return true;
        }

        @Override
        public boolean replaceField(Field field, Object target, int value) {
            this.unsafe.putInt(target, unsafe.objectFieldOffset(field), value);
            return true;
        }

        @Override
        public boolean replaceStaticField(Field field, int value) {
            this.unsafe.putInt(this.unsafe.staticFieldBase(field), unsafe.staticFieldOffset(field), value);
            return true;
        }

        @Override
        public boolean replaceField(Field field, Object target, long value) {
            this.unsafe.putLong(target, unsafe.objectFieldOffset(field), value);
            return true;
        }

        @Override
        public boolean replaceStaticField(Field field, long value) {
            this.unsafe.putLong(this.unsafe.staticFieldBase(field), unsafe.staticFieldOffset(field), value);
            return true;
        }

        @Override
        public boolean replaceField(Field field, Object target, float value) {
            this.unsafe.putFloat(target, unsafe.objectFieldOffset(field), value);
            return true;
        }

        @Override
        public boolean replaceStaticField(Field field, float value) {
            this.unsafe.putFloat(this.unsafe.staticFieldBase(field), unsafe.staticFieldOffset(field), value);
            return true;
        }

        @Override
        public boolean replaceField(Field field, Object target, double value) {
            this.unsafe.putDouble(target, unsafe.objectFieldOffset(field), value);
            return true;
        }

        @Override
        public boolean replaceStaticField(Field field, double value) {
            this.unsafe.putDouble(this.unsafe.staticFieldBase(field), unsafe.staticFieldOffset(field), value);
            return true;
        }

        @Override
        public boolean replaceField(Field field, Object target, char value) {
            return false;
        }

        @Override
        public boolean replaceStaticField(Field field, char value) {
            return false;
        }

        @Override
        @SuppressWarnings("unchecked")
        public <T> T instantiateObject(Class<T> clazz) throws InstantiationException {
            return (T) this.unsafe.allocateInstance(clazz);
        }
    }

    private static class Dummy extends UnsafeUtil {
        private boolean logError(Field field) {
            InfinityLib.instance.getLogger().error("Failed to initialize UnsafeUtil, can not replace field " + field.getName());
            return false;
        }

        @Override
        public boolean replaceField(Field field, Object target, Object value) {
            return this.logError(field);
        }

        @Override
        public boolean replaceStaticField(Field field, Object value) {
            return this.logError(field);
        }

        @Override
        public boolean replaceField(Field field, Object target, boolean value) {
            return this.logError(field);
        }

        @Override
        public boolean replaceStaticField(Field field, boolean value) {
            return this.logError(field);
        }

        @Override
        public boolean replaceField(Field field, Object target, byte value) {
            return this.logError(field);
        }

        @Override
        public boolean replaceStaticField(Field field, byte value) {
            return this.logError(field);
        }

        @Override
        public boolean replaceField(Field field, Object target, short value) {
            return this.logError(field);
        }

        @Override
        public boolean replaceStaticField(Field field, short value) {
            return this.logError(field);
        }

        @Override
        public boolean replaceField(Field field, Object target, int value) {
            return this.logError(field);
        }

        @Override
        public boolean replaceStaticField(Field field, int value) {
            return this.logError(field);
        }

        @Override
        public boolean replaceField(Field field, Object target, long value) {
            return this.logError(field);
        }

        @Override
        public boolean replaceStaticField(Field field, long value) {
            return this.logError(field);
        }

        @Override
        public boolean replaceField(Field field, Object target, float value) {
            return this.logError(field);
        }

        @Override
        public boolean replaceStaticField(Field field, float value) {
            return this.logError(field);
        }

        @Override
        public boolean replaceField(Field field, Object target, double value) {
            return this.logError(field);
        }

        @Override
        public boolean replaceStaticField(Field field, double value) {
            return this.logError(field);
        }

        @Override
        public boolean replaceField(Field field, Object target, char value) {
            return this.logError(field);
        }

        @Override
        public boolean replaceStaticField(Field field, char value) {
            return this.logError(field);
        }

        @Override
        public <T> T instantiateObject(Class<T> clazz) throws InstantiationException {
            InfinityLib.instance.getLogger().error("Failed to initialize UnsafeUtil, can not instantiate object for " + clazz.getName());
            throw new InstantiationException();
        }
    }


}
