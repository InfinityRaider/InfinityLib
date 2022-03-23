package com.infinityraider.infinitylib.world;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.infinityraider.infinitylib.InfinityLib;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Set;

public class StructureInjector {
    private final Set<IInfStructure> structures;
    private final ResourceLocation target;

    protected StructureInjector(ResourceLocation target) {
        this.structures = Sets.newIdentityHashSet();
        this.target = target;
    }

    protected void addStructure(IInfStructure structure) {
        this.structures.add(structure);
    }

    @SuppressWarnings("unchecked")
    protected void inject(RegistryAccess registries) {
        // TODO: fix this
        /*
        StructureTemplatePool pool = registries.registryOrThrow(Registry.TEMPLATE_POOL_REGISTRY).getOptional(this.target).orElse(null);
        if(pool == null) {
            InfinityLib.instance.getLogger().error("Could not inject structures into {0}, pool not found", this.target);
            return;
        } try {
            // fetch the field
            Field field = ObfuscationReflectionHelper.findField(StructureTemplatePool.class, "field_214953_e");
            // set accessible
            field.setAccessible(true);
            // remove final modifier
            Field modifiersField = Field.class.getDeclaredField("modifiers");
            modifiersField.setAccessible(true);
            modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
            // make a copy of the field in a new list
            List<StructureTemplatePool> pieces = Lists.newArrayList((List<StructureTemplatePool>) field.get(pool));
            // inject new pieces
            this.structures.forEach(structure -> {
                InfinityLib.instance.getLogger().info("Injecting structure {0} into {1}", structure.id(), this.target);
                LegacySingleJigsawPiece piece = StructureTemplatePool.func_242849_a(structure.id().toString()).apply(structure.placement());
                for (int i = 0; i < structure.weight(); i++) {
                    pieces.add(piece);
                }
            });
            // set the field
            field.set(pool, pieces);
        } catch(Exception e) {
            InfinityLib.instance.getLogger().error("Failed to inject structures into {0}, exception was thrown", this.target);
            InfinityLib.instance.getLogger().printStackTrace(e);
        }
        */
    }
}
