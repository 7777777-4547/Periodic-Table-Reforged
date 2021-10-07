package me.jellysquid.mods.lithium.common.jvm;

import com.google.common.collect.ImmutableMap;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.Map;

@SuppressWarnings("unchecked")
public class ClassConstructors {
    private static Constructor<ImmutableMap<?, ?>> FAST_IMMUTABLE_REFERENCE_HASH_MAP_CONSTRUCTOR;

    public static void init() {
        try {
            initGuavaExtensions();
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("Couldn't perform gross class loading hacks", e);
        }
    }

    private static void initGuavaExtensions() throws ReflectiveOperationException {
        ClassLoader loader = ImmutableMap.class.getClassLoader();

        ClassDefineTool.defineClass(loader, "com.google.common.collect.lithiumImmutableMapEntry");
        ClassDefineTool.defineClass(loader, "com.google.common.collect.lithiumImmutableReferenceHashMap");
        ClassDefineTool.defineClass(loader, "com.google.common.collect.lithiumEntrySet");
        ClassDefineTool.defineClass(loader, "com.google.common.collect.lithiumEntrySetIterator");

        FAST_IMMUTABLE_REFERENCE_HASH_MAP_CONSTRUCTOR = (Constructor<ImmutableMap<?, ?>>)
                loader.loadClass("com.google.common.collect.lithiumImmutableReferenceHashMap")
                        .getDeclaredConstructor(Map.class);
    }

    public static <K, V> ImmutableMap<K, V> createFastImmutableMap(Map<K, V> orig) {
        try {
            return (ImmutableMap<K, V>) FAST_IMMUTABLE_REFERENCE_HASH_MAP_CONSTRUCTOR.newInstance(orig);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("Could not instantiate collection", e);
        }
    }
}
