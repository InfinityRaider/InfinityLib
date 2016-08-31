package com.infinityraider.infinitylib.block.blockstate;

import com.google.common.collect.ImmutableMap;
import net.minecraft.block.Block;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;

public class BlockStateWithPos<S extends IBlockState> implements IBlockStateWithPos<S> {
    private final S state;
    private final BlockPos pos;

    public BlockStateWithPos(S state, BlockPos pos) {
        this.state = state;
        this.pos = pos;
    }

    @Override
    public BlockPos getPos() {
    return pos;
}

    @Override
    public S getWrappedState() {
        return this.state;
    }

    @Override
    public Collection<IProperty<?>> getPropertyNames() {
        return getWrappedState().getPropertyNames();
    }

    @Override
    public <T extends Comparable<T>> T getValue(IProperty<T> property) {
        return getWrappedState().getValue(property);
    }

    @Override
    public <T extends Comparable<T>, V extends T> IBlockState withProperty(IProperty<T> property, V value) {
        return getWrappedState().withProperty(property, value);
    }

    @Override
    public <T extends Comparable<T>> IBlockState cycleProperty(IProperty<T> property) {
        return getWrappedState().cycleProperty(property);
    }

    @Override
    public ImmutableMap<IProperty<?>, Comparable<?>> getProperties() {
        return getWrappedState().getProperties();
    }

    @Override
    public Block getBlock() {
        return getWrappedState().getBlock();
    }

    @Override
    public boolean onBlockEventReceived(World worldIn, BlockPos pos, int id, int param) {
        return getWrappedState().onBlockEventReceived(worldIn, pos, id, param);
    }

    @Override
    public void neighborChanged(World worldIn, BlockPos pos, Block block) {
        getWrappedState().neighborChanged(worldIn, pos, block);
    }

    @Override
    public Material getMaterial() {
        return getWrappedState().getMaterial();
    }

    @Override
    public boolean isFullBlock() {
        return getWrappedState().isFullBlock();
    }

    @Override
    public boolean func_189884_a(Entity entity) {
        return getWrappedState().func_189884_a(entity);
    }

    @Override
    public int getLightOpacity() {
        return getWrappedState().getLightOpacity();
    }

    @Override
    public int getLightOpacity(IBlockAccess world, BlockPos pos) {
        return getWrappedState().getLightOpacity(world, pos);
    }

    @Override
    public int getLightValue() {
        return getWrappedState().getLightValue();
    }

    @Override
    public int getLightValue(IBlockAccess world, BlockPos pos) {
        return getWrappedState().getLightValue(world, pos);
    }

    @Override
    public boolean isTranslucent() {
        return getWrappedState().isTranslucent();
    }

    @Override
    public boolean useNeighborBrightness() {
        return getWrappedState().useNeighborBrightness();
    }

    @Override
    public MapColor getMapColor() {
        return getWrappedState().getMapColor();
    }

    @Override
    public IBlockState withRotation(Rotation rot) {
        getWrappedState().withRotation(rot);
        return this;
    }

    @Override
    public IBlockState withMirror(Mirror mirrorIn) {
        getWrappedState().withMirror(mirrorIn);
        return this;
    }

    @Override
    public boolean isFullCube() {
        return getWrappedState().isFullCube();
    }

    @Override
    public EnumBlockRenderType getRenderType() {
        return getWrappedState().getRenderType();
    }

    @Override
    public int getPackedLightmapCoords(IBlockAccess source, BlockPos pos) {
        return getWrappedState().getPackedLightmapCoords(source, pos);
    }

    @Override
    public float getAmbientOcclusionLightValue() {
        return getWrappedState().getAmbientOcclusionLightValue();
    }

    @Override
    public boolean isBlockNormalCube() {
        return getWrappedState().isBlockNormalCube();
    }

    @Override
    public boolean isNormalCube() {
        return getWrappedState().isNormalCube();
    }

    @Override
    public boolean canProvidePower() {
        return getWrappedState().canProvidePower();
    }

    @Override
    public int getWeakPower(IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
        return getWrappedState().getWeakPower(blockAccess, pos, side);
    }

    @Override
    public boolean hasComparatorInputOverride() {
        return getWrappedState().hasComparatorInputOverride();
    }

    @Override
    public int getComparatorInputOverride(World worldIn, BlockPos pos) {
        return getWrappedState().getComparatorInputOverride(worldIn, pos);
    }

    @Override
    public float getBlockHardness(World worldIn, BlockPos pos) {
        return getWrappedState().getBlockHardness(worldIn, pos);
    }

    @Override
    public float getPlayerRelativeBlockHardness(EntityPlayer player, World worldIn, BlockPos pos) {
        return getWrappedState().getPlayerRelativeBlockHardness(player, worldIn, pos);
    }

    @Override
    public int getStrongPower(IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
        return getWrappedState().getStrongPower(blockAccess, pos, side);
    }

    @Override
    public EnumPushReaction getMobilityFlag() {
        return getWrappedState().getMobilityFlag();
    }

    @Override
    public IBlockState getActualState(IBlockAccess blockAccess, BlockPos pos) {
        return getWrappedState().getActualState(blockAccess, pos);
    }

    @Override
    public AxisAlignedBB getSelectedBoundingBox(World worldIn, BlockPos pos) {
        return getWrappedState().getSelectedBoundingBox(worldIn, pos);
    }

    @Override
    public boolean shouldSideBeRendered(IBlockAccess blockAccess, BlockPos pos, EnumFacing facing) {
        return getWrappedState().shouldSideBeRendered(blockAccess, pos, facing);
    }

    @Override
    public boolean isOpaqueCube() {
        return getWrappedState().isOpaqueCube();
    }

    @Nullable
    @Override
    public AxisAlignedBB getCollisionBoundingBox(World worldIn, BlockPos pos) {
        return getWrappedState().getCollisionBoundingBox(worldIn, pos);
    }

    @Override
    public void addCollisionBoxToList(World worldIn, BlockPos pos, AxisAlignedBB box, List<AxisAlignedBB> mask, @Nullable Entity entity) {
        getWrappedState().addCollisionBoxToList(worldIn, pos, box, mask, entity);
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockAccess blockAccess, BlockPos pos) {
        return getWrappedState().getBoundingBox(blockAccess, pos);
    }

    @Override
    public RayTraceResult collisionRayTrace(World worldIn, BlockPos pos, Vec3d start, Vec3d end) {
        return getWrappedState().collisionRayTrace(worldIn, pos, start, end);
    }

    @Override
    public boolean isFullyOpaque() {
        return getWrappedState().isFullyOpaque();
    }

    @Override
    public boolean doesSideBlockRendering(IBlockAccess world, BlockPos pos, EnumFacing side) {
        return getWrappedState().doesSideBlockRendering(world, pos, side);
    }

    @Override
    public boolean isSideSolid(IBlockAccess world, BlockPos pos, EnumFacing side) {
        return getWrappedState().isSideSolid(world, pos, side);
    }
}
