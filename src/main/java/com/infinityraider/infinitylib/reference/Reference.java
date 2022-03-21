package com.infinityraider.infinitylib.reference;

public final class Reference {
    public static final String MOD_ID = /*^${mod.id}^*/ "infinitylib";
    public static final String MOD_NAME = /*^${mod.name}^*/ "InfinityLib";
    public static final String AUTHOR = /*^${mod.author}^*/ "InfinityRaider";

    public static final String VER_MAJOR = /*^${mod.version_major}^*/ "2";
    public static final String VER_MINOR = /*^${mod.version_minor}^*/ "1";
    public static final String VER_PATCH = /*^${mod.version_patch}^*/ "0";
    public static final String MOD_VERSION = /*^${mod.version}^*/ VER_MAJOR + "." + VER_MINOR + "." + VER_PATCH;
    public static final String VERSION = /*^${mod.version_minecraft}-${mod.version}^*/ "1.18.2-" + MOD_VERSION;

    public static final String GUI_FACTORY_CLASS = "com.infinityraider.infinitylib.gui.GuiFactory";

    public static final String DEPENCENCY = "required-after:infinitylib";
}